package com.inzynierka;
import ij.ImagePlus;

public class Image
{
    private ImagePlus imp;

    public  Image(ImagePlus imp) {
        this.imp = imp;
    }

    public boolean isTypeCompatible() {
        int type = imp.getType();
        if (type == ImagePlus.GRAY8)
            return true;
        if (type == ImagePlus.GRAY16)
            return true;
        if (type == ImagePlus.GRAY32)
            return true;
        return false;
    }

    public boolean isSameSize(Image ref) {
        ImagePlus imp = ref.getImagePlus();
        if (imp.getWidth() != getImagePlus().getWidth())
            return false;
        if (imp.getHeight() != getImagePlus().getHeight())
            return false;
        return true;
    }

    public ImagePlus getImagePlus() {
        return imp;
    }
}
