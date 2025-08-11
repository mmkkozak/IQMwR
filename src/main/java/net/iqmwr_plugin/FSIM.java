package net.iqmwr_plugin;

import net.imagej.Dataset;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.real.FloatType;

import java.util.ArrayList;
import java.util.List;

public class FSIM {

    public static double fsim(Dataset refImage, Dataset testImage, OpService opService) {

        Img<FloatType> reference = Helpers.toFloatImg(refImage);
        Img<FloatType> test = Helpers.toFloatImg(testImage);

        Img<FloatType> gmRef = (Img<FloatType>) opService.filter().sobel(reference);
        Img<FloatType> gmTest = (Img<FloatType>) opService.filter().sobel(test);
        Img<FloatType> pcRef = computePhaseCongruency(reference, opService);
        Img<FloatType> pcTest = computePhaseCongruency(test, opService);

        final double T1 = 0.85, T2 = 0.01;
//        final double T1 = 0.85, T2 = 160;
        Cursor<FloatType> cg1 = gmRef.cursor();
        Cursor<FloatType> cg2 = gmTest.cursor();
        Cursor<FloatType> cpc1 = pcRef.cursor();
        Cursor<FloatType> cpc2 = pcTest.cursor();

        double numerator = 0.0;
        double denominator = 0.0;

        while (cg1.hasNext()) {
            double g1 = cg1.next().getRealDouble();
            double g2 = cg2.next().getRealDouble();
            double pc1 = cpc1.next().getRealDouble();
            double pc2 = cpc2.next().getRealDouble();

            double spc = (2 * pc1 * pc2 + T1) / (pc1 * pc1 + pc2 * pc2 + T1);
            double sg = (2 * g1 * g2 + T2) / (g1 * g1 + g2 * g2 + T2);
            double sl = spc * sg;
            double weight = Math.max(pc1, pc2);

            numerator += sl * weight;
            denominator += weight;
        }

        return denominator > 0 ? numerator / denominator : 0.0;
    }

    public static double fsim_g(Dataset refImage, Dataset testImage, OpService opService) {
        //simplified version of FSIM, using only gradient magnitude (GM)
        Img<FloatType> reference = Helpers.toFloatImg(refImage);
        Img<FloatType> test = Helpers.toFloatImg(testImage);

        Img<FloatType> gmRef = (Img<FloatType>) opService.filter().sobel(reference);
        Img<FloatType> gmTest = (Img<FloatType>) opService.filter().sobel(test);

        final double T2 = 0.01;
        Cursor<FloatType> c1 = gmRef.cursor();
        Cursor<FloatType> c2 = gmTest.cursor();

        double numerator = 0.0;
        double denominator = 0.0;

        while (c1.hasNext() && c2.hasNext()) {
            double g1 = c1.next().getRealDouble();
            double g2 = c2.next().getRealDouble();

            double similarity = (2 * g1 * g2 + T2) / (g1 * g1 + g2 * g2 + T2);
            double weight = Math.max(g1, g2);

            numerator += similarity * weight;
            denominator += weight;
        }

        return denominator > 0 ? numerator / denominator : 0.0;
    }

    private static List<Img<ComplexFloatType>> createLogGaborFilters(final long[] dims) {
        final int numScales = 4;
        final double minWavelength = 3.0;
        final double mult = 2.1;
        final double sigmaOnf = 0.55;
        int M = (int) dims[0], N = (int) dims[1];
        double[] u1 = new double[M], u2 = new double[N];

        // wektor częstotliwości normalizowanych w każdym wymiarze
        for (int i = 0; i < M; i++)
            u1[i] = (i < M / 2 ? i : i - M) / (double) M;
        for (int j = 0; j < N; j++)
            u2[j] = (j < N / 2 ? j : j - N) / (double) N;

        // generacja długości fali dla skal
        double[] wavelength = new double[numScales];
        wavelength[0] = minWavelength;
        for (int s = 1; s < numScales; s++)
            wavelength[s] = wavelength[s - 1] * mult;

        List<Img<ComplexFloatType>> filters = new ArrayList<>();
        for (int s = 0; s < numScales; s++) {
            double fo = 1.0 / wavelength[s];
//            Img<ComplexFloatType> filter = ops.create().img(dims, new ComplexFloatType());
            Img<ComplexFloatType> filter = ArrayImgs.complexFloats(dims);
            Cursor<ComplexFloatType> c = filter.cursor();

            while (c.hasNext()) {
                c.fwd();
                int x = c.getIntPosition(0), y = c.getIntPosition(1);
                double radius = Math.hypot(u1[x], u2[y]);
                double realVal = 0;
                if (radius > 0) {
                    double logTerm = Math.log(radius / fo);
                    realVal = Math.exp(-logTerm * logTerm /
                            (2 * Math.log(sigmaOnf) * Math.log(sigmaOnf)));
                }
                c.get().setReal((float) realVal);
                c.get().setImaginary((float) 0);
            }
            filters.add(filter);
        }
        return filters;
    }

    private static Img<FloatType> computePhaseCongruency(Img<FloatType> input, OpService ops) {

        @SuppressWarnings("unchecked")
        Img<ComplexFloatType> fft = (Img<ComplexFloatType>) ops.run("filter.fft", input);

        // log-Gabor filters matching FFT dimensions
        long[] fftDims = new long[fft.numDimensions()];
        fft.dimensions(fftDims);
        List<Img<ComplexFloatType>> lgFilters = createLogGaborFilters(fftDims);

        Img<FloatType> phaseCongruency = input.factory().create(input);
        phaseCongruency.forEach(FloatType::setZero);

        for (Img<ComplexFloatType> lgFilter : lgFilters) {
            // multiplying in frequency domain
            @SuppressWarnings("unchecked")
            Img<ComplexFloatType> filteredFreq = (Img<ComplexFloatType>) ops.run("math.multiply", fft, lgFilter);

            Img<FloatType> spatial = new ArrayImgFactory<>(new FloatType()).create(input);
            ops.run("filter.ifft", spatial, filteredFreq);

            Cursor<FloatType> pcCursor = phaseCongruency.cursor();
            Cursor<FloatType> spatialCursor = spatial.cursor();
            while (pcCursor.hasNext()) {
                pcCursor.next().add(spatialCursor.next());
            }
        }

        // normalization
        double maxVal = ops.stats().max(phaseCongruency).getRealDouble();
        if (maxVal > 1) {
            for (FloatType p : phaseCongruency) {
                p.setReal(p.getRealDouble() / maxVal);
            }
        }

        return phaseCongruency;
    }

}
