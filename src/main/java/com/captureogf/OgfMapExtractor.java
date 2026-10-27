package com.captureogf;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;

public class OgfMapExtractor {
    public OgfMapExtractor() {
        JFrame frame = new JFrame("OpenGeofiction Map Extractor");
        BoundingBoxMap map = new BoundingBoxMap();

        // Center on London, zoom level 11
        map.setDisplayPosition(new Coordinate(36, 129), 11);

        JButton extractButton = new JButton("Extract area");
        extractButton.addActionListener(e -> {
            if (!map.hasSelection()) {
                JOptionPane.showMessageDialog(frame, "Drag a rectangle on the map first.");
                return;
            }

            System.out.printf(
                "Bounding box:%n"
                + "Minimum latitude:  %.6f%n"
                + "Minimum longitude: %.6f%n"
                + "Maximum latitude:  %.6f%n"
                + "Maximum longitude: %.6f%n",
                map.getMinLat(),
                map.getMinLon(),
                map.getMaxLat(),
                map.getMaxLon()
            );
        });

        frame.setLayout(new BorderLayout());
        frame.add(map, BorderLayout.CENTER);
        frame.add(extractButton, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OgfMapExtractor::new);
    }
}