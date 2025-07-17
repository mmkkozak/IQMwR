package net.iqmwr_plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Helpers {

    static Dataset absoluteValue(Dataset img1, Dataset img2, DatasetService datasetService) {
        // method to calculate absolute value of Dataset (image) difference
        final Img<? extends RealType<?>> img1Data = img1.getImgPlus();
        final Img<? extends RealType<?>> img2Data = img2.getImgPlus();

        ImgFactory<DoubleType> factory = new ArrayImgFactory<>(new DoubleType());
        Img<DoubleType> result = factory.create(img1Data);

        // cursors allow pixel by pixel operations - retrieving and saving values
        Cursor<? extends RealType<?>> cursor1 = img1Data.cursor();
        Cursor<? extends RealType<?>> cursor2 = img2Data.cursor();
        Cursor<DoubleType> cursorOut = result.cursor();

        while (cursor1.hasNext()) {
            double v1 = cursor1.next().getRealDouble();
            double v2 = cursor2.next().getRealDouble();
            cursorOut.next().setReal(Math.abs(v1 - v2));
        }
        // the result is returned as a Dataset type for further operations
        return datasetService.create(result);
    }

    static double squaredSum(Dataset input) {
        Img<? extends RealType<?>> inImg = input.getImgPlus();
        Cursor<? extends RealType<?>> inCursor = inImg.cursor();

        double sum = 0.0;
        while (inCursor.hasNext()) {
            double value = inCursor.next().getRealDouble();
            sum += Math.pow(value, 2);
        }
        return sum;
    }

    static Dataset subtract(Dataset img1, Dataset img2, DatasetService datasetService) {

        final Img<? extends RealType<?>> img1Data = img1.getImgPlus();
        final Img<? extends RealType<?>> img2Data = img2.getImgPlus();

        ImgFactory<DoubleType> factory = new ArrayImgFactory<>(new DoubleType());
        Img<DoubleType> result = factory.create(img1Data);

        Cursor<? extends RealType<?>> cursor1 = img1Data.cursor();
        Cursor<? extends RealType<?>> cursor2 = img2Data.cursor();
        Cursor<DoubleType> cursorOut = result.cursor();

        while (cursor1.hasNext()) {
            double v1 = cursor1.next().getRealDouble();
            double v2 = cursor2.next().getRealDouble();
            cursorOut.next().setReal(v1 - v2);
        }
        return datasetService.create(result);
    }

    static Dataset add(Dataset img1, Dataset img2, DatasetService datasetService) {

        final Img<? extends RealType<?>> img1Data = img1.getImgPlus();
        final Img<? extends RealType<?>> img2Data = img2.getImgPlus();

        ImgFactory<DoubleType> factory = new ArrayImgFactory<>(new DoubleType());
        Img<DoubleType> result = factory.create(img1Data);

        Cursor<? extends RealType<?>> cursor1 = img1Data.cursor();
        Cursor<? extends RealType<?>> cursor2 = img2Data.cursor();
        Cursor<DoubleType> cursorOut = result.cursor();

        while (cursor1.hasNext()) {
            double v1 = cursor1.next().getRealDouble();
            double v2 = cursor2.next().getRealDouble();
            cursorOut.next().setReal(v1 + v2);
        }
        return datasetService.create(result);
    }

    static double stdValue(Dataset img1, double mean) {
        final Img<? extends RealType<?>> img1Data = img1.getImgPlus();
        double std = 0.0;
        Cursor<? extends RealType<?>> cursor1 = img1Data.cursor();

        while (cursor1.hasNext()) {
            std += Math.pow((cursor1.next().getRealDouble() - mean), 2);
        }
        return std / (img1.size() - 1);
    }
}
