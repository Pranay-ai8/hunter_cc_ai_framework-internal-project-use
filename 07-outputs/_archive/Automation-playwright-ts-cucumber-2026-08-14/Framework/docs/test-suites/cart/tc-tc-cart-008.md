# TC_CART_008 — problem_user's Remove Button Behavior

| Field | Value |
|---|---|
| Module | Cart |
| Status | 🔵 Active — In Progress |
| Suite Tag(s) | @inprogress |
| Data File | resources/data/cartdata.csv |
| Key | problem_user_remove |
| Feature File | Scripts/features/{smoke,regression}/cart.feature |
| Step File | Scripts/steps/cart.steps.ts |
| Source Spec | 07-outputs/TestCases/FunctionalTestCases.md (module: Cart) |

## Suggested Gherkin

See `Scripts/features/{smoke,regression}/cart.feature` for the generated scenario tagged `@DataFile:resources/data/cartdata.csv @Key:problem_user_remove`.

## Automation Status

Generated as a real scenario but tagged `@inprogress` and excluded from `@smoke`/`@regression`/`@e2e` default runs — see FunctionalTestCases.md "Clarifications Required" and GapAnalysis.md for the open question blocking a hard assertion.

## Live Verification Note (this automation run) — resolves part of the GapAnalysis.md conflict

Confirmed against https://www.saucedemo.com/: problem_user's Cart page **Remove button works normally** — the item was removed successfully. This corroborates the docx (which does not mention a non-functional Remove button) over the PDF's claim (Technical Requirement 6) that it is non-functional. Recommend updating GapAnalysis.md's "Conflicting Requirements" entry to reflect this live confirmation.
