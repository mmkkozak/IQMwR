package net.iqmwr_plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.real.DoubleType;

public class CNR {

    public static double cnr1(
            Dataset refImage, Dataset testImage, OpService opService, DatasetService datasetService) {

////        RandomAccessibleInterval<?> noise = (RandomAccessibleInterval<?>) opService.run("math.subtract", refImage, testImage);
//        Dataset abs_noise = Helpers.absoluteValue(refImage, testImage, datasetService);
//
////        System.out.println(refImage.size());
//        double mean_signal = ((DoubleType) opService.run("mean", refImage)).getRealDouble();
//        double mean_noise = ((DoubleType) opService.run("mean", abs_noise)).getRealDouble();
//        double std_noise = ((DoubleType) opService.run("stdDev", abs_noise)).getRealDouble();
//        double cnr = Math.abs(mean_signal - mean_noise) / std_noise;
//        return 20.0 * Math.log10(cnr);
        Dataset diff = Helpers.subtract(refImage, testImage, datasetService);
        double std_signal = ((DoubleType) opService.run("stdDev", refImage)).getRealDouble();
        double std_noise = ((DoubleType)  opService.run("stdDev", diff)).getRealDouble();
        return std_signal / std_noise;
    }

    public static double cnr2(Dataset refImage, Dataset testImage, OpService opService, DatasetService datasetService) {

        Dataset diff = Helpers.subtract(refImage, testImage, datasetService);
        double mean_noise = ((DoubleType)  opService.run("mean", diff)).getRealDouble();
        double std_noise = ((DoubleType)  opService.run("stdDev", diff)).getRealDouble();

        double mean_signal = ((DoubleType) opService.run("mean", refImage)).getRealDouble();

        return Math.abs(mean_signal - mean_noise) / std_noise;
    }

    public static double cnr3(Dataset refImage, Dataset testImage, OpService opService, DatasetService datasetService) {

        Dataset diff = Helpers.subtract(refImage, testImage, datasetService);
        double mean_noise = ((DoubleType)  opService.run("mean", diff)).getRealDouble();
        double std_noise = ((DoubleType)  opService.run("stdDev", diff)).getRealDouble();

        double mean_test = ((DoubleType) opService.run("mean", testImage)).getRealDouble();
        double std_test = ((DoubleType) opService.run("stdDev", testImage)).getRealDouble();

        return (mean_test - mean_noise) / Math.sqrt(Math.pow(std_test, 2) + Math.pow(std_noise, 2));
    }
}
