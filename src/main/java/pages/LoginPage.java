package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import factory.DriverFactory;
import utils.AESCrypto;
import utils.functional.ElementUtils;

import static utils.AESCrypto.decrypt;
import static utils.ConfigReader.getProperty;
import static utils.functional.ElementUtils.waitUntilElementDisplayed;

public class LoginPage {
    private final Page page;
    private final Locator usernameField;
    private final Locator passwordField;
    private final Locator loginButton;
    private final Locator booksSearchBox;
    private final Locator autolyticsMessage;
    private final Locator incorrectPasswordToastMsg;
    private final Locator credentialsRequiredMsg;

    public LoginPage(Page page) {
      this.page =  page;
        this.usernameField = page.locator("[placeholder='Username']");
        this.passwordField = page.locator("[placeholder='Password']");
        this.loginButton = page.locator("#login");
        this.booksSearchBox = page.getByPlaceholder("Type to search");
        this.autolyticsMessage = page.getByText("Login Successful !");
        this.incorrectPasswordToastMsg = page.getByText("User not found or password is incorrect !!");
        this.credentialsRequiredMsg = page.getByText("All fields are required !");
    }

    public void navigateTo(String urlKey) {
        DriverFactory.safeNavigate(getProperty(urlKey), 3, 1000);
       // page.navigate(getProperty(urlKey));
    }

    public void login(String usernameKey, String encryptedPassword) throws Exception {
        fillUsername(usernameKey);
        fillPassword(decrypt(encryptedPassword));
        clickLogin();
    }

    public void fillUsername(String usernameKey) {
        usernameField.fill(getProperty(usernameKey));
    }

    public void fillPassword(String encryptedPassword) throws Exception {
        passwordField.fill(decrypt(encryptedPassword));
    }

    public void clickLogin() {
        loginButton.click();
    }

    public void clickIconByText(String iconText) {
        page.getByText(iconText, new Page.GetByTextOptions().setExact(true)).click();
    }


    public boolean isProfilePageVisible() {
        return waitUntilElementDisplayed(booksSearchBox, 60);
    }

    public boolean isJewelLandingPageVisible() {
        return waitUntilElementDisplayed(autolyticsMessage, 60);
    }

    public boolean isInCorrectPasswordToastMsjDisplay() {
        return waitUntilElementDisplayed(incorrectPasswordToastMsg, 60);
    }

    public boolean isCredentialsRequiredToastMsgDisplayed(){
        return waitUntilElementDisplayed(credentialsRequiredMsg,60);
    }
    public void userLoggedinViaCredential(String userType) throws Exception {

            String username;
            String password;
            ElementUtils.clickOnButtonByName(DriverFactory.getPage(),"Sign In Via Credentials");
            switch (userType) {
                case "admin":
                    username=getProperty("adminUsername");
                    password=getProperty("adminPassword");
                    break;
                case "companyAdmin":
                    username=getProperty("companyAdmin_username");
                    password=getProperty("companyAdmin_password");
                    break;
                case "incorrectUser":
                    username="testabc";
                    password=getProperty("companyAdmin_password");
                    break;
                case "emptyUser":
                    username="";
                    password="";
                    break;
                case "normalUser":
                    username= getProperty("JewelUsername");
                    password=getProperty("JewelPassword");
                    break;
                default:
                    username=getProperty("JewelUsername");
                    password=getProperty("JewelPassword");
            }
            ElementUtils.fillValueInPlaceholder("Username",username);
            ElementUtils.fillValueInPlaceholder("Password", AESCrypto.decrypt(password));
            ElementUtils.clickOnButtonByName(DriverFactory.getPage(),"Sign In");
        }

    public void userLoggedInWithEmptyFields(String field) throws Exception {
        String username = "";
        String password = "";
        ElementUtils.clickOnButtonByName(DriverFactory.getPage(), "Sign In Via Credentials");
        if (field.contains("username")) {
            password = getProperty("companyAdmin_password");
        } else if (field.contains("password")) {
            username = getProperty("companyAdmin_username");
        }

        ElementUtils.fillValueInPlaceholder(DriverFactory.getPage(), "Username", username);
        ElementUtils.fillValueInPlaceholder(DriverFactory.getPage(), "Password", AESCrypto.decrypt(password));
        ElementUtils.clickOnButtonByName(DriverFactory.getPage(), "Sign In");
    }
    public static void logoutFromJewel(){
        Locator logoutIcon = DriverFactory.getPage().locator("svg[data-icon='arrow-right-from-bracket']");
        waitUntilElementDisplayed(logoutIcon,2000);
        logoutIcon.click();

    }
    public static boolean isUserLoggedOut(){

        return DriverFactory.getPage().getByLabel("Sign In Via Credentials").isVisible();
    }
}
