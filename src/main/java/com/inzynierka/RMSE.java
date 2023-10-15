package com.inzynierka;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class RMSE {
    public static final double rmseCounter(
            ImageStack refImage,
            ImageStack testImage) {

        int nx = refImage.getWidth();
        int ny = refImage.getHeight();
        int N = 0;
        double r, t, mse=0.0;

        for(int z=0; z<refImage.getSize(); z++ ) {

            ImageProcessor lRef = refImage.getProcessor(z+1);
            ImageProcessor lTest = testImage.getProcessor(z+1);

            for (int y = 0; y < nx; y++) {
                for (int x = 0; x < ny; x++) {
                    r = lRef.getPixelValue(x, y);
                    t = lTest.getPixelValue(x, y);

                    if (!Double.isNaN(t) && !Double.isNaN(r)) {
                        mse += (r-t) *(r-t);
                        N++;

                    }
                }
            }
        }

        double rmse = 0;

        if (N > 0) {
            mse /= N;

            if (mse != 0.0) {
                rmse = Math.sqrt(mse);
            }
        }

        return rmse;
    }

    public static final double getRMSE(ImagePlus refImage, ImagePlus testImage) {
        return rmseCounter(refImage.getStack(), testImage.getStack());
    }
}
