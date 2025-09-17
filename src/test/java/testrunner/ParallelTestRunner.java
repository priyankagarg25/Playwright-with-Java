package testrunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepdefinitions", "hooks"},
        plugin = {
                "pretty",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true,
        tags = "@default"
)
public class ParallelTestRunner extends AbstractTestNGCucumberTests {

    // Static block runs BEFORE scenario discovery
    static {
        String dynamicTags = System.getProperty("cucumber.filter.tags");
        if (dynamicTags != null && !dynamicTags.isEmpty()) {
            System.setProperty("cucumber.filter.tags", dynamicTags);
        }
    }
    private static final ThreadLocal<String> browserName = new ThreadLocal<>();

    @BeforeClass(alwaysRun = true)
    @Parameters("browser")
    public void setBrowser(@Optional("chrome") String browser) {
        browserName.set(browser);
        System.setProperty("current.browser", browserName.get());
    }

    public static String getBrowser() {
        return browserName.get();
    }
    @Override
    @org.testng.annotations.DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
