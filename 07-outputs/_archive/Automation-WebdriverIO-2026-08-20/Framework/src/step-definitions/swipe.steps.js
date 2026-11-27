import { When, Then } from '@wdio/cucumber-framework';
import { expect } from '@wdio/globals';
import SwipePage from '../pageobjects/swipe.page.js';

Then(/^the carousel should be displayed$/, async () => {
    const displayed = await SwipePage.isCarouselDisplayed();
    expect(displayed).toBe(true);
});

When(/^I swipe the carousel horizontally forward$/, async () => {
    await SwipePage.swipeCarouselHorizontal(true);
});

When(/^I swipe the carousel horizontally backward$/, async () => {
    await SwipePage.swipeCarouselHorizontal(false);
});

When(/^I swipe the carousel vertically downward$/, async () => {
    await SwipePage.swipeCarouselVertical(true);
});
