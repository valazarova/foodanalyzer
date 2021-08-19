package bg.sofia.uni.fmi.mjt.foodanalyzer.dto;

public class NutritionalValues {
    public static final String NUTRIENTS_STRING = " calories : %s, protein: %s ,fat: %s , carbs: %s , fiber %s ";
    private double calories;
    private double protein;
    private double fat;
    private double carbs;
    private double fiber;

    public NutritionalValues(double calories, double protein, double fat, double carbs,
                             double fiber) {
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.fiber = fiber;
    }

    public String formatNutrients() {
        return String.format(NUTRIENTS_STRING, calories, protein, fat, carbs, fiber);
    }
}