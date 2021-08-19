package bg.sofia.uni.fmi.mjt.foodanalyzer.dto;

public class NotBrandedFoodItems extends FoodItem {

    public NotBrandedFoodItems(String description, int id, String dataType) {
        super(description, id, dataType);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean isBranden() {
        return false;
    }

    @Override
    public String getBarcode() {
        return "-1";
    }
}
