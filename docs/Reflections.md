# Reflections on AI-Assisted Software Engineering

This is a living document. The examples below cover the initial requirements,
architecture, and MVP build. They should be revisited after manual acceptance
testing and later development increments.

## Example 1: Challenging the initial product concept

### Prompt and intent

The initial request proposed a pixel room containing a whiteboard and trash bin,
controlled through commands that added and deleted to-dos. The prompt also
included the assignment restriction against recreating a to-do manager.

The prompt was intentionally broad because the first goal was feasibility and
planning, not code generation. It asked the LLM not to hallucinate and to
inspect the inherited tP stack.

### LLM assumptions and response

The LLM treated product functionality, rather than visual presentation, as the
deciding factor. It concluded that adding game graphics did not make a to-do
manager a different product. It recommended preserving the visual interaction
while changing the underlying utility.

### Engineering judgement and verification

This was useful pushback. A superficially appealing implementation could have
violated an explicit assignment restriction. The conclusion was verified
against the supplied assignment text and the current official Project Duke
guidance. The student then replaced the concept with a farm timer.

### What could improve

The prompt could have stated an explicit decision criterion such as “reject any
idea whose core data model remains tasks.” That would make the evaluation less
dependent on the LLM inferring the distinction between theme and function.

## Example 2: Turning a farm game into a personal utility

### Prompt and intent

The follow-up proposed planting, watering, fertilizer, timed growth, harvesting,
six plots, and a harvest dashboard. It again requested a non-hallucinated
feasibility check.

### LLM assumptions and response

The LLM identified a new ambiguity: a pure farming game might not qualify as a
personal utility app. It suggested framing the same mechanics as a gamified
countdown timer and harvest tracker. It also proposed absolute timestamps,
continued growth while closed, six independent plot state machines, and a
one-time 25% fertilizer reduction.

### Engineering judgement and verification

The student explicitly approved the combined assumptions before coding. The
timing design was checked against JavaFX documentation, which states that
animation keyframes are not guaranteed to execute at an exact instant. The
implementation therefore uses `java.time.Clock` and persisted `Instant` values
for correctness, with JavaFX `Timeline` used only to refresh the display.

### What could improve

Fertilizer makes a countdown shorter, which weakens a strict “focus timer”
interpretation. Describing the product as a general personal countdown timer is
more accurate. A later usability study could determine whether users understand
that distinction.

## Example 3: Asking for enforceable code quality

### Prompt and intent

The student asked how code quality would be ensured throughout the build, not
merely reviewed after implementation.

### LLM assumptions and response

The LLM proposed a single Gradle quality gate combining compiler warnings,
Checkstyle, JUnit, coverage verification, and CI. It treated deterministic time
testing and architectural boundaries as quality properties rather than relying
only on a coverage percentage.

### Engineering judgement and verification

The implementation compiles with `-Xlint:all -Werror`. The `check` task fails
below 80% line or 70% branch coverage for core packages. Time tests inject a
mutable clock and never sleep. The build also packages the JAR early instead of
waiting for submission week.

The first test run exposed a missing JUnit Platform launcher dependency under
Gradle 9.1. A later test exposed an unescaped percent sign in a formatted help
message. Both failures demonstrate that compiling production code alone was not
enough; executing the real toolchain caught concrete integration mistakes.

### What could improve

Coverage thresholds can encourage low-value tests. Future prompts should ask
for mutation-resistant behavioural tests and review uncovered branches rather
than only increasing a number. GUI behaviour still needs manual and
cross-platform inspection.
