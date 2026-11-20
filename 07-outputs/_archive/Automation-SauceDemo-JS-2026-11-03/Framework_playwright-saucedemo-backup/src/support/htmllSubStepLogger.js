/**
 * HTML sub-step badge logger — attaches info/success/failure badges to the
 * Cucumber World's attachment stream so they show up per-step in reports.
 */
class HTMLSubStepLogger {
    constructor(attach) {
        this.attach = attach || (() => {});
    }

    _badge(label, color, message) {
        const html = `<div style="padding:4px 8px;margin:2px 0;border-left:4px solid ${color};background:#f7f7f7;font-family:monospace;font-size:12px;">
            <strong style="color:${color};">${label}</strong> ${message}
        </div>`;
        try {
            this.attach(html, "text/html");
        } catch {
            // Attachment stream not available (e.g. outside a scenario context) — ignore.
        }
    }

    info(message) {
        this._badge("INFO", "#2b7de9", message);
    }

    success(message) {
        this._badge("PASS", "#2e8b57", message);
    }

    failure(message) {
        this._badge("FAIL", "#c0392b", message);
    }
}

module.exports = HTMLSubStepLogger;
