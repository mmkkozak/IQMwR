package iqmwr_plugin;

import net.imagej.Dataset;
import net.imagej.ops.OpService;

public class CNR {

    public static void cnrCounter(Dataset refImage, Dataset testImage, OpService opService) {



//        double mean_signal = opService.run("mean", refImage);
        System.out.println(opService.run("mean", refImage));
//        int N = 0;
//        double r = 0.0;
//        double t = 0.0;
//        double mean_signal = 0.0, mean_noise =0.0;
//        double cnr = 0.0, sd_noise = 0.0;

//        int width = (int) refImage.getWidth();
//        int height = (int) refImage.getHeight();
//        double[][]  noise = new double[width][height];
//        N = width * height;


//        for (int y = 0; y < refImage.getWidth(); y++) {
//            for (int x = 0; x < refImage.getHeight(); x++) {
//                r = lRef.getPixel(y, x);
//                t = lTest.getPixel(y, x);
//                mean_signal += r;
//                mean_noise += Math.abs(t - r);
//                noise[y][x] = Math.abs(t - r);
//            }
//        }
//        mean_signal /= N;
//        mean_noise /= N;

//        for (int y = 0; y < refImage.getWidth(); y++) {
//            for (int x = 0; x < refImage.getHeight(); x++) {
//                sd_noise += Math.pow((noise[y][x] - mean_noise), 2);
//            }
//        }
//
//        cnr = Math.abs(mean_signal - mean_noise) / Math.sqrt(sd_noise / (N - 1));
//        return 10.0 * Math.log10(cnr);
    }

//    public static final double getCNR(ImagePlus refImage, ImagePlus testImage) {
//        return cnrCounter(refImage.getStack(), testImage.getStack());
//    }
}
