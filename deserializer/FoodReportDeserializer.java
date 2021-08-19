package bg.sofia.uni.fmi.mjt.foodanalyzer.deserializer;

import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodReport;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.NutritionalValues;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class FoodReportDeserializer implements JsonDeserializer<FoodReport> {

    @Override
    public FoodReport deserialize(JsonElement jsonElement, Type typeOfT,
                                  JsonDeserializationContext context) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("description").getAsString();
        String ingredients = null;
        NutritionalValues nutritionalValues = null;

        if (jsonObject.has("ingredients")) {
            ingredients = jsonObject.get("ingredients").getAsString();
        }

        if (jsonObject.has("labelNutrients")) {
            JsonObject nutrients = jsonObject.get("labelNutrients").getAsJsonObject();
            nutritionalValues = intializeNutrients(nutrients);
        }

        return new FoodReport(nutritionalValues, name, ingredients);
    }

    private NutritionalValues intializeNutrients(JsonObject nutrients) {
        double protein = -1;
        double calories = -1;
        double fat = -1;
        double carbohydrates = -1;
        double fiber = -1;

        if (nutrients.has("protein")) {
            protein = nutrients.getAsJsonObject("protein").get("value").getAsDouble();
        }
        if (nutrients.has("calories")) {
            calories = nutrients.getAsJsonObject("calories").get("value").getAsDouble();
        }
        if (nutrients.has("fat")) {
            fat = nutrients.getAsJsonObject("fat").get("value").getAsDouble();
        }
        if (nutrients.has("carbohydrates")) {
            carbohydrates = nutrients.getAsJsonObject("carbohydrates").get("value").getAsDouble();
        }
        if (nutrients.has("fiber")) {
            fiber = nutrients.getAsJsonObject("fiber").get("value").getAsDouble();
        }
        return new NutritionalValues(calories, protein, fat, carbohydrates, fiber);
    }
}