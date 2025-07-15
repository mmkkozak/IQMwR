package net.iqmwr_plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

public class SNR {

    public static double snr1(
            Dataset refImage, Dataset testImage, OpService opService, DatasetService datasetService) {

//        RandomAccessibleInterval<?> noiseRaw = (RandomAccessibleInterval<?>) opService.run("math.subtract", refImage, testImage);
//        @SuppressWarnings("unchecked")
//        RandomAccessibleInterval<DoubleType> noise = (RandomAccessibleInterval<DoubleType>) noiseRaw;
//        Dataset diff = datasetService.create(noise);
        Dataset diff = Helpers.subtract(refImage, testImage, datasetService);
        double mean_signal = ((DoubleType) opService.run("mean", refImage)).getRealDouble();
        double std_noise = ((DoubleType)  opService.run("stdDev", diff)).getRealDouble();
        return mean_signal / std_noise;
    }

    public static double snr2(Dataset refImage, Dataset testImage, OpService opService, DatasetService datasetService) {

//        RandomAccessibleInterval<?> imgSum = (RandomAccessibleInterval<?>) opService.run("math.add", refImage, testImage);
//        @SuppressWarnings("unchecked")
//        RandomAccessibleInterval<DoubleType> sum = (RandomAccessibleInterval<DoubleType>) imgSum;
//        Dataset summed = datasetService.create(sum);
        Dataset summed = Helpers.subtract(refImage, testImage, datasetService);
        double sum_mean = ((DoubleType) opService.run("mean", summed)).getRealDouble();

//        RandomAccessibleInterval<?> noiseRaw = (RandomAccessibleInterval<?>) opService.run("math.subtract", refImage, testImage);
//        @SuppressWarnings("unchecked")
//        RandomAccessibleInterval<DoubleType> noise = (RandomAccessibleInterval<DoubleType>) noiseRaw;
//        Dataset diff = datasetService.create(noise);
        Dataset diff = Helpers.subtract(refImage, testImage, datasetService);
        double std_noise = ((DoubleType)  opService.run("stdDev", diff)).getRealDouble();

        return sum_mean / std_noise / Math.sqrt(2);
    }

}
