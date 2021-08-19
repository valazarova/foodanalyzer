package bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions;

public class BadRequestException extends Exception {
    public BadRequestException() {
        super("Bad input parameters");
    }
}
