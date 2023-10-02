package com.inzynierka;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Dialog implements PlugIn {
    GenericDialog gendialog = new GenericDialog("Image Quality Measurement Markers");
    GridBagLayout layout = new GridBagLayout();
    Panel modePanel = new Panel(layout);
    JLabel imageLabel = new JLabel();

    Dialog(ArrayList<Image> imageList, int imageNumber) {

        int refImageIndex = 0;
        int test1ImageIndex = 1;
        int test2ImageIndex = 2;
        int test3ImageIndex = 3;
        int test4ImageIndex = 4;

        boolean[] modeStates = new boolean[]{true, true, true, true, true};
        String[] modeOptions = new String[]{"SSIM", "SNR", "PSNR", "RMSE", "MAE"};
        String[] imagesName = new String[imageNumber];


        //Get name of images
        for (int i = 0; i < imageNumber; i++)
            imagesName[i] = WindowManager.getImage(i + 1).getTitle();


        //Generic Dialog
        gendialog.addChoice("Reference Image", imagesName, imagesName[refImageIndex]);
        gendialog.addMessage("Test: ");
        gendialog.addChoice("Image 1", imagesName, imagesName[test1ImageIndex]);
        gendialog.addChoice("* Image 2", imagesName, imagesName[test2ImageIndex]);
        gendialog.addChoice("* Image 3", imagesName, imagesName[test3ImageIndex]);
        gendialog.addChoice("* Image 4", imagesName, imagesName[test4ImageIndex]);
        gendialog.addMessage("* - Optional");
        gendialog.addMessage("Modes:");
        gendialog.addCheckboxGroup(modeOptions.length / 2 + 1, 2, modeOptions, modeStates);

        gendialog.showDialog();
//
        if (gendialog.wasOKed()) {

            refImageIndex = gendialog.getNextChoiceIndex();
            //...
            for(int i=0; i<modeStates.length; i++){
                modeStates[i] = gendialog.getNextBoolean();
            }

            //Search for mode that user clicked
            boolean calculateModes = false;
            for(int i=0; i<6; i++)
                if(modeStates[i])
                    calculateModes= true;
            if(calculateModes == false)
                return;

            //get iamges from ImageJ
            ImagePlus refImage = WindowManager.getImage(refImageIndex + 1);
            ImagePlus testImage1 = WindowManager.getImage(test1ImageIndex + 1);
            ImagePlus testImage2 = WindowManager.getImage(test2ImageIndex + 1);
            ImagePlus testImage3 = WindowManager.getImage(test3ImageIndex + 1);
            ImagePlus testImage4 = WindowManager.getImage(test4ImageIndex + 1);

            //Create results table
            ResultsTable resultTable = new ResultsTable();
            resultTable.incrementCounter();
            resultTable.setPrecision(6);




            if (modeStates[0]) //SSIM
            {
                resultTable.addValue("SSIM", getSSIM(refImage, testImage1));
                if (testImage2 == null) {
                    return;
                } else {
                    resultTable.addValue("SSIM", getSSIM(refImage, testImage2));
                }
                if (testImage3 == null) {
                    return;
                } else {
                    resultTable.addValue("SSIM", getSSIM(refImage, testImage3));
                }
                if (testImage4 == null) {
                    return;
                } else {
                    resultTable.addValue("SSIM", getSSIM(refImage, testImage4));
                }
            }
        }
    }
    public static final double ssimCounter(
            ImageProcessor refImage,
            ImageProcessor testImage)
    {
        IJ.showMessage("Obliczone");
//        if(refImage.getWidth() != testImage.getWidth() || refImage.getHeight() != testImage.getHeight())
////            return IJ.error("ERROR: Add images are not in the same size!");
//            return -1;

        return 0;
    }

    public static  final double getSSIM( ImagePlus refImage, ImagePlus testImage) {
        return ssimCounter(refImage.getProcessor(), testImage.getProcessor());
    }


    @Override
    public void run(String s) {

    }


}

