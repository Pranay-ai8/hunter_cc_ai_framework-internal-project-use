/**
 * HTML sub-step badge logger — attaches info/success/failure badges to the
 * current Cucumber step for the multi-cucumber-html-reporter output.
 */
class HTMLSubStepLogger {
    constructor(world) {
        this.world = world;
    }

    async info(message) {
        await this._attach(message, "info");
    }

    async success(message) {
        await this._attach(message, "success");
    }

    async failure(message) {
        await this._attach(message, "failure");
    }

    async _attach(message, status) {
        if (this.world && typeof this.world.attach === "function") {
            await this.world.attach(`[${status.toUpperCase()}] ${message}`, "text/plain");
        }
    }
}

module.exports = HTMLSubStepLogger;
