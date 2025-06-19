import ij.IJ;
import net.imagej.ImageJ;
import net.imagej.Dataset;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.io.location.FileLocation;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.imagej.display.ImageDisplayService;

@Plugin(type = Command.class, menuPath = "Plugins>Image Quality Measures with Reference")
public class ImageQualityRM implements Command {

    @Parameter
    private ImageJ ij;

    @Parameter
    private UIService uiService;

    @Parameter
    private ImageDisplayService imageDisplayService;

    @Parameter(label = "Reference Image")
    private Dataset referenceImage;

    @Parameter(label = "Test Image")
    private Dataset testImage;

//    int openImagesCount = 0;

    @Override
    public void run() {
//        openImagesCount = WindowManager.getImageCount();
//        File file = null;
//        ImagePlus image;
//        Opener opener = new Opener();
//
//        while (openImagesCount < 2) {
////            IJ.error("Hello world " + openImagesCount);
//            try {
//                file = uiService.chooseFile(null, "open");
//                if (file == null) return;
//                image = opener.openImage(file.getPath());
//                if (image == null) {
//                    IJ.error("ImageQualityRM error", "Failed to open image " + file.getPath());
//                    continue;
//                }
//                uiService.show(image);
//                System.out.println(openImagesCount);
//            } catch(Exception ex) {
//                IJ.error("ImageQualityRM error", ex.getMessage());
//            }
//            openImagesCount = WindowManager.getImageCount();
//            System.out.println(openImagesCount);
//        }
//        while (getOpenDatasetCount() < 2) {
//            File file = uiService.chooseFile(null, "open");
//            if (file == null) {
//                uiService.showDialog("You need to open at least two images.", DialogPrompt.MessageType.ERROR_MESSAGE);
//                return;
//            }
//
//            Dataset dataset = null;
//            try {
//                dataset = ij.scifio().datasetIO().open(new FileLocation(file).getURI().toString());
//            } catch (IOException e) {
//                uiService.showDialog("Failed to open file:\n" + e.getMessage(), DialogPrompt.MessageType.ERROR_MESSAGE);
//                return;
//            }
//            uiService.show(dataset);
//
//        }
//
//    }
//
//    protected int getOpenDatasetCount () {
//        List<?> displays = imageDisplayService.getImageDisplays();
//        return displays.size();
    }
}