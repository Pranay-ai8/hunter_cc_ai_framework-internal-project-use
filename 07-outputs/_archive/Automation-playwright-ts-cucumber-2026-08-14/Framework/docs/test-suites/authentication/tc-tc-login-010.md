# TC_LOGIN_010 — Session Auto-Expires After 10 Minutes of Inactivity

| Field | Value |
|---|---|
| Module | Authentication |
| Status | 🔵 Active — In Progress |
| Suite Tag(s) | @inprogress |
| Data File | resources/data/logindata.csv |
| Key | session_timeout |
| Feature File | Scripts/features/{smoke,regression}/login.feature |
| Step File | Scripts/steps/login.steps.ts |
| Source Spec | 07-outputs/TestCases/FunctionalTestCases.md (module: Authentication) |

## Suggested Gherkin

See `Scripts/features/{smoke,regression}/login.feature` for the generated scenario tagged `@DataFile:resources/data/logindata.csv @Key:session_timeout`.

## Automation Status

Generated as a real scenario but tagged `@inprogress` and excluded from `@smoke`/`@regression`/`@e2e` default runs — see FunctionalTestCases.md "Clarifications Required" and GapAnalysis.md for the open question blocking a hard assertion.

## Live Verification Note (this automation run)

This scenario is expected to fail/be flaky by design — SauceDemo does not appear to implement any real inactivity-based session timeout on a short observable timescale, so the reduced stand-in wait (a few seconds, in place of a real 10 minutes) does not reproduce an expiry. Ran without any structural/config error; the assertion failure is a genuine, expected outcome pending GAP-FR-03 resolution, not a framework bug.
