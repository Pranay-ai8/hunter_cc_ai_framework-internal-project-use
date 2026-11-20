import { browser } from '@wdio/globals';

/**
 * Low-level W3C pointer-action gestures shared across page objects (Swipe, Drag).
 * All coordinates are absolute device pixels — callers are responsible for
 * deriving them from an element's location/size.
 */

/**
 * Performs a single-finger swipe from one point to another.
 */
export async function swipe(fromX, fromY, toX, toY) {
    await browser.performActions([
        {
            type: 'pointer',
            id: 'finger1',
            parameters: { pointerType: 'touch' },
            actions: [
                { type: 'pointerMove', duration: 0, x: Math.round(fromX), y: Math.round(fromY) },
                { type: 'pointerDown', button: 0 },
                { type: 'pause', duration: 100 },
                { type: 'pointerMove', duration: 600, x: Math.round(toX), y: Math.round(toY) },
                { type: 'pointerUp', button: 0 },
            ],
        },
    ]);
    await browser.releaseActions();
    // Give the app's animation/state a moment to settle before the next assertion.
    await browser.pause(500);
}

/**
 * Swipes horizontally across an element's own bounds (not full-screen coordinates),
 * using a 70%-of-width travel distance centered on the element.
 * @param {WebdriverIO.Element} element target element (e.g. a carousel)
 * @param {boolean} forward true = swipe right-to-left (next item), false = left-to-right (previous item)
 */
export async function swipeElementHorizontal(element, forward = true) {
    const location = await element.getLocation();
    const size = await element.getSize();
    const y = location.y + size.height / 2;
    const leftX = location.x + size.width * 0.15;
    const rightX = location.x + size.width * 0.85;
    if (forward) {
        await swipe(rightX, y, leftX, y);
    } else {
        await swipe(leftX, y, rightX, y);
    }
}

/**
 * Swipes vertically across an element's own bounds.
 * @param {WebdriverIO.Element} element target element
 * @param {boolean} down true = swipe bottom-to-top (scroll down), false = top-to-bottom (scroll up)
 */
export async function swipeElementVertical(element, down = true) {
    const location = await element.getLocation();
    const size = await element.getSize();
    const x = location.x + size.width / 2;
    const topY = location.y + size.height * 0.15;
    const bottomY = location.y + size.height * 0.85;
    if (down) {
        await swipe(x, bottomY, x, topY);
    } else {
        await swipe(x, topY, x, bottomY);
    }
}

/**
 * Drags fromElement onto toElement using Appium's element-relative pointer origin
 * (lets the driver resolve each element's center instead of us computing coordinates).
 *
 * Live-run finding: raw `browser.performActions([...])` does NOT resolve a
 * WebdriverIO element (or ChainablePromiseElement) passed as an action's
 * `origin` — it serializes to `{}` and the driver rejects it with
 * "An unknown element '{}' is set for action item 'pointerMove'". WebdriverIO's
 * higher-level `browser.action('pointer', ...)` builder DOES resolve
 * element/promise origins correctly (awaits them and substitutes the real W3C
 * element reference) — use that builder instead of the raw performActions
 * array whenever an origin needs to be an element.
 */
export async function dragElementToElement(fromElement, toElement) {
    await browser
        .action('pointer', { parameters: { pointerType: 'touch' } })
        .move({ origin: fromElement })
        .down({ button: 0 })
        .pause(150)
        .move({ duration: 400, origin: toElement })
        .pause(150)
        .up({ button: 0 })
        .perform();
    await browser.releaseActions();
    await browser.pause(500);
}
