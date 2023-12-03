package com.inzynierka;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class PSNR {
    public static final double psnrCounter(
            ImageStack refImage,
            ImageStack testImage) {

        double r, t, mse = 0.0, maxSignal = 0.0;
        double psnr = 0.0;
        int N = refImage.getWidth() * refImage.getHeight();
        int bits_per_pixel_ref = refImage.getBitDepth();
        int bits_per_pixel_test = testImage.getBitDepth();

        if (bits_per_pixel_ref != bits_per_pixel_test) {
            IJ.error("ERROR: Images must have the same number of bits per pixel");
        }

        if (bits_per_pixel_ref == 8) {
            maxSignal = (Math.pow(2, bits_per_pixel_ref) - 1);
        }
        if (bits_per_pixel_ref == 16) {
            maxSignal = (Math.pow(2, bits_per_pixel_ref) - 1);
        }
        if (bits_per_pixel_ref == 32) {
            maxSignal = (Math.pow(2, bits_per_pixel_ref) - 1);
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
            psnr = 10.0 * Math.log10((maxSignal * maxSignal) / mse);
        }
        return psnr;
    }

    public static final double getPSNR(ImagePlus refImage, ImagePlus testImage) {
        return psnrCounter(refImage.getStack(), testImage.getStack());
    }
}

