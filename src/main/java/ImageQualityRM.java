import net.imagej.ImageJ;
import net.imagej.Dataset;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplay;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.display.Display;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;

import java.io.File;
import java.io.IOException;
import java.util.*;

import net.imagej.display.ImageDisplayService;

@Plugin(type = DynamicCommand.class, menuPath = "Plugins>Image Quality Measures with Reference")
public class ImageQualityRM extends DynamicCommand{

    @Parameter
    private ImageJ ij;

    @Parameter
    private UIService uiService;

    @Parameter
    private ImageDisplayService imageDisplayService;

    @Parameter(label="Reference Image")
    private String referenceImageName;

    @Parameter(label="Test Image")
    private String testImageName;

    @Parameter(label = "Zastosuj metrykę PSNR")
    private boolean usePSNR;
//
//    @Parameter(label = "Zastosuj metrykę SSIM")
//    private boolean useSSIM;
//
//    @Parameter(label = "Zastosuj metrykę MSE")
//    private boolean useMSE;

    @Override
    public void initialize() {

        // handling duplicates on dropdown list coming from parameters referenceImageName, testImageName
        HashMap<String, ImageDisplay> imagesNames = new HashMap<>();
        for (ImageDisplay d : imageDisplayService.getImageDisplays()) {
            String name = d.getName();
            if (!imagesNames.containsKey(name)) {
                imagesNames.put(name, d);
            }
        }
        List<String> names = new ArrayList<>(imagesNames.keySet());
        // assigning unique images names values to dropdown list
        MutableModuleItem<String> refItem = getInfo().getMutableInput("referenceImageName", String.class);
        refItem.setChoices(names);
        MutableModuleItem<String> testItem = getInfo().getMutableInput("testImageName", String.class);
        testItem.setChoices(names);

    }

    @Override
    public void run() {

        if (referenceImageName.equals(testImageName)) {
            uiService.showDialog("The same image was chosen as reference and test.", DialogPrompt.MessageType.WARNING_MESSAGE);
        }
        System.out.println("Reference image: " + referenceImageName);
        System.out.println("Test image: " + testImageName);

//        List<String> selectedMetrics = new ArrayList<>();
//        if (usePSNR) selectedMetrics.add("PSNR");
//        if (useSSIM) selectedMetrics.add("SSIM");
//        if (useMSE) selectedMetrics.add("MSE");

//        System.out.println("Wybrane metryki: " + selectedMetrics);
    }
}