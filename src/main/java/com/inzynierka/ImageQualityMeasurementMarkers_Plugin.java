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

        if (gendialog.wasOKed()) {

            ArrayList<ImagePlus> testImages = new ArrayList<ImagePlus>(imageNumber);

            refImageIndex = gendialog.getNextChoiceIndex();

            for (int i = 0; i < imageNumber - 1; i++) {
                int idx = gendialog.getNextChoiceIndex();
//              testImages.add( WindowManager.getImage(idx + 1));
                testImages.add(imageList.get(idx));
            }
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

            //get iamges from ImageJ
            ImagePlus refImage = imageList.get(refImageIndex); //WindowManager.getImage(refImageIndex + 1);
//            ImagePlus testImage1 = imageList.get(1);//WindowManager.getImage(test1ImageIndex + 1);
//

            //Create results table
            ResultsTable resultTable = new ResultsTable();
            resultTable.incrementCounter();
            resultTable.setPrecision(6);

            //
            testImages.stream().forEach(testImage -> {
                resultTable.addValue("Testing Image", testImage.getTitle());
                if (modeStates[0]) //SSIM
                {
                    resultTable.addValue("SSIM", getSSIM(refImage, testImage));
                }
                if (modeStates[1]) //SNR
                {
                    resultTable.addValue("SNR [db]", getSNR(refImage, testImage));
                }
                if (modeStates[2]) //PSNR
                {
                    resultTable.addValue("PSNR [db]", getPSNR(refImage, testImage));
                }
                if (modeStates[3]) //RMSE
                {
                    resultTable.addValue("RMSE", getRMSE(refImage, testImage));
                }
                if (modeStates[4]) //MAE
                {
                    resultTable.addValue("MAE", getMAE(refImage, testImage));
                }

                resultTable.addRow();
            });
            resultTable.deleteRow(imageNumber - 1);
            resultTable.show("Result");
        }

        //Macro
        String macro = Macro.getOptions();
    }

}

