# How to Add a New Scenario

1. Read the TC spec, or create one in `07-outputs/TestCases/FunctionalTestCases.md` first.
2. Create/append a `docs/test-suites/<module>/tc-<id>-<title>.md` file (status `📋 Planned`).
3. If the module's Page Object (`../PageObjects/<module>.page.ts`) doesn't yet have the locators/actions/validations you need, add them — locators as `get` properties, per `03-standards/locator-standards.md` priority order.
4. Add the scenario to `../Scripts/features/<suite>/<module>.feature`:
   - Tag with `@DataFile:resources/data/<file>.csv @Key:<id>` plus the suite tag (`@smoke`/`@regression`/`@e2e`/`@sanity`), or `@inprogress` if the candidate is Partial.
   - Reference the TC ID in a leading `# TC_...` comment.
5. Add/extend the matching `../Scripts/steps/<module>.steps.ts`, typing `this: ScenarioContext` and delegating all logic to the Page Object — never reference locators directly.
6. Add a pipe-delimited row to the referenced `resources/data/<file>.csv` keyed by `@Key:`.
7. If the new Page Object is new (not just a new method), register it on `src/utils/ScenarioContext.ts` as a private field + lazy getter.
8. Update `docs/test-suites/<module>/index.md` and `docs/test-suites/index.md` status.
9. Run `npm run test:smoke` / `test:regression` locally before opening a PR.
