package net.iqmwr_plugin;

import net.imagej.Dataset;
import net.imglib2.type.numeric.RealType;

public class PSNR {

    public static double psnr(Dataset refImage, Dataset testImage) {
        RealType<?> type = refImage.firstElement();
        double max = type.getMaxValue();
        double mse = Statistics.mse(refImage, testImage);

        return 10 * Math.log10(Math.pow(max, 2) / mse);
    }
}
