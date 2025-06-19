import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import org.scijava.command.CommandModuleItem;
import org.scijava.module.Module;
import org.scijava.module.ModuleInfo;
import org.scijava.module.ModuleItem;
import org.scijava.module.MutableModuleItem;
import org.scijava.module.process.AbstractPreprocessorPlugin;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;

import java.util.*;

@Plugin(type = PreprocessorPlugin.class)
public class Preprocessor extends AbstractPreprocessorPlugin {
    @Parameter
    private ImageDisplayService displayService;
    @Parameter
    private UIService uiService;

    @Override
    public void process(Module module) {
        if (!module.getDelegateObject().getClass().equals(ImageQualityRM.class)) return;

        HashMap<String, ImageDisplay> nameToDataset = new HashMap<>();
        for (ImageDisplay d : displayService.getImageDisplays()) {
            String name = d.getName();
            if (!nameToDataset.containsKey(name)) {
                nameToDataset.put(name, d);
            }
        }
//        System.out.println("Name: " + nameToDataset.keySet());
        if (nameToDataset.size() < 2) {
//            uiService.showDialog("Otwórz co najmniej 2 obrazy.", DialogPrompt.MessageType.ERROR_MESSAGE);
            cancel("You need to open at least 2 images.");
            return;
        }
        List<String> names = new ArrayList<>(nameToDataset.keySet());
//        String[] names =  new String[nameToDataset.size()];
//        int i = 0;
//        for (String name : nameToDataset.keySet()) {
//            names[i] =  name;
//            i++;
//        }
//        System.out.println(names.toString());
        ModuleInfo info = module.getInfo();
//        @SuppressWarnings("unchecked")
//        MutableModuleItem<String> refItem = (MutableModuleItem<String>) info.getInput("referenceImage");
//        refItem.setChoices(names);
////        MutableModuleItem<String> test = module.getInfo().getInput("testImage");
////        test.setChoices(names);
//        @SuppressWarnings("unchecked")
//        MutableModuleItem<String> testItem = (MutableModuleItem<String>) info.getInput("referenceImage");
//        testItem.setChoices(names);
//        for (String name : Arrays.asList("referenceImage", "testImage")) {
//            ModuleItem<?> item = info.getInput(name);
////            System.out.println(item.getChoices());
//            if (item instanceof CommandModuleItem) {
//                @SuppressWarnings("unchecked")
//                CommandModuleItem<Dataset> cmdItem = (CommandModuleItem<Dataset>) item;
//                cmdItem.set("choices", "names");
//                System.out.println(cmdItem.is("choices"));
//            }
//        }

        System.out.println("JEST");
    }
}
