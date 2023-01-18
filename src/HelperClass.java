import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class HelperClass {
    public static String formatTime(long secs) {
        return String.format("%2d:%02d", (secs % 3600) / 60, secs % 60);
    }

    public static String formatTotalTime(long secs) {
        return String.format("%2d min %2d sec", (secs % 3600) / 60, secs % 60);
    }

    public static String getAvgColorHex(Image image) {
        // Create a PixelReader for the Image
        PixelReader pixelReader = image.getPixelReader();

        // Get the width and height of the image
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Initialize the total red, green, and blue values to 0
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;

        // Iterate over all pixels
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the color of the pixel
                int pixelColor = pixelReader.getArgb(x, y);

                // Extract the red, green, and blue values
                int red = (pixelColor >> 16) & 0xff;
                int green = (pixelColor >> 8) & 0xff;
                int blue = pixelColor & 0xff;

                // Add the values to the total
                totalRed += red;
                totalGreen += green;
                totalBlue += blue;
            }
        }

        // Calculate the average red, green, and blue values in the range of 0.0 to 1.0
        double numPixels = width * height;
        double avgRed = totalRed / (255.0 * numPixels);
        double avgGreen = totalGreen / (255.0 * numPixels);
        double avgBlue = totalBlue / (255.0 * numPixels);

        // Convert the Color object to a hex string
        String hex = String.format("#%02x%02x%02x", (int) (avgRed * 255), (int) (avgGreen * 255), (int) (avgBlue * 255));

        Color color = Color.web(hex);
        if (color.getBrightness() <= Color.BLACK.getBrightness()) {
            return "#323232";
        }
        return hex;
    }

}
