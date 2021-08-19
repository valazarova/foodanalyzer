package bg.sofia.uni.fmi.mjt.foodanalyzer.dto;


public class BrandedFoodItem extends FoodItem {

    private String barcode;

    public BrandedFoodItem(String description, int id, String dataType, String barcode) {
        super(description, id, dataType);
        this.barcode = barcode;
    }


    @Override
    public String toString() {
        return super.toString() + " , barcode: " + barcode;
    }

    @Override
    public boolean isBranden() {
        return true;
    }

    @Override
    public String getBarcode() {
        return this.barcode;
    }
}
