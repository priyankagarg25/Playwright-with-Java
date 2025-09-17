package stepdefinitions;

import factory.DriverFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import pages.LoginPage;
import utils.functional.ElementUtils;

import static pages.LoginPage.isUserLoggedOut;
import static pages.LoginPage.logoutFromJewel;

public class CommonSteps {
    @Given("user click on {string} tab")
    public void user_click_on_tab(String tabName) {
        try {
            ElementUtils.switchLeftPaneTabs(tabName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Then("verify the URL of {string} tab")
    public void verifyTheURLOfTab(String tabName) {
        try {
            Assert.assertTrue(ElementUtils.isPageUrlVisible(tabName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @And("user is logged out")
    public void userIsLoggedOut() {
        try {
             logoutFromJewel();
          // Assert.assertTrue(isUserLoggedOut());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
