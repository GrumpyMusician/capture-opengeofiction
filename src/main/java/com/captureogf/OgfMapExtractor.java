
package com.captureogf;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import org.openstreetmap.gui.jmapviewer.Coordinate;


public class OgfMapExtractor {

    JFrame frame = new JFrame("Capture OpenGeofiction");
    BoundingBoxMap map = new BoundingBoxMap();

    public OgfMapExtractor() {
        int stage = 0; // 0: bbox; 1: final config; 2: export!
        Preset[] listPresets = {
            new Preset("Custom Coordinates"),
            new Preset("East Uletha", 63.140, 72.548, 175.702, 23.016),
            new Preset("East Uletha Minor", 96.895, 47.904, 150.051, 25.247),
            new Preset("Tarephia", -17.3502, 35.4212, 58.8065, -26.8107),
            new Preset("World", -18.1100, 80.6825, 179.8627, -75.9798)
        };
        
        Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        JButton next = new JButton("Next");
        JButton prev = new JButton("Previous");

        JPanel bboxPanel = new JPanel(new GridLayout(2, 7, 5, 5));
        for (String label : new String[]{"Presets", "North X", "North Y", "South X", "South Y", "Input Bounds ", "Next"}){
            bboxPanel.add(new JLabel(label));
        }
        JTextField x1 = new JTextField(8), y1 = new JTextField(8);
        JTextField x2 = new JTextField(8), y2 = new JTextField(8);
        JComboBox<Preset> presets = new JComboBox<>(listPresets);
        JButton submit = new JButton("Enter Bounds");
        bboxPanel.add(presets);
        for (JTextField field : new JTextField[]{x1, y1, x2, y2})
        bboxPanel.add(field);
        bboxPanel.add(submit);
        bboxPanel.add(next);
        bboxPanel.setBorder(border);

        JPanel configPanel = new JPanel(new GridLayout(2, 6, 5, 5));
        for (String label : new String[]{"Previous", "Zoom", "Export Folder", "Number of Rows", "Number of Columns", "Next"}){
            configPanel.add(new JLabel(label));
        }
        JSlider zoom = new JSlider(0, 19);
        zoom.setMajorTickSpacing(5);
        zoom.setMinorTickSpacing(1);
        zoom.setPaintTicks(true);
        zoom.setPaintLabels(true);
        configPanel.add(prev);
        configPanel.add(zoom);
        configPanel.setBorder(border);


        map.setBoundingBoxListener(c -> {
            x1.setText("" + c[3]);
            y1.setText("" + c[2]);
            x2.setText("" + c[1]);
            y2.setText("" + c[0]);
        });

        presets.addActionListener(e -> {
            Preset selected = (Preset) presets.getSelectedItem(); 
            if (selected.getName().equals("Custom Coordinates")){
                map.setLock(true);
                x1.setEnabled(true);
                y1.setEnabled(true);
                x2.setEnabled(true);
                y2.setEnabled(true);
                submit.setEnabled(true);
            } else {
                map.setLock(false);
                x1.setEnabled(false);
                y1.setEnabled(false);
                x2.setEnabled(false);
                y2.setEnabled(false);
                submit.setEnabled(false);
                x1.setText(String.valueOf(selected.getCoordinates()[0]));
                y1.setText(String.valueOf(selected.getCoordinates()[1]));
                x2.setText(String.valueOf(selected.getCoordinates()[2]));
                y2.setText(String.valueOf(selected.getCoordinates()[3]));

                map.setBoundingBox(
                    Double.parseDouble(y1.getText()),
                    Double.parseDouble(x1.getText()),
                    Double.parseDouble(y2.getText()),
                    Double.parseDouble(x2.getText())
                );
            }
        });

        x1.addActionListener(e -> submitCoordinates(x1, y1, x2, y2));
        y1.addActionListener(e -> submitCoordinates(x1, y1, x2, y2));
        x2.addActionListener(e -> submitCoordinates(x1, y1, x2, y2));
        y2.addActionListener(e -> submitCoordinates(x1, y1, x2, y2));

        submit.addActionListener(e -> submitCoordinates(x1, y1, x2, y2));
        
        next.addActionListener(e -> {
            if (stage == 0){
                frame.remove(bboxPanel);
                frame.add(configPanel, BorderLayout.SOUTH);
                frame.revalidate();
                frame.repaint();
            } else {

            }
        });

        prev.addActionListener(e -> {
            if (stage == 1){

            } else {

            }
        });

        map.setDisplayPosition(new Coordinate(36, 129), 5);

        frame.add(map, BorderLayout.CENTER);
        frame.add(bboxPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void submitCoordinates(JTextField x1, JTextField y1, JTextField x2, JTextField y2){
        try {map.setBoundingBox(
                Double.parseDouble(y1.getText()),
                Double.parseDouble(x1.getText()),
                Double.parseDouble(y2.getText()),
                Double.parseDouble(x2.getText())
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Enter valid coordinates.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OgfMapExtractor::new);
    }
}
