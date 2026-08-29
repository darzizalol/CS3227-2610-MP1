# Focus Farm User Guide

Focus Farm is a Java desktop countdown timer and harvest tracker. It turns six
independent timers into a small pixel-inspired farm: plant a crop, water it to
start its timer, optionally fertilize it once, and harvest it when it matures.

## Requirements

- Java SE 25
- Windows, Linux, or macOS
- A screen resolution of at least 980 × 650

## Starting Focus Farm

### From the project folder

macOS users with the Homebrew `openjdk@25` formula can run:

```bash
JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./gradlew run
```

On any operating system where Java 25 is already the active JDK:

```bash
./gradlew run
```

Windows users should use `gradlew.bat run`.

### From the packaged JAR

From the repository root:

```bash
java -jar release/FocusFarm.jar
```

On this project's macOS development machine, Java 25 is installed side-by-side,
so use the explicit executable:

```bash
/opt/homebrew/opt/openjdk@25/bin/java -jar release/FocusFarm.jar
```

JavaFX uses native libraries. The checked-in MVP JAR is built for macOS Apple
Silicon. The CI workflow builds separate JAR artifacts for Windows, Linux, and
macOS; use the artifact built for your operating system and CPU architecture.

If Focus Farm temporarily cannot save a crop transition, it reports the error
once and retries automatically on subsequent countdown ticks. Countdown and
dashboard updates resume after saving recovers.

## Interface

- The left side contains the six farm plots and harvest dashboard.
- The right side contains the farm terminal and command input.
- Each plot shows its crop, lifecycle state, and countdown.
- The dashboard shows inventory counts, total harvests, growing plots, ready
  plots, and the most recent harvest.

## Commands

All commands begin with `/`. Commands and crop names are case-insensitive.
Extra spaces between words are accepted.

### Plant a crop

```text
/plant <plot> <crop> <duration>
```

Example:

```text
/plant 2 carrot 10s
```

- Plot must be from `1` to `6`.
- Crop must be `carrot`, `tomato`, `corn`, `strawberry`, `pumpkin`, or
  `cabbage`.
- Duration must be between 10 seconds and 60 minutes.
- Use `s` for seconds or `m` for minutes, such as `30s` or `5m`.
- Planting does not begin the timer. The plot must be watered next.

### Water a planted crop

```text
/water <plot>
```

Watering begins the configured countdown. A crop can only be watered once.

### Fertilize a growing crop

```text
/fertilize <plot>
```

Fertilizer reduces the crop's current remaining time by 25%. It can be applied
only once per crop and only while the crop is actively growing.

### Harvest a mature crop

```text
/harvest <plot>
```

Harvesting is allowed only when the plot says `READY TO HARVEST`. The harvested
crop is added to the dashboard inventory and the plot becomes empty.

### View text status

```text
/status
```

This prints the crop and state of every plot in the terminal.

### View help or exit

```text
/help
/exit
```

`/exit` saves the farm before closing the window.

## Persistence

Farm data is saved automatically to `data/farm.json`, relative to the folder
from which the application was started. Growth is based on absolute timestamps,
so watered crops continue to grow while Focus Farm is closed.

If saved JSON is unreadable, Focus Farm moves it to a timestamped
`farm.json.corrupt-*` backup and starts an empty farm rather than silently
overwriting the damaged file.

## MVP limitations

- There are exactly six plots.
- Fertilizer is unlimited but can be applied only once to each planted crop.
- There is no currency, shop, weather, farmer movement, sound, or direct mouse
  interaction with plots.
- The terminal is the only way to change farm state.
