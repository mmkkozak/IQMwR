package net.iqmwr_plugin;

import net.imagej.DatasetService;
import net.imagej.Dataset;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imagej.ops.OpService;
import net.imagej.table.DefaultResultsTable;
import net.imagej.table.ResultsTable;
import org.scijava.command.DynamicCommand;
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
    private OpService opService;
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

    @Parameter(description = "MSE", label = "Mean Squared Error (MSE)")
    private Boolean useMSE;
    @Parameter(description = "RMSE", label = "Root Mean Squared Error (RMSE)")
    private Boolean useRMSE;
    @Parameter(description = "MAE", label = "Mean Absolute Error (MAE)")
    private Boolean useMAE;
    @Parameter(description = "SNR", label = "Signal-to-Noise Ratio (SNR)")
    private Boolean useSNR;
    @Parameter(description = "CNR", label = "Contrast-to-Noise Ratio (CNR)")
    private Boolean useCNR;
    @Parameter(description = "PSNR", label = "Peak Signal-to-Noise Ratio (PSNR)")
    private Boolean usePSNR;
    @Parameter(description = "SSIM", label = "Structural Similarity Index Measure (SSIM)")
    private Boolean useSSIM;
    @Parameter(description = "MSSIM", label = "Mean Structural Similarity Index Measure (MSSIM)")
    private Boolean useMSSIM;
    @Parameter(description = "UIQI", label = "Universal Image Quality Index (UIQI)")
    private Boolean useUIQI;
    @Parameter(description = "MS-SSIM", label = "Multi-scale Structural Similarity Index Measure (MS-SSIM)")
    private Boolean useMS_SSIM;
    @Parameter(description = "FSIM", label = "Feature Similarity Index Matrix (FSIM)")
    private Boolean useFSIM;
    @Parameter(description = "GMSD", label = "Gradient Magnitude Similarity Deviation (GMSD)")
    private Boolean useGMSD;
    final private List<MutableModuleItem<Boolean>> checkboxes = new ArrayList<>();
    final private Map<String, Boolean> selectedMetrics = new HashMap<>();

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
                selectedMetrics.put(checkbox.getDescription(), true);
            }
        }

        // ensuring a metric to calculate is chosen
        if (!selectedMetrics.containsValue(true)) {
            uiService.showDialog("No metric was chosen - action canceled.", DialogPrompt.MessageType.WARNING_MESSAGE);
            return;
        }

        // preparing the result table
        ResultsTable table = new DefaultResultsTable();
        table.appendColumns("Measure value");
        // populating the result table
        Map<String, Boolean> sortedMetrics = new TreeMap<>(selectedMetrics);
        for (String metric : sortedMetrics.keySet()) {
            if (sortedMetrics.get(metric)) {
                if(metric.equals("SNR")) {
                    table.appendRow(metric + " 1");
                    table.appendRow(metric + " 2");
                } else if (metric.equals("CNR")) {
                    table.appendRow(metric + " 1");
                    table.appendRow(metric + " 2");
                    table.appendRow(metric + " 3");
                } else table.appendRow(metric);
                double val;
                switch (metric) {
                    case "MAE":
                        val = Statistics.mae(refDataset, testDataset, opService, datasetService);
                        break;
                    case "MSE":
                        val = Statistics.mse(refDataset, testDataset);
                        break;
                    case "RMSE":
                        val = Statistics.rmse(refDataset, testDataset);
                        break;
                    case "SNR":
                        val = SNR.snr1(refDataset, testDataset, opService, datasetService);
                        table.set(0, table.getRowCount()-2, val);
                        val = SNR.snr2(refDataset, testDataset, opService, datasetService);
                        break;
                    case "CNR":
                        val = CNR.cnr1(refDataset, testDataset, opService, datasetService);
                        table.set(0, table.getRowCount()-3, val);
                        val = CNR.cnr2(refDataset, testDataset, opService, datasetService);
                        table.set(0, table.getRowCount()-2, val);
                        val = CNR.cnr3(refDataset, testDataset, opService, datasetService);
                        break;
                    case "PSNR":
                        val = PSNR.psnr(refDataset, testDataset);
                        break;
                    case "SSIM":
                        val = SSIM.ssim(refDataset, testDataset, opService);
                        break;
                    case "MSSIM":
                        val = SSIM.mssim(refDataset, testDataset);
                        break;
                    case "UIQI":
                        val = SSIM.q_index(refDataset, testDataset);
                        break;
                    case "MS-SSIM":
                        val = SSIM.multiscale_ssim(refDataset, testDataset, opService, datasetService);
                        break;
                    case "FSIM":
                        val = FSIM.fsim(refDataset, testDataset, opService);
                        break;
                    case "GMSD":
                        val = GMSD.gmsd(refDataset, testDataset, opService, datasetService);
                        break;
                    default:
                        val = 0;
                }
                table.set(0, table.getRowCount()-1, val);
            }
        }

        String tableName = "Results for: reference = '" + referenceImageName + "', test = '" + testImageName + "'";
        uiService.show(tableName, table);

    }

    private Dataset findDatasetByName(String name) {
        for (Dataset ds : datasetService.getDatasets()) {
            if (ds.getName().equals(name)) return ds;
        }
        return null;
    }

}