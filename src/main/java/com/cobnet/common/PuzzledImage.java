package com.cobnet.common;

import com.cobnet.spring.boot.dto.Base64Image;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class PuzzledImage implements Serializable {

    private final int jigsawWidth;

    private final int jigsawHeight;

    private final int circleR;

    private final int padding;

    @JsonIgnore
    private transient final int[][] template;

    @JsonIgnore
    private transient final BufferedImage image;

    @JsonIgnore
    private transient final BufferedImage jigsawImage;

    private final int jigsawX;

    private final int jigsawY;

    public PuzzledImage(BufferedImage image, int jigsawWidth, int jigsawHeight, int circleR, int padding) throws IOException {

        this.jigsawWidth = jigsawWidth;
        this.jigsawHeight = jigsawHeight;
        this.circleR = circleR;
        this.padding = padding;
        this.template = generateImage();
        this.image = image;
        this.jigsawImage = new BufferedImage(jigsawWidth, jigsawHeight, BufferedImage.TYPE_4BYTE_ABGR);
        Random random = new Random();
        this.jigsawX = random.nextInt(image.getWidth() - 2 * jigsawWidth) + jigsawWidth;
        this.jigsawY = random.nextInt(image.getHeight() - jigsawHeight);
        render();
    }

    public int getJigsawWidth() {
        return jigsawWidth;
    }

    public int getJigsawHeight() {
        return jigsawHeight;
    }

    public int getCircleR() {
        return circleR;
    }

    public int getPadding() {
        return padding;
    }

    @JsonIgnore
    public int[][] getTemplate() {
        return template;
    }

    @JsonIgnore
    public BufferedImage getImage() {
        return image;
    }

    @JsonIgnore
    public BufferedImage getJigsawImage() {
        return jigsawImage;
    }

    public int getJigsawX() {
        return jigsawX;
    }

    public int getJigsawY() {
        return jigsawY;
    }

    @JsonIgnore
    private int[][] generateImage() {

        int[][] key = new int[jigsawWidth][jigsawHeight];

        double x2 = jigsawWidth - circleR;

        double location = circleR + Math.random() * (jigsawWidth - 3 * circleR - padding);
        double circle = Math.pow(circleR, 2);

        double x = jigsawWidth - circleR - padding;
        double y = jigsawHeight - circleR - padding;

        for (int i = 0; i < jigsawWidth; i++) {

            for (int j = 0; j < jigsawHeight; j++) {

                double d2 = Math.pow(j - 2, 2) + Math.pow(i - location, 2);
                double d3 = Math.pow(i - x2, 2) + Math.pow(j - location, 2);

                if ((j <= y && d2 < circle) || (i >= x && d3 > circle)) {
                    key[i][j] = 0;
                }  else {
                    key[i][j] = 1;
                }
            }
        }

        return key;
    }

    private void render() throws IOException {
        //TODO random circle position
        int[][] martrix = new int[3][3];
        int[] values = new int[9];

        for (int i = 0; i < jigsawWidth; i++) {

            for (int j = 0; j < jigsawHeight; j++) {

                int rgb = template[i][j];

                int rgb_ori = image.getRGB(jigsawX + i, jigsawY + j);

                if (rgb == 1) {

                    jigsawImage.setRGB(i, j, rgb_ori);
                    readPixel(jigsawX + i, jigsawY + j, values);
                    fillMatrix(martrix, values);
                    image.setRGB(jigsawX + i, jigsawY + j, avgMatrix(martrix));

                } else {
                    jigsawImage.setRGB(i, j, rgb_ori & 0x00ffffff);
                }
            }
        }
    }

    private void readPixel(int x, int y, int[] pixels) {

        int xStart = x - 1;
        int yStart = y - 1;
        int current = 0;

        for (int i = xStart; i < 3 + xStart; i++) {

            for (int j = yStart; j < 3 + yStart; j++) {

                int tx = i;

                if (tx < 0) {

                    tx = -tx;

                } else if (tx >= image.getWidth()) {

                    tx = x;
                }

                int ty = j;

                if (ty < 0) {

                    ty = -ty;

                } else if (ty >= image.getHeight()) {

                    ty = y;
                }
                pixels[current++] = image.getRGB(tx, ty);

            }
        }
    }

    private void fillMatrix(int[][] matrix, int[] values) {

        int filled = 0;

        for (int i = 0; i < matrix.length; i++) {

            int[] x = matrix[i];

            for (int j = 0; j < x.length; j++) {

                x[j] = values[filled++];
            }
        }
    }

    private static int avgMatrix(int[][] matrix) {

        int r = 0;
        int g = 0;
        int b = 0;

        for (int i = 0; i < matrix.length; i++) {

            int[] x = matrix[i];

            for (int j = 0; j < x.length; j++) {

                if (j == 1) {

                    continue;
                }

                Color c = new Color(x[j]);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }

        return new Color(r / 8, g / 8, b / 8).getRGB();
    }
}
