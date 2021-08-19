package bg.sofia.uni.fmi.mjt.foodanalyzer.cache;

import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodReport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class FoodReportsManager {
    private static final String FILE_DELIMITER = ",,";
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    FoodReportsManager(Writer writer, Reader reader) {
        this.bufferedReader = new BufferedReader(reader);
        this.bufferedWriter = new BufferedWriter(writer);
    }

    public void writeFoodReportToFile(String id, FoodReport foodReport) {

        try {
            bufferedWriter.write(id);
            bufferedWriter.write(FILE_DELIMITER);
            bufferedWriter.write(foodReport.toString());
            bufferedWriter.write(System.lineSeparator());
            bufferedWriter.flush();

        } catch (IOException exc) {
            throw new RuntimeException("An error occurred while writing to file", exc);
        }
    }

    public String readFoodReportFromFile(String id) {
        String report = "";
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parameters = line.split(FILE_DELIMITER);
                if (parameters[0].equals(id)) {
                    report = parameters[1];
                }
            }
        } catch (IOException exc) {
            throw new RuntimeException("An error occurred while reading from file ", exc);
        }
        return report;
    }
}
