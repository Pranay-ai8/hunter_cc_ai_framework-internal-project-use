package com.codecraft.wdiodemoapp.dragtests;

import com.codecraft.wdiodemoapp.framework.TestBase;
import com.codecraft.wdiodemoapp.Pages;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DragTests extends TestBase {

    @Test(description = "TC-DRAG-001: Puzzle piece can be dragged to a target slot", groups = {"smoke", "regression"})
    public void verifyDragPieceToSlot() {
        Pages.NavigationPage().goToDragTab();
        Assert.assertTrue(Pages.DragPage().isDragScreenDisplayed());
        int piecesBefore = Pages.DragPage().puzzlePieceCount();
        Pages.DragPage().dragPieceToMatchingSlot("l1");
        int piecesAfter = Pages.DragPage().puzzlePieceCount();
        Assert.assertTrue(piecesAfter < piecesBefore,
                "Placing a piece in its matching slot should remove it from the loose-piece tray");
    }

    @Test(description = "TC-DRAG-002: Puzzle completion is detected when all pieces are correctly placed",
            groups = {"regression"})
    public void verifyPuzzleCompletionDetected() {
        Pages.NavigationPage().goToDragTab();
        Assert.assertTrue(Pages.DragPage().isDragScreenDisplayed());
        for (String position : Pages.DragPage().gridPositions()) {
            Pages.DragPage().dragPieceToMatchingSlot(position);
        }
        Assert.assertTrue(Pages.DragPage().isCompletionMessageDisplayed(),
                "A 'Congratulations' / Retry completion state should appear once all 9 pieces are correctly placed");
        Pages.DragPage().retryPuzzle();
    }

    @Test(description = "TC-DRAG-003: Incorrect placement does not falsely complete the puzzle", groups = {"regression"})
    public void verifyIncorrectPlacementDoesNotComplete() {
        Pages.NavigationPage().goToDragTab();
        Assert.assertTrue(Pages.DragPage().isDragScreenDisplayed());
        Pages.DragPage().dragFirstAvailablePieceToSlot("r3");
        Assert.assertFalse(Pages.DragPage().isCompletionMessageDisplayed(),
                "A single, likely-mismatched placement should not trigger the completion state");
        Pages.DragPage().resetPuzzle();
    }
}
