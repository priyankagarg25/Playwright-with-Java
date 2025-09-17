package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportManager {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String reportPath = System.getProperty("user.dir") + "/reports/GemEcoSystemReport.html";

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle("Automation Test Report");
            spark.config().setReportName("Playwright-Cucumber Automation");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
        }
        return extent;
    }

/*
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("reports/ParallelReport.html");
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle("Automation Test Report");
            spark.config().setReportName("Cucumber + Playwright Test Results");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
        }
        return extent;
    }
*/
/*
    public static synchronized ExtentTest createTest(String scenarioName, String browserName) {
        ExtentTest test = getInstance()
                .createTest(scenarioName)
                .assignCategory(browserName) // Group by browser
                .assignAuthor(Thread.currentThread().getName()); // Group by thread
        extentTest.set(test);
        return test;
    }*/
    public static synchronized ExtentTest createTest(String scenarioName, String browserName) {
        // Fallback in case browserName is missing
        if (browserName == null || browserName.trim().isEmpty()) {
            browserName = "DefaultBrowser";
        }

        ExtentTest test = getInstance()
                .createTest(scenarioName + " [" + browserName + "]")
                .assignCategory(browserName)
                .assignAuthor("Thread: " + Thread.currentThread().getId());

        extentTest.set(test);
        return test;
    }

    public static synchronized ExtentTest getTest() {
        return extentTest.get();
    }

    public static synchronized void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }
    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
