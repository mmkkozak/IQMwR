package com.inzynierka;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

import javax.swing.*;

public class SSIM {
    public static final double ssimCounter(
            ImageStack refImage,
            ImageStack testImage) {

        double s_deviation_x, s_deviation_y, s_deviation_xy;

        for(int i =0; i<refImage.getSize(); i++) {

        }

        return Math.random();
    }

    public static final double getSSIM(ImagePlus refImage, ImagePlus testImage) {
        return ssimCounter(refImage.getStack(), testImage.getStack());
    }
}
