package com.inzynierka;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class SNR {

    public static final double snrCounter(
            ImageStack refImage,
            ImageStack testImage) {

        int nx = refImage.getWidth();
        int ny = refImage.getHeight();
        int N = 0;
        double r, t, num = 0.0, den = 0.0;

        int nzr = refImage.getSize();
        int nzt = testImage.getSize();

        for(int z=1; z<=Math.max(nzr, nzt); z++ ) {

            int ir = Math.min(z, nzr);
            int it = Math.min(z, nzt);
            ImageProcessor lRef = refImage.getProcessor(it);
            ImageProcessor lTest  = testImage.getProcessor(ir);


            for (int y = 0; y < nx; y++) {
                for (int x = 0; x < ny; x++) {
                    r = lRef.getPixelValue(x, y);
                    t = lTest.getPixelValue(x, y);

                    if (!Double.isNaN(t) && !Double.isNaN(r)) {
                        num += r * r;
                        den += (r - t) * (r - t);
                        N++;

                    }
                }
            }
        }


        double snr = 0;

        if (N > 0) {
            num /= N;
            den /= N;

            if (den != 0.0) {
                snr = 10.0 * Math.log10(num / den);
            }
        }


        return snr;
    }


    public static final double getSNR(ImagePlus refImage, ImagePlus testImage) {
        return snrCounter(refImage.getStack(), testImage.getStack());
    }
}
