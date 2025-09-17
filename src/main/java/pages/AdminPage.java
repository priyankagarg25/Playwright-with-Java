package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import factory.DriverFactory;

import java.util.UUID;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static utils.functional.ElementUtils.*;

public class AdminPage {
    Page page;
    private String projectName;
    private String projectDescription;
    private final Locator Project_Name_Field;
    private final Locator Project_Type_Dropdown;
    private final Locator Project_Descrption_Field;
    private final Locator Project_Environment_Field;
    private final Locator CREATE_BUTTON;
    private final Locator projectApprovalRequestMsg;
    private final Locator AlertMsg;
    private final Locator ProjectNameFilterIcon;
    private final Locator SelectOptionsDropdown;
    private final Locator Project_Name_FilterText;
    private final Locator Main_Data;
    private final Locator Row_Data;
    private final Locator Confirm_Button;
    private final Locator Project_Deletion_Msg;
    private final Locator Project_Access_Approval_Msg;
    private final Locator Project_Access_Approved_Msg;
    private final Locator Project_Created_Msg;
    private final Locator ActionDiv;

    public AdminPage(Page page) {
        this.page = DriverFactory.getPage().locator("Projects").isVisible()
                ? DriverFactory.getPage()
                : getSwitchedTab();
        this.Project_Name_Field = page.getByPlaceholder("Project Name");
        this.Project_Type_Dropdown = page.getByText("Application");
        this.Project_Descrption_Field = page.locator("#desc");
        this.Project_Environment_Field = page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Environment\\(s\\) \\*Note: Press Enter to add a new env value !$"))).getByRole(AriaRole.TEXTBOX);
        this.projectApprovalRequestMsg = page.getByText("Project sent for approval");
        this.Project_Deletion_Msg = page.getByText("Project has been deleted successfully");
        this.Project_Access_Approval_Msg = page.getByText("Request has been sent");
        this.Project_Created_Msg = page.getByText("Project is created successfully !!");
        this.CREATE_BUTTON = page.getByLabel("Create", new Page.GetByLabelOptions().setExact(true));
        this.AlertMsg = page.getByRole(AriaRole.ALERT);
        this.ProjectNameFilterIcon = page.getByRole(AriaRole.COLUMNHEADER, new Page.GetByRoleOptions().setName("Project Name  Filter")).getByLabel("Filter");
        this.SelectOptionsDropdown = page.locator("div:nth-child(3) > .p-multiselect-trigger");
        this.Project_Name_FilterText = page.getByRole(AriaRole.TEXTBOX).nth(1);
        this.Main_Data = page.locator("#mainData");
        this.Row_Data = page.getByLabel("Projects").locator("tbody");
        this.Confirm_Button = page.locator("xpath=//div[@class=\"btn btn-success\"]");
        this.ActionDiv = page.getByText("Project Name *Project Type*ApplicationTask Flow *NewUnder");
        projectName = "TestPOC" + UUID.randomUUID().toString().substring(0, 8);
        projectDescription = "TestPOC Description " + UUID.randomUUID().toString().substring(0, 8);
        Project_Access_Approved_Msg = page.getByText("Access Approved!");
    }

    /**
     * Method to click on Buttons present on the Admin screen
     *
     * @param buttonName
     */

    public void clickOnAdminTabButtonByName(String buttonName) {
        if (page.isClosed()) {
            page = DriverFactory.getPage().locator("Projects").isVisible()
                    ? DriverFactory.getPage()
                    : getSwitchedTab();
        }
        Locator button = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(buttonName));
        //waitUntilElementDisplayed(button,10000);
        button.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        button.click();
    }

    /**
     * @return : returns Project name for referencing the next methods
     */

    public String createNewProject(String inputType, String projectType, String env) {

        waitForFiledToBeEditable(Project_Name_Field, 10000);
        giveProjectName(inputType, projectName);
        Project_Type_Dropdown.click();
        page.locator("xpath=//li/span[contains(text(),'" + projectType + "')]").click();
        waitForFiledToBeEditable(Project_Environment_Field, 10000);

        waitForFiledToBeEditable(Project_Descrption_Field, 10000);
        Project_Descrption_Field.type(projectDescription, new Locator.TypeOptions().setDelay(50));


        Project_Environment_Field.click();
        Project_Environment_Field.clear();
        Project_Environment_Field.type(env, new Locator.TypeOptions().setDelay(50));
        Project_Environment_Field.press("Enter");

        String currentValue = Project_Name_Field.inputValue();
        if (currentValue.isEmpty()) {
            giveProjectName(inputType, projectName);
        } else {
            System.out.println("Field is empty, placeholder might be visible");
        }
        if (inputType.equals("all")) {
            waitForLocator(CREATE_BUTTON, 10000);
            CREATE_BUTTON.click();
        }

        return projectName;
    }

    private void giveProjectName(String inputType, String projectName) {
        if (inputType.equals("all")) {
            Project_Name_Field.fill(projectName);
        } else if (inputType.equals("some")) {
            Project_Name_Field.fill("");
            System.out.println("Required field missing");
        }



    }


    /**
     * Method to validate project created or not
     *
     * @return: status as boolean
     */

    public boolean isProjectCreated(String userType) {
        if(userType.equalsIgnoreCase("normalUser")) {
            return waitUntilElementDisplayed(projectApprovalRequestMsg, 60);
        } else if (userType.equalsIgnoreCase("admin")) {
            return waitUntilElementDisplayed(Project_Created_Msg, 60);
        }
        return false;
    }


    /**
     * Method to check project is visible in the grid
     *
     * @param projectName
     * @return
     */
    public boolean waitForProjectToBeVisible(String projectName, double timeoutMillis) {
        Locator locator = page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName(projectName));

        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(timeoutMillis));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to check project is invisible in the grid
     *
     * @param projectName
     * @return
     */
    public boolean waitForProjectToBeInvisible(String projectName, double timeoutMillis) {

        Locator locator = page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName(projectName));

        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.DETACHED) // element removed from DOM
                    .setTimeout(timeoutMillis));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Method to approve the project by admin user
     */
    public void checkAndApproveProject(String flow) {
        Locator projectApproveSvg = page.getByLabel(flow).locator("svg");
        projectApproveSvg.first().click();
        if (flow.equalsIgnoreCase("Project Request Access")) {
            assertThat(AlertMsg).containsText("Project Creation Request Approved Successfully");
        } else {
            assertThat(Project_Access_Approval_Msg)
                    .isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(60000));
        }

    }

    /**
     * Method to approve the project by admin user
     */
    public void checkAndApproveAccessRequest(String action, String projectName,String tabDetails,String message) {
        waitForLocator(  page.getByLabel("Select null").nth(1),3000);
        page.getByRole(AriaRole.COLUMNHEADER, new Page.GetByRoleOptions().setName("Project  Filter")).getByLabel("Filter").click();
        page.getByText("Select Option(s)").click();
        page.getByRole(AriaRole.TEXTBOX).nth(2).fill(projectName);
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(projectName)).locator("div").nth(1).click();
        page.getByRole(AriaRole.COLUMNHEADER, new Page.GetByRoleOptions().setName("Project  Filter")).click();
        page.getByText(Pattern.compile(".*Total 1 Request\\(s\\) Found.*")).click();
        if(action.equalsIgnoreCase("approve")) {
            page.getByLabel("Access Approvals").locator("svg").first().click();
        }else {
            page.getByLabel("Access Approvals").locator("svg").nth(1).click();
        }
        validateMessageAndClosePopup(tabDetails,message);
    }

    /**
     * Method to filter the project by project name and check the status
     *
     * @param projectName
     */
    public void isStatusActive(String projectName, String status) {
        //  Page secondTab = ElementUtils.getSwitchedTab();
        ProjectNameFilterIcon.click();
        SelectOptionsDropdown.click();
        if(status.equalsIgnoreCase("ACTIVE")) {
            Project_Name_FilterText.fill(projectName);
            page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(projectName)).locator("div").nth(1).click();
            Main_Data.click();
            assertThat(Row_Data).containsText(status);
        }else{
            Project_Name_FilterText.fill(projectName);
           assertThat(
                    page.getByRole(
                            AriaRole.OPTION,
                            new Page.GetByRoleOptions().setName(projectName)
                    ).locator("div").nth(1)
            ).not().isVisible();

        }
    }

    /**
     * This method validates that create button is disabled when something is missing or incorrect
     *
     * @return
     */
    public boolean isProjectNotCreated() {
        return CREATE_BUTTON.isDisabled();
    }

    public void isActionIconClicked(String iconName) {
        //  Page secondTab = ElementUtils.getSwitchedTab();

        // Create locator for the icon
        Locator editProjectIcon = page.getByLabel(iconName);

        // Wait dynamically until icon is visible and enabled
        waitForLocator(editProjectIcon, 10000);

        // Click the icon (Playwright will also auto-wait for clickable)
        editProjectIcon.click();
    }


    /**
     * This method checks for field to be non editable.
     *
     * @param fieldName
     * @return boolean value of editability check
     */

    public boolean isFieldNonEditable(String fieldName) {
        //  Page secondTab = ElementUtils.getSwitchedTab();
        String classAttribute = page.getByPlaceholder(fieldName).getAttribute("class");
        return classAttribute.contains("p-disabled");
    }

    /**
     * This method checks for field to be editable.
     *
     * @param fieldName
     * @return boolean value of editability check
     */

    public boolean isFieldEditable(String fieldName) {

        String classAttribute = page.locator("xpath=//div[contains(text(),'" + fieldName + "')]/following-sibling:: textarea").getAttribute("class");
        return !classAttribute.contains("p-disabled");
    }

    /**
     * This method clicks on yes button of confirmation popup
     */

    public void ConformButtonClicked() {
        Confirm_Button.click();
    }

    /**
     * This method validate project deletion message is displayed
     *
     * @return boolean value of msg displayed
     */

    public boolean isProjectDeleted() {
        return waitUntilElementDisplayed(Project_Deletion_Msg, 60);
    }

    public void requestProjectAccess(String projectName, String action, String role, String message) {

        waitForLocator(page.getByLabel(action).getByText("Select Project(s)"), 10000);
        page.getByLabel(action).getByText("Select Project(s)").click();
        page.getByRole(AriaRole.TEXTBOX).nth(3).fill(projectName);
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(projectName)).locator("div").nth(1).click();
        page.getByLabel(action).locator("span").filter(new Locator.FilterOptions().setHasText("Select Access Role(s)")).click();
        page.getByLabel(role).click();
        page.getByLabel(action).getByLabel("Request Access").click();
         validateMessageAndClosePopup(action,message);

    }

    private void validateMessageAndClosePopup(String action, String message) {
        Locator msj = page.getByText(message);
        String msg = msj.textContent();
        System.out.println("messgae is :" + msg);
        assertThat(msj).containsText(message);
        waitForLocator(page.getByLabel(action).getByLabel("Close"), 2000);
        page.getByLabel(action).getByLabel("Close").click();
    }

    public void requestUserRoleAccessInProject(String projectName, String action, String role) {

        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(" '" + projectName + "'")).locator("div").nth(1);
        page.getByLabel(action).getByLabel("Select Access Role(s)").click();
        page.getByLabel(role).click();
        page.getByLabel(action).getByLabel("Request Access").click();

    }

    /**
     * This method validate project request access message is displayed
     *
     * @return boolean value of msg displayed
     */

    public boolean isAccessRequestSent() {
        return waitUntilElementDisplayed(Project_Access_Approval_Msg, 60);
    }

    public boolean isRequestApproved() {
        return waitUntilElementDisplayed(Project_Access_Approved_Msg, 60);
    }
}

