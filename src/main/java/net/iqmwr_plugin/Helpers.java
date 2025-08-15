package net.iqmwr_plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

public class Helpers {

    public static Img<FloatType> toFloatImg(Dataset dataset) {
        final Img<? extends RealType<?>> input = dataset.getImgPlus();

        long[] dims = new long[input.numDimensions()];
        input.dimensions(dims);

        Img<FloatType> floatImg = new ArrayImgFactory<>(new FloatType()).create(dims);

        Cursor<? extends RealType<?>> inCursor = input.cursor();
        Cursor<FloatType> outCursor = floatImg.cursor();

        while (inCursor.hasNext() && outCursor.hasNext()) {
            double val = inCursor.next().getRealDouble();
            outCursor.next().setReal(val);
        }

        return floatImg;
    }

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

    static double varianceValue(Dataset img1, double mean) {
        final Img<? extends RealType<?>> img1Data = img1.getImgPlus();
        double variance = 0.0;
        Cursor<? extends RealType<?>> cursor1 = img1Data.cursor();

        while (cursor1.hasNext()) {
            variance += Math.pow((cursor1.next().getRealDouble() - mean), 2);
        }
        return variance / (img1.size() - 1);
    }

    static double[][] generateGaussianKernel(int size, double sigma) {
        if(size%2 == 0) size++; // size of the kernel should be an odd number
        double[][] kernel = new double[size][size];
        int ctr = size / 2;
        double sum = 0.0;

        for (int y = -ctr; y <= ctr; y++) {
            for (int x = -ctr; x <= ctr; x++) {
                double exp = Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
                kernel[y + ctr][x + ctr] = exp;
                sum += exp;
            }
        }

        // normalizing to unit sum
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                kernel[y][x] /= sum;
            }
        }

        return kernel;
    }

}
