package bg.sofia.uni.fmi.mjt.foodanalyzer.deserializer;

import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.BrandedFoodItem;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodItem;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.NotBrandedFoodItems;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class FoodItemDeserializer implements JsonDeserializer<FoodItem> {

    @Override
    public FoodItem deserialize(JsonElement jsonElement, Type typeOfT,
                                JsonDeserializationContext context) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String description = jsonObject.get("description").getAsString();
        int id = jsonObject.get("fdcId").getAsInt();
        String type = jsonObject.get("dataType").getAsString();

        if (type.equals("Branded")) {
            String barcode = jsonObject.get("gtinUpc").getAsString();
            return new BrandedFoodItem(description, id, type, barcode);
        } else {
            return new NotBrandedFoodItems(description, id, type);
        }
    }
}
