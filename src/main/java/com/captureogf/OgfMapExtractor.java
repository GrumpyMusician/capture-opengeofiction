
package com.captureogf;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class OgfMapExtractor {

    JFrame frame = new JFrame("Capture OpenGeofiction");
    BoundingBoxMap map = new BoundingBoxMap();

    public OgfMapExtractor() {
        Stage stage = new Stage();
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

        /*
            BBOX PANEL
        */
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

        /*
            CONFIG PANEL
        */
        JPanel configPanel = new JPanel(new GridLayout(2, 7, 5, 5));
        JSlider zoom = new JSlider(0, 19);
        JLabel zoomLabel = new JLabel("Zoom: " + String.valueOf(zoom.getValue()));
        configPanel.add(new JLabel("Previous"));
        configPanel.add(zoomLabel);
        for (String label : new String[]{"Destination Folder", "Image Config", "Number of Rows", "Number of Columns", "Next"}){
            configPanel.add(new JLabel(label));
        }
        
        configPanel.add(prev);
        configPanel.add(zoom);
        JButton directorySelect = new JButton("Select Folder");
        configPanel.add(directorySelect);
        String[] configSet = {"One Image", "Grid of Images"};
        JComboBox<String> exportConfigMap = new JComboBox<>(configSet);
        configPanel.add(exportConfigMap);
        JTextField numRows = new JTextField("1"), numCols = new JTextField("1");
        numRows.setEnabled(false);
        numCols.setEnabled(false);
        configPanel.add(numRows);
        configPanel.add(numCols);

        configPanel.setBorder(border);

        /* 
            LISTENERS
        */
        map.setBoundingBoxListener(c -> {
            x1.setText("" + c[3]);
            y1.setText("" + c[2]);
            x2.setText("" + c[1]);
            y2.setText("" + c[0]);
        });

        presets.addActionListener(e -> {
            Preset selected = (Preset) presets.getSelectedItem(); 
            if (selected.getName().equals("Custom Coordinates")){
                map.setLock(false);
                x1.setEnabled(true);
                y1.setEnabled(true);
                x2.setEnabled(true);
                y2.setEnabled(true);
                submit.setEnabled(true);
            } else {
                map.setLock(true);
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

        exportConfigMap.addActionListener(e -> {
            if (exportConfigMap.getSelectedIndex() == 0){
                numRows.setText("1");
                numCols.setText("1");
                numRows.setEnabled(false);
                numCols.setEnabled(false);
            } else {
                numRows.setEnabled(true);
                numCols.setEnabled(true);
            }
        });

        directorySelect.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setDialogTitle("Select a Folder");
            chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home"), "Downloads"));
            int result = chooser.showOpenDialog(null); 

            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFolder = chooser.getSelectedFile();
                String folderPath = selectedFolder.getAbsolutePath();
                
                directorySelect.setText(folderPath);
            }
        });

        x1.addActionListener(e -> submitCoordinates(x1, y1, x2, y2));
        y1.addActionListener(e -> submitCoordinates(x1, y1, x2, y2));
        x2.addActionListener(e -> submitCoordinates(x1, y1, x2, y2));
        y2.addActionListener(e -> submitCoordinates(x1, y1, x2, y2));

        submit.addActionListener(e -> submitCoordinates(x1, y1, x2, y2));

        zoom.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                zoomLabel.setText("Zoom: " + String.valueOf(zoom.getValue()));
            }
        });
        
        next.addActionListener(e -> {
            if(!checkCoordinates(x1, y1, x2, y2)){
                JOptionPane.showMessageDialog(frame, "Enter valid coordinates.");
                return;
            }
            if(!checkGrid(numRows)){
                JOptionPane.showMessageDialog(frame, "Number of rows and columns must be positive.");
                return;
            }
            if (!checkDirectory(directorySelect) && stage.isConfigStage()){
                JOptionPane.showMessageDialog(frame, "Select your destination folder.");
                return;
            }
            if (stage.isBoundingStage()){
                configPanel.add(next);
                frame.remove(bboxPanel);
                frame.add(configPanel, BorderLayout.SOUTH);
                frame.revalidate();
                frame.repaint();
                map.setLock(true);
            } else {
                frame.remove(configPanel);
                frame.revalidate();
                frame.repaint();

                ImageHandler exportImage = new ImageHandler(Double.parseDouble(x1.getText()), Double.parseDouble(y1.getText()), Double.parseDouble(x2.getText()), Double.parseDouble(y2.getText()), zoom.getValue(), Integer.parseInt(numRows.getText()), Integer.parseInt(numCols.getText()), directorySelect.getText(), "https://tile.opengeofiction.net/ogf-carto/{z}/{x}/{y}.png");
                exportImage.createWindow();
                exportImage.createDestination("outputCaptureOGF");
                exportImage.importImages();
            }
            stage.nextStage();
        });

        prev.addActionListener(e -> {
            bboxPanel.add(next);
            frame.remove(configPanel);
            frame.add(bboxPanel, BorderLayout.SOUTH);
            frame.revalidate();
            frame.repaint();
            map.setLock(false);
            stage.prevStage();
        });

        /* 
            FINAL STUFF
        */
        map.setDisplayPosition(new Coordinate(36, 129), 5);
        frame.add(map, BorderLayout.CENTER);
        frame.add(bboxPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void submitCoordinates(JTextField x1, JTextField y1, JTextField x2, JTextField y2){
        try {
            x1.setText(String.valueOf(Math.max(-180.0, Math.min(180.0, Double.parseDouble(x1.getText())))));
            y1.setText(String.valueOf(Math.max(-90.0, Math.min(90.0, Double.parseDouble(y1.getText())))));
            x2.setText(String.valueOf(Math.max(-180.0, Math.min(180.0, Double.parseDouble(x2.getText())))));
            y2.setText(String.valueOf(Math.max(-90.0, Math.min(90.0, Double.parseDouble(y2.getText())))));

            map.setBoundingBox(
                Double.parseDouble(y1.getText()),
                Double.parseDouble(x1.getText()),
                Double.parseDouble(y2.getText()),
                Double.parseDouble(x2.getText())
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Enter valid coordinates.");
        }
    }

    public boolean checkCoordinates(JTextField x1, JTextField y1, JTextField x2, JTextField y2) {
        try {
            double x1Val = Double.parseDouble(x1.getText());
            double y1Val = Double.parseDouble(y1.getText());
            double x2Val = Double.parseDouble(x2.getText());
            double y2Val = Double.parseDouble(y2.getText());

            if ((y1Val >= -90 && y1Val <= 90) && 
                (y2Val >= -90 && y2Val <= 90) && 
                (x1Val >= -180 && x1Val <= 180) && 
                (x2Val >= -180 && x2Val <= 180)) {
                return true;
            }
            
        } catch (NumberFormatException ex) {
            return false;
        }
        
        return false;
    }

    public boolean checkGrid(JTextField val) {
        try {
            int value = Integer.parseInt(val.getText());
            
            if (value >= 1) {
                return true;
            }
            
        } catch (NumberFormatException ex) {
            return false;
        }
        
        return false;
    }

    public boolean checkDirectory(JButton val){
        return !val.getText().equals("Select Folder");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OgfMapExtractor::new);
    }
}
