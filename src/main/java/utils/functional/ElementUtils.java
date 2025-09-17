package utils.functional;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import factory.DriverFactory;

import java.util.List;

import static factory.DriverFactory.threadLocalDriver;


public class ElementUtils {
  private static Page page= DriverFactory.getPage();

    public ElementUtils(Page page) {
        this.page = page;
    }
    public static void clickButtonByAriaLabel(String ariaLabel) {
        Locator button = page.locator("button[aria-label='" + ariaLabel + "']");
        button.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        button.click();
    }

    public static Page waitForNewTab(Runnable triggerAction, int timeoutMillis) {
        BrowserContext context = DriverFactory.getContext();

        // Wait for a new page, wrapping the action that triggers it
        Page newPage = context.waitForPage(
                new BrowserContext.WaitForPageOptions().setTimeout(timeoutMillis),
                triggerAction
                );

        // Ensure it’s active
        newPage.bringToFront();
        threadLocalDriver.set(newPage);

        return newPage;
    }

    public static Page getSwitchedTab() {
        BrowserContext context = DriverFactory.getContext();
        List<Page> pages = context.pages();

        if (pages.isEmpty()) {
            throw new RuntimeException("No tabs are currently open.");
        }

        Page activePage = (pages.size() == 1) ? pages.get(0) : pages.get(pages.size() - 1);

        activePage.bringToFront();
        threadLocalDriver.set(activePage);

        return activePage;
    }

    // Get Locator from current page
    public Locator getElement(String selector) {
        return DriverFactory.getPage().locator(selector);
    }

    // Get Locator from a specific iframe using frame name or selector
    public Locator getElementFromFrame(String frameSelector, String elementSelector) {
        FrameLocator frameLocator = DriverFactory.getPage().frameLocator(frameSelector);
        return frameLocator.locator(elementSelector);
    }

    // Get Frame directly by name, URL, or index
    public Locator getElementFromFrameByName(String frameName, String elementSelector) {
        Frame frame = DriverFactory.getPage().frame(frameName);
        if (frame != null) {
            return frame.locator(elementSelector);
        }
        throw new RuntimeException("Frame not found: " + frameName);
    }
    public static boolean waitUntilElementDisplayed(Locator locator, int timeoutSec) {
        boolean elementVisible = locator.isVisible();
        int timer = 0;
        while (!elementVisible && timer < timeoutSec) {
            try {
                waitForLocator(locator,1000);
                elementVisible = locator.isVisible();
                timer++;

            } catch (Exception e) {
                System.out.println(locator + "was not visible.");
            }
        }
        return elementVisible;
    }

    // Optional: handle browser alert/dialog
    public void handleAlert(String action, String promptText) {
        DriverFactory.getPage().onceDialog(dialog -> {
            System.out.println("Dialog message: " + dialog.message());
            switch (action.toLowerCase()) {
                case "accept":
                    dialog.accept(promptText);
                    break;
                case "dismiss":
                    dialog.dismiss();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid dialog action");
            }
        });
    }
    public static void clickOnButtonByName(Page page ,String buttonName){
        Locator button = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(buttonName));
        button.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        button.click();
    }

    public static void fillValueInPlaceholder(String placeholderName ,String value){

        DriverFactory.getPage().getByPlaceholder(placeholderName).click();
        DriverFactory.getPage().getByPlaceholder(placeholderName).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        DriverFactory.getPage().getByPlaceholder(placeholderName).fill(value);
    }
    public static void fillValueInPlaceholder(Page page ,String placeholderName ,String value){
        page.getByPlaceholder(placeholderName).click();
        page.getByPlaceholder(placeholderName).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        page.getByPlaceholder(placeholderName).fill(value);
    }

    public static void clickONLinkByName(Page page, String linkName){

        Locator link = page.locator("xpath=//a/h2[contains(text(),'"+linkName+"')]");
        link.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        link.click();
    }

    public static void switchLeftPaneTabs(String tabName) {
        Page secondTab = getSwitchedTab();
        Locator newTab = secondTab.locator(String.format("xpath=//a[@href='#/%s']", tabName));
        newTab.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        newTab.click();
        secondTab.bringToFront();
    }

    public static boolean isPageUrlVisible(String tabName){
        Page secondTab = getSwitchedTab();
        String adminUrl = secondTab.url();
        return adminUrl.contains(tabName);
    }


    public static void waitForLocator(Locator locator, int timeout){
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeout)); // configurable timeout

    }
    public static void waitForFiledToBeEditable(Locator locator,double time){
        page.waitForCondition(locator::isEditable,
                new Page.WaitForConditionOptions().setTimeout(time));
    }
    /**
     * Switch to the tab where the locator is visible.
     * If the locator is visible on the current page, stay on it.
     *
     * @param locatorString CSS or XPath selector of the element.
     * @param timeoutMillis Timeout for visibility check in each tab.
     * @return Page where the locator is visible.
     */
    public Page switchToTabIfElementNotVisible(String locatorString, int timeoutMillis) {
        BrowserContext context = DriverFactory.getContext();
        List<Page> pages = context.pages();

        // If no tabs are open, throw an error
        if (pages.isEmpty()) {
            throw new RuntimeException("No browser tabs are currently open!");
        }

        // ✅ First, check the currently active page
        Page currentPage = pages.get(pages.size() - 1);
        Locator locator = currentPage.locator(locatorString);

        if (locator.isVisible(new Locator.IsVisibleOptions().setTimeout(timeoutMillis))) {
            // Element is already visible on current tab ✅
            return currentPage;
        }

        // 🔄 Otherwise, loop through all open tabs and find where the element is visible
        for (Page page : pages) {
            Locator tabLocator = page.locator(locatorString);
            if (tabLocator.isVisible(new Locator.IsVisibleOptions().setTimeout(timeoutMillis))) {
                page.bringToFront();
                DriverFactory.setPage(page); // 🔹 Update threadLocal page reference
                return page;
            }
        }

        throw new RuntimeException("Element '" + locatorString + "' not found on any open tabs!");
    }
    public static void closeExtraPagesAfterLoad(Page activePage) {
        BrowserContext context = DriverFactory.getContext();

        // ✅ Wait until the current page is fully loaded
        activePage.waitForLoadState(LoadState.LOAD);

        // ✅ Now safely close other tabs
        for (Page page : context.pages()) {
            if (!page.equals(activePage)) {
                try {
                    page.close();
                } catch (Exception e) {
                    System.err.println("Failed to close extra page: " + e.getMessage());
                }
            }
        }

        // Update thread-local driver to the active page
        DriverFactory.setPage(activePage);
    }
    public static void closeDialogIfVisible(Page page) {
        Locator closeButton = page.getByLabel("Close");

        try {
            // Wait briefly to see if the dialog appears
            if (closeButton.isVisible(new Locator.IsVisibleOptions().setTimeout(3000))) {
                closeButton.click();
                System.out.println("Dialog closed successfully.");
            } else {
                System.out.println("Dialog not found, skipping close action.");
            }
        } catch (PlaywrightException e) {
            // If the element is not found within the timeout, ignore it safely
            System.out.println("Dialog did not appear, continuing execution...");
        }
    }

}
