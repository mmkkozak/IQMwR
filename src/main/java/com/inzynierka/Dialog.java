package com.inzynierka;

import ij.IJ;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

public class Dialog implements ActionListener {
    JDialog dialog = new JDialog(IJ.getInstance(), "Image Quality Measurement Markers");
    JPanel dialogPanel = new JPanel(new BorderLayout());
    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints constraint = new GridBagConstraints();
    BoxLayout boxLayout = new BoxLayout(dialogPanel, BoxLayout.Y_AXIS);
    JPanel refImagePanel = new JPanel(layout);
    JPanel testImagePanel = new JPanel(layout);
    JPanel modePanel = new JPanel(layout);
    JToolBar tool = new JToolBar();
    JButton computeButton = new JButton("Compute");
    JButton cancelButton = new JButton("Cancel");
    JComboBox cmbRefImage = new JComboBox();
    JComboBox cmbImage1 = new JComboBox();
    JComboBox cmbImage2 = new JComboBox();
    JComboBox cmbImage3 = new JComboBox();
    JComboBox cmbImage4 = new JComboBox();
    JCheckBox SSIM = new JCheckBox("SSIM");
    JCheckBox SNR = new JCheckBox("SNR");
    JCheckBox PSNR = new JCheckBox("PSNR");
    JCheckBox RMSE = new JCheckBox("RMSE");
    JCheckBox MAE = new JCheckBox("MAE");


    Dialog(ArrayList<Image> imageList) {
        boolean[] modeStates = new boolean[]{true, true, true, true, true};

//        cmbRefImage .addItem("0123456789012345678901234567890123456789");
//        cmbImage1 .addItem("0123456789012345678901234567890123456789");
//        cmbImage2 .addItem("0123456789012345678901234567890123456789");
//        cmbImage3 .addItem("0123456789012345678901234567890123456789");
//        cmbImage4 .addItem("0123456789012345678901234567890123456789");


        //Choose image Section
        //Reference image
        refImagePanel.setBorder(BorderFactory.createEtchedBorder());
        addComponent( refImagePanel, 0, 0, 1, 1, 5, new JLabel("Image: "));
        addComponent( refImagePanel, 0,  1,1,2,5, cmbRefImage );


        //Test Image
        testImagePanel.setBorder(BorderFactory.createEtchedBorder());
        // 1 - required
        addComponent(testImagePanel, 2, 0, 1, 1, 5,new JLabel("Image 1: "));
        addComponent(testImagePanel, 2,  1,1,1,5, cmbImage1 );

        //2 - optional
        addComponent(testImagePanel, 3, 0, 1, 1, 5, new JLabel("Image 2: "));
        addComponent(testImagePanel, 3,  1,1,1,5, cmbImage2 );
        addComponent(testImagePanel, 3,  2,1,1,5, new JLabel("(Optional)"));

        //3 - optional
        addComponent(testImagePanel, 4, 0, 1, 1, 5, new JLabel("Image 3: "));
        addComponent(testImagePanel, 4,  1,1,1,5, cmbImage3 );
        addComponent(testImagePanel, 4,  2,1,1,5, new JLabel("(Optional)"));

        //4 - optional
        addComponent(testImagePanel, 5, 0, 1, 1, 5, new JLabel("Image 4: "));
        addComponent(testImagePanel, 5,  1,1,1,5, cmbImage4 );
        addComponent(testImagePanel, 5,  2,1,1,5, new JLabel("(Optional)"));


        //Choose mode/s Section
        modePanel.setBorder(BorderFactory.createEtchedBorder());
        addComponent(modePanel, 0,0,1,1,5 , SSIM);
        addComponent(modePanel, 0,1,1,1,5 , SNR);
        addComponent(modePanel, 0,2,1,1,5 , PSNR);
        addComponent(modePanel, 0,3,1,1,5 , RMSE);
        addComponent(modePanel, 0,4,1,1,5 , MAE);


        //Button Section
        computeButton.setFocusable(false);
        cancelButton.setFocusable(false);


        //Tool Bar
        tool.add(computeButton);
        tool.add(cancelButton);


        //Add Listeners
        computeButton.addActionListener(this);
        cancelButton.addActionListener(this);
        SSIM.addActionListener(this);
//        refImagePanel.addComponentListener((ComponentListener) this);
//        testImagePanel.addComponentListener(this);
//        modePanel.addComponentListener(this);


        //Dialog Panel
        dialogPanel.setLayout(boxLayout);

        TitledBorder refImageTitlePanel;
        refImageTitlePanel =  BorderFactory.createTitledBorder("Reference");
        refImagePanel.setBorder(refImageTitlePanel);
        dialogPanel.add(refImagePanel);

        TitledBorder imageTitlePanel;
        imageTitlePanel =  BorderFactory.createTitledBorder("Test");
        testImagePanel.setBorder(imageTitlePanel);
        dialogPanel.add(testImagePanel);

        TitledBorder modeTitlePanel;
        modeTitlePanel =  BorderFactory.createTitledBorder("Mode");
        modePanel.setBorder(modeTitlePanel);
        dialogPanel.add(modePanel);

        dialogPanel.add(tool);


        //Dialog
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.add(dialogPanel);

        dialog.setResizable(true);
        dialog.pack();

        dialog.setMinimumSize(dialog.getSize());
        dialog.pack();
        dialog.setVisible(true);
    }


    //Function to add component to panel
    private void addComponent(JPanel imagePanel, int row, int col, int width, int height, int space, JComponent component) {
        constraint.gridy = row;
        constraint.gridx = col;
        constraint.gridwidth = width;
        constraint.gridheight = height;
        constraint.insets = new Insets(space, space, space, space);
        constraint.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(component, constraint);
        imagePanel.add(component);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == computeButton){
            Results results = new Results();
        }
        if(e.getSource() == cancelButton){
            System.exit(0);
        }
        if(e.getSource() == SSIM) {
          SSIM ssim = new SSIM();
        }

//        if(e.getSource() == MAE) {
//            MAE mae = new MAE();
//        }
    }


}
