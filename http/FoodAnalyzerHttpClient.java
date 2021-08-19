package bg.sofia.uni.fmi.mjt.foodanalyzer.http;

import bg.sofia.uni.fmi.mjt.foodanalyzer.deserializer.FoodItemSetDeserializer;
import bg.sofia.uni.fmi.mjt.foodanalyzer.deserializer.FoodReportDeserializer;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodItem;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodReport;
import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.BadRequestException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.FoodReportNotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class FoodAnalyzerHttpClient {

    public static final String URL_DELIMITER = "%20";
    public static final String API_KEY = "uIsfMKUqGhB8YZvzSQLg2qU8VGtMdXsyCMOKseRW";
    private static final String SEARCH_ADDRESS
            = "https://api.nal.usda.gov/fdc/v1/foods/search?query=%s&requireAllWords=true&api_key=%s";

    public static final String FOOD_REPORT_ADDRESS =
            "https://api.nal.usda.gov/fdc/v1/food/%s?api_key=%s";

    private final HttpClient client;

    public FoodAnalyzerHttpClient(HttpClient client) {
        this.client = client;
    }

    public Set<FoodItem> getFoodItemBySearch(String keyWord) throws BadRequestException, FoodReportNotFoundException {

        URI uri = createURI(SEARCH_ADDRESS, keyWord);
        String jsonResponse = sendRequest(uri);
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonResponse);
        Set<FoodItem> matchedFoods = deserializedSet(jsonObject);
        return matchedFoods;
    }


    public FoodReport getFoodReport(String id) throws BadRequestException, FoodReportNotFoundException {

        URI uri = createURI(FOOD_REPORT_ADDRESS, id);
        String jsonResponse = sendRequest(uri);
        FoodReport foodReport = deserializedFoodReport(jsonResponse);

        return foodReport;
    }


    private String formatFoodItem(String foodItem) {
        return Arrays.stream(foodItem.split(" ")).collect(Collectors.joining(URL_DELIMITER));
    }

    private Set<FoodItem> deserializedSet(JsonObject object) {

        Type foodItemSet = new TypeToken<Set<FoodItem>>() {
        }.getType();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(foodItemSet, new FoodItemSetDeserializer());
        Gson customGson = gsonBuilder.create();
        return customGson.fromJson(object, foodItemSet);
    }

    private FoodReport deserializedFoodReport(String jsonString) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(FoodReport.class, new FoodReportDeserializer());
        Gson customGson = gsonBuilder.create();
        return customGson.fromJson(jsonString, FoodReport.class);
    }

    private URI createURI(String address, String keyword) {
        try {
            String formattedKeyWord = formatFoodItem(keyword);
            String uri = String.format(address, formattedKeyWord, API_KEY);
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("An error occurred while creating an instance of the URI object", e);
        }
    }

    private String sendRequest(URI uri) throws BadRequestException, FoodReportNotFoundException {
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

        String jsonResponse = null;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                throw new FoodReportNotFoundException();
            }
            if (response.statusCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                throw new BadRequestException();
            }
            jsonResponse = response.body();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("An error occurred while sending a request to the API", e);
        }
        return jsonResponse;
    }

}
