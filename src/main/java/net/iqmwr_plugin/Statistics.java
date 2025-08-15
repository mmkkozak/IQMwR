package net.iqmwr_plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Statistics {

    public static double mae(
            Dataset refImage, Dataset testImage, OpService opService, DatasetService datasetService) {
        Dataset abs_diff = Helpers.absoluteValue(refImage, testImage, datasetService);
        return ((DoubleType)  opService.run("mean", abs_diff)).getRealDouble();
    }

    public static double mse (Dataset refImage, Dataset testImage) {
        final Img<? extends RealType<?>> refData = refImage.getImgPlus();
        final Img<? extends RealType<?>> testData = testImage.getImgPlus();

        Cursor<? extends RealType<?>> refCursor = refData.cursor();
        Cursor<? extends RealType<?>> testCursor = testData.cursor();

        double sum = 0.0;
        while (refCursor.hasNext()) {
            double diff = refCursor.next().getRealDouble() - testCursor.next().getRealDouble();
            sum += Math.pow(diff, 2);
        }
        return sum/refData.size();
    }

    public static double rmse (Dataset refImage, Dataset testImage) {
        double mse = mse(refImage, testImage);
        return Math.sqrt(mse);
    }

}
