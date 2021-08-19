package bg.sofia.uni.fmi.mjt.foodanalyzer.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public abstract class FoodItem {

    public static final String FOOD_ITEM_STRING = "Description %s , id: %d";


    private String description;
    private int id;
    private String dataType;


    public FoodItem(String description, int id, String dataType) {
        this.description = description;
        this.id = id;
        this.dataType = dataType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof FoodItem)) {
            return false;
        }

        FoodItem other = (FoodItem) obj;

        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format(FOOD_ITEM_STRING, description, id, dataType);
    }

    public abstract boolean isBranden();

    public abstract String getBarcode();
}



