# ImmutableColumnInfo TableModelEditor Repro

<!-- Plugin description -->
Minimal IntelliJ Platform plugin that reproduces a bug in `TableModelEditor`/`ValidatingTableEditor` with `ImmutableColumnInfo`.

Steps to reproduce:
1. Run the IDE from this project: `./gradlew runIde`.
2. In the IDE, open menu: Tools → ImmutableColumnInfo Repro → "ImmutableColumnInfo TableModelEditor Demo".
3. Edit the Name/Age cells and press OK.
4. Expected: the resulting list reflects edits. Actual: edits are not applied because editors assume in-place mutation via `ColumnInfo.setValue`, ignoring `ImmutableColumnInfo.withValue`.
<!-- Plugin description end -->

## How to run
```bash
./gradlew runIde
```

Open Tools → ImmutableColumnInfo Repro → "ImmutableColumnInfo TableModelEditor Demo".
