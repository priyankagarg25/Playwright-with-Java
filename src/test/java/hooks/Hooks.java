package hooks;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import factory.DriverFactory;
import io.cucumber.java.*;
import org.testng.ITestContext;
import org.testng.Reporter;
import pages.AdminPage;
import testrunner.ParallelTestRunner;
import utils.ConfigReader;
import utils.ReportManager;

public class Hooks {
    public DriverFactory driverFactory;
    public Page page;
    public Browser browser;
    private static ExtentReports extent;
    private static boolean isReportInitialized = false;
    protected AdminPage adminPage;

    public static ThreadLocal<ExtentTest> scenarioTest = new ThreadLocal<>();

    @BeforeAll
    public static void setupReport() {
        if (!isReportInitialized) {
            extent = ReportManager.getInstance();
            isReportInitialized = true;
        }
    }

    @Before(order = 0)
    public void beforeScenario(Scenario scenario) {
        String browserName = System.getProperty("browser", "chrome");
        ExtentTest test =  ReportManager.createTest(scenario.getName(), browserName);
      //  ExtentTest test = extent.createTest(scenario.getName());
        scenarioTest.set(test);
    }

    @After(order = 0)
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {

            scenarioTest.get().fail("Scenario failed: " + scenario.getName());

        } else {
            scenarioTest.get().pass("Scenario passed: " + scenario.getName());
        }
        if (scenario.isFailed()) {
            ScreenshotUtils.captureScreenshot(scenario, "Final Failed Screenshot");
        }
        DriverFactory.closeContextOnly();
    }

    @Before(order = 1)
    public void launchBrowser() {
        // 1️⃣ First check Maven system property
        String browserName = System.getProperty("browser");

        // 2️⃣ If not passed via Maven, try fetching from testng.xml parameter
        if (browserName == null || browserName.isEmpty()) {
            ITestContext context = Reporter.getCurrentTestResult().getTestContext();
            if (context != null && context.getCurrentXmlTest() != null) {
                browserName = context.getCurrentXmlTest().getParameter("browser");
            }
        }

        // 3️⃣ If not provided anywhere, fall back to config.properties
        if (browserName == null || browserName.isEmpty()) {
            browserName = ConfigReader.getProperty("browser");
        }

        // 4️⃣ Default if still not found
        if (browserName == null || browserName.isEmpty()) {
            browserName = "chrome";
        }

        driverFactory = new DriverFactory();
        page = driverFactory.initDriver(browserName);
    }


  /*  @Before(order = 1)
    public void launchBrowser() {

        String browserName = ConfigReader.getProperty("browser");  //Fetching browser value from config file
        driverFactory = new DriverFactory();
        page = driverFactory.initDriver(browserName);
    }*/

    @AfterStep
    public void captureScreenshotAfterStep(Scenario scenario) {
        ScreenshotUtils.captureScreenshot(scenario, "Step Screenshot");
    }


// Runs once after all tests (Cucumber + JUnit @AfterAll or TestNG @AfterSuite)
    @AfterAll
    public static void globalTeardown() {
        DriverFactory.closeAll();
        ReportManager.flush();
    }

    // Helper to access ExtentTest in step definitions
    public static ExtentTest getExtentTest() {
        return scenarioTest.get();
    }
}
