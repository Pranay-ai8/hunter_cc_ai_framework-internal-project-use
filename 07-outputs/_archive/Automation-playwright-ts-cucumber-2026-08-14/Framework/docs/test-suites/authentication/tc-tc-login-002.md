# TC_LOGIN_002 — locked_out_user Is Blocked

| Field | Value |
|---|---|
| Module | Authentication |
| Status | 🟢 Active — Automated |
| Suite Tag(s) | @regression |
| Data File | resources/data/logindata.csv |
| Key | locked_out_login |
| Feature File | Scripts/features/{smoke,regression}/login.feature |
| Step File | Scripts/steps/login.steps.ts |
| Source Spec | 07-outputs/TestCases/FunctionalTestCases.md (module: Authentication) |

## Suggested Gherkin

See `Scripts/features/{smoke,regression}/login.feature` for the generated scenario tagged `@DataFile:resources/data/logindata.csv @Key:locked_out_login`.

## Automation Status

Automated and included in the default `@smoke`/`@regression` tag runs.

## Live Verification Note (this automation run)

Confirmed error text against https://www.saucedemo.com/ is **"Epic sadface: Sorry, this user has been locked out."** — the "Epic sadface: " prefix is present on the live site but was not carried in FunctionalTestCases.md's expected result. `resources/data/logindata.csv` (`locked_out_login`) has been corrected to the exact live string.
