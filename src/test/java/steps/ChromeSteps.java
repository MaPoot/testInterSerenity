package steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import po.chrome.ChromeHomePo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.thucydides.core.webdriver.ThucydidesWebDriverSupport.getDriver;

public class ChromeSteps {
    private final ChromeHomePo chromeHomePo = new ChromeHomePo();

    @Given("I open the google webpage")
    public void iOpenTheGoogleWebpage() {
        getDriver().get("https://www.google.com/");
    }

    @Then("I send a text to the search bar")
    public void iSendATextToTheSearchBar() {
        chromeHomePo.fillSearchBar();
    }
}
