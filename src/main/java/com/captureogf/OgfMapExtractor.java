package com.captureogf;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class OgfMapExtractor {
    public OgfMapExtractor() {
        JFrame frame = new JFrame("OpenGeofiction Map Extractor");

        frame.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;

        BoundingBoxMap map = new BoundingBoxMap();
        map.setDisplayPosition(new Coordinate(36, 129), 11);
        //frame.add(map, gbc);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OgfMapExtractor::new);
    }
}