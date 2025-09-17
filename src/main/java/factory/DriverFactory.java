package factory;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import utils.ConfigReader;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DriverFactory {

    // ThreadLocal instances to ensure thread safety
    public static final ThreadLocal<Playwright> threadLocalPlaywright = new ThreadLocal<>();
    public static final ThreadLocal<Browser> threadLocalBrowser = new ThreadLocal<>();
    public static final ThreadLocal<BrowserContext> threadLocalContext = new ThreadLocal<>();
    public static final ThreadLocal<Page> threadLocalDriver = new ThreadLocal<>();

    // Initialize browser for this thread
    public Page initDriver(String browserName) {
        boolean headless = Boolean.parseBoolean(ConfigReader.getProperty("headless"));

        try {
            // Create a new Playwright instance for this thread
            Playwright playwright = Playwright.create();
            threadLocalPlaywright.set(playwright);

            BrowserType browserType;
            switch (browserName.toLowerCase()) {
                case "firefox":
                    browserType = playwright.firefox();
                    break;
                case "chrome":
                case "chromium":
                    browserType = playwright.chromium();
                    break;
                case "webkit":
                    browserType = playwright.webkit();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browserName);
            }

            // Launch a new browser per thread
            Browser browser = browserType.launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(headless)
                            .setTimeout(30000)
            );
            threadLocalBrowser.set(browser);

            // Create a new context per thread
            BrowserContext context = browser.newContext();
            threadLocalContext.set(context);

            // Optional: start tracing
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(false)
            );

            // Open a new page per thread
            Page page = context.newPage();
            threadLocalDriver.set(page);

            return page;

        } catch (PlaywrightException pe) {
            System.err.println("Playwright initialization failed: " + pe.getMessage());
            throw pe;
        } catch (Exception e) {
            System.err.println("Unexpected error during browser setup: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public static synchronized BrowserContext getContext() {
        return threadLocalContext.get();
    }

    public static synchronized Browser getBrowser() {
        return threadLocalBrowser.get();
    }

    public static synchronized Playwright getPlaywright() {
        return threadLocalPlaywright.get();
    }

    // Close browser and cleanup
    public static synchronized void closeAll() {
        try {
            if (threadLocalContext.get() != null) {
                threadLocalContext.get().close();
            }
            if (threadLocalBrowser.get() != null) {
                threadLocalBrowser.get().close();
            }
            if (threadLocalPlaywright.get() != null) {
                threadLocalPlaywright.get().close();
            }
        } finally {
            threadLocalContext.remove();
            threadLocalBrowser.remove();
            threadLocalPlaywright.remove();
            threadLocalDriver.remove();
        }
    }
/*
    public static synchronized synchronized Page getPage() {
        return threadLocalDriver.get();

    }*/
    public static synchronized Page getPage() {
        BrowserContext context = DriverFactory.getContext();

        if (context == null) {
            throw new RuntimeException("No active browser context found!");
        }

        List<Page> pages = context.pages();
        if (pages.isEmpty()) {
            throw new RuntimeException("No open pages found in the current context!");
        }

        // Prefer the last opened page
        Page currentPage = pages.get(pages.size() - 1);

        // If it's already closed, fallback to the first one
        if (currentPage.isClosed()) {
            currentPage = pages.get(0);
        }

        currentPage.bringToFront();
        threadLocalDriver.set(currentPage);

        return currentPage;
    }

/*    public Page getPage(Locator locator){
        Page page = threadLocalDriver.get();
        if(!locator.isVisible()){
            page= DriverFactory.getPage();
        }
        return page;
    }*/


    /**
     * Optional helper to navigate with retry (useful for net::ERR_SOCKET_NOT_CONNECTED)
     */
    public static synchronized void safeNavigate(String url, int retries, int waitMillis) {
        int attempt = 0;
        while (attempt < retries) {
            try {
                getPage().navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
                return;
            } catch (PlaywrightException e) {
                System.err.println("Navigation attempt " + (attempt + 1) + " failed: " + e.getMessage());
                attempt++;
                try {
                    TimeUnit.MILLISECONDS.sleep(waitMillis);
                } catch (InterruptedException ignored) {
                }
                if (attempt == retries) throw e;
            }
        }
    }
    public static synchronized void closeContextOnly() {
        try {
            if (threadLocalDriver.get() != null) {
                threadLocalDriver.get().close();
                threadLocalDriver.remove();
            }
            if (threadLocalContext.get() != null) {
                threadLocalContext.get().tracing().stop(
                        new Tracing.StopOptions().setPath(Paths.get("trace-" + System.currentTimeMillis() + ".zip"))
                );
                threadLocalContext.get().close();
                threadLocalContext.remove();
            }
        } catch (Exception e) {
            System.err.println("Error while closing context: " + e.getMessage());
        }
    }
    public static synchronized void setPlaywright(Playwright playwright) {
        threadLocalPlaywright.set(playwright);
    }

    // 🔹 New Method: Update Current Page Reference
    public static synchronized void setPage(Page page) {
        threadLocalDriver.set(page);
    }

}

