package com.inzynierka;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class MAE {
    public static final double maeCounter(
            ImageStack refImage,
            ImageStack testImage) {

        int N = 0;
        double r, t, mae = 0.0;

        for(int z=0; z < refImage.getSize(); z++ ) {

            ImageProcessor lRef = refImage.getProcessor(z+1);
            ImageProcessor lTest = testImage.getProcessor(z+1);

            for (int y = 0; y < refImage.getWidth(); y++) {
                for (int x = 0; x < refImage.getHeight(); x++) {
                    r = lRef.getPixelValue(x, y);
                    t = lTest.getPixelValue(x, y);

                    if (!Double.isNaN(t) && !Double.isNaN(r)) {
                        mae += Math.abs(r-t) ;
                        N++;

                    }
                }
            }
        }
        if (N > 0) {
            mae /= N;
        }

        return mae;
    }

    public static final double getMAE(ImagePlus refImage, ImagePlus testImage) {
        return maeCounter(refImage.getStack(), testImage.getStack());
    }
}
