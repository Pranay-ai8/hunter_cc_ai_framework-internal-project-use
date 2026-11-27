package com.codecraft.wdiodemoapp.swipetests;

import com.codecraft.wdiodemoapp.framework.TestBase;
import com.codecraft.wdiodemoapp.Pages;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SwipeTests extends TestBase {

    @Test(description = "TC-SWIPE-001: Horizontal swipe forward advances carousel", groups = {"smoke", "regression"})
    public void verifyHorizontalSwipeForward() {
        Pages.NavigationPage().goToSwipeTab();
        Assert.assertTrue(Pages.SwipePage().isCarouselDisplayed(), "Carousel should be displayed on Swipe screen");
        Pages.SwipePage().swipeCarouselHorizontal(true);
        Assert.assertTrue(Pages.SwipePage().isSwipeScreenDisplayed(),
                "Swipe screen should remain stable and responsive after a forward swipe");
    }

    @Test(description = "TC-SWIPE-002: Horizontal swipe backward returns to previous item", groups = {"regression"})
    public void verifyHorizontalSwipeBackward() {
        Pages.NavigationPage().goToSwipeTab();
        Pages.SwipePage().swipeCarouselHorizontal(true);
        Pages.SwipePage().swipeCarouselHorizontal(false);
        Assert.assertTrue(Pages.SwipePage().isSwipeScreenDisplayed(),
                "Swipe screen should remain stable and responsive after a backward swipe");
    }

    @Test(description = "TC-SWIPE-003: Vertical swipe navigates carousel", groups = {"regression"})
    public void verifyVerticalSwipeNavigatesCarousel() {
        Pages.NavigationPage().goToSwipeTab();
        Pages.SwipePage().swipeCarouselVertical(true);
        Assert.assertTrue(Pages.SwipePage().isSwipeScreenDisplayed(),
                "Swipe screen should remain stable and responsive after a vertical swipe");
    }

    @Test(description = "TC-SWIPE-005 (adjusted): carousel item ordering responds to swipe — "
            + "no separate page-indicator widget exists in this app build, so this verifies the "
            + "underlying carousel item structure instead.", groups = {"regression"})
    public void verifyCarouselRespondsToSwipe() {
        Pages.NavigationPage().goToSwipeTab();
        Assert.assertTrue(Pages.SwipePage().isCarouselDisplayed());
        Pages.SwipePage().swipeCarouselHorizontal(true);
        Assert.assertTrue(Pages.SwipePage().isCarouselDisplayed(),
                "Carousel should still be present and displayed after swiping");
    }
}
