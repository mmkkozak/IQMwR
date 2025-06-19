import net.imagej.ImageJ;
import net.imagej.Dataset;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplay;
import org.scijava.Initializable;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.display.Display;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.io.location.FileLocation;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;

import java.io.File;
import java.io.IOException;
import java.util.*;

import net.imagej.display.ImageDisplayService;

@Plugin(type = Command.class, menuPath = "Plugins>Image Quality Measures with Reference")
public class ImageQualityRM extends DynamicCommand{

    @Parameter
    private ImageJ ij;

    @Parameter
    private UIService uiService;

    @Parameter
    private ImageDisplayService imageDisplayService;

    @Parameter(label="Reference Image", choices={})
    private String referenceImage;

    @Parameter(label="Test Image")
    private String testImage;

//    @Parameter(label = "Zastosuj metrykę PSNR")
//    private boolean usePSNR;
//
//    @Parameter(label = "Zastosuj metrykę SSIM")
//    private boolean useSSIM;
//
//    @Parameter(label = "Zastosuj metrykę MSE")
//    private boolean useMSE;

    private boolean imagesOpenFlag = false;

    @Override
    public void initialize() {

//        List<ImageDisplay> openImages = imageDisplayService.getImageDisplays();
        HashMap<String, ImageDisplay> nameToDataset = new HashMap<>();
        for (ImageDisplay d : imageDisplayService.getImageDisplays()) {
            String name = d.getName();
            if (!nameToDataset.containsKey(name)) {
                nameToDataset.put(name, d);
            }
        }
        List<String> names = new ArrayList<>(nameToDataset.keySet());

        MutableModuleItem<String> refItem = getInfo().getMutableInput("referenceImage", String.class);
        refItem.setChoices(names);

        MutableModuleItem<String> testItem = getInfo().getMutableInput("testImage", String.class);
        testItem.setChoices(names);

//        if (imageDisplayService.getImageDisplays().size() < 2) {
//            uiService.showDialog("Musisz mieć otwarte przynajmniej 2 obrazy.",
//                    DialogPrompt.MessageType.ERROR_MESSAGE);
//            return;
//        } else {
////            System.out.println(openImages);
//            imagesOpenFlag = true;
//        }
    }

    @Override
    public void run() {

        if (!imagesOpenFlag) return;

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
//        }

//        System.out.println("Reference image: " + referenceImage.getName());
//        System.out.println("Test image: " + testImage.getName());

//        List<String> selectedMetrics = new ArrayList<>();
//        if (usePSNR) selectedMetrics.add("PSNR");
//        if (useSSIM) selectedMetrics.add("SSIM");
//        if (useMSE) selectedMetrics.add("MSE");

//        System.out.println("Wybrane metryki: " + selectedMetrics);
    }

//    protected int getOpenDatasetCount () {
//        List<?> displays = imageDisplayService.getImageDisplays();
//        return displays.size();
//    }
}