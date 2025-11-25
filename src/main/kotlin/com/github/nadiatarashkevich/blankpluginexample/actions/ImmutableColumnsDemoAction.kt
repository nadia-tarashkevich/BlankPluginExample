package com.github.nadiatarashkevich.blankpluginexample.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.CollectionItemEditor
import com.intellij.util.ui.ImmutableColumnInfo
import com.intellij.util.ui.table.TableModelEditor
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.BoxLayout

/**
 * Action that opens a small dialog demonstrating how TableModelEditor and
 * ValidatingTableEditor behave with ImmutableColumnInfo columns.
 *
 * To reproduce the bug: try editing cells and press OK. The resulting list
 * remains unchanged because ImmutableColumnInfo does not mutate items, while
 * the editors assume in-place mutation via ColumnInfo.setValue().
 */
class ImmutableColumnsDemoAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val dialog = ImmutableDataDialog(project)
        dialog.show()
    }
}

private data class Person(val name: String, val age: Int)

private class ImmutableDataDialog(project: Project?) : DialogWrapper(project) {
    private val initial = listOf(
        Person("Alice", 30),
        Person("Bob", 25)
    )

    private val editor: TableModelEditor<Person>

    init {
        title = "ImmutableColumnInfo Demo"

        val nameCol = object : ImmutableColumnInfo<Person, String>("Name") {
            override fun valueOf(item: Person): String = item.name
            override fun withValue(item: Person, value: String): Person = item.copy(name = value)
            override fun isCellEditable(item: Person): Boolean = true
            override fun getColumnClass(): Class<*> = String::class.java
        }

        val ageCol = object : ImmutableColumnInfo<Person, Int>("Age") {
            override fun valueOf(item: Person): Int = item.age
            override fun withValue(item: Person, value: Int): Person = item.copy(age = value)
            override fun isCellEditable(item: Person): Boolean = true
            override fun getColumnClass(): Class<*> = Int::class.javaPrimitiveType ?: Integer::class.java
        }

        val itemEditor = object : CollectionItemEditor<Person> {
            override fun getItemClass(): Class<Person> = Person::class.java
            override fun clone(item: Person, forInPlaceEditing: Boolean): Person = item.copy()
        }

        editor = TableModelEditor(arrayOf(nameCol, ageCol), itemEditor, "No items")
        editor.reset(initial.toMutableList())

        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.add(JBLabel("Edit values and press OK. Expected: values change. Actual: with ImmutableColumnInfo they don't."))
        panel.add(editor.createComponent())
        return panel
    }

    override fun doOKAction() {
        // Apply changes collected by the editor
        val result: List<Person> = editor.apply()
        // Show the result in the title so it's visible during manual testing
        // If the bug reproduces, 'result' will equal 'initial'.
        title = "Result: $result"
        super.doOKAction()
    }
}
