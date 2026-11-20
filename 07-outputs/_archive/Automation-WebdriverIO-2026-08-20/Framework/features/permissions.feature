@permissions
Feature: Permissions
  As a user
  I want the app's permission switches to reflect the platform's real security model
  So that granting/toggling behavior matches actual Android OS behavior, not an assumption

  Background:
    Given the app is launched
    And I navigate to the Permissions screen via the side menu

  @smoke
  Scenario: Camera permission reflects the Android default granted state
    Then the "camera" permission should be granted by default

  @regression
  Scenario Outline: Permission switches reflect the Android default granted state
    Then the "<permission>" permission should be granted by default

    Examples:
      | permission |
      | microphone |
      | location   |
      | photos     |

  @regression
  Scenario: Toggling an already-granted permission is blocked in-app on Android
    Given the "camera" permission is already granted
    When I toggle the "camera" permission switch
    Then the "camera" permission should remain granted
