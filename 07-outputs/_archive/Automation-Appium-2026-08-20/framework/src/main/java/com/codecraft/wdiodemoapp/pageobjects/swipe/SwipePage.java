package com.codecraft.wdiodemoapp.pageobjects.swipe;

import com.codecraft.wdiodemoapp.utils.Base;
import com.codecraft.wdiodemoapp.utils.DriverFactory;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

public class SwipePage extends Base {

    private final SwipePageObjectRepository repo;

    public SwipePage() {
        repo = new SwipePageObjectRepository(DriverFactory.getDriverForFramework());
    }

    public boolean isSwipeScreenDisplayed() {
        waitUntilElementIsVisible(repo.swipeScreen);
        return isElementVisible(repo.swipeScreen);
    }

    public boolean isCarouselDisplayed() {
        waitUntilElementIsVisible(repo.carousel);
        return isElementVisible(repo.carousel);
    }

    /** Swipes horizontally across the carousel's own bounds (not full-screen coordinates). */
    public void swipeCarouselHorizontal(boolean forward) {
        Point loc = repo.carousel.getLocation();
        Dimension size = repo.carousel.getSize();
        int y = loc.getY() + size.getHeight() / 2;
        int leftX = loc.getX() + (int) (size.getWidth() * 0.15);
        int rightX = loc.getX() + (int) (size.getWidth() * 0.85);
        if (forward) {
            swipe(rightX, y, leftX, y);
        } else {
            swipe(leftX, y, rightX, y);
        }
    }

    public void swipeCarouselVertical(boolean down) {
        Point loc = repo.carousel.getLocation();
        Dimension size = repo.carousel.getSize();
        int x = loc.getX() + size.getWidth() / 2;
        int topY = loc.getY() + (int) (size.getHeight() * 0.15);
        int bottomY = loc.getY() + (int) (size.getHeight() * 0.85);
        if (down) {
            swipe(x, bottomY, x, topY);
        } else {
            swipe(x, topY, x, bottomY);
        }
    }
}
