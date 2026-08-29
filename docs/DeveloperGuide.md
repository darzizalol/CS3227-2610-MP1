# Focus Farm Developer Guide

## Product purpose

Focus Farm is a gamified personal countdown timer and harvest tracker. It is
deliberately not a task manager: it stores no task descriptions, deadlines, or
done/not-done state.

## Technology

- Java SE 25
- JavaFX 25.0.4
- Gradle 9.1 wrapper
- Jackson 2.22 for JSON persistence
- JUnit Jupiter 5.14
- Checkstyle 14
- JaCoCo 0.8.15

## Architecture

```text
JavaFX UI
  MainWindow ─ FarmView ─ PlotView
             ├ DashboardView
             └ ChatPanel
       │
LogicManager ─ CommandParser ─ FarmCommand implementations
       │
Farm ─ FarmPlot state machines ─ Clock
       │
FarmStorage ─ JsonFarmStorage
```

### UI

`MainWindow` owns the 70:30 split, one finite-lifetime JavaFX `Timeline`, and
the presentation refresh cycle. `FarmView` and `DashboardView` receive only
immutable snapshots. UI animations are cosmetic and never change model state.

`CountdownRefreshCoordinator` keeps the timeline independent of persistence
failures. It retries on the next tick, reports a continuing outage only once,
and announces when refreshes recover.

The crop drawings use original JavaFX shapes. The MVP has no external image,
font, music, or sound assets.

### Logic

`CommandParser` converts slash commands into `FarmCommand` objects. Each command
changes the `Farm` through its public API and returns a `CommandResult` containing
feedback, an optional plot ID, and a cosmetic `FarmEvent`.

`LogicManager` applies each mutation to an independent candidate farm. It saves
the candidate snapshot before publishing that candidate as the live farm, so a
storage failure cannot leak an unpersisted command or timer transition into the
UI. Read-only commands do not cause writes. Time-driven transitions are saved
only when a crop actually becomes ready.

The `/exit` command is the deliberate exception to the read-only rule:
`LogicManager` saves before returning its exit request. A save failure therefore
reaches the UI before shutdown is scheduled, leaving the application open for a
retry.

### Model and time

Each `FarmPlot` implements this state machine:

```text
EMPTY --plant--> PLANTED --water--> GROWING --time--> READY --harvest--> EMPTY
                                      |
                                      +--fertilize once, reduce remaining time 25%
```

The model stores `readyAt` as an `Instant`. A JavaFX animation or delayed UI
frame cannot change maturity. `java.time.Clock` is injected so tests can advance
time without sleeping.

### Storage

`JsonFarmStorage` writes a schema-versioned snapshot to a temporary file and
then replaces the data file. It requests an atomic move and falls back to a
normal replacement on file systems that do not support atomic moves.

Malformed or unsupported data is moved to a timestamped backup before a clean
farm is created. Storage paths use `java.nio.file.Path` and are OS-independent.

## Quality process

Run the mandatory local gate:

```bash
./gradlew clean check
```

The gate compiles with `-Xlint:all -Werror`, runs Checkstyle, executes JUnit,
and enforces at least 80% line and 70% branch coverage for model, logic, and
storage packages. UI code is manually inspected because JavaFX rendering is
not meaningfully verified by unit coverage alone.

CI repeats the quality gate and builds a platform-specific JAR on Windows,
Linux, and macOS. Before release, also run:

```bash
./gradlew release
java -jar release/FocusFarm.jar
```

## Testing strategy

- Model tests cover every lifecycle transition and invalid transition.
- Clock tests cover one instant before readiness, exact readiness, restart, and
  fertilizer calculations without `Thread.sleep`.
- Parser tests cover all commands, whitespace, case, malformed durations,
  unknown crops, and invalid argument counts.
- Storage tests cover missing files, round trips, wrong schemas, corrupt JSON,
  backup creation, and parent-directory creation.
- Logic tests inject save failures and verify that command and timer mutations
  are discarded until persistence succeeds.
- Exit tests verify that success is returned only after saving and that a failed
  shutdown save leaves `/exit` available for retry.
- UI coordination tests verify that failed timer saves are retried, duplicate
  errors are suppressed, and the dashboard refreshes after recovery.
- A manual GUI session checks the 70:30 layout, countdown refresh, animations,
  dashboard updates, resizing, restart, and the packaged JAR.

## Acknowledgements

- The component separation and command architecture were informed by the
  CS2103/T AddressBook-Level3-derived tP.
  That project and AB3 are available under the MIT License. No address-book
  domain classes, commands, or assets were copied into Focus Farm.
- JavaFX provides the desktop UI and animation APIs.
- Jackson provides JSON serialization.
- JUnit, Checkstyle, JaCoCo, and Gradle provide automated verification.
- OpenAI Codex was used for requirements analysis, planning, implementation,
  test generation, review, and documentation. All output remains subject to
  human verification by the student.
