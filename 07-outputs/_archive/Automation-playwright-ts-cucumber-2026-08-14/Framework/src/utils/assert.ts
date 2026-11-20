/**
 * Soft-assertion helper.
 *
 * Accumulates failures per scenario (stored on `ScenarioContext.softFailures`) instead
 * of throwing immediately, so a step can record multiple independent checks. `hooks.ts`
 * (`After`) inspects the accumulated list and throws a summary error if any failures were
 * recorded, even though the scenario otherwise "passed".
 */
export class SoftAssert {
  private failures: string[] = [];

  check(condition: boolean, message: string): void {
    if (!condition) {
      this.failures.push(message);
    }
  }

  hasFailures(): boolean {
    return this.failures.length > 0;
  }

  getFailures(): string[] {
    return [...this.failures];
  }

  reset(): void {
    this.failures = [];
  }

  assertAll(): void {
    if (this.hasFailures()) {
      const summary = this.failures.map((f, i) => `  ${i + 1}. ${f}`).join('\n');
      this.reset();
      throw new Error(`Soft-assertion failures accumulated during scenario:\n${summary}`);
    }
  }
}
