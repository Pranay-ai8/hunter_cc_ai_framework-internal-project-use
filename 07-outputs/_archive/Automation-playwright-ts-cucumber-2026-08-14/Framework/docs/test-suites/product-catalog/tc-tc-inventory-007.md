# TC_INVENTORY_007 — problem_user Sees Broken/Incorrect Product Images

| Field | Value |
|---|---|
| Module | Product Catalog |
| Status | 🔵 Active — In Progress |
| Suite Tag(s) | @inprogress |
| Data File | resources/data/inventorydata.csv |
| Key | problem_user_images |
| Feature File | Scripts/features/{smoke,regression}/inventory.feature |
| Step File | Scripts/steps/inventory.steps.ts |
| Source Spec | 07-outputs/TestCases/FunctionalTestCases.md (module: Product Catalog) |

## Suggested Gherkin

See `Scripts/features/{smoke,regression}/inventory.feature` for the generated scenario tagged `@DataFile:resources/data/inventorydata.csv @Key:problem_user_images`.

## Automation Status

Generated as a real scenario but tagged `@inprogress` and excluded from `@smoke`/`@regression`/`@e2e` default runs — see FunctionalTestCases.md "Clarifications Required" and GapAnalysis.md for the open question blocking a hard assertion.

## Live Verification Note (this automation run)

Confirmed against https://www.saucedemo.com/: **all 6** of problem_user's inventory images resolve to the exact same broken-image asset URL (`.../assets/sl-404-*.jpg`). This corroborates both source PRDs' claim of broken/incorrect images for problem_user.
