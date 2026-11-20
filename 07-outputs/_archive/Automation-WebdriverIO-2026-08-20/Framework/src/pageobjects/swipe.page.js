import { $ } from '@wdio/globals';
import Page from './page.js';
import { swipeElementHorizontal, swipeElementVertical } from '../helper/gestures.js';

/**
 * Selectors verified live — see AutomationSummary.md "Live Selector Verification".
 *
 * Live-run finding (carried over from this app's Appium-Java sibling framework,
 * re-confirmed here): this carousel's resource-ids ("Carousel",
 * "__CAROUSEL_ITEM_0__", ...) have no colon, so a plain `id=` lookup on Android
 * auto-prefixes the current app package and never matches — xpath on the
 * literal resource-id is used instead. iOS has no resource-id/xpath-prefixing
 * quirk at all; the same identifiers are exposed directly as accessibility ids.
 */
class SwipePage extends Page {
    get swipeScreen() {
        return $('~Swipe-screen');
    }

    get carousel() {
        return this.isIOS() ? $('~Carousel') : $('//*[@resource-id="Carousel"]');
    }

    get carouselItem0() {
        return this.isIOS() ? $('~__CAROUSEL_ITEM_0__') : $('//*[@resource-id="__CAROUSEL_ITEM_0__"]');
    }

    get carouselItem1() {
        return this.isIOS() ? $('~__CAROUSEL_ITEM_1__') : $('//*[@resource-id="__CAROUSEL_ITEM_1__"]');
    }

    async isSwipeScreenDisplayed() {
        return this.isDisplayed(this.swipeScreen);
    }

    async isCarouselDisplayed() {
        await this.carousel.waitForDisplayed();
        return this.carousel.isDisplayed();
    }

    async swipeCarouselHorizontal(forward = true) {
        await swipeElementHorizontal(this.carousel, forward);
    }

    async swipeCarouselVertical(down = true) {
        await swipeElementVertical(this.carousel, down);
    }
}

export default new SwipePage();
