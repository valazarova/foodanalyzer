package bg.sofia.uni.fmi.mjt.foodanalyzer;

import bg.sofia.uni.fmi.mjt.foodanalyzer.cache.CacheManager;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodItem;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodReport;
import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.BadRequestException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.FoodReportNotFoundException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.http.FoodAnalyzerHttpClient;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandExecutor {

    public static final String ERROR_MESSAGE = "Not enough arguments provided. ";
    public static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command";
    public static final String NOT_FOUND_MESSAGE = "No item with the given barcode exists in the cache ";
    public static final String GET_FOOD_REPORT = "get-food-report";
    public static final String GET_FOOD_BY_KEYWORD = "get-food";
    public static final String GET_FOOD_BY_BARCODE = "get-food-by-barcode";

    public static final String CODE_PARAMETER = "--code=";
    private FoodAnalyzerHttpClient httpClient;
    private BarcodeExtractor barcodeExtractor;
    private CacheManager cacheManager;

    public CommandExecutor(FoodAnalyzerHttpClient httpClient, BarcodeExtractor barcodeExtractor,
                           CacheManager cacheManager) {
        this.httpClient = httpClient;
        this.barcodeExtractor = barcodeExtractor;
        this.cacheManager = cacheManager;
    }

    public String respondToClient(String message) {

        String[] parameters = message.split("\\s+");
        if (parameters.length < 2) {
            return ERROR_MESSAGE;
        }
        return processCommand(parameters);
    }


    private String processCommand(String[] parameters) {
        String answer = "";
        String command = parameters[0];

        if (command.equals(GET_FOOD_BY_BARCODE)) {
            answer = getFoodByBarcode(parameters);
        } else if (command.equals(GET_FOOD_REPORT)) {
            answer = getFoodReport(parameters);
        } else if (command.equals(GET_FOOD_BY_KEYWORD)) {
            answer = getFoodBySearch(parameters);
        } else {
            answer = UNKNOWN_COMMAND_MESSAGE;
        }
        System.out.println(answer);
        return answer;

    }

    private String getFoodReport(String[] parameters) {

        String resultFromFile = cacheManager.readFoodReportFromFile(parameters[1]);
        if (!resultFromFile.isEmpty()) {
            System.out.println("found in file");
            return resultFromFile;
        }
        String id = parameters[1];
        FoodReport foodReport = null;

        try {
            foodReport = httpClient.getFoodReport(id);
        } catch (BadRequestException | FoodReportNotFoundException e) {
            return e.getMessage();
        }

        cacheManager.writeFoodReportToFile(id, foodReport);
        return foodReport.toString();
    }

    private String matchedFoodsToString(Set<FoodItem> matchedFoods) {
        return matchedFoods.stream().map(FoodItem::toString).collect(Collectors.joining("\n"));
    }

    private String getFoodBySearch(String[] parameters) {
        String keyWord = Arrays.stream(Arrays.copyOfRange(parameters, 1, parameters.length))
                .collect(Collectors.joining(" "));

        String resultFromFile = cacheManager.searchItemByKeyword(keyWord);

        if (!resultFromFile.isEmpty()) {
            System.out.println("found in file");
            return resultFromFile;
        }
        Set<FoodItem> matchedFoods = null;
        try {
            matchedFoods = httpClient.getFoodItemBySearch(keyWord);
        } catch (BadRequestException | FoodReportNotFoundException e) {
            return "Bad input parameters.";
        }
        cacheManager.writeFoodItemsToFile(keyWord, matchedFoods);
        return matchedFoodsToString(matchedFoods);
    }

    private String getFoodByBarcode(String[] parameters) {
        String barcode = getBarcode(parameters);
        if (barcode == null) {
            return "Wrong barcode-image location given ";
        }
        String answer = cacheManager.searchItemByBarcode(barcode);
        if (answer.isEmpty()) {
            answer = NOT_FOUND_MESSAGE;
        }
        return answer;
    }

    private String getBarcode(String[] parameters) {

        String barcode;
        if (parameters.length == 3) {
            barcode = parameters[0].contains(CODE_PARAMETER) ? fetchBarcodeInfo(parameters[1])
                    : fetchBarcodeInfo(parameters[2]);

        } else {
            if (parameters[1].contains(CODE_PARAMETER)) {
                barcode = fetchBarcodeInfo(parameters[1]);
            } else {
                String pathToImage = fetchBarcodeInfo(parameters[1]);
                barcode = barcodeExtractor.decodeImage(pathToImage);
            }
        }
        return barcode;
    }

    private String fetchBarcodeInfo(String parameter) {
        return parameter.split("=")[1];
    }

}
