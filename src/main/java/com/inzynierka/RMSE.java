package com.inzynierka;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class RMSE {
    public static final double rmseCounter(
            ImageStack refImage,
            ImageStack testImage) {

        double r, t, mse = 0.0;
        double rmse = 0;
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
                mse += (r - t) * (r - t);
            }
        }
        mse /= N;

        if (mse != 0.0) {
            rmse = Math.sqrt(mse);
        }
        return rmse;
    }

    public static final double getRMSE(ImagePlus refImage, ImagePlus testImage) {
        return rmseCounter(refImage.getStack(), testImage.getStack());
    }
}
