# Reflections on AI-Assisted Software Engineering

This document records how I used prompting and AI tools while developing Focus
Farm. I did not use the AI only to generate code. I also used it during
planning, implementation, review, debugging, and documentation. The examples
below focus on prompts that changed how I worked or exposed limitations in the
AI-assisted process.

## Example 1: Giving context and asking the AI to question me

### How I prompted

I started by giving the AI the CS3227 project description and my project idea.
The important part of my prompt was that I did not immediately ask it to start
coding. I asked it to question my requirements, clarify anything uncertain,
check whether the idea was feasible, and avoid hallucinating.

I formulated the prompt this way because my initial requirements were still
rough. If I had only said “build this app”, the AI could have filled in missing
details based on its own assumptions. Those assumptions might not match what I
wanted or what the assignment required.

### What I learnt

Giving the assignment context helped keep the discussion within the actual
project constraints. Asking for questions also made the planning phase more
interactive. Instead of treating the first idea as final, I could review the
AI's interpretation and decide whether I agreed with it.

The prompt gradually became more specific. I first supplied the context and
idea, then reviewed the proposed technology stack, architecture, interface, and
features. I only approved the build after I was satisfied with that plan. This
gave me a clear checkpoint between discussion and implementation.

### What I would do differently

Next time, I would still ask the AI to challenge the requirements, but I would
also list the decisions that must remain mine. This would make it clearer which
parts the AI may propose and which parts it should leave open for confirmation.

## Example 2: Turning an idea into an implementation plan

### How I prompted

After the discussion phase, I asked the AI to turn the approved idea into a
structured plan. The plan covered the architecture, a 70:30 farm-and-chat
interface, the six farm plots, the dashboard, commands, and JSON persistence.
The architecture separated the application into UI, logic, model, and storage
components.

I also wanted the work divided into phases:

1. Bootstrap the project.
2. Build the domain foundation.
3. Add commands and storage.
4. Build a vertical UI slice.
5. Complete the farm and dashboard.
6. Harden and package the release.

The prompt included quality gates such as Checkstyle, strict compiler warnings,
JUnit, JaCoCo coverage, cross-platform CI, and JAR generation.

### Assumptions and engineering judgement

The AI assumed that the UI, command handling, model, and persistence should be
separate. I agreed with this because it made the design easier to extend and
kept JavaFX concerns away from the core farm logic. However, I still had to
review whether the proposed structure matched the size of an individual school
project. A design can look organised but still be unnecessarily complicated.

The diagrams and phased plan were useful because they gave me something
concrete to review before code was produced. I could check that the six plots,
dashboard, chat panel, and required farm actions were represented. The quality
gates also gave me a clearer way to verify generated code instead of accepting
it because it looked correct.

### What I would do differently

I would ask for smaller acceptance criteria under each phase. The high-level
phases were useful, but explicit completion checks would make it easier to tell
whether a phase was truly complete before moving on.

## Example 3: Correcting the Git commit strategy

### What went wrong

The AI initially placed a large amount of work into one giant commit. The code
might still work, but the history did not follow normal software engineering
practice. It was difficult to see which commit introduced the model, storage,
UI, tests, or documentation.

This happened because my implementation prompt focused on the finished MVP and
did not state how the work should be committed. The AI optimised for completing
the build, while I assumed that it would naturally create separate commits by
feature or concern.

### How the prompt evolved

I explicitly asked the AI to reorganise the work into focused commits. I also
asked it to record the commit convention in the project-level memory so that the
same mistake would not be repeated. This was an example where correcting the
current output was not enough; I also wanted to improve later behaviour.

### What I learnt

AI-assisted development still requires me to state process requirements. Good
code is only one part of software engineering. Commit history, reviewability,
and traceability also matter. Next time, I would specify the branch and commit
strategy before coding begins and inspect the history after each major phase,
instead of fixing the entire history at the end.

## Example 4: Improving the developer and user documentation prompts

### What went wrong

The AI did not automatically produce the level of developer documentation I
expected. Important items such as user stories and detailed diagrams were
missing. Some generated diagrams also lacked enough detail to explain the
system clearly.

My first documentation request was too general. Asking for a Developer Guide
and User Guide did not define the expected standard, structure, or depth. The AI
produced what it considered sufficient, but that did not necessarily match the
style I was familiar with from CS2103/T.

### How I refined the prompt

I made the requirements more concrete. I asked for the Developer Guide to use a
top-down, breadth-first explanation and to include the necessary architecture,
sequence, and activity diagrams. I also stated that the User Guide should be
simple, easy to follow, and supported by screenshots. Where necessary, I could
ask the AI to refer to the CS2103/T tP documentation to understand the expected
standard and the tools used to draw diagrams.

### Engineering judgement required

The AI could help rewrite text and propose diagrams, but I still had to decide
which diagrams were actually useful and whether they matched the code. Adding
more diagrams does not automatically improve documentation. They must explain
the correct flow and remain consistent with the current implementation.

Next time, I would provide a documentation checklist at the start and ask the
AI to map every requested section to evidence in the code before writing it.

## Example 5: Using Greptile findings without accepting them blindly

### How I used AI review

Once the codebase became large, reading every file manually took too much time.
I used Greptile to review the code and highlight areas that needed attention.
I then asked the coding AI to verify individual findings and fix them only when
they were valid.

This helped filter the code so I could focus on higher-risk areas. However, the
review output was not a replacement for understanding the code. I still spent
a lot of time following the reasoning behind each finding and checking how the
affected logic worked.

### Verification and limitations

I did not accept a finding only because it came from another AI tool. I used my
own developer testing workflow and manually tested the relevant scenarios. This
was especially important for bugs involving several steps or a failure path,
where a short review comment might not describe the full behaviour.

This was also where prompting became less effective than manual work. AI review
was good at pointing me towards suspicious code, but I still needed to execute
the scenario, observe the result, and decide whether the proposed fix preserved
the intended behaviour.

### What I would do differently

For future reviews, I would ask for a reproducible test case together with every
finding. That would make the reasoning easier to verify and would turn a review
comment into a concrete regression test before any fix is applied.

## Overall reflection

The main benefit of AI was speed: it helped me move from a rough idea to a plan,
generate implementation work, review a large codebase, and polish
documentation. The main limitation was that it often needed more explicit
instructions about software engineering process and documentation standards.

The most important judgement still remained with me. I had to approve the plan,
decide whether the architecture was appropriate, correct the Git workflow,
judge the usefulness of documentation, and verify code-review findings through
testing. The quality of the result depended not only on the AI's output, but on
how carefully I framed the prompts and checked what it produced.
