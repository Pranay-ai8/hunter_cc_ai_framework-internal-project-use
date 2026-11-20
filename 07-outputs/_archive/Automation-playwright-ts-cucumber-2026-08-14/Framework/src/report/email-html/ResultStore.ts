export interface ScenarioResult {
  name: string;
  status: 'passed' | 'failed' | 'skipped';
  tcId?: string;
}

/**
 * In-memory store of scenario results for the current run, consumed by
 * `HtmlEmailReport` in `AfterAll` to build the post-run email summary.
 */
class ResultStoreImpl {
  private results: ScenarioResult[] = [];

  add(result: ScenarioResult): void {
    this.results.push(result);
  }

  all(): ScenarioResult[] {
    return [...this.results];
  }

  summary(): { total: number; passed: number; failed: number; skipped: number } {
    return {
      total: this.results.length,
      passed: this.results.filter((r) => r.status === 'passed').length,
      failed: this.results.filter((r) => r.status === 'failed').length,
      skipped: this.results.filter((r) => r.status === 'skipped').length,
    };
  }

  reset(): void {
    this.results = [];
  }
}

export const ResultStore = new ResultStoreImpl();
