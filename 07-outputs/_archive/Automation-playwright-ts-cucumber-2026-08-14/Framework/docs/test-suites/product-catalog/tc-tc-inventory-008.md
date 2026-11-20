# TC_INVENTORY_008 — error_user Sort Interaction May Throw a Random Error

| Field | Value |
|---|---|
| Module | Product Catalog |
| Status | 🔵 Active — In Progress |
| Suite Tag(s) | @inprogress |
| Data File | resources/data/inventorydata.csv |
| Key | error_user_sort |
| Feature File | Scripts/features/{smoke,regression}/inventory.feature |
| Step File | Scripts/steps/inventory.steps.ts |
| Source Spec | 07-outputs/TestCases/FunctionalTestCases.md (module: Product Catalog) |

## Suggested Gherkin

See `Scripts/features/{smoke,regression}/inventory.feature` for the generated scenario tagged `@DataFile:resources/data/inventorydata.csv @Key:error_user_sort`.

## Automation Status

Generated as a real scenario but tagged `@inprogress` and excluded from `@smoke`/`@regression`/`@e2e` default runs — see FunctionalTestCases.md "Clarifications Required" and GapAnalysis.md for the open question blocking a hard assertion.

## Live Verification Note (this automation run)

Observed error_user's sort dropdown across all 4 options (az/za/lohi/hilo) in a single run: the product order **did not change** for any option — the dropdown value changes but the list never re-sorts. No runtime/console error was thrown on this run. This is consistent with the docx's "sort may not function" claim, though not with a thrown error specifically. Behavior remains non-deterministic across runs by design; this scenario is observational, not a hard assertion.
