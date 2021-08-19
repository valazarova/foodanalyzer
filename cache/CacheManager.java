package bg.sofia.uni.fmi.mjt.foodanalyzer.cache;

import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodItem;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodReport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;

public class CacheManager {

    private static final String SEPARATOR = File.separator;
    private final Path pathToDir;
    private final Path pathToFoodReports;
    private final Path pathToFoodItems;
    private final FoodItemsManager foodItemsManager;
    private final FoodReportsManager foodReportsManager;


    public CacheManager(String dirName, String foodReportsFileName, String foodItemsFileName) {

        pathToDir = Path.of(dirName);
        pathToFoodReports = Path.of(dirName + SEPARATOR + foodReportsFileName);
        pathToFoodItems = Path.of(dirName + SEPARATOR + foodItemsFileName);

        boolean dirAlreadyExists = Files.exists(pathToDir);

        if (!dirAlreadyExists) {
            createDirAndFiles();
        } else {
            createFileIfAbsent(pathToFoodItems);
            createFileIfAbsent(pathToFoodReports);
        }

        try {
            BufferedReader foodItemsBr = Files.newBufferedReader(pathToFoodItems);
            BufferedReader foodReportsBr = Files.newBufferedReader(pathToFoodReports);
            BufferedWriter foodItemsBw = Files.newBufferedWriter(pathToFoodItems, StandardOpenOption.APPEND);
            BufferedWriter foodReportsBw = Files.newBufferedWriter(pathToFoodReports, StandardOpenOption.APPEND);
            foodItemsManager = new FoodItemsManager(foodItemsBw, foodItemsBr);
            foodReportsManager = new FoodReportsManager(foodReportsBw, foodReportsBr);

        } catch (IOException exc) {
            throw new RuntimeException("An error occurred while creating cache's readers/writers", exc);
        }
    }

    public String readFoodReportFromFile(String id) {
        return foodReportsManager.readFoodReportFromFile(id);
    }

    public void writeFoodReportToFile(String id, FoodReport foodReport) {
        foodReportsManager.writeFoodReportToFile(id, foodReport);
    }


    public void writeFoodItemsToFile(String keyWord, Set<FoodItem> foodItemSet) {
        foodItemsManager.writeFoodItemsToFile(keyWord, foodItemSet);
    }

    public String searchItemByKeyword(String keyWord) {
        return foodItemsManager.searchItemByKeyword(keyWord);
    }

    public String searchItemByBarcode(String barcode) {
        return foodItemsManager.searchItemByBarcode(barcode);
    }

    private void createDirAndFiles() {
        try {
            Files.createDirectory(pathToDir);
            Files.createFile(pathToFoodReports);
            Files.createFile(pathToFoodItems);
        } catch (IOException exception) {
            throw new RuntimeException("An error occurred while creation of files", exception);
        }
    }

    private void createFileIfAbsent(Path pathToFile) {
        try {
            boolean fileAlreadyExists = Files.exists(pathToFile);
            if (!fileAlreadyExists) {
                Files.createFile(pathToFile);
            }
        } catch (IOException exception) {
            throw new RuntimeException("An error occurred while creation of files", exception);
        }
    }

}
