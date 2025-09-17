package stepdefinitions;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import factory.DriverFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import pages.AdminPage;
import pages.Workspace;
import utils.SharedData;
import utils.SharedDataManager;
import utils.ReportManager;
import utils.functional.ElementUtils;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AdminSteps extends BaseSteps {
    private String projectName;
    private String inputType;
    private final String scenarioId = "global";
    //AdminPage adminPage = new AdminPage(DriverFactory.getPage());

    @When("user click on {string} button on admin tab")
    public void userClickOnButtonOnAdminTab(String buttonName) {
        try {
            adminPage.clickOnAdminTabButtonByName(buttonName);
            log(" user click on :" + buttonName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @And("user fills {string} required fields and {string} project type and {string} environment")
    public void userFillsRequiredFieldsAndProjectType(String inputType, String projectType, String env) {
        try {
            projectName = adminPage.createNewProject(inputType, projectType, env);
            SharedData data = new SharedData();
            data.setProjectName(projectName);
            SharedDataManager.save(scenarioId, data);
            System.out.println("Project Created: " + data.getProjectName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Then("verify project is created successfully by {string}")
    public void verifyProjectIsCreatedSuccessfullyBy(String userType) {
        try {
            Assert.assertTrue(adminPage.isProjectCreated(userType));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @And("finds the project and approve it in {string}")
    public void findsTheProjectAndApproveIt(String flow) {
        try {
            SharedData data = SharedDataManager.load(scenarioId);
            assert data != null;
            Assert.assertTrue(adminPage.waitForProjectToBeVisible(data.getProjectName(), 10000));
            adminPage.checkAndApproveProject(flow);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Then("user finds the project and validates the status")
    public void userFindsTheProjectAndValidatesTheStatus() {
        try {
            SharedData data = SharedDataManager.load(scenarioId);
            assert data != null;
            adminPage.isStatusActive(data.getProjectName(),"ACTIVE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Then("verify project is not created")
    public void verifyProjectIsNotCreated() {
        try {
            Assert.assertTrue(adminPage.isProjectNotCreated());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @And("user clicks on {string} icon")
    public void userClicksOnIcon(String iconName) {
        try {
            adminPage.isActionIconClicked(iconName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Then("verify that {string} field is non-editable")
    public void verifyThatFieldIsNonEditable(String fieldName) {
        try {
            Assert.assertTrue(adminPage.isFieldNonEditable(fieldName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Then("verify that {string} field is editable")
    public void verifyThatFieldIsEditable(String fieldName) {
        try {
            Assert.assertTrue(adminPage.isFieldEditable(fieldName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @And("user confirms deletion")
    public void userConfirmsDeletion() {
        try {
            adminPage.ConformButtonClicked();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Then("validate project is deleted successfully")
    public void validateProjectIsDeletedSuccessfully() {
        try {
            SharedData data = SharedDataManager.load(scenarioId);
            assert data != null;
            Assert.assertTrue(adminPage.isProjectDeleted());
            Assert.assertTrue(adminPage.waitForProjectToBeInvisible(data.getProjectName(), 10000));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @And("verify that user role access request created successfully")
    public void verifyThatUserRoleAccessRequestCreatedSuccessfully() {
        try {
            SharedData data = SharedDataManager.load(scenarioId);
            assert data != null;
            Assert.assertTrue(adminPage.isAccessRequestSent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @And("user finds the project in {string} for {string} role in popup")
    public void userFindsTheProjectInForRoleInPopup(String action, String role) {
        try {
            SharedData data = SharedDataManager.load(scenarioId);
            assert data != null;
            adminPage.requestUserRoleAccessInProject(data.getProjectName(), action, role);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @And("user create request for {string} with {string} role and {string} message")
    public void userCreateRequestForWithRole( String action, String role, String message) {
        try {
            SharedData data = SharedDataManager.load(scenarioId);
            assert data != null;
            adminPage.requestProjectAccess(data.getProjectName(), action, role, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @And("finds the project and perform {string} in {string} and validate {string} message")
    public void findsTheProjectAndApproveIn(String action, String tabDetails,String message) {
        SharedData data = SharedDataManager.load(scenarioId);
        assert data != null;
        adminPage.checkAndApproveAccessRequest(action, data.getProjectName(),tabDetails,message);
    }

    @And("verify that user role access request perform {string} action successfully")
    public void verifyThatUserRoleAccessRequestPerformActionSuccessfully(String action) {
        try {
            SharedData data = SharedDataManager.load(scenarioId);
            assert data != null;
            if(action.equalsIgnoreCase("approve")){
                adminPage.isStatusActive(data.getProjectName(),"ACTIVE");
            }else{
                adminPage.isStatusActive(data.getProjectName(),"PENDING");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @And("verify that user role access request approved successfully")
    public void verifyThatUserRoleAccessRequestApprovedSuccessfully() {

    }
}