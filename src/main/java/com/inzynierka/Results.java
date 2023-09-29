package com.inzynierka;

import ij.IJ;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Results {
    JDialog dialog = new JDialog(IJ.getInstance(), "Results");
    DefaultTableModel model = new DefaultTableModel();
    JTable table = new JTable(model);
    JPanel resultPanel = new JPanel();
    Results() {
        //Table Model
        model.addColumn("Test image");
        model.addColumn("SSIM");
        model.addColumn("SNR [db]");
        model.addColumn("PSNE [db]");
        model.addColumn("RMSE");
        //...

        //Table
        table.setShowGrid(true);

        //Panel
        resultPanel.setPreferredSize(new Dimension(300,200));
        resultPanel.add(table);

        dialog.add(resultPanel);
        dialog.pack();
        dialog.setVisible(true);

    }
}
