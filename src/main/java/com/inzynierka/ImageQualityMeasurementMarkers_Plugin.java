package com.inzynierka;


import ij.*;
import ij.io.Opener;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.*;
import ij.process.ImageProcessor;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ImageQualityMeasurementMarkers_Plugin implements PlugIn {

    @Override
    public void run(String s) {
        GenericDialog gendialog = new GenericDialog("Image Quality Measurement Markers");

        int refImageIndex = 0;
        boolean[] modeStates = new boolean[]{true, true, true, true, true};
        String[] modeOptions = new String[]{"SSIM", "SNR", "PSNR", "RMSE", "MAE"};

        //List of images
        Opener opener = new Opener();
        ImagePlus image1 = opener.openImage("C:/Users/Admin/Desktop/Inzynierka_maven/Inzynierka/src/main/resources/images/Image1.png");
        ImagePlus image2 = opener.openImage("C:/Users/Admin/Desktop/Inzynierka_maven/Inzynierka/src/main/resources/images/Image2.png");
        ImagePlus image3 = opener.openImage("C:/Users/Admin/Desktop/Inzynierka_maven/Inzynierka/src/main/resources/images/Image3.png");

        List<ImagePlus> imageList = Arrays.asList(image1, image2, image3);
        int imageNumber = imageList.size();
//        int imageNumber = WindowManager.getImageCount();

        String[] imageNames = new String[imageNumber];

        if (imageNumber < 2) {
            IJ.error("ERROR: Program work with two and more open image!");
            return;
        }
        imageList.subList(1, imageNumber).stream().forEach(testImage -> {
            if (imageList.get(0).getWidth() != testImage.getWidth() || imageList.get(0).getHeight() != testImage.getHeight())
                IJ.error("ERROR: Add images are not in the same size!");
        });

        //Get name of images
        for (int i = 0; i < imageNumber; i++) {
//            imageNames[i] = WindowManager.getImage(i + 1).getTitle();
            imageNames[i] = imageList.get(i).getTitle();
        }

        //Generic Dialog
        gendialog.addChoice("Reference Image", imageNames, imageNames[refImageIndex]);
        gendialog.addMessage("Test: ");
        for (int i = 1; i < imageNumber; i++) {
            gendialog.addChoice("Image" + i, imageNames, imageNames[i]);
        }
        gendialog.addMessage("Modes:");
        gendialog.addCheckboxGroup(modeOptions.length / 2 + 1, 2, modeOptions, modeStates);

        gendialog.showDialog();
//
        if (gendialog.wasOKed()) {

            refImageIndex = gendialog.getNextChoiceIndex();
            //...
            for (int i = 0; i < modeStates.length; i++) {
                modeStates[i] = gendialog.getNextBoolean();
            }

            //Search for mode that user clicked
            boolean calculateModes = false;
            for (int i = 0; i < 1; i++)
                if (modeStates[i])
                    calculateModes = true;
            if (calculateModes == false)
                return;

            //get iamges from ImageJ
            ImagePlus refImage = imageList.get(0); //WindowManager.getImage(refImageIndex + 1);
//            ImagePlus testImage1 = imageList.get(1);//WindowManager.getImage(test1ImageIndex + 1);
//            ImagePlus testImage2 = imageList.get(2);//WindowManager.getImage(test2ImageIndex + 1);
//            ImagePlus testImage3 = imageList.get(3);//WindowManager.getImage(test3ImageIndex + 1);
//            ImagePlus testImage4 = imageList.get(4);//WindowManager.getImage(test4ImageIndex + 1);

            //Create results table
            ResultsTable resultTable = new ResultsTable();
            resultTable.incrementCounter();
            resultTable.setPrecision(6);


            imageList.subList(1, imageNumber).stream().forEach(testImage -> {
            if (modeStates[0]) //SSIM
            {
                    resultTable.addValue("SSIM", getSSIM(refImage, testImage));
            }
            if (modeStates[1]) //SSIM
            {
                    resultTable.addValue("SNR", getSSIM(refImage, testImage));
            }
            resultTable.addRow();
        });
            resultTable.deleteRow(imageNumber-1);
            resultTable.show("Jebać kapusi smacznej kawusi");
        }
    }

    public static final double ssimCounter(
            ImageProcessor refImage,
            ImageProcessor testImage) {
        return Math.random();
    }

    public static final double getSSIM(ImagePlus refImage, ImagePlus testImage) {
        return ssimCounter(refImage.getProcessor(), testImage.getProcessor());
    }

}

