package net.iqmwr_plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.real.DoubleType;

public class SNR {

    public static double snr1(
            Dataset refImage, Dataset testImage, OpService opService, DatasetService datasetService) {

        Dataset diff = Helpers.subtract(refImage, testImage, datasetService);
        double mean_signal = ((DoubleType) opService.run("mean", refImage)).getRealDouble();
        double std_noise = ((DoubleType)  opService.run("stdDev", diff)).getRealDouble();
        return mean_signal / std_noise;
    }

    public static double snr2(Dataset refImage, Dataset testImage, OpService opService, DatasetService datasetService) {

        Dataset summed = Helpers.add(refImage, testImage, datasetService);
        double sum_mean = ((DoubleType) opService.run("mean", summed)).getRealDouble();

        Dataset diff = Helpers.subtract(refImage, testImage, datasetService);
        double std_noise = ((DoubleType)  opService.run("stdDev", diff)).getRealDouble();

        return sum_mean / std_noise / Math.sqrt(2);
    }

}
