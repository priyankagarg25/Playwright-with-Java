Feature: Validate the functionality of admin page of GemEcosystem

  Background:
    Given user navigates to "baseurl"
    When user click on "Login"

  Scenario: Validate the URL of admin screen
    When user "Sign In Via Credentials" and logged in using "admin" User
    Then verify that user is logged in
    And user navigate to the "jewel" application
    When user click on "admin" tab
    Then verify the URL of "admin" tab

  Scenario: Validate project creation on the admin screen
    When user "Sign In Via Credentials" and logged in using "normalUser" User
    Then verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    When user click on "Create Project" button on admin tab
    And user fills "all" required fields and "Team" project type and "QA" environment
    Then verify project is created successfully by "normalUser"


  Scenario: Validate super admin approves the project
    When user "Sign In Via Credentials" and logged in using "admin" User
    Then verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    When user click on "Project Approval" button on admin tab
    And finds the project and approve it in "Project Request Access"


  Scenario: Validate project status and action buttons are active after approval
    When user "Sign In Via Credentials" and logged in using "normalUser" User
    Then verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    Then user finds the project and validates the status
    And user clicks on "Edit Access" icon

  Scenario: Validate project name remains un editable after creation and approval
    When user "Sign In Via Credentials" and logged in using "normalUser" User
    Then verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    And user finds the project and validates the status
    And user clicks on "Edit Project Details" icon
    Then verify that "Project Name" field is non-editable

  Scenario: Validate Project Description is editable after creation and approval
    When user "Sign In Via Credentials" and logged in using "normalUser" User
    Then verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    And user finds the project and validates the status
    And user clicks on "Edit Project Details" icon
    Then verify that "Project Description" field is editable

  Scenario: Validate user cannot request access to project they already have access to.
    When user "Sign In Via Credentials" and logged in using "normalUser" User
    Then verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    When user click on "Project Access" button on admin tab
    And user create request for "Project Access" with "QA" role and "Something Went Wrong. Please refresh the Page / Try Again" message

  Scenario: Validate user is able to delete the project
    When user "Sign In Via Credentials" and logged in using "normalUser" User
    Then verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    And user finds the project and validates the status
    And user clicks on "Delete Project" icon
    And user confirms deletion
    Then validate project is deleted successfully

  Scenario: Validate user is able to create, delete and recreate the project
    When user "Sign In Via Credentials" and logged in using "admin" User
    Then verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    When user click on "Create Project" button on admin tab
    And user fills "all" required fields and "Team" project type and "qa" environment
    Then verify project is created successfully by "admin"
    When user finds the project and validates the status
    And user clicks on "Delete Project" icon
    And user confirms deletion
    Then validate project is deleted successfully
    When user click on "Create Project" button on admin tab
    And user fills "all" required fields and "Team" project type and "qa" environment
    And user finds the project and validates the status

  Scenario: Validate user created user role access request for a project access
    When user "Sign In Via Credentials" and logged in using "normalUser" User
    And verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    And user click on "Project Access" button on admin tab
    And user create request for "Project Access" with "QA" role and "Request has been sent" message


  Scenario: Validate project creation on the admin screen with missing details
    When user "Sign In Via Credentials" and logged in using "normalUser" User
    Then verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    When user click on "Create Project" button on admin tab
    And user fills "some" required fields and "Team" project type and "qa" environment
    Then verify project is not created

  Scenario Outline: Validate admin user approve user role access request for a project access
    When user "Sign In Via Credentials" and logged in using "admin" User
    And verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    And user click on "Create Project" button on admin tab
    And user fills "all" required fields and "Team" project type and "<Role>" environment
    And user finds the project and validates the status
    And user is logged out
    And user "Sign In Via Credentials" and logged in using "normalUser" User
    #And verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    And user click on "Project Access" button on admin tab
    And user create request for "Project Access" with "<Role>" role and "Request has been sent" message
    Examples:
      | Role   |
      | qa     |
      | dev    |
      | admin  |
      | viewer |

  Scenario Outline: Validate admin user approve user role access request for a project access..
    When user "Sign In Via Credentials" and logged in using "admin" User
    And verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    And user click on "Create Project" button on admin tab
    And user fills "all" required fields and "Team" project type and "qa" environment
    And user finds the project and validates the status
    And user is logged out
    And user "Sign In Via Credentials" and logged in using "normalUser" User
#And verify that user is logged in
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    And user click on "Project Access" button on admin tab
    And user create request for "Project Access" with "QA" role and "Request has been sent" message
    And user is logged out
    Then user "Sign In Via Credentials" and logged in using "admin" User
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify the URL of "admin" tab
    And user click on "Access Approval" button on admin tab
    And finds the project and perform "<action>" in "Access Approval" and validate "<message>" message
    And user is logged out
    And user "Sign In Via Credentials" and logged in using "normalUser" User
    And user navigate to the "jewel" application
    And user click on "admin" tab
    And verify that user role access request perform "<action>" action successfully
    Examples:
      | action  | message          |
      | approve | Access Approved! |
      | reject  | Access denied!   |

