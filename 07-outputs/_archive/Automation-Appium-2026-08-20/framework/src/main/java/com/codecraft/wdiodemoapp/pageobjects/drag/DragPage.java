package com.codecraft.wdiodemoapp.pageobjects.drag;

import com.codecraft.wdiodemoapp.utils.Base;
import com.codecraft.wdiodemoapp.utils.DriverFactory;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DragPage extends Base {

    private static final List<String> GRID_POSITIONS =
            List.of("l1", "c1", "r1", "l2", "c2", "r2", "l3", "c3", "r3");

    // Exact-match piece/slot lookups use accessibility id directly — it resolves to Android's
    // content-desc or iOS's name transparently, unlike xpath's platform-specific attribute names.
    private final DragPageObjectRepository repo;

    public DragPage() {
        repo = new DragPageObjectRepository(DriverFactory.getDriverForFramework());
    }

    public boolean isDragScreenDisplayed() {
        waitUntilElementIsVisible(repo.dragScreen);
        return isElementVisible(repo.dragScreen);
    }

    private WebElement dropSlot(String position) {
        return driver.findElement(AppiumBy.accessibilityId("drop-" + position));
    }

    private WebElement piece(String position) {
        return driver.findElement(AppiumBy.accessibilityId("drag-" + position));
    }

    // Prefix-match ("starts-with") has no accessibility-id equivalent, so these two still need
    // xpath — checking both @content-desc (Android) and @name (iOS) keeps them cross-platform.
    public int puzzlePieceCount() {
        return driver.findElements(By.xpath("//*[starts-with(@content-desc,'drag-') or starts-with(@name,'drag-')]")).size();
    }

    public int dropSlotCount() {
        return driver.findElements(By.xpath("//*[starts-with(@content-desc,'drop-') or starts-with(@name,'drop-')]")).size();
    }

    /** Drags one arbitrary available piece onto one arbitrary drop slot. */
    public void dragFirstAvailablePieceToSlot(String slotPosition) {
        List<WebElement> pieces = driver.findElements(
                By.xpath("//*[starts-with(@content-desc,'drag-') or starts-with(@name,'drag-')]"));
        if (pieces.isEmpty()) {
            throw new IllegalStateException("No draggable puzzle pieces found");
        }
        dragAndDrop(pieces.get(0), dropSlot(slotPosition));
    }

    public void dragPieceToMatchingSlot(String position) {
        dragAndDrop(piece(position), dropSlot(position));
    }

    public void resetPuzzle() {
        clickWithHeal(repo.renewButton, "Renew Button");
    }

    public boolean isCompletionMessageDisplayed() {
        return isElementVisible(repo.retryButton);
    }

    public void retryPuzzle() {
        clickWithHeal(repo.retryButton, "Retry Button");
    }

    public List<String> gridPositions() {
        return GRID_POSITIONS;
    }
}
