import { $, $$ } from '@wdio/globals';
import Page from './page.js';
import { dragElementToElement } from '../helper/gestures.js';

const GRID_POSITIONS = ['l1', 'c1', 'r1', 'l2', 'c2', 'r2', 'l3', 'c3', 'r3'];

/**
 * Selectors verified live — see AutomationSummary.md "Live Selector Verification".
 * "drag-" (piece) and "drop-" (slot) identifiers follow the pattern {l|c|r}{1|2|3} (3x3 grid).
 */
class DragPage extends Page {
    get dragScreen() {
        return $('~Drag-drop-screen');
    }

    get renewButton() {
        return $('~renew');
    }

    get retryButton() {
        return $('~button-Retry');
    }

    piece(position) {
        return $(`~drag-${position}`);
    }

    dropSlot(position) {
        return $(`~drop-${position}`);
    }

    async isDragScreenDisplayed() {
        return this.isDisplayed(this.dragScreen);
    }

    async puzzlePieceCount() {
        return (await $$('//*[starts-with(@content-desc,"drag-") or starts-with(@name,"drag-")]')).length;
    }

    async dropSlotCount() {
        return (await $$('//*[starts-with(@content-desc,"drop-") or starts-with(@name,"drop-")]')).length;
    }

    async dragPieceToMatchingSlot(position) {
        await dragElementToElement(this.piece(position), this.dropSlot(position));
    }

    /** Drags one arbitrary available piece onto one arbitrary drop slot. */
    async dragFirstAvailablePieceToSlot(slotPosition) {
        const pieces = await $$('//*[starts-with(@content-desc,"drag-") or starts-with(@name,"drag-")]');
        if (pieces.length === 0) {
            throw new Error('No draggable puzzle pieces found');
        }
        await dragElementToElement(pieces[0], this.dropSlot(slotPosition));
    }

    async resetPuzzle() {
        // Live-run finding: tapping the Drag tab does not mean the screen (and its
        // "renew" control) has finished rendering yet — an immediate find right
        // after the tab click is flaky. Wait for the drag screen first.
        await this.isDragScreenDisplayed();
        await this.clickWithHeal(this.renewButton, 'Renew Button');
    }

    async isCompletionMessageDisplayed() {
        return this.retryButton.isDisplayed();
    }

    async retryPuzzle() {
        await this.clickWithHeal(this.retryButton, 'Retry Button');
    }

    gridPositions() {
        return GRID_POSITIONS;
    }
}

export default new DragPage();
