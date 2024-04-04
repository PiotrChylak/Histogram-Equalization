
package org.example;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.lang.Thread;

public class ImageTransformation {
    private BufferedImage image;
    private int height;
    private int width;

    public ImageTransformation() {
    }

    public void loadImage(String path) throws IOException {
        File imageFile = new File(path);
        this.image = ImageIO.read(imageFile);
        this.height = this.image.getHeight();
        this.width = this.image.getWidth();
    }

    public void saveImage(String path) throws IOException {
        File imageFile = new File(path);
        ImageIO.write(this.image, "png", imageFile);
    }

    public void changeBrightness(int value) {
        int width = this.image.getWidth();
        int height = this.image.getHeight();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                Color color = new Color(this.image.getRGB(j, i));
                int R = color.getRed() + value;
                int G = color.getGreen() + value;
                int B = color.getBlue() + value;
                R = Math.max(0, Math.min(255, R));
                G = Math.max(0, Math.min(255, G));
                B = Math.max(0, Math.min(255, B));

                Color newColor = new Color(R, G, B);
                this.image.setRGB(j, i, newColor.getRGB());
            }
        }
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Metoda changeBrightness (bez podzielenia na wątki) wykonana w czasie: " + executionTime + " ms");
    }

    public void cBwThreads(int value) throws InterruptedException {
        int coresNumber = Runtime.getRuntime().availableProcessors();
        int chunkNumber = this.height / coresNumber;
        Thread[] threads = new Thread[coresNumber];

        int i;
        for (i = 0; i < coresNumber; ++i) {
            int threadNumber = i;
            threads[i] = new Thread(() -> {
                int start = threadNumber * chunkNumber;
                int end;
                if (threadNumber == coresNumber - 1) {
                    end = height;
                } else {
                    end = start + chunkNumber;
                }

                for (int j = start; j < end; j++) {
                    for (int k = 0; k < this.width; k++) {
                        Color color = new Color(this.image.getRGB(k, j));

                        int R = color.getRed() + value;
                        int G = color.getGreen() + value;
                        int B = color.getBlue() + value;
                        R = Math.max(0, Math.min(255, R));
                        G = Math.max(0, Math.min(255, G));
                        B = Math.max(0, Math.min(255, B));

                        Color newColor = new Color(R, G, B);
                        this.image.setRGB(k, j, newColor.getRGB());
                    }
                }
            });
        }

        long startTime = System.currentTimeMillis();

        for (Thread x : threads) {
            x.start();
        }

        Thread[] allThreads = threads;
        int threadsLength = threads.length;

        for (int tI = 0; tI < threadsLength; ++tI) {
            Thread thread = allThreads[tI];
            thread.join();
        }
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Metoda cBwThreads (z wykorzystaniem wątków) wykonana w czasie: " + executionTime + " ms");
    }

    public int[] calculateHistogram(int color) {
        int width = this.image.getWidth();
        int height = this.image.getHeight();
        int[] histogram = new int[256];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color rgb = new Color(this.image.getRGB(j, i));
                int value = 0;
                switch (color) {
                    case 0:
                        value = rgb.getRed();
                        break;
                    case 1:
                        value = rgb.getGreen();
                        break;
                    case 2:
                        value = rgb.getBlue();
                }
                int frequency = histogram[value]++;
            }
        }
        return histogram;
    }

    public int[] generateCumHistogram(int[] histogram){
        int pixels = this.height * this.width;
        int[] cumHistogram = new int[256];
        cumHistogram[0] = histogram[0];

        for(int i = 1; i < 256; i++){
            cumHistogram[i] = cumHistogram[i - 1] + histogram[i];
        }

        int[] normHistogram = new int[256];

        for(int i = 0; i < 256; i++){
            normHistogram[i] = cumHistogram[i] * 255 / pixels;
        }
        return normHistogram;
    }

    public void equalizeHistogram() {
        int[] hRed = this.calculateHistogram(0);
        int[] hGreen = this.calculateHistogram(1);
        int[] hBlue = this.calculateHistogram(2);

        int[] normRed = this.generateCumHistogram(hRed);
        int[] normGreen = this.generateCumHistogram(hGreen);
        int[] normBlue = this.generateCumHistogram(hBlue);

        for(int i = 0; i < this.height; i++){
            for(int j = 0; j < this.width; j++){
                Color color = new Color(this.image.getRGB(j, i));

                int R = color.getRed();
                int G = color.getGreen();
                int B = color.getBlue();

                int eqR = normRed[R];
                int eqG = normGreen[G];
                int eqB = normBlue[B];

                Color targetColor = new Color(eqR, eqG, eqB);
                this.image.setRGB(j, i, targetColor.getRGB());
            }
        }
    }

    public void toCIEXYZ(){
        ColorSpace XYZ = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
        ColorConvertOp op = new ColorConvertOp(XYZ, null);
        this.image = op.filter(this.image, null);
    }

    public void toRGB(){
        ColorSpace RGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorConvertOp op = new ColorConvertOp(RGB, null);
        this.image = op.filter(this.image, null);
    }
}
