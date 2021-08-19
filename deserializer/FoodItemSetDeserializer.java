package bg.sofia.uni.fmi.mjt.foodanalyzer.deserializer;

import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodItem;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import java.util.HashSet;
import java.util.Set;

public class FoodItemSetDeserializer implements JsonDeserializer<Set<FoodItem>> {
    @Override
    public Set<FoodItem> deserialize(JsonElement jsonElement, Type typeOfT,
                                     JsonDeserializationContext context) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Set<FoodItem> foodItemSet = new HashSet<>();
        JsonArray foodItems = jsonObject.get("foods").getAsJsonArray();
        for (int i = 0; i < foodItems.size(); i++) {

            FoodItem item = getDeserializedItem(foodItems.get(i).getAsJsonObject());
            foodItemSet.add(item);
        }
        return foodItemSet;

    }

    private FoodItem getDeserializedItem(JsonObject foodItem) {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(FoodItem.class, new FoodItemDeserializer());
        FoodItem deserializedItem = gsonBuilder.create().fromJson(foodItem, FoodItem.class);
        return deserializedItem;

    }
}
