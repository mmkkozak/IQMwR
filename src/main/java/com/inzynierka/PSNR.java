package com.inzynierka;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class PSNR {
    public static final double psnrCounter(
            ImageStack refImage,
            ImageStack testImage) {

        int N = 0;
        double r, t, mse = 0.0;
        double maxSignal =  -Double.MAX_VALUE;
        double psnr = 0.0;

        for(int z=0; z< refImage.getSize(); z++ ) {

            ImageProcessor lRef = refImage.getProcessor(z+1);
            ImageProcessor lTest = testImage.getProcessor(z+1);

            for (int y = 0; y < refImage.getWidth(); y++) {
                for (int x = 0; x < refImage.getHeight(); x++) {
                    r = lRef.getPixel(x, y);
                    if(r > maxSignal) {
                        maxSignal = r;
                    }
                    t = lTest.getPixel(x, y);
                    if (!Double.isNaN(t) && !Double.isNaN(r)) {
                        mse += (r - t) * (r - t);
                        N++;
                    }
                }
            }
        }

        if (N > 0) {
            mse /= N;
            if (mse != 0.0) {
                psnr = 10.0 * Math.log10(maxSignal * maxSignal/mse);
            }
        }

        return psnr;
    }

    public static final double getPSNR(ImagePlus refImage, ImagePlus testImage) {
        return psnrCounter(refImage.getStack(), testImage.getStack());
    }
}

