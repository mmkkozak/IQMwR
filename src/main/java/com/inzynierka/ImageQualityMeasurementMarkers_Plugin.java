package com.inzynierka;

import ij.*;
import ij.io.Opener;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.*;
import ij.Macro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.inzynierka.CNR.getCNR;
import static com.inzynierka.MAE.getMAE;
import static com.inzynierka.PSNR.getPSNR;
import static com.inzynierka.RMSE.getRMSE;
import static com.inzynierka.SNR.getSNR;
import static com.inzynierka.SSIM.getSSIM;

public class ImageQualityMeasurementMarkers_Plugin implements PlugIn {

    @Override
    public void run(String s) {
        GenericDialog gendialog = new GenericDialog("Image Quality Measurement Markers");

        int refImageIndex = 0;
        int testImageIndex = 0;
        boolean[] modeStates = new boolean[]{true, true, true, true, true, true};
        String[] modeOptions = new String[]{"SSIM", "SNR", "CNR", "PSNR", "RMSE", "MAE"};
        String refImg = "", testImg = "";
        int macro = 0;

        //Create results table
        ResultsTable resultTable = new ResultsTable();
        resultTable.incrementCounter();
        resultTable.setPrecision(6);


//        int[] windowImageList = WindowManager.getIDList();
//        List<ImagePlus> imageList = Arrays.asList(image1, image2);
//        int imageNumber = imageList.size();

        int imageNumber = WindowManager.getImageCount();
        String[] imageNames = new String[imageNumber];

        //Get name of images
        for (int i = 0; i < imageNumber; i++) {
            imageNames[i] = WindowManager.getImage(i + 1).getTitle();
//            imageNames[i] = windowImageList.get(i).getTitle();
        }

//ERROR SECTION

        if (imageNumber < 2) {
            IJ.error("ERROR: No open image! You need 2");
            return;
        }
//        imageList.subList(1, imageNumber).stream().forEach(testImage -> {
//            if (imageList.get(0).getWidth() != testImage.getWidth() || imageList.get(0).getHeight() != testImage.getHeight())
//                IJ.error("ERROR: Added images are not in the same size!");
//        });


//MACRO
        String macro_ij = Macro.getOptions();

        if (macro_ij == null) {
            //Generic Dialog
            gendialog.addChoice("Reference Image", imageNames, imageNames[0]);
            gendialog.addChoice("Test Image", imageNames, imageNames[1]);
            gendialog.addMessage("Markers");
            gendialog.addCheckboxGroup(modeOptions.length / 4 + 1, 4, modeOptions, modeStates);
            gendialog.showDialog();


            if (gendialog.wasOKed()) {

//            ArrayList<ImagePlus> testImages = new ArrayList<ImagePlus>(imageNumber);

                refImageIndex = gendialog.getNextChoiceIndex();
                refImg = imageNames[refImageIndex];

                testImageIndex = gendialog.getNextChoiceIndex();
                testImg = imageNames[testImageIndex];

//            for (int i = 0; i < imageNumber - 1; i++) {
//                int idx = gendialog.getNextChoiceIndex();
////                testImages.add( WindowManager.getImage(idx + 1));
//                testImages.add(windowImageList.get(idx));
//            }
                for (int i = 0; i < modeStates.length; i++) {
                    modeStates[i] = gendialog.getNextBoolean();
                }

                //Search for mode that user clicked
                boolean calculateModes = false;
                for (int i = 0; i < 5; i++)
                    if (modeStates[i])
                        calculateModes = true;
                if (calculateModes == false)
                    return;
            }
        } else {
            if (macro_ij != null) {
                refImg = Macro.getValue(macro_ij, "reference", "IMGREF");
                testImg = Macro.getValue(macro_ij, "test", "IMAGETEST");
            } else {
                IJ.error("ERROR: No arguments!");
                return;
            }
        }

//            ImagePlus refImage = imageList.get(refImageIndex);
        ImagePlus refImage = WindowManager.getImage(refImg);
//          ImagePlus testImage = imageList.get(testImageIndex);
        ImagePlus testImage = WindowManager.getImage(testImg);

        if (refImage.getWidth() != testImage.getWidth()) {
            if (refImage.getHeight() != testImage.getHeight()) {
                IJ.error("ERROR: Added images are not in the same size!");
                return;
            }
        }

//            testImages.stream().forEach(testImage -> {
        resultTable.addValue("Reference Image", refImage.getTitle());
        resultTable.addValue("Testing Image", testImage.getTitle());
        if (modeStates[0]) //SSIM
        {
            resultTable.addValue("SSIM", getSSIM(refImage, testImage));
        }
        if (modeStates[1]) //SNR
        {
            resultTable.addValue("SNR [db]", getSNR(refImage, testImage));
        }
        if (modeStates[2]) //CNR
        {
            resultTable.addValue("CNR [db]", getCNR(refImage, testImage));
        }
        if (modeStates[3]) //PSNR
        {
            resultTable.addValue("PSNR [db]", getPSNR(refImage, testImage));
        }
        if (modeStates[4]) //RMSE
        {
            resultTable.addValue("RMSE", getRMSE(refImage, testImage));
        }
        if (modeStates[5]) //MAE
        {
            resultTable.addValue("MAE", getMAE(refImage, testImage));
        }

//                resultTable.addRow();
//            });
        String name = "";
        name = "Results for: reference= " + refImg + "  " + "test= " + testImg;
        resultTable.deleteRow(imageNumber - 1);
        resultTable.show(name);
    }


}



