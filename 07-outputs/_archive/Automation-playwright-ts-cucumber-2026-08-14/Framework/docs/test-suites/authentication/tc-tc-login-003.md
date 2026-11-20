# TC_LOGIN_003 — Blank Username Shows Required Error

| Field | Value |
|---|---|
| Module | Authentication |
| Status | 🟢 Active — Automated |
| Suite Tag(s) | @regression |
| Data File | resources/data/logindata.csv |
| Key | blank_username |
| Feature File | Scripts/features/{smoke,regression}/login.feature |
| Step File | Scripts/steps/login.steps.ts |
| Source Spec | 07-outputs/TestCases/FunctionalTestCases.md (module: Authentication) |

## Suggested Gherkin

See `Scripts/features/{smoke,regression}/login.feature` for the generated scenario tagged `@DataFile:resources/data/logindata.csv @Key:blank_username`.

## Automation Status

Automated and included in the default `@smoke`/`@regression` tag runs.

## Live Verification Note (this automation run)

Confirmed error text against https://www.saucedemo.com/ is **"Epic sadface: Username is required"** (prefixed with "Epic sadface: ", no trailing period) — differs from FunctionalTestCases.md's documented "Username is required." `resources/data/logindata.csv` (`blank_username`) has been corrected to the exact live string.
