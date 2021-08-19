package bg.sofia.uni.fmi.mjt.foodanalyzer.dto;

import java.util.Objects;

public class FoodReport {

    public static final String FOOD_REPORT_STRING = "Food report of product \" %s \" -> ";
    public static final String INGREDIENTS_STRING = "Ingredients: [ %s ]";
    public static final String NO_INGREDIENTS_MESSAGE = "No information available about ingredients.";
    public static final String NO_NUTRIENTS_MESSAGE = "No information available about nutritional values.";


    private NutritionalValues nutritionalValues;
    private String name;
    private String ingredients;


    public FoodReport(NutritionalValues nutritionalValues, String name, String ingredients) {
        this.nutritionalValues = nutritionalValues;
        this.name = name;
        this.ingredients = ingredients;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof FoodReport)) {
            return false;
        }

        FoodReport other = (FoodReport) obj;

        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ingredients);
    }

    @Override
    public String toString() {
        return String.format(FOOD_REPORT_STRING, name) + nutrientsString(this.nutritionalValues) + ","
                + ingredientsToString(this.ingredients);
    }

    private String ingredientsToString(String ingredients) {
        if (ingredients == null) {
            return NO_INGREDIENTS_MESSAGE;
        }
        return String.format(INGREDIENTS_STRING, ingredients.toLowerCase());
    }

    private String nutrientsString(NutritionalValues nutritionalValues) {
        if (nutritionalValues == null) {
            return NO_NUTRIENTS_MESSAGE;
        }
        return nutritionalValues.formatNutrients();
    }

}