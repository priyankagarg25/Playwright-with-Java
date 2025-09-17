package stepdefinitions;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import factory.DriverFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import pages.LoginPage;
import pages.Workspace;
import utils.AESCrypto;
import utils.ExcelUtils;
import utils.ReportManager;
import utils.functional.ElementUtils;

import java.util.List;

import static utils.functional.ElementUtils.closeExtraPagesAfterLoad;

public class LoginSteps extends BaseSteps {


    @Given("^user navigates to \"([^\"]*)\"$")
    public void navigateToUrl(String url) {
        loginPage.navigateTo(url);
        log(" user navigates to :"+url);
    }

    @When("^user enters \"([^\"]*)\" username$")
    public void enterUsername(String username) {
        ElementUtils.fillValueInPlaceholder("Username",username);
        loginPage.fillUsername(username);
        log(" user enter user name as :"+username);
    }

    @When("^user clicks Login button$")
    public void clickLogin() {
        loginPage.clickLogin();
        log(" user click on login");
    }

    @When("^user clicks on \"([^\"]*)\" icon in main page")
    public void clickOnIcon(String iconName) {
        loginPage.clickIconByText(iconName);
        log(" user click on : "+iconName);
    }

    @Then("verify that user is logged in and navigated to Profile page")
    public void verifyProfilePage() {
        Assert.assertTrue(loginPage.isProfilePageVisible());
        log("user is logged in and navigated to Profile page");
    }

    @Then("^user verifies data as \"([^\"]*)\" in \"([^\"]*)\" row and \"([^\"]*)\" column from \"([^\"]*)\" sheet in \"([^\"]*)\" file")
    public void clickOnIcon(String expectedValue, int rowNum, int colNum, String sheetName, String fileName) {
        String actualValue = ExcelUtils.getRowColValue(fileName, sheetName, rowNum, colNum);
        Assert.assertEquals(expectedValue, actualValue);
        log(" user click on :");
    }

    @When("user click on {string}")
    public void userClickOnButtonOrIcon(String buttonName) {
        ElementUtils.clickOnButtonByName(DriverFactory.getPage(),buttonName);
        log(" user click on : "+buttonName);
    }

    @Then("verify that user is logged in")
    public void verifyThatUserIsLoggedIn() {
        Assert.assertTrue(loginPage.isJewelLandingPageVisible());
        log(" verifying that user is logged in");

    }

    @When("user {string} and logged in using {string} User")
    public void userLoggedInUsingUser(String SignInType, String userType) throws Exception {
        BrowserContext context = DriverFactory.getContext();
        List<Page> pages = context.pages();
        // Close all extra tabs automatically
        closeExtraPagesAfterLoad(page);
        if(SignInType.equalsIgnoreCase("Sign In Via Credentials")){
            loginPage.userLoggedinViaCredential(userType);
            log(" user logged in as : "+ userType);
        }
    }

    @Then("verify that user is not logged in")
    public void verifyThatUserIsNotLoggedIn() {
        Assert.assertTrue(loginPage.isInCorrectPasswordToastMsjDisplay());
        log("verify that user is not logged in");
    }

    @And("user navigate to the {string} application")
    public void userNavigateToTheApplication(String appName) {
        Workspace.LinkPage linkPage = workspace.getLinkPageFromName(appName);

        if (linkPage == null) {
            throw new IllegalArgumentException("Invalid application name: " + appName);
        }

        // Navigate to a popup (e.g., new tab) - use only if it's supposed to open a new tab
         Page popupPage = workspace.navigateToPopupLink(linkPage);
        log(String.format("User navigates to the %s application", appName));
        // OR, use this if it's just a regular redirect in the same tab
        // workspace.redirectToLink(linkPage);
    }

    @Then("verify that user received warning and not logged in")
    public void verifyThatUserReceivedWarningAndNotLoggedIn() {
        Assert.assertTrue(loginPage.isCredentialsRequiredToastMsgDisplayed());
        log("verify that user received warning and not logged in");
    }

    @When("user {string} and login using emptyField {string}")
    public void userAndLoginUsingEmptyField(String signInType, String fields) throws Exception {
        loginPage.userLoggedInWithEmptyFields(fields);
        log(String.format("user %s and login using emptyField %s",signInType,fields));
    }


}
