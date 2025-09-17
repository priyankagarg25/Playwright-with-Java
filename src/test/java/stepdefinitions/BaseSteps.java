package stepdefinitions;

import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Page;
import factory.DriverFactory;
import hooks.Hooks;
import pages.AdminPage;
import pages.LoginPage;
import pages.Workspace;
import utils.functional.ElementUtils;
/*
public class BaseSteps {
    protected ExtentTest test;
    protected Page page;
    protected  LoginPage loginPage = new LoginPage(DriverFactory.getPage());
    protected AdminPage adminPage = new AdminPage(DriverFactory.getPage().locator("Projects").isVisible()
            ? DriverFactory.getPage()
            : ElementUtils.getSwitchedTab());
    protected  Workspace workspace = new Workspace(DriverFactory.getPage().locator("Taskboard Taskboard").isVisible()
            ? DriverFactory.getPage()
            : ElementUtils.getSwitchedTab());

    public BaseSteps() {
        this.test = Hooks.getExtentTest();
        this.page = DriverFactory.getPage();
    }

    protected void log(String message) {
        test.info(message);
    }

}*/
public class BaseSteps {
    protected ExtentTest test;
    protected Page page;
    protected LoginPage loginPage;
    protected AdminPage adminPage;
    protected Workspace workspace;

    public BaseSteps() {
        // Fetch extent report per thread
        this.test = Hooks.getExtentTest();

        // Always get the thread-specific page
        this.page = DriverFactory.getPage();

        // Initialize pages AFTER page is available
        this.loginPage = new LoginPage(page);

        if (page.locator("text=Projects").isVisible()) {
            this.adminPage = new AdminPage(page);
        } else {
            this.adminPage = new AdminPage(ElementUtils.getSwitchedTab());
        }

        if (page.locator("text=Taskboard Taskboard").isVisible()) {
            this.workspace = new Workspace(page);
        } else {
            this.workspace = new Workspace(ElementUtils.getSwitchedTab());
        }
    }

    protected void log(String message) {
        test.info(message);
    }
}

