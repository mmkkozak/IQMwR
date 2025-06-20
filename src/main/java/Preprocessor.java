import net.imagej.display.ImageDisplayService;
import org.scijava.module.Module;
import org.scijava.module.process.AbstractPreprocessorPlugin;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = PreprocessorPlugin.class)
public class Preprocessor extends AbstractPreprocessorPlugin {

    @Parameter
    private ImageDisplayService displayService;

    @Override
    public void process(Module module) {
        if (!module.getDelegateObject().getClass().equals(ImageQualityRM.class)) return;

        // ensuring images to run the plugin are open
        if (displayService.getImageDisplays().size() < 2) {
            cancel("You need to open at least 2 images first.");
        }
    }
}
