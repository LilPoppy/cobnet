package com.cobnet.common;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage toGrayscale(BufferedImage image) {

        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D graphic = result.createGraphics();
        graphic.drawImage(image, 0, 0, Color.WHITE, null);

        for (int i = 0; i < result.getHeight(); i++) {

            for (int j = 0; j < result.getWidth(); j++) {

                Color c = new Color(result.getRGB(j, i));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                Color newColor = new Color(red + green + blue, red + green + blue, red + green + blue);
                result.setRGB(j, i, newColor.getRGB());
            }
        }

        graphic.dispose();

        return result;
    }

    public static int getLuminance(BufferedImage image) {

        Color color = averageColor(image, 0, 0, image.getWidth(), image.getHeight());

        return (77 * ((color.getRGB() >> 16) & 255) + 150 * ((color.getRGB() >> 8) & 255) + 29  * ((color.getRGB()) & 255)) >> 8;
    }

    public static Color averageColor(BufferedImage image, int x0, int y0, int width, int height) {

        int x1 = x0 + width;
        int y1 = y0 + height;
        long sumr = 0, sumg = 0, sumb = 0;
        for (int x = x0; x < x1; x++) {

            for (int y = y0; y < y1; y++) {

                Color pixel = new Color(image.getRGB(x, y));
                sumr += pixel.getRed();
                sumg += pixel.getGreen();
                sumb += pixel.getBlue();
            }
        }
        int total = width * height;

        return new Color((float)(sumr / total) / 255, (float)(sumg / total) / 255, (float)(sumb / total) / 255);
    }

    public static float[] getImageAverageHSV(BufferedImage image) {

        Color color = ImageUtils.averageColor(image, 0, 0, image.getWidth(), image.getHeight());

        return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    }

}
