Image Quality Measurement Markers (IQMM) is a plug-in for ImageJ (Fiji) to calculate full-reference quality metrics. 
The plugin allows you to calculate indicators such as SSIM, SNR [db], CNR [db], PSNR [db], RMSE and MAE.

![image](https://github.com/Gysiaq/IQMM/assets/116911891/f05d0447-2d99-4831-9611-a7c156b620e3)

Plugin works on Macro:
run("IQMM", "reference=Image1.png test=Image2.png ssim snr cnr psnr rmse mae");
