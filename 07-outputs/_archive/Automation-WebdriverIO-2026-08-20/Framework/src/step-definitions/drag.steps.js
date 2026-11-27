import { Given, When, Then } from '@wdio/cucumber-framework';
import { expect } from '@wdio/globals';
import DragPage from '../pageobjects/drag.page.js';

let piecesBeforeDrag;

Given(/^I reset the puzzle$/, async () => {
    await DragPage.resetPuzzle();
});

When(/^I drag the puzzle piece at position "([^"]*)" to its matching slot$/, async (position) => {
    piecesBeforeDrag = await DragPage.puzzlePieceCount();
    await DragPage.dragPieceToMatchingSlot(position);
});

Then(/^the loose puzzle piece count should have decreased$/, async () => {
    const after = await DragPage.puzzlePieceCount();
    expect(after).toBeLessThan(piecesBeforeDrag);
});

When(/^I drag every puzzle piece to its matching slot$/, async () => {
    for (const position of DragPage.gridPositions()) {
        await DragPage.dragPieceToMatchingSlot(position);
    }
});

Then(/^the puzzle completion message should be displayed$/, async () => {
    const displayed = await DragPage.isCompletionMessageDisplayed();
    expect(displayed).toBe(true);
    await DragPage.retryPuzzle();
});

When(/^I drag an available puzzle piece to slot "([^"]*)"$/, async (slotPosition) => {
    await DragPage.dragFirstAvailablePieceToSlot(slotPosition);
});

Then(/^the puzzle completion message should not be displayed$/, async () => {
    const displayed = await DragPage.isCompletionMessageDisplayed();
    expect(displayed).toBe(false);
});
