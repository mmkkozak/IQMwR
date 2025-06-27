package iqmwr_plugin;

import net.imagej.ImageJ;

public class Main {

    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();

        // load the dataset
//        final Dataset dataset = ij.scifio().datasetIO().open(file.getPath());

        // display the dataset
//        ij.ui().show(dataset);
        //TODO
        // check for open windows and count - done
        // prompt to open images if count < 2 - done
        // exception - "cancel" opening files breaks the plugin execution - irrelevant
        // prepare the plugin menu - done
        // get user's choice - done
        // prepare the result table - done
        // calculate the result

    }

}
