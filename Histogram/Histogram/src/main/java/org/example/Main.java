package org.example;

import java.awt.color.ColorSpace;
import java.awt.image.ColorConvertOp;
import java.io.IOException;

public class Main {
    public Main() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String path = "C:\\Users\\piotc\\Desktop\\exampleH.png\\";
        String path2 = "C:\\Users\\piotc\\Desktop\\convertedExample3.png";

        ImageTransformation imageTransformation = new ImageTransformation();

        imageTransformation.loadImage(path);

//        imageTransformation.changeBrightness(50);
//        imageTransformation.cBwThreads(50);

        imageTransformation.toCIEXYZ();
        imageTransformation.toRGB();

        imageTransformation.equalizeHistogram();

        imageTransformation.saveImage(path2);
    }
}