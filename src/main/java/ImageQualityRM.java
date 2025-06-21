import net.imagej.DatasetService;
import net.imagej.ImageJ;
import net.imagej.Dataset;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import org.scijava.command.DynamicCommand;
import org.scijava.display.Display;
import org.scijava.module.ModuleItem;
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
    private Boolean useSSIM;
    @Parameter(label = "SNR")
    private Boolean useSNR;
    @Parameter(label = "CNR")
    private Boolean useCNR;
    @Parameter(label = "PSNR")
    private Boolean usePSNR;
    @Parameter(label = "RMSE")
    private Boolean useRMSE;
    @Parameter(label = "MAE")
    private Boolean useMAE;

    private List<MutableModuleItem<Boolean>> checkboxes = new ArrayList<>();
    private Map<String, Boolean> selectedMetrics = new HashMap<>();

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

        // initializing selectedMetrics map with false values and preparing checkboxes list for checking values
        for (ModuleItem<?> input : getInfo().inputs()) {
            if (input.getType() == Boolean.class && input instanceof MutableModuleItem ) {
                @SuppressWarnings("unchecked")
                MutableModuleItem<Boolean> item = (MutableModuleItem<Boolean>) input;
                checkboxes.add(item);
                selectedMetrics.put(input.getLabel(), false);
            }
        }

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
        } else if (!refDataset.getType().getClass().equals(testDataset.getType().getClass())) {
            uiService.showDialog("Error: Images must be of the same bit depth.",
                    DialogPrompt.MessageType.ERROR_MESSAGE);
            return;
        }

        // retrieving checkboxes values to a dictionary
        for (MutableModuleItem<Boolean> checkbox : checkboxes) {
            if (checkbox.getValue(this)) {
                selectedMetrics.put(checkbox.getLabel(), true);
            }
        }

        // ensuring a metric to calculate is chosen
        if (!selectedMetrics.containsValue(true)) {
            uiService.showDialog("No metric was chosen - action canceled.", DialogPrompt.MessageType.WARNING_MESSAGE);
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