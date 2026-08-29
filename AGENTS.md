# Focus Farm agent instructions

Focus Farm is a Java SE 25 and JavaFX 25 desktop application. It is a gamified
countdown timer and harvest tracker, not a to-do manager.

## Non-negotiable design rules

- Keep domain, command, and storage code independent of JavaFX.
- Use `java.time.Clock` for all authoritative time decisions. Tests must not sleep.
- Persist absolute readiness timestamps so growth continues while the app is closed.
- Treat animations as cosmetic responses to model changes; animations cannot mutate farm state.
- Keep the farm fixed at six plots for the MVP.
- Do not add runtime LLM calls, credentials, network services, or telemetry.
- Use only original, generated, or properly attributed assets.

## Required checks

After every Java or build change, run:

```bash
./gradlew clean check
```

Before a release, also run:

```bash
./gradlew release
java -jar release/FocusFarm.jar
```

Update tests, user/developer documentation, `logs/`, and reflections whenever
observable behaviour or the development process changes.

## Git workflow

- Use `master` as the primary branch. Do not develop features directly on it.
- Name working branches `<purpose>-<short-kebab-case-name>`, with no spaces or
  slashes. Examples: `feat-build-app`, `bug-fix-timer`, and `misc-hotfix`.
- Choose a purpose that communicates the change, such as `feat`, `bug`, `docs`,
  `test`, `refactor`, `chore`, or `misc`.
- Make small, coherent commits separated by feature or engineering concern.
  Do not combine unrelated changes into one large commit.
- Commit tests with the behaviour they verify. Commit documentation with the
  feature it describes, or as a separate documentation-only commit when it
  spans several features.
- Use concise, imperative commit subjects that state the completed change.
- Run the required checks before committing code or build configuration.
