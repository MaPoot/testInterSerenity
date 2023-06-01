package pojo.CleaningSessions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static net.thucydides.core.webdriver.ThucydidesWebDriverSupport.getDriver;

public class PokeAPIPage {
    @FindBy(xpath = "//input[@id=\"url-input\"]")
    private WebElement text;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement submit;

    private WebDriverWait wait;

    public PokeAPIPage() {
        PageFactory.initElements(getDriver(), this);
        this.wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
    }

    public void sendText(String textSrt) {
        wait.until(ExpectedConditions.visibilityOf(text)).clear();
        this.text.sendKeys(textSrt);
    }

    public void clickSubmit() {
        this.submit.click();
    }
}
