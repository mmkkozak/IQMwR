package com.inzynierka;

import ij.ImagePlus;
import ij.Macro;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.io.Opener;

public class Main {
    public static void main(String[] args){
        ImageQualityMeasurementMarkers_Plugin plugin = new ImageQualityMeasurementMarkers_Plugin();


        Opener opener = new Opener();
        ImagePlus image1 = opener.openImage("C:/Users/Admin/Desktop/Inzynierka_maven/Inzynierka/src/main/resources/images/Image1.png");
        ImagePlus image2 = opener.openImage("C:/Users/Admin/Desktop/Inzynierka_maven/Inzynierka/src/main/resources/images/Image2.png");
        WindowManager.addWindow(new ImageWindow(image1));
        WindowManager.addWindow(new ImageWindow(image2));



        plugin.run("hello world");
        plugin.run("hello world");

    }
}
