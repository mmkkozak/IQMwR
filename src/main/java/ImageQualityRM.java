import net.imagej.DatasetService;
import net.imagej.ImageJ;
import net.imagej.Dataset;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import org.scijava.command.DynamicCommand;
import org.scijava.display.Display;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;
import java.util.*;

@Plugin(type = DynamicCommand.class, menuPath = "Plugins>Image Quality Measures with Reference")
public class ImageQualityRM extends DynamicCommand{

    @Parameter
    private ImageJ ij;

    @Parameter
    private UIService uiService;
    @Parameter
    private ImageDisplayService imageDisplayService;
    @Parameter
    private DatasetService datasetService;

    @Parameter(label="Reference Image")
    private String referenceImageName;
    @Parameter(label="Test Image")
    private String testImageName;

    @Parameter(label = "SSIM")
    private boolean useSSIM;
    @Parameter(label = "SNR")
    private boolean useSNR;
    @Parameter(label = "CNR")
    private boolean useCNR;
    @Parameter(label = "PSNR")
    private boolean usePSNR;
    @Parameter(label = "RMSE")
    private boolean useRMSE;
    @Parameter(label = "MAE")
    private boolean useMAE;

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

        // initializing Dataset variables with chosen images
        Dataset refDataset = findDatasetByName(referenceImageName);
        Dataset testDataset = findDatasetByName(testImageName);

        // exception handling
        if (refDataset == null) {
            uiService.showDialog(String.format("Error: Unable to use image %s", referenceImageName),
                    DialogPrompt.MessageType.ERROR_MESSAGE);
            return;
        } else if (testDataset == null) {
            uiService.showDialog(String.format("Error: Unable to use image %s", testImageName),
                    DialogPrompt.MessageType.ERROR_MESSAGE);
            return;
        } else if (!Arrays.equals(refDataset.dimensionsAsLongArray(), testDataset.dimensionsAsLongArray())) {
            uiService.showDialog("Error: Images must be of the same size.", DialogPrompt.MessageType.ERROR_MESSAGE);
            return;
        }
        else if (!refDataset.getType().getClass().equals(testDataset.getType().getClass())) {
            uiService.showDialog("Error: Images must be of the same bit depth.",
                    DialogPrompt.MessageType.ERROR_MESSAGE);
            return;
        }

        ArrayList<String> selectedMetrics = new ArrayList<String>();
        if (useSSIM) selectedMetrics.add("SSIM");
        if (useSNR) selectedMetrics.add("SNR");
        if (useCNR) selectedMetrics.add("CNR");
        if (usePSNR) selectedMetrics.add("PSNR");
        if (useRMSE) selectedMetrics.add("RMSE");
        if (useMAE) selectedMetrics.add("MAE");

        // ensuring a metric to calculate is chosen
        if (selectedMetrics.isEmpty()) {
            uiService.showDialog("No metric was chosen.", DialogPrompt.MessageType.WARNING_MESSAGE);
            return;
        }

        System.out.println(selectedMetrics);

    }

    private Dataset findDatasetByName(String name) {
        for (Dataset ds : datasetService.getDatasets()) {
            if (ds.getName().equals(name)) return ds;
        }
        return null;
    }

}