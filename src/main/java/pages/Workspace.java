package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import factory.DriverFactory;
import utils.functional.ElementUtils;
import com.microsoft.playwright.*;

public class Workspace {
    private final Page page;
    private final Locator jewelLink;
    private final Locator agileCrafterLink;
    private final Locator taskboardLink;
    private final Locator profileLink;
    private final Locator logoutLink;

    public Workspace(Page page) {
        this.page = DriverFactory.getPage().locator("Taskboard Taskboard").isVisible()
                ? DriverFactory.getPage()
                : ElementUtils.getSwitchedTab();
        this.jewelLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Jewel Jewel"));
        this.agileCrafterLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Agile Crafter Agile Crafter"));
        this.taskboardLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Taskboard Taskboard"));
        this.profileLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Profile"));
        this.logoutLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Logout"));
    }

    public enum LinkPage {
        JEWEL("Jewel"),
        AGILE_CRAFTER("Agile Crafter Agile Crafter"),
        TASKBOARD("Taskboard Taskboard"),
        PROFILE("Profile"),
        LOGOUT("Logout");

        private final String linkName;

        LinkPage(String linkName) {
            this.linkName = linkName;
        }

        public String getLinkName() {
            return linkName;
        }
    }

    public void redirectToLink(LinkPage linkPage) {
        ElementUtils.clickONLinkByName(page, linkPage.getLinkName());
    }

    // Example of navigating and handling popup (e.g., new tab)
    public Page navigateToPopupLink(LinkPage linkPage) {
        return page.waitForPopup(() -> {
            ElementUtils.clickONLinkByName(page, linkPage.getLinkName());
        });
    }
    public LinkPage getLinkPageFromName(String appName) {
        switch (appName.toLowerCase().trim()) {
            case "jewel":
                return Workspace.LinkPage.JEWEL;
            case "agile crafter":
                return Workspace.LinkPage.AGILE_CRAFTER;
            case "taskboard":
                return Workspace.LinkPage.TASKBOARD;
            case "profile":
                return Workspace.LinkPage.PROFILE;
            case "logout":
                return Workspace.LinkPage.LOGOUT;
            default:
                return null; // or throw exception if preferred
        }
    }

}
