package net.iqmwr_plugin;

import net.imagej.Dataset;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class SSIM {

    static double L, C1, C2;
    static double K1 = 0.01, K2 = 0.03;

    public static double ssim(Dataset refImage, Dataset testImage, OpService opService) {
        double mean_signal = ((DoubleType) opService.run("mean", refImage)).getRealDouble();
        double mean_test = ((DoubleType) opService.run("mean", testImage)).getRealDouble();
        double sigma_st = corr_coeff(refImage, testImage, mean_signal, mean_test);
        double sigma_signal = Helpers.varianceValue(refImage, mean_signal);
        double sigma_test = Helpers.varianceValue(testImage, mean_test);
        RealType<?> type = refImage.firstElement();
        L = type.getMaxValue();
        C1 = Math.pow((L*K1), 2);
        C2 = Math.pow((L*K2), 2);

        return (2*mean_signal*mean_test + C1) * (2*sigma_st + C2) /
                ((Math.pow(mean_signal, 2) + Math.pow(mean_test, 2) + C1) * (sigma_signal + sigma_test + C2));
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

    public static double mssim(Dataset refImage, Dataset testImage) {
        RealType<?> type = refImage.firstElement();
        L = type.getMaxValue();
        C1 = Math.pow((L*K1), 2);
        C2 = Math.pow((L*K2), 2);
        final int M = 11; //window size
        final double[][] weights = Helpers.generateGaussianKernel(M, 1.5);
        final int center = M / 2;
        double summedLocalSSIMs = 0.0;

        long width = refImage.dimension(0);
        long height = refImage.dimension(1);
        RandomAccess<? extends RealType<?>> refImg = refImage.randomAccess();
        RandomAccess<? extends RealType<?>> testImg = testImage.randomAccess();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double mu_s = 0.0, mu_t = 0.0;
                double sigma_s_2 = 0.0, sigma_t_2 = 0.0, sigma_st = 0.0;

                // calculating weighted local means
                for (int j = -center; j <= center; j++) {
                    for (int i = -center; i <= center; i++) {
                        // mirror padding
                        int xx = mirror(x + i, (int) width);
                        int yy = mirror(y + j, (int) height);

                        refImg.setPosition(new long[]{xx, yy});
                        testImg.setPosition(new long[]{xx, yy});

                        double ps = refImg.get().getRealDouble();
                        double pt = testImg.get().getRealDouble();
                        double w = weights[j + center][i + center];

                        mu_s += w * ps;
                        mu_t += w * pt;
                    }
                }

                // other weighted local statistics
                for (int j = -center; j <= center; j++) {
                    for (int i = -center; i <= center; i++) {
                        int xx = mirror(x + i, (int) width);
                        int yy = mirror(y + j, (int) height);

                        refImg.setPosition(new long[]{xx, yy});
                        testImg.setPosition(new long[]{xx, yy});

                        double ps = refImg.get().getRealDouble();
                        double pt = testImg.get().getRealDouble();
                        double w = weights[j + center][i + center];

                        sigma_s_2 += w * Math.pow(ps - mu_s, 2);
                        sigma_t_2 += w * Math.pow(pt - mu_t, 2);
                        sigma_st += w * (ps - mu_s) * (pt - mu_t);
                    }
                }

                double ssim = (2 * mu_s * mu_t + C1) * (2 * sigma_st + C2) /
                        ((mu_s * mu_s + mu_t * mu_t + C1) * (sigma_s_2 + sigma_t_2 + C2));

                summedLocalSSIMs += ssim;
            }
        }

        return summedLocalSSIMs / (width * height);
    }

    private static int mirror(int pos, int dimSize) {
        if (pos < 0) return -pos;
        if (pos >= dimSize) return 2 * dimSize - pos - 2;
        return pos;
    }
}
