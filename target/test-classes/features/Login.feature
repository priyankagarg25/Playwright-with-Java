
Feature: Verify the functionalities of Elements page

  @login
  Scenario: Verify User is able to login via credential to the gemEcoSystem
    Given user navigates to "baseurl"
    When user click on "Login"
    When user "Sign In Via Credentials" and logged in using "admin" User
    Then verify that user is logged in
    And user navigate to the "jewel" application
  @login
  Scenario: Verify User is not able to login via incorrect user credential to the gemEcoSystem
    Given user navigates to "baseurl"
    When user click on "Login"
    When user "Sign In Via Credentials" and logged in using "incorrectUser" User
    Then verify that user is not logged in

#  Scenario: Verify User is able to login to the gemEcoSystem
#    Given user navigates to "baseurl"
#    When user click on "Login"
#    When user clicks on "Login via SSO" icon in main page
#    Then verify that user is logged in
@login
  Scenario Outline: Verify User is not able to login via empty credential to the gemEcoSystem using emptyField "<emptyField>"
    Given user navigates to "baseurl"
    When user click on "Login"
    When user "Sign In Via Credentials" and login using emptyField "<emptyField>"
    Then verify that user received warning and not logged in
    Examples:
      | emptyField    |
      | username_Empty |
      | password_Empty |
      | both_Empty    |