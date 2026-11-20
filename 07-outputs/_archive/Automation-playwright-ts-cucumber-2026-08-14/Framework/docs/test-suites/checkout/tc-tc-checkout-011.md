# TC_CHECKOUT_011 — problem_user's Last Name Field May Fail

| Field | Value |
|---|---|
| Module | Checkout |
| Status | 🔵 Active — In Progress |
| Suite Tag(s) | @inprogress |
| Data File | resources/data/checkoutdata.csv |
| Key | problem_user_lastname |
| Feature File | Scripts/features/{smoke,regression}/checkout.feature |
| Step File | Scripts/steps/checkout.steps.ts |
| Source Spec | 07-outputs/TestCases/FunctionalTestCases.md (module: Checkout) |

## Suggested Gherkin

See `Scripts/features/{smoke,regression}/checkout.feature` for the generated scenario tagged `@DataFile:resources/data/checkoutdata.csv @Key:problem_user_lastname`.

## Automation Status

Generated as a real scenario but tagged `@inprogress` and excluded from `@smoke`/`@regression`/`@e2e` default runs — see FunctionalTestCases.md "Clarifications Required" and GapAnalysis.md for the open question blocking a hard assertion.

## Live Verification Note (this automation run) — resolves part of the GapAnalysis.md conflict

Confirmed against https://www.saucedemo.com/: with problem_user, after filling valid First Name/Last Name/Postal Code and clicking Continue on Checkout Step 1, the app **remained on Step 1** (did not advance to Step 2). This corroborates the docx's claim of a Last Name/Step-1 defect for problem_user, which the PDF does not mention. Recommend updating GapAnalysis.md's "Conflicting Requirements" entry to reflect this live confirmation.
