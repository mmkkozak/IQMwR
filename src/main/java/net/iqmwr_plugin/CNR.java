package net.iqmwr_plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class CNR {

    public static double cnrCounter(
            Dataset refImage, Dataset testImage, OpService opService, DatasetService datasetService) {

//        RandomAccessibleInterval<?> noise = (RandomAccessibleInterval<?>) opService.run("math.subtract", refImage, testImage);
        Dataset abs_noise = absoluteValue(refImage, testImage, datasetService);

        System.out.println(refImage.size());
        double mean_signal = ((DoubleType) opService.run("mean", refImage)).getRealDouble();
        double mean_noise = ((DoubleType)  opService.run("mean", abs_noise)).getRealDouble();
        double std_noise = ((DoubleType)  opService.run("stdDev", abs_noise)).getRealDouble();
        double cnr = Math.abs(mean_signal - mean_noise) / std_noise;
        return 20.0 * Math.log10(cnr);
//        System.out.println(opService.run("mean", refImage));
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
private static Dataset absoluteValue(Dataset img1, Dataset img2, DatasetService datasetService) {
    final Img<? extends RealType<?>> img1Data = img1.getImgPlus();
    final Img<? extends RealType<?>> img2Data = img2.getImgPlus();

    // Tworzymy wynik (typu float, ale można zmienić)
    ImgFactory<DoubleType> factory = new ArrayImgFactory<>(new DoubleType());
    Img<DoubleType> result = factory.create(img1Data);

    Cursor<? extends RealType<?>> cursor1 = img1Data.cursor();
    Cursor<? extends RealType<?>> cursor2 = img2Data.cursor();
    Cursor<DoubleType> cursorOut = result.cursor();

    while (cursor1.hasNext()) {
        double v1 = cursor1.next().getRealDouble();
        double v2 = cursor2.next().getRealDouble();
        cursorOut.next().setReal(Math.abs(v1 - v2));
    }

    // Zwracamy jako Dataset
    return datasetService.create(result);
}

}
