@drag
Feature: Drag and Drop
  As a user
  I want to drag puzzle pieces into their matching slots
  So that the drag-and-drop gesture and completion detection work correctly

  Background:
    Given the app is launched
    And I navigate to the Drag tab
    And I reset the puzzle

  @smoke
  Scenario: A puzzle piece can be dragged to its matching slot
    Then the Drag screen should be displayed
    When I drag the puzzle piece at position "l1" to its matching slot
    Then the loose puzzle piece count should have decreased

  @regression
  Scenario: Puzzle completion is detected once all pieces are correctly placed
    When I drag every puzzle piece to its matching slot
    Then the puzzle completion message should be displayed

  @regression
  Scenario: An incorrect placement does not falsely complete the puzzle
    When I drag an available puzzle piece to slot "r3"
    Then the puzzle completion message should not be displayed
