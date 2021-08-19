package bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions;

public class FoodReportNotFoundException extends Exception {
    public FoodReportNotFoundException() {
        super("The food report couldn't be found.Non existing id provided");
    }
}
