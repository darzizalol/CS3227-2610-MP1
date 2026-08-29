# AI interaction summary: Focus Farm MVP

Date: 2026-08-29
Verification status: Pending final student review

## User goal

Build an individual Java SE 25 desktop utility based on the previous CS2103/T
tP technology. The approved product is Focus Farm, a pixel-inspired personal
countdown timer and harvest tracker with six plots and a 70:30 farm-to-terminal
layout.

## Decisions

- Rejected a themed to-do manager because it remained too close to the
  prohibited iP/tP functionality.
- Reframed the farm mechanics as a countdown utility rather than a pure game.
- Any of six crop types can be planted in any empty plot.
- Watering starts growth; an unwatered crop does not grow.
- Growth continues while the app is closed using persisted absolute timestamps.
- Fertilizer is usable once per crop and removes 25% of the current remaining
  time.
- Durations range from 10 seconds to 60 minutes and use `s` or `m` suffixes.
- The model owns correctness; JavaFX animations are cosmetic only.
- No runtime LLM, API key, network service, external artwork, sound, or telemetry
  is included in the MVP.

## Implementation summary

- Installed Homebrew `openjdk@25` side-by-side without changing the system Java.
- Bootstrapped Gradle 9.1, JavaFX 25.0.4, Jackson, JUnit, Checkstyle, and JaCoCo.
- Implemented a six-plot domain state machine and injectable clock.
- Implemented slash-command parsing and command objects.
- Implemented schema-versioned JSON persistence, temporary-file writes, and
  corrupt-file backups.
- Implemented a JavaFX 70:30 interface with six plots, shape-based crop graphics,
  countdowns, chat transcript, animations, and harvest dashboard.
- Added deterministic tests, coverage enforcement, strict compilation, CI, user
  documentation, developer documentation, and reflection drafts.

## Problems found during verification

- Gradle 9.1 requires the JUnit Platform launcher on the test runtime classpath;
  it was added after the first test process failed to start.
- A literal `25%` inside a formatted text block was interpreted as a format
  conversion; it was escaped and covered by command execution tests.
- The first countdown calculation displayed 11 seconds for an exact 10-second
  duration. The remaining-time calculation was corrected to use millisecond
  ceiling division.

## Verification completed in this session

- The complete Checkstyle, test, and coverage gate passed.
- A deterministic mixed-state farm was rendered to PNG and visually inspected.
- The packaged JAR launched successfully on macOS Apple Silicon with Java 25.
- End-to-end command flows, persistence, corrupt-data recovery, and clock-driven
  transitions were exercised through automated tests without sleeping.

## Remaining verification

- Allow Windows and Linux CI jobs to build and validate their platform JARs
  after pushing.
- Run manual interaction tests on Windows and Linux.
- Student must review this summary and correct any inaccurate statements before
  submission.

## Git workflow follow-up

- Replaced the single MVP implementation commit with focused commits for build
  tooling, the domain model, persistence, command handling, JavaFX UI, release
  automation, documentation, and repository conventions.
- Renamed the primary branch from `main` to `master` and the active development
  branch from `codex/focus-farm-mvp` to `feat-focus-farm-mvp`.
- Adopted `<purpose>-<short-kebab-case-name>` for working branches and recorded
  the convention in `AGENTS.md` and Codex project-memory context.
- Preserved the former MVP commit under a local backup tag so the history rewrite
  is recoverable until the new history has been reviewed.

## Failed-save consistency follow-up

- Confirmed a review finding that a state-changing command mutated the live farm
  before its snapshot was saved. When storage failed, the UI could later expose
  state that had never reached disk.
- Added regression tests that reproduced retained command mutations, retained
  timer transitions, and snapshot methods that unexpectedly advanced lifecycle
  state.
- Changed `LogicManager` to mutate an independent farm copy, save its snapshot,
  and publish it only after persistence succeeds. A failed save now discards the
  candidate state.
- Made snapshots observational rather than state-changing so UI reads cannot
  bypass the transactional refresh path.

## Countdown retry follow-up

- Confirmed that the JavaFX error handler stopped the only refresh timeline
  after a crop-transition save failed, with no path to restart it.
- Added a refresh coordinator that leaves the timeline running, retries failed
  transitions each second, suppresses repeated outage messages, and announces
  recovery after the first successful retry.
- Added a behavioral test using a recoverable storage failure to verify the
  retry, message suppression, successful UI refresh, and ready-state transition.

## Exit durability follow-up

- Confirmed that `/exit` previously returned “Farm saved” and scheduled shutdown
  before the actual save in `Application.stop()`, where a failure reached only
  standard error.
- Moved the exit save into `LogicManager.execute()` before the exit result is
  returned. A failed save now reaches the command UI and prevents shutdown.
- Added regression tests for successful save-before-exit ordering, failed-save
  behavior, and a successful retry after storage recovers.

## Javadoc follow-up

- Added concise API contracts for production model, logic, command, storage,
  application, and JavaFX classes, plus reusable test support utilities.
- Documented record components, public constants, parameters, return values,
  and expected domain exceptions without adding comments to obvious private
  implementation steps.
- Added warning-free Javadoc generation to the Gradle `check` gate. Generated
  documentation is available at `build/docs/javadoc/index.html`.
- Kept the history reviewable with separate commits for model, logic, storage,
  UI/application, test support, build enforcement, and project documentation.
