# TC_MENU_004 — Reset App State Resets Sort Order Without Logout

| Field | Value |
|---|---|
| Module | Navigation |
| Status | 🟢 Active — Automated |
| Suite Tag(s) | @regression |
| Data File | resources/data/menudata.csv |
| Key | reset_resets_sort |
| Feature File | Scripts/features/{smoke,regression}/menu.feature |
| Step File | Scripts/steps/menu.steps.ts |
| Source Spec | 07-outputs/TestCases/FunctionalTestCases.md (module: Navigation) |

## Suggested Gherkin

See `Scripts/features/{smoke,regression}/menu.feature` for the generated scenario tagged `@DataFile:resources/data/menudata.csv @Key:reset_resets_sort`.

## Automation Status

Automated and included in the default `@smoke`/`@regression` tag runs.

## Live Verification Note (this automation run) — corrects a stale documented behavior

Confirmed against https://www.saucedemo.com/: **Reset App State does NOT reset the product sort order** — both the sort dropdown's selected value and the rendered product order remain exactly as last set after clicking Reset App State. This contradicts RequirementAnalysis.md §9 (Business Rule 8) and FunctionalTestCases.md's expected result for this TC. The generated scenario/step assertion has been corrected to match the verified live behavior (`Scripts/steps/menu.steps.ts`, `the sort order should remain unchanged after reset...`) rather than the stale documentation. Recommend updating RequirementAnalysis.md/FunctionalTestCases.md accordingly.
