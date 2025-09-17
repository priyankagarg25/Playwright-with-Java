package hooks;

import com.microsoft.playwright.Page;
import io.cucumber.java.Scenario;
import io.cucumber.plugin.event.PickleStepTestStep;
import utils.functional.ElementUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static hooks.Hooks.scenarioTest;

public class ScreenshotUtils {

    private static final String SCREENSHOT_DIR = "reports/screenshots/";

    public static void captureScreenshot(Scenario scenario, String label) {
        try {
            // Ensure screenshots folder exists
            File dir = new File(SCREENSHOT_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Get the Playwright page
            Page page = ElementUtils.getSwitchedTab();

            // Generate unique screenshot name
            String scenarioName = scenario.getName().replaceAll("\\s+", "_");
            String screenshotName = scenarioName.replace("\"", "") + "_" + System.currentTimeMillis() + ".png";
            String screenshotPath = SCREENSHOT_DIR + screenshotName;

            // Take screenshot
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));

            // Attach screenshot to Cucumber report
            byte[] screenshotBytes = Files.readAllBytes(Paths.get(screenshotPath));
            scenario.attach(screenshotBytes, "image/png", label);

            // Get the current step text
            String currentStep = getCurrentStep(scenario).orElse("Step");

            // Create relative path for Extent Report
            String relativePath = "screenshots/" + screenshotName;

            // Create HTML for step + thumbnail
            String thumbnailHtml =

                    "<div style='display:flex; align-items:center; gap:12px; margin-top:10px;'>" +
                            "<span style='font-weight:bold; color:#2a2a2a;'>" + currentStep + ":</span>" +
                            "<a href='" +relativePath+ "' target='_blank'>" +
                            "<img src='" +relativePath+ "' height='90' width='150' " +
                            "style='margin-left:35px; border:1px solid #ccc; border-radius:4px; " +
                            "box-shadow:1px 1px 6px rgba(0,0,0,0.2);'/>" +
                            "</a></div>";

            // Log into Extent Report
            scenarioTest.get().info(thumbnailHtml);

            System.out.println("📸 Screenshot captured for step: " + currentStep);

        } catch (Exception e) {
            System.err.println("❌ Failed to capture screenshot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extracts the current executing Gherkin step.
     */
    private static Optional<String> getCurrentStep(Scenario scenario) {
        try {
            return scenario.getSourceTagNames().stream()
                    .filter(tag -> tag.startsWith("@step"))
                    .findFirst();
        } catch (Exception e) {
            return Optional.of("Step");
        }
    }
}
