package po.chrome;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static net.thucydides.core.webdriver.ThucydidesWebDriverSupport.getDriver;

public class ChromeHomePo {
    @FindBy(xpath = "//textArea[@title=\"Buscar\"]")
    private WebElement searchBar;

    private final WebDriverWait wait;

    public ChromeHomePo() {
        PageFactory.initElements(getDriver(), this);
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
    }

    public void fillSearchBar(){
        wait.until(ExpectedConditions.visibilityOf(searchBar)).sendKeys("Hello");
    }
}
