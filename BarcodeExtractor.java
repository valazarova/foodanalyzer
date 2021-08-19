package bg.sofia.uni.fmi.mjt.foodanalyzer;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.oned.UPCAReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BarcodeExtractor {

    public String decodeImage(String path) {
        try {
            UPCAReader reader = new UPCAReader();
            BufferedImage image = ImageIO.read(new File(path));
            BinaryBitmap bitmap = convertImageToBinaryBitmap(image);
            Result result = reader.decode(bitmap);
            return result.getText();
        } catch (ReaderException | IOException e) {
            throw new RuntimeException("Error occurred while decoding image for barcode", e);
        }
    }

    private BinaryBitmap convertImageToBinaryBitmap(BufferedImage image) {
        int[] pixels = image.getRGB(0, 0,
                image.getWidth(), image.getHeight(),
                null, 0, image.getWidth());
        RGBLuminanceSource source = new RGBLuminanceSource(image.getWidth(),
                image.getHeight(),
                pixels);
        return new BinaryBitmap(new HybridBinarizer(source));
    }

}