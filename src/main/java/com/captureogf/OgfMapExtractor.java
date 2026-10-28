
package com.captureogf;

import javax.swing.*;
import java.awt.*;
import org.openstreetmap.gui.jmapviewer.Coordinate;


public class OgfMapExtractor {
    public OgfMapExtractor() {
        Preset[] listPresets = {
            new Preset("East Uletha", 63.140, 72.548, 175.702, 23.016),
            new Preset("East Uletha Minor", 96.895, 47.904, 150.051, 25.247),
            new Preset("Tarephia", -17.3502, 35.4212, 58.8065, -26.8107),
            new Preset("World", -18.1100, 80.6825, 179.8627, -75.9798)
        };

        String[] namePresets = new String[listPresets.length + 1];
        namePresets[listPresets.length] = "Custom Coordinates";
        for (int i = 0; i < listPresets.length; i++){
            namePresets[i] = listPresets[i].getName();
        }

        JFrame frame = new JFrame("Capture OpenGeofiction");
        BoundingBoxMap map = new BoundingBoxMap();

        JTextField x1 = new JTextField(8), y1 = new JTextField(8);
        JTextField x2 = new JTextField(8), y2 = new JTextField(8);
        JButton submit = new JButton("Submit Bounds");

        JPanel panel = new JPanel(new GridLayout(2, 6, 5, 5));

        for (String label : new String[]{"Presets", "North X", "North Y", "South X", "South Y", ""})
            panel.add(new JLabel(label));

        JComboBox presets = new JComboBox(namePresets);
        panel.add(presets);

        for (JTextField field : new JTextField[]{x1, y1, x2, y2})
        panel.add(field);

        panel.add(submit);

        map.setBoundingBoxListener(c -> {
            x1.setText("" + c[3]);
            y1.setText("" + c[2]);
            x2.setText("" + c[1]);
            y2.setText("" + c[0]);});

        submit.addActionListener(e -> {
            try {
                map.setBoundingBox(
                    Double.parseDouble(y2.getText()),
                    Double.parseDouble(x1.getText()),
                    Double.parseDouble(y1.getText()),
                    Double.parseDouble(x2.getText())
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter valid coordinates.");
            }
        });

        map.setDisplayPosition(new Coordinate(36, 129), 11);

        frame.add(map, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OgfMapExtractor::new);
    }
}
