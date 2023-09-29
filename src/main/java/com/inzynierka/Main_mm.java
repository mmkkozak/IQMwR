package com.inzynierka;
import ij.*;
import ij.plugin.*;

import java.awt.*;
import java.sql.Array;
import java.util.ArrayList;

public class Main_mm implements PlugIn {

    public static void main(String[] args) {
        //List of images
        ArrayList<Image> imageList = new ArrayList<Image>();

        int imageNumber = WindowManager.getImageCount();
//        if(imageNumber < 2) {
//            IJ.error("ERROR: Program work with 2 and more open image");
//            return;
//        }

        //Build the dialog box
        Dialog dialog = new Dialog(imageList);
    }

    @Override
    public void run(String s) {

    }

//    public void run(String arg) {
//    }

}