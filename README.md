**Image Quality Measures with Reference for ImageJ Fiji**

This repository contains source code for an ImageJ Fiji plugin. The plugin implements 12 full-reference image quality assessment (FR-IQA) measures for grayscale images:
MSE, RMSE, MAE, SNR, CNR, PSNR, SSIM, MSSIM, UIQI, MS-SSIM, FSIM and GMSD.


**Installing the plugin**

In case of installing a newer version over a previous release, removing an old .jar file from Fiji.app\plugins\jars folder is required.

1. Download the versioned .jar file, either from master branch or realeses of this repository.
2. Open ImageJ Fiji. From the menu bar, choose "Plugins > Install". Select the downloaded .jar file and follow the program prompts.
3. To use the plugin, navigate to "Plugins > Analyze > Image Quality Measures with Reference".

**Running the plugin**

The plugin should not run if there are less than 2 images open in your ImageJ.

After it runs, the user has to choose a reference and test image.

If the same image is chosen as test and reference, the plugin will inform the user about it.

The user can choose any of the available IQA metrics.
Choosing SNR will calculate 2 variants and CNR 3 variants of the measure.

In case you want to see the full measure name, you can hover over the checkbox label.

<img height="400" alt="image" align="center" src="https://github.com/user-attachments/assets/4dd33508-f493-4851-8384-0bdaec4103b5" />
<br></br>
After confirming choices, a few seconds may pass until the result table appears.
<br></br>
<img height="350" alt="image" align="center" src="https://github.com/user-attachments/assets/e577e95e-c785-4531-ad6f-3cfabdddaa34" />

<br>A legacy results table was added in verion 1.1.0 for ImageJ Macro simplified usage.</br>

<b>Saving the results</b>

You can select all the table fields, copy and paste e.g. to a spreadsheet **or** choose the result table window, than in the ImageJ menu bar "File > Export > Table..." and save it as a .csv file.

