package com.inzynierka;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class MAE {
    public static final double maeCounter(
            ImageStack refImage,
            ImageStack testImage) {

        double r, t, mae = 0.0;
        int N = refImage.getWidth() * refImage.getHeight();
        int bits_per_pixel_ref = refImage.getBitDepth();
        int bits_per_pixel_test = testImage.getBitDepth();

        if (bits_per_pixel_ref != bits_per_pixel_test) {
            IJ.error("ERROR: Images must have the same number of bits per pixel");
        }

        ImageProcessor lRef = refImage.getProcessor(1);
        ImageProcessor lTest = testImage.getProcessor(1);

        for (int y = 0; y < refImage.getWidth(); y++) {
            for (int x = 0; x < refImage.getHeight(); x++) {
                r = lRef.getPixel(y, x);
                t = lTest.getPixel(y, x);
                mae += Math.abs(r - t);
            }
        }
        mae /= N;

        return mae;
    }

    public static final double getMAE(ImagePlus refImage, ImagePlus testImage) {
        return maeCounter(refImage.getStack(), testImage.getStack());
    }
}
