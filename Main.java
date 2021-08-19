package bg.sofia.uni.fmi.mjt.foodanalyzer;

public class Main {


    public static void main(String[] args) {
        FoodAnalyzerServer server = new FoodAnalyzerServer(6666, "D:\\cache");
        server.start();
    }
}
