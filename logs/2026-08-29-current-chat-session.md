# AI interaction summary: Focus Farm development session

Date: 2026-08-29
Verification status: Pending final student review

## User goals

- Plan and build an individual Java SE 25 desktop utility using the inherited
  CS2103/T technology stack.
- Develop Focus Farm as a pixel-inspired countdown and harvest tracker with six
  plots, a dashboard, and a 70:30 farm-to-terminal interface.
- Use AI for planning, implementation, code review, debugging, documentation,
  and reflection while retaining individual engineering judgement.
- Keep the Git history readable through purpose-named branches and focused
  commits.

## Prompting and planning approach

- Supplied the assignment requirements and project idea before requesting code.
- Asked the AI to question unclear requirements, check feasibility, and avoid
  inventing missing details.
- Reviewed the proposed technology stack, architecture, UI layout, features,
  implementation phases, and quality gates before approving the MVP build.
- Separated the design into UI, logic, model, and storage concerns so that core
  farm behaviour remained independent of JavaFX.

## MVP implementation summary

- Bootstrapped the Gradle, Java 25, JavaFX, Jackson, JUnit, Checkstyle, and JaCoCo
  project.
- Implemented six timed plots with planting, watering, one-time fertilizer,
  harvesting, inventory, and harvest history.
- Implemented slash commands, JSON persistence, clock-based crop timing, and a
  JavaFX farm, dashboard, and terminal interface.
- Added automated checks, coverage thresholds, cross-platform CI configuration,
  release packaging, and project documentation.

## Git workflow correction

- Identified that the initial MVP history placed too much work in one commit.
- Reorganised the work into commits separated by build tooling, model, storage,
  commands, UI, release automation, and documentation concerns.
- Adopted `master` as the primary branch and
  `<purpose>-<short-kebab-case-name>` for working branches.
- Recorded the branching and commit conventions in `AGENTS.md` so later AI work
  would follow the same process.

## Code-review findings verified

- Confirmed that failed saves could leave command and timer mutations in the
  live farm. Commit `71ad2ec` changed `LogicManager` to save a candidate copy
  before publishing it.
- Confirmed that a countdown save failure stopped the refresh timeline. Commit
  `6c64ca4` kept retries active, suppressed duplicate errors, and reported
  recovery.
- Confirmed that `/exit` reported a successful save before the deferred shutdown
  save occurred. Commit `ee52982` moved saving before the exit result and kept
  the app open when saving failed.
- Confirmed that repeated failed retries could leave farm and dashboard views
  frozen. Commit `3b730ab` introduced clock-derived display snapshots while
  retaining committed persistence state. This commit is not contained in the
  current branch and still requires an explicit integration decision.

## Review and verification approach

- Used Greptile findings to locate potentially risky code instead of manually
  reading the whole repository without a starting point.
- Treated each finding as a concern to reproduce, not as proof of a bug.
- Added focused regression tests before or alongside fixes and ran the complete
  Gradle quality gate after Java changes.
- Used manual developer scenarios to understand multi-step and failure-path
  behaviour that could not be accepted from a review comment alone.

## Cross-platform and release findings

- Confirmed that the source and Gradle configuration avoid OS-specific paths and
  that CI is configured for Windows, Linux, and macOS.
- Confirmed that JavaFX contains native libraries, so a packaged JAR is specific
  to the operating system and CPU architecture used to build it.
- Launched the checked-in macOS Apple Silicon JAR successfully with Java 25.
- Did not claim Windows or Linux runtime verification because those platform
  builds and manual launches were not completed in this session.

## Javadoc and code-quality follow-up

- Added concise Javadocs for production model, logic, commands, storage,
  application, and JavaFX APIs, plus reusable test utilities.
- Documented public constants, record components, parameters, return values, and
  domain exceptions without adding noise to obvious private implementation
  steps.
- Added warning-free Javadoc generation to `./gradlew clean check`.
- Split the work into seven focused commits and verified 38 tests. These changes
  were merged into `master` through pull request #2.

## Reflection follow-up

- Reworked `docs/Reflections.md` around the supplied first-person notes rather
  than keeping generic or unsupported reflection examples.
- Covered planning prompts, implementation planning, the giant-commit mistake,
  documentation prompting, and Greptile-assisted review with manual testing.
- The latest reflection is on `docs-polish-reflections` at commit `09f5a83` and
  is not contained in the current branch.

## Tooling limitation

- The no-mistakes pipeline reached its automated review step but could not run
  because its Claude reviewer was not logged in.
- Later work did not invoke no-mistakes after the student explicitly requested
  that it be skipped.

## Verification completed

- `./gradlew clean check` passed after the Java and build changes.
- Checkstyle, compiler warning checks, JUnit, JaCoCo coverage, and Javadoc
  generation passed on the documented branch states.
- Javadocs generated without warnings at `build/docs/javadoc/index.html`.
- Documentation diffs were checked for whitespace errors before committing.

## Remaining work and student review

- Review this summary and correct any statement that does not match the intended
  development record.
- Decide whether to integrate commit `3b730ab` for failed-retry display updates.
- Review and merge the latest `docs-polish-reflections` branch if appropriate.
- Validate and launch platform-specific JARs on Windows and Linux.
- Continue adding a separate verified summary for each later development
  session.
