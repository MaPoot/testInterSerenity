package steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpMessage;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.remote.http.*;
import pojo.CleaningSessions.CleaningSessionErrorResponse;
import pojo.CleaningSessions.CleaningSessionResponse;
import pojo.CleaningSessions.CleaningSessions;
import pojo.CleaningSessions.PokeAPIPage;
import utils.APIOperations;

import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.thucydides.core.webdriver.ThucydidesWebDriverSupport.getDriver;
import static org.openqa.selenium.remote.http.HttpMethod.GET;

public class HoverTestSteps extends BaseSteps {
    private final CleaningSessions cleaningSession = new CleaningSessions();
    private CleaningSessionResponse cleaningSessionResponse;
    private Response response;

    private final PokeAPIPage poke = new PokeAPIPage();

    @Given("A room of dimension")
    public void aRoomOfDimension(DataTable roomDimension) {
        Map<String, String> roomDimensionMap = roomDimension.transpose().asMap();

        Set<String> keys = roomDimensionMap.keySet();
        List<Integer> dimension = new ArrayList<>();

        keys.forEach(key -> {
            if(roomDimensionMap.get(key) != null){
                if(roomDimensionMap.get(key).equals("null")){
                    dimension.add(null);
                }else{
                    dimension.add(Integer.parseInt(roomDimensionMap.get(key)));
                }
            }
        });

        cleaningSession.setRoomSize(dimension);
    }

    @Then("Set the initial position of the Hover to")
    public void setTheInitialPositionOfTheHoverTo(DataTable initialPosition) {
        Map<String, String> initialPositionMap = initialPosition.transpose().asMap();

        Set<String> keys = initialPositionMap.keySet();
        List<Integer> coordinates = new ArrayList<>();

        keys.forEach(key -> {
            if (initialPositionMap.get(key) != null) {
                if (initialPositionMap.get(key).equals("null")) {
                    coordinates.add(null);
                } else {
                    coordinates.add(Integer.parseInt(initialPositionMap.get(key)));
                }
            }
        });

        cleaningSession.setCoordinates(coordinates);
    }


    @Then("Set the dirt patches")
    public void setTheDirtPatches(DataTable dirtPatches) {
        Map<String, List<String>> dirtPatchesMap = dirtPatches.transpose().asMap((Type) String.class, List.class);
        Set<String> keys = dirtPatchesMap.keySet();

        List<List<Integer>> dirtPatchesList = new ArrayList<>();
        for (int i = 0; i < dirtPatches.height() - 1; i++) {
            List<Integer> coordinates = new ArrayList<>();
            for (String key : keys) {
                coordinates.add(Integer.parseInt(dirtPatchesMap.get(key).get(i)));
            }

            dirtPatchesList.add(coordinates);
        }

        cleaningSession.setPatches(dirtPatchesList);
    }


    @Then("I print the Cleaning Session")
    public void iPrintTheCleaningSession() throws JsonProcessingException {
        System.out.println(cleaningSession);
        String body = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cleaningSession);

        System.out.println(body);
    }

    @Then("I set the instructions {string}")
    public void iSetTheInstructions(String instructions) {
        cleaningSession.setInstructions(instructions);
    }

    @Then("Send a POST request to {string}")
    public void sendAPOSTRequestTo(String api) throws JsonProcessingException {
        String endpoint = variables.getProperty("api.hover.url") + api;
        String body = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cleaningSession);

        System.out.println(body);
        response = APIOperations.post(endpoint, body);
    }

    @Then("Validate the response code is {int}")
    public void validateTheResponseCodeIs(int responseCode) {
        Assert.assertEquals(responseCode, response.getStatusCode());
    }

    @Then("Validate the response body")
    public void validateTheResponseBody(Map<String, String> validations) throws JsonProcessingException {
        cleaningSessionResponse = mapper.readValue(response.asString(), CleaningSessionResponse.class);

        validations.keySet().forEach(key -> {
            switch (key) {
                case "patches" ->
                        Assert.assertEquals(Integer.parseInt(validations.get("patches")), cleaningSessionResponse.getPatches());
                case "coords" -> {
                    List<Integer> expected = Arrays.stream(validations.get("coords").replaceAll("[\\[\\]]", "").split(","))

                            .map(s -> Integer.parseInt(s.trim())).toList();
                    Assert.assertEquals(expected, cleaningSessionResponse.getCoordinates());
                }
            }
        });

    }

    @Then("Validate Error Response")
    public void validateErrorResponse(Map<String, String> validations) throws JsonProcessingException {
        mapper.readValue(response.asString(), CleaningSessionErrorResponse.class);
        JsonPath jsonResponse = response.jsonPath();

        validations.keySet().forEach(key -> {
            String actual = jsonResponse.get(key) == null ? null : jsonResponse.getString(key);
            String expected = validations.get(key).equals("null") ? null : validations.get(key);
            Assert.assertEquals(expected, actual);
        });
    }

    @Given("This is a broken step")
    public void thisIsABrokenStep() {
        Integer.parseInt("Hello");
    }

    @Given("I open the pokeApi Page")
    public void iOpenThePokeApiPage() {
        getDriver().get("https://pokeapi.co/");
    }

    @Then("Just wait {int}")
    public void justWait(int time) throws InterruptedException {
        Thread.sleep(time);
    }

    @Then("Send text {string}")
    public void sendText(String text) {
        poke.sendText(text);
    }

    @Then("Click submit")
    public void clickSubmit() {
        Route route = Route.matching(req -> GET == req.getMethod() && req.getUri().endsWith("/pikachu")).to(
                () -> req -> {
                    HttpResponse res = NetworkInterceptor.PROCEED_WITH_REQUEST;

                    System.out.println(res.getStatus());
                    System.out.println(res);
                    System.out.println(req);
                    return res;
                }
        );

        try (NetworkInterceptor interceptor = new NetworkInterceptor(getDriver(), route)) {
            // Your code here;
            poke.clickSubmit();
        }
    }
}