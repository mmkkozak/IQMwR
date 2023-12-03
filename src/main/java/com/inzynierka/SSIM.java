package com.inzynierka;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class SSIM {

    public static double ssimCalculator(
            ImageStack refImage,
            ImageStack testImage) {


        double C1 = 0, C2 = 0;
        double mssim = 0.0;
        double ssim_sum = 0.0;
        int width = refImage.getWidth();
        int height = refImage.getHeight();
        int bits_per_pixel_ref = refImage.getBitDepth();
        int bits_per_pixel_test = testImage.getBitDepth();

        if (bits_per_pixel_ref != bits_per_pixel_test) {
            IJ.error("ERROR: Images must have the same number of bits per pixel");
        }

        double K1 = 0.01;
        double K2 = 0.03;
        if (bits_per_pixel_ref == 8) {
            C1 = (Math.pow(2, bits_per_pixel_ref) - 1) * K1;
            C2 = (Math.pow(2, bits_per_pixel_ref) - 1) * K2;
        }
        if (bits_per_pixel_ref == 16) {
            C1 = (Math.pow(2, bits_per_pixel_ref) - 1) * K1;
            C2 = (Math.pow(2, bits_per_pixel_ref) - 1) * K2;
        }
        if (bits_per_pixel_ref == 32) {
            C1 = (Math.pow(2, bits_per_pixel_ref) - 1) * K1;
            C2 = (Math.pow(2, bits_per_pixel_ref) - 1) * K2;
        }
        C1 *= C1;
        C2 *= C2;

        if (C1 < 0 || C2 < 0) {
            IJ.error("ERROR: Value of C1 or C2 is < 0");
        }


        //GENERATE GAUSSIAN BLUR
        int radius = 5;
        double sigma = 1.5;
        float[] weightKernel = new float[121];
        float sum = 0;

        for (int j = -radius; j <= radius; j++) {
            for (int i = -radius; i <= radius; i++) {
                float result = (float) (Math.exp((double) -((i * i) + (j * j)) / (2 * sigma * sigma)));
                weightKernel[((j + radius) * 11) + (i + radius)] = result;
                sum += result;
            }
        }
        int kernel_size = 121;
        for (int j = 0; j < kernel_size; j++) {
            weightKernel[j] /= sum;
        }


        ImageProcessor lRef = refImage.getProcessor(1);
        ImageProcessor lTest = testImage.getProcessor(1);

        ImageProcessor lRef_copy = lRef.duplicate();
        ImageProcessor lTest_copy = lTest.duplicate();


        int N = lRef_copy.getWidth() * lRef_copy.getHeight();
        int M = 0;

        for (int i = 0; i < width - 10; i++) {
            for (int j = 0; j < height - 10; j++) {

                double mu_x = 0.0, mu_y = 0.0, mu_xy = 0.0, mu_x_sq = 0.0, mu_y_sq = 0.0;
                double sigma_xy = 0.0, sigma_x = 0.0, sigma_y = 0.0, sd_x_sq = 0.0, sd_y_sq = 0.0;
                double sd_x, sd_y, sd_xy = 0.0;
                double num, den;
                M++;

                for (int y = 0; y < 11; y++) {
                    for (int x = 0; x < 11; x++) {
                        mu_x += lRef_copy.getPixel(i + y, j + x) * weightKernel[(y * 11) + x];
                        mu_y += lTest_copy.getPixel(i + y, j + x) * weightKernel[(y * 11) + x];
                    }
                }
                if (N > 0 && (N - 1) > 0) {
                    mu_xy = mu_x * mu_y;
                    mu_x_sq = mu_x * mu_x;
                    mu_y_sq = mu_y * mu_y;

                    for (int y = 0; y < 11; y++) {
                        for (int x = 0; x < 11; x++) {
                            sigma_x += Math.pow(lRef_copy.getPixel(i + y, j + x) - mu_x, 2) * weightKernel[y * 11 + x];
                            sigma_y += Math.pow(lTest_copy.getPixel(i + y, j + x) - mu_y, 2) * weightKernel[y * 11 + x];
                            sigma_xy += (lRef_copy.getPixel(i + y, j + x) - mu_x) * (lTest_copy.getPixel(i + y, j + x) - mu_y) * weightKernel[y * 11 + x];
                        }
                    }
                    sd_x = Math.sqrt(sigma_x);
                    sd_y = Math.sqrt(sigma_y);
                    sd_xy = sigma_xy;
                    sd_x_sq = sd_x * sd_x;
                    sd_y_sq = sd_y * sd_y;

                    num = (2 * mu_xy + C1) * (2 * sd_xy + C2);
                    den = (mu_x_sq + mu_y_sq + C1) * (sd_x_sq + sd_y_sq + C2);
                    ssim_sum += num / den;
                }
            }
        }
        mssim = ssim_sum / M;
        return mssim;
    }

    public static double getSSIM(ImagePlus refImage, ImagePlus testImage) {
        return ssimCalculator(refImage.getStack(), testImage.getStack());
    }
}
