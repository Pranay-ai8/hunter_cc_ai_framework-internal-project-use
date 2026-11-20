# TC_LOGIN_005 — Unknown Credentials Rejected

| Field | Value |
|---|---|
| Module | Authentication |
| Status | 🟢 Active — Automated |
| Suite Tag(s) | @regression |
| Data File | resources/data/logindata.csv |
| Key | unknown_credentials |
| Feature File | Scripts/features/{smoke,regression}/login.feature |
| Step File | Scripts/steps/login.steps.ts |
| Source Spec | 07-outputs/TestCases/FunctionalTestCases.md (module: Authentication) |

## Suggested Gherkin

See `Scripts/features/{smoke,regression}/login.feature` for the generated scenario tagged `@DataFile:resources/data/logindata.csv @Key:unknown_credentials`.

## Automation Status

Automated and included in the default `@smoke`/`@regression` tag runs.

## Live Verification Note (this automation run)

Confirmed error text against https://www.saucedemo.com/ is **"Epic sadface: Username and password do not match any user in this service"** (prefixed, no trailing period). `resources/data/logindata.csv` (`unknown_credentials`) has been corrected to the exact live string.
