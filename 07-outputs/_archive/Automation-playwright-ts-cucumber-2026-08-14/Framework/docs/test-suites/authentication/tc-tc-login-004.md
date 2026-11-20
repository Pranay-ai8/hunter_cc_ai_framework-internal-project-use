# TC_LOGIN_004 — Blank Password Shows Required Error

| Field | Value |
|---|---|
| Module | Authentication |
| Status | 🟢 Active — Automated |
| Suite Tag(s) | @regression |
| Data File | resources/data/logindata.csv |
| Key | blank_password |
| Feature File | Scripts/features/{smoke,regression}/login.feature |
| Step File | Scripts/steps/login.steps.ts |
| Source Spec | 07-outputs/TestCases/FunctionalTestCases.md (module: Authentication) |

## Suggested Gherkin

See `Scripts/features/{smoke,regression}/login.feature` for the generated scenario tagged `@DataFile:resources/data/logindata.csv @Key:blank_password`.

## Automation Status

Automated and included in the default `@smoke`/`@regression` tag runs.

## Live Verification Note (this automation run)

Confirmed error text against https://www.saucedemo.com/ is **"Epic sadface: Password is required"** (prefixed, no trailing period) — differs from FunctionalTestCases.md's documented "Password is required." `resources/data/logindata.csv` (`blank_password`) has been corrected to the exact live string.
