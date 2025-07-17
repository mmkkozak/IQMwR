package net.iqmwr_plugin;

import net.imagej.Dataset;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class SSIM {

    public static double ssim(Dataset refImage, Dataset testImage, OpService opService) {
        double mean_signal = ((DoubleType) opService.run("mean", refImage)).getRealDouble();
        double mean_test = ((DoubleType) opService.run("mean", testImage)).getRealDouble();
        double sigma_st = corr_coeff(refImage, testImage, mean_signal, mean_test);
        double std_signal = Helpers.stdValue(refImage, mean_signal);
        double std_test = Helpers.stdValue(testImage, mean_test);
        RealType<?> type = refImage.firstElement();
        double L = type.getMaxValue();
        double K1 = 0.01, K2 = 0.03;
        double C1 = Math.pow((L*K1), 2);
        double C2 = Math.pow((L*K2), 2);

        return (2*mean_signal*mean_test + C1) * (2*sigma_st + C2) /
                (Math.pow(mean_signal, 2) + Math.pow(mean_test, 2) + C1) * (Math.pow(std_signal, 2) + Math.pow(std_test, 2) + C2);
    }

    private static double corr_coeff(Dataset img1, Dataset img2, double mean1, double mean2) {
        double sum = 0.0;
        final Img<? extends RealType<?>> img1Data = img1.getImgPlus();
        final Img<? extends RealType<?>> img2Data = img2.getImgPlus();
        Cursor<? extends RealType<?>> cursor1 = img1Data.cursor();
        Cursor<? extends RealType<?>> cursor2 = img2Data.cursor();

        while (cursor1.hasNext()) {
            double x = cursor1.next().getRealDouble();
            double y = cursor2.next().getRealDouble();
            sum += (x - mean1) * (y - mean2);
        }
        return sum / (img1.size() - 1);
    }
}
