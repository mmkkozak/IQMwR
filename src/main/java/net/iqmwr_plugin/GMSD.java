package net.iqmwr_plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

public class GMSD {

    public static double gmsd(Dataset refImage, Dataset testImage, OpService opService, DatasetService datasetService) {
        Img<FloatType> reference = Helpers.toFloatImg(refImage);
        Img<FloatType> test = Helpers.toFloatImg(testImage);

        Img<FloatType> gmRef = (Img<FloatType>) opService.filter().sobel(reference);
        Img<FloatType> gmTest = (Img<FloatType>) opService.filter().sobel(test);

        ImgFactory<DoubleType> factory = new ArrayImgFactory<>(new DoubleType());
        Img<DoubleType> gmsMap = factory.create(reference);

        Cursor<FloatType> c1 = gmRef.cursor();
        Cursor<FloatType> c2 = gmTest.cursor();
        Cursor<DoubleType> cMap = gmsMap.cursor();

        double c = 0.0026;

        while (c1.hasNext() && c2.hasNext()) {
            double ms = c1.next().getRealDouble();
            double mt = c2.next().getRealDouble();
            double gms = (2 * ms * mt + c) / (ms * ms + mt * mt + c);
            cMap.next().setReal(gms);
        }

        Dataset gmsMapDataset = datasetService.create(gmsMap);

        return ((DoubleType)  opService.run("stdDev", gmsMapDataset)).getRealDouble();
    }
}
