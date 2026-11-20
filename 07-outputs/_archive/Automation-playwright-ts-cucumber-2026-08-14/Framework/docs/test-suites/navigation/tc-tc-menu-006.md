# TC_MENU_006 — About Navigates Away from the Application

| Field | Value |
|---|---|
| Module | Navigation |
| Status | 🔵 Active — In Progress |
| Suite Tag(s) | @inprogress |
| Data File | resources/data/menudata.csv |
| Key | about_link |
| Feature File | Scripts/features/{smoke,regression}/menu.feature |
| Step File | Scripts/steps/menu.steps.ts |
| Source Spec | 07-outputs/TestCases/FunctionalTestCases.md (module: Navigation) |

## Suggested Gherkin

See `Scripts/features/{smoke,regression}/menu.feature` for the generated scenario tagged `@DataFile:resources/data/menudata.csv @Key:about_link`.

## Automation Status

Generated as a real scenario but tagged `@inprogress` and excluded from `@smoke`/`@regression`/`@e2e` default runs — see FunctionalTestCases.md "Clarifications Required" and GapAnalysis.md for the open question blocking a hard assertion.

## Live Verification Note (this automation run) — resolves GAP-FR-04

Confirmed against https://www.saucedemo.com/: the "About" menu link navigates to **https://saucelabs.com/**. GAP-FR-04 is resolved.
