# Disclaimer
This reflection was polished by an AI, but the core content, engineering decisions, and reflections are entirely my own (ZhengHao).

# Reflections on AI-Assisted Software Engineering 

This document captures my workflow using prompting and AI tools while building Focus Farm for CS3227. I didn't just treat the AI as an overgrown autocomplete for code generation—I integrated it into the entire lifecycle: planning, implementation, code review, debugging, and writing docs. The examples below highlight prompts that actually shifted my workflow or exposed where AI-assisted dev falls short.

## Example 1: Giving context and asking the AI to grill me

### How I prompted

I started by giving the AI the CS3227 project description and my initial idea. The crux of my prompt was deliberately *not* asking it to write code right away. Instead, I told it to grill me on my requirements, clarify ambiguities, sanity-check the idea's feasibility, and strictly avoid hallucinating features.

I framed the prompt this way because my initial specs were pretty rough. If I had just said "build this app", the AI would have confidently filled in the blanks with its own assumptions—which might not have aligned with what I actually wanted or what the assignment rubric demanded.

### What I learnt

Establishing the assignment context kept the AI grounded in reality and project constraints. Asking for pushback also made the planning phase highly interactive. Instead of treating the first generated idea as gospel, I could review the AI's interpretation and decide if we were actually on the same page.

My prompts gradually became more specific. I fed it context, reviewed the proposed tech stack, architecture, UI, and features, and only approved the build once I was satisfied with the blueprint. This gave me a strict, necessary checkpoint between ideation and execution.

### What I would do differently

Next time, I would still ask the AI to challenge the requirements, but I would explicitly list my non-negotiables upfront. This establishes clear boundaries on which parts the AI can creatively propose and which parts it needs to leave alone.

## Example 2: Turning an idea into an implementation plan

### How I prompted

Once we locked in the idea, I prompted the AI to map out a structured implementation plan. The blueprint covered the architecture, a 70:30 farm-and-chat interface, the six farm plots, the dashboard, commands, and JSON persistence. The architecture cleanly decoupled the application into UI, logic, model, and storage components.

I also forced it to split the work into logical phases:

1. Bootstrap the project.
2. Build the domain foundation.
3. Add commands and storage.
4. Build a vertical UI slice.
5. Complete the farm and dashboard.
6. Harden and package the release.

The prompt included strict quality gates: Checkstyle, strict compiler warnings, JUnit, JaCoCo coverage, cross-platform CI, and JAR generation.

### Assumptions and engineering judgement

The AI assumed the UI, command handling, model, and persistence should be isolated. I agreed—it’s standard SWE practice, makes the design extensible, and keeps JavaFX spaghetti out of the core farm logic. However, I still had to do a vibe check on whether the proposed structure actually matched the scope of an individual school project. A design can look beautifully organized on paper but still be massively over-engineered.

The diagrams and phased plan were incredibly useful because they gave me a concrete artifact to review before a single line of code was generated. I could verify that the six plots, dashboard, chat panel, and core actions were accounted for. The quality gates also gave me a systematic way to verify the generated code, rather than just blindly accepting it because it compiled.

### What I would do differently

I would ask for granular acceptance criteria under each phase. The high-level phases were good, but explicit "Done" checklists would make it much easier to tell if a phase was genuinely complete before moving on to the next.

## Example 3: Correcting the Git commit strategy

### What went wrong

The AI initially dumped a massive chunk of work into one monolithic commit. Sure, the code worked, but the history was a nightmare. It violated basic version control hygiene—it was impossible to isolate which commit introduced the model, storage, UI, tests, or docs.

This happened because my implementation prompt indexed heavily on finishing the MVP and completely ignored the commit strategy. The AI optimized for getting the code to run, while I assumed it would naturally create atomic, feature-scoped commits.

### How the prompt evolved

I explicitly commanded the AI to rewrite the history and reorganize the work into focused commits. More importantly, I had it record this commit convention into the project-level memory so it wouldn't make the same mistake twice. This was a great example of not just fixing the current output, but actively patching the AI's future behavior.

### What I learnt

AI-assisted development still requires aggressive hand-holding when it comes to SWE processes. Shipping working code is only half the job—commit history, reviewability, and traceability matter just as much. Next time, I’ll define the branching and commit strategy *before* coding starts, and inspect the history after each major phase instead of trying to untangle it at the end.

## Example 4: Improving the developer and user docs

### What went wrong

The AI didn't magically output the level of developer documentation I expected. Crucial artifacts like user stories and detailed diagrams were missing, and the diagrams it *did* generate lacked the depth needed to actually explain the system. 

My initial prompt was too generic. Just asking for a "Developer Guide" and "User Guide" didn't define the expected standard, structure, or technical depth. The AI generated what it thought was "good enough," but it fell way short of the rigor I was used to from modules like CS2103T.

### How I refined the prompt

I got highly specific. I demanded that the Developer Guide use a top-down, breadth-first approach, complete with proper architecture, sequence, and activity diagrams. I also specified that the User Guide had to be idiot-proof and heavily supported by screenshots. I even told the AI to reference the standard CS2103T tP documentation to calibrate its expectations and to use the right tools for diagramming.

### Engineering judgement required

The AI is great at drafting text and proposing diagrams, but I still had to be the filter. I had to decide if a diagram actually added value or if it accurately reflected the codebase. Throwing more UML at a doc doesn't make it better—it has to explain the right flows and stay perfectly synced with the implementation.

Next time, I’ll feed it a strict documentation checklist upfront and force the AI to map every requested section to actual evidence in the codebase before it starts writing.

## Example 5: Using Greptile findings without blind trust

### How I used AI review

As the codebase grew, manual review became a bottleneck. I brought in Greptile to run sweeps over the code and flag suspicious areas, then used my coding AI to verify those findings and patch the valid ones.

This acted as a great filter, letting me focus my mental energy on high-risk areas. But the review output wasn't a replacement for actually understanding the codebase. I still had to spend significant time tracing the logic behind each flag to see how it impacted the system.

### Verification and limitations

I never blindly accepted a finding just because an AI tool flagged it. I stuck to my standard developer testing workflow and manually tested the edge cases. This was critical for multi-step bugs or failure paths, where a single-line review comment rarely captures the full blast radius of a bug.

This is exactly where prompting hits a wall compared to manual engineering. AI review is excellent at pointing a flashlight at weird code, but I still had to execute the scenario, observe the state, and make the final call on whether a proposed fix preserved the intended behavior.

### What I would do differently

For future reviews, I’ll prompt the AI to generate a reproducible test case alongside every finding. That forces it to prove the bug exists, makes the reasoning easier to verify, and turns a vague review comment into a concrete regression test *before* any code gets touched.

## Example 6: Separating build success from runtime compatibility

### What went wrong

The JAR checked into `release/` appeared to fail on Windows even though the
Windows CI job was green. Inspecting the archive showed that it contained only
macOS Apple Silicon JavaFX classes and native libraries. CI had proved that the
project could build on Windows, but it had not proved that the checked-in JAR
could run there.

### How I refined the release

I asked the AI to verify the binary contents before proposing a fix. After
confirming the platform mismatch, I chose a single cross-platform fat JAR for
the supported Windows x64, Linux x64, and macOS ARM64 targets. The build now
checks that each platform's JavaFX Glass runtime is present before copying the
JAR into `release/`.

### What I learnt

Passing source tests and compiling on several operating systems do not verify a
specific release artifact. Native dependencies must be inspected and the final
packaged file must be launched on every target platform. I added a bounded
packaged-JAR smoke mode to CI so each runner now proves that the real JavaFX
application can initialize and show its window.

## Overall reflection

The biggest ROI of using AI was pure velocity: it rapidly accelerated moving from rough idea to structured plan, generating boilerplate, reviewing a bulky codebase, and formatting docs. The main limitation is that it requires explicit, constant guardrails around SWE processes and documentation standards.

At the end of the day, the core engineering judgement still sits entirely with me. I had to approve the architecture, enforce the Git workflow, curate the documentation, and rigorously verify code-review findings. The quality of the final product didn't just depend on the AI's raw output—it depended heavily on how tightly I scoped the prompts and how strictly I audited the results.
