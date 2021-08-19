package bg.sofia.uni.fmi.mjt.foodanalyzer.cache;

import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Set;

public class FoodItemsManager {

    private static final String FILE_DELIMITER = ",,";
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    FoodItemsManager(Writer writer, Reader reader) {
        this.bufferedReader = new BufferedReader(reader);
        this.bufferedWriter = new BufferedWriter(writer);
    }


    public void writeFoodItemsToFile(String keyWord, Set<FoodItem> foodItemSet) {
        if (foodItemSet.isEmpty()) {
            writeNotFoundMessage(bufferedWriter, keyWord);
        }
        for (FoodItem foodItem : foodItemSet) {
            writeItemToFile(bufferedWriter, keyWord, foodItem);
        }

    }

    public String searchItemByKeyword(String keyWord) {
        String matchedItems = "";
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                matchedItems += searchKeywordInLine(line, keyWord);
            }
        } catch (IOException exc) {
            throw new RuntimeException("An error occurred while reading from file ", exc);
        }
        return matchedItems;
    }

    public String searchItemByBarcode(String barcode) {
        String match = "";
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                match = searchBarcodeInLine(barcode, line);
                if (!match.isEmpty()) {
                    return match;
                }
            }
        } catch (IOException exc) {
            throw new RuntimeException("An error occurred while reading from file ", exc);
        }
        return match;
    }

    private String searchKeywordInLine(String line, String keyWord) {
        String[] parameters = line.split(FILE_DELIMITER);
        String name = parameters[0];
        String match = "";
        if (name.equals(keyWord)) {
            String information = parameters[2];
            match = information + System.lineSeparator();
        }
        return match;
    }

    private String searchBarcodeInLine(String barcode, String line) {
        String[] parameters = line.split(FILE_DELIMITER);
        String currentBarcode = parameters[1];
        String match = "";
        if (barcode.equals(currentBarcode)) {
            String information = parameters[2];
            match += information;
        }
        return match;
    }

    private void writeNotFoundMessage(BufferedWriter bufferedWriter, String keyWord) {
        try {
            bufferedWriter.write(keyWord);
            bufferedWriter.write(FILE_DELIMITER);
            bufferedWriter.write("-1");
            bufferedWriter.write(FILE_DELIMITER);
            bufferedWriter.write("No such item could be found");
            bufferedWriter.write(System.lineSeparator());
            bufferedWriter.flush();

        } catch (IOException exc) {
            throw new RuntimeException("An error occurred while writing to file ", exc);
        }
    }

    private void writeItemToFile(BufferedWriter bufferedWriter, String keyWord, FoodItem foodItem) {
        try {
            bufferedWriter.write(keyWord);
            bufferedWriter.write(FILE_DELIMITER);
            bufferedWriter.write(foodItem.getBarcode());
            bufferedWriter.write(FILE_DELIMITER);
            bufferedWriter.write(foodItem.toString());
            bufferedWriter.write(System.lineSeparator());
            bufferedWriter.flush();

        } catch (IOException exc) {
            throw new RuntimeException("An error occurred while writing to file ", exc);
        }
    }

}
