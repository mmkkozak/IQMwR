package net.iqmwr_plugin;

import ij.ImagePlus;
import net.imagej.DatasetService;
import net.imagej.ImageJ;
import net.imagej.Dataset;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imagej.ops.OpService;
import net.imagej.table.DefaultResultsTable;
import net.imagej.table.ResultsTable;
import org.scijava.command.DynamicCommand;
import org.scijava.convert.ConvertService;
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

    @Parameter(label = "SSIM")
    private Boolean useSSIM;
    @Parameter(label = "MSSIM")
    private Boolean useMSSIM;
    @Parameter(label = "SNR1")
    private Boolean useSNR1;
    @Parameter(label = "SNR2")
    private Boolean useSNR2;
    @Parameter(label = "CNR1")
    private Boolean useCNR1;
    @Parameter(label = "CNR2")
    private Boolean useCNR2;
    @Parameter(label = "CNR3")
    private Boolean useCNR3;
    @Parameter(label = "PSNR")
    private Boolean usePSNR;
    @Parameter(label = "RMSE")
    private Boolean useRMSE;
    @Parameter(label = "MAE")
    private Boolean useMAE;
    @Parameter(label = "MSE")
    private Boolean useMSE;

    private List<MutableModuleItem<Boolean>> checkboxes = new ArrayList<>();
    private Map<String, Boolean> selectedMetrics = new HashMap<>();

    @Parameter
    private ConvertService convertService;

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

        // preparing the result table
        ResultsTable table = new DefaultResultsTable();
        table.appendColumns("Measure value");
        // populating the result table
        for (String metric : selectedMetrics.keySet()) {
            if (selectedMetrics.get(metric)) {
                table.appendRow(metric);
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
                    case "SNR1":
                        val = SNR.snr1(refDataset, testDataset, opService, datasetService);
                        break;
                    case "SNR2":
                        val = SNR.snr2(refDataset, testDataset, opService, datasetService);
                        break;
                        //TODO check why CNRs turn is reversed in the table
                    case "CNR1":
                        val = CNR.cnr1(refDataset, testDataset, opService, datasetService);
                        break;
                    case "CNR2":
                        val = CNR.cnr2(refDataset, testDataset, opService, datasetService);
                        break;
                    case "CNR3":
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
                    default:
                        val = 0;
                }
                table.set(0, table.getRowCount()-1, val);
            }
        }

        String tableName = "Results for: reference = '" + referenceImageName + "', test = '" + testImageName + "'";
        uiService.show(tableName, table);

//        ImagePlus imp1 = convertService.convert(refDataset.getImgPlus(), ImagePlus.class);
//        ImagePlus imp2 = convertService.convert(testDataset.getImgPlus(), ImagePlus.class);
//        if (imp1 == null || imp2 == null) {
//            System.out.println("Błąd: konwersja Dataset -> ImagePlus zwróciła null");
//            return;
//        }
//
//        if (imp1.getStack() == null || imp2.getStack() == null) {
//            System.out.println("Błąd: ImagePlus nie ma stacka!");
//            return;
//        }
//        System.out.println(CNR_old.getCNR(imp1, imp2));


    }

    private Dataset findDatasetByName(String name) {
        for (Dataset ds : datasetService.getDatasets()) {
            if (ds.getName().equals(name)) return ds;
        }
        return null;
    }

}