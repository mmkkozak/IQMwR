package net.iqmwr_plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

import java.util.Arrays;

public class SSIM {

    static double L, C1, C2;
    static double K1 = 0.01, K2 = 0.03, epsilon = 0;

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

    private static double[] ssimComponents(Dataset refImage, Dataset testImage, int window_size, int x, int y, boolean... flags){
        final int center = window_size / 2;
        final double[][] weights = Helpers.generateGaussianKernel(window_size, 1.5);
        double mu_s = 0.0, mu_t = 0.0;
        double sigma_s_2 = 0.0, sigma_t_2 = 0.0, sigma_st = 0.0;
        long width = refImage.dimension(0);
        long height = refImage.dimension(1);
        RandomAccess<? extends RealType<?>> refImg = refImage.randomAccess();
        RandomAccess<? extends RealType<?>> testImg = testImage.randomAccess();

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
                if(flags.length != 0) w = 1.0;

                mu_s += w * ps;
                mu_t += w * pt;
            }
        }

        if(flags.length != 0) {
            mu_s /= (window_size * window_size);
            mu_t /= (window_size * window_size);
        }

        // other weighted local statistics using means for calculation
        for (int j = -center; j <= center; j++) {
            for (int i = -center; i <= center; i++) {
                int xx = mirror(x + i, (int) width);
                int yy = mirror(y + j, (int) height);

                refImg.setPosition(new long[]{xx, yy});
                testImg.setPosition(new long[]{xx, yy});

                double ps = refImg.get().getRealDouble();
                double pt = testImg.get().getRealDouble();
                double w = weights[j + center][i + center];
                if(flags.length != 0) w = 1.0;

                sigma_s_2 += w * Math.pow(ps - mu_s, 2);
                sigma_t_2 += w * Math.pow(pt - mu_t, 2);
                sigma_st += w * (ps - mu_s) * (pt - mu_t);
            }
        }

        if(flags.length != 0) {
            sigma_s_2 /= (window_size * window_size);
            sigma_t_2 /= (window_size * window_size);
            sigma_st /= (window_size * window_size);
        }

        double l = (2 * mu_s * mu_t + C1) / (mu_s * mu_s + mu_t * mu_t + C1 + epsilon);
        double c = (2 * Math.sqrt(sigma_s_2 * sigma_t_2) + C2) / (sigma_s_2 + sigma_t_2 + C2 + epsilon);
        double s = (2 * sigma_st + C2) / (2 * Math.sqrt(sigma_s_2 * sigma_t_2) + C2 + epsilon);

        return new double[]{l, c, Math.abs(s)};
    }

    public static double mssim(Dataset refImage, Dataset testImage) {
        RealType<?> type = refImage.firstElement();
        L = type.getMaxValue();
        C1 = Math.pow((L*K1), 2);
        C2 = Math.pow((L*K2), 2);
        final int window = 11;
        double summedLocalSSIMs = 0.0;

        long width = refImage.dimension(0);
        long height = refImage.dimension(1);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] components = ssimComponents(refImage, testImage, window, x, y);
                double ssim = components[0] * components[1] * components[2];
                summedLocalSSIMs += ssim;
            }
        }

        return summedLocalSSIMs / (width * height);
    }

    public static double q_index(Dataset refImage, Dataset testImage) {
        C1 = 0;
        C2 = 0;
        epsilon = 1e-8; // to maintain stability instead of C1, C2
        final int window = 8;
        double summedLocalSSIMs = 0.0;

        long width = refImage.dimension(0);
        long height = refImage.dimension(1);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] components = ssimComponents(refImage, testImage, window, x, y, true);
                double ssim = components[0] * components[1] * components[2];
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

    public static double multiscale_ssim(Dataset refImage, Dataset testImage, OpService opService, DatasetService datasetService) {
        RealType<?> type = refImage.firstElement();
        L = type.getMaxValue();
        C1 = Math.pow((L*K1), 2);
        C2 = Math.pow((L*K2), 2);
        final int window = 11;

        long origin_width = refImage.dimension(0);
        long origin_height = refImage.dimension(1);
        int minSize = 32;
        int numScales = Math.min(
                (int) Math.floor(Math.log(origin_width / (double) minSize) / Math.log(2)),
                (int) Math.floor(Math.log(origin_height / (double) minSize) / Math.log(2))
        );
        numScales = Math.min(numScales, 5);
        numScales = Math.max(numScales, 1);
        double[] all_weights = {0.0448, 0.2856, 0.3001, 0.2363, 0.1333};
        double[] scale_weights = Arrays.copyOf(all_weights, numScales);
        double weights_sum = 0;
        for (double w : scale_weights) weights_sum += w;
        for (int i = 0; i < scale_weights.length; i++) scale_weights[i] /= weights_sum;
        double msssim = 1.0;

        Dataset ref = refImage;
        Dataset test = testImage;

        for (int level = 0; level < numScales; level++) {
            long width = ref.dimension(0);
            long height = ref.dimension(1);
            double summedLocalSSIMs = 0.0;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    double[] components = ssimComponents(ref, test, window, x, y);
                    if (level < numScales - 1) {
                        summedLocalSSIMs += Math.pow(components[1] * components[2], scale_weights[level]);
                    } else {
                        summedLocalSSIMs += Math.pow(components[0] * components[1] * components[2], scale_weights[level]);
                    }
                }
            }

            msssim *= summedLocalSSIMs / (width * height);
            if (level < numScales - 1) {
                ref = downsample(gaussianBlur(ref, opService, datasetService), datasetService);
                test = downsample(gaussianBlur(test, opService, datasetService), datasetService);
            }
        }

        return msssim;
    }

    private static Dataset gaussianBlur(Dataset input, OpService ops, DatasetService datasetService) {
        if (input.dimension(0) <= 1 || input.dimension(1) <= 1) return input;

        @SuppressWarnings("unchecked")
        RandomAccessibleInterval<DoubleType> inputImg = (RandomAccessibleInterval<DoubleType>) input.getImgPlus();
        @SuppressWarnings("unchecked")
        RandomAccessibleInterval<DoubleType> result =
                (RandomAccessibleInterval<DoubleType>) ops.run("filter.gauss", inputImg, 1.5);

        return datasetService.create(result);
    }

    private static Dataset downsample(Dataset input, DatasetService datasetService) {
        if (input.dimension(0) <= 2 || input.dimension(1) <= 2) return input;

        long newWidth = input.dimension(0) / 2;
        long newHeight = input.dimension(1) / 2;

        if (newWidth < 1 || newHeight < 1) return input;

        Img<? extends RealType<?>> inImg = input.getImgPlus();
        Img<FloatType> outImg = new ArrayImgFactory<>(new FloatType()).create(newWidth, newHeight);

        Cursor<? extends RealType<?>> inCur = inImg.localizingCursor();
        RandomAccess<FloatType> outRA = outImg.randomAccess();

        while (inCur.hasNext()) {
            inCur.fwd();
            long x = inCur.getLongPosition(0);
            long y = inCur.getLongPosition(1);

            if (x % 2 == 0 && y % 2 == 0) {
                long outX = x / 2;
                long outY = y / 2;
                if (outX < newWidth && outY < newHeight) {
                    outRA.setPosition(new long[]{outX, outY});
                    outRA.get().setReal(inCur.get().getRealDouble());
                }
            }
        }

        return datasetService.create(outImg);
    }

}
