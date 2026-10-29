package com.captureogf;

import javax.swing.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

public class ImageHandler {
    private double x1, y1, x2, y2;
    private int zoom, numRows, numCols;
    private String url;
    private Path destination;
    private ProgressWindow window = new ProgressWindow();

    public ImageHandler(double x1, double y1, double x2, double y2, int zoom, int numRows, int numCols, String destination, String url){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.zoom = zoom;
        this.numRows = numRows;
        this.numCols = numCols;
        this.destination = Path.of(destination);
        this.url = url;
    }

    public void createWindow(){
        JFrame frame = new JFrame("Progress");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this.window);
        frame.pack();
        frame.setSize(500,400);
        frame.setVisible(true);
    }

    public Path createDestination(String newFolder){
        this.window.setLabel("Setting up directories...");
        this.window.updateProgress(0, 2);
        try {
            this.window.addOutput("Checking for existing folder " + newFolder, true);

            Path newFolderPath = destination.resolve(newFolder);

            if (Files.exists(newFolderPath)) {
                this.window.addOutput("New folder found.");
                this.window.addOutput("Removing existing folder " + newFolder, true);

                try (var paths = Files.walk(newFolderPath)) {
                    for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                        Files.delete(path);
                    }
                }
            } else {
                this.window.addOutput("No new folder found.");
            }
            this.window.updateProgress(1);
            
            this.window.addOutput("Creating " + newFolder, true);
            Files.createDirectories(newFolderPath);
            this.window.addOutput("Folder " + newFolder + " created.\n");
            this.window.updateProgress(2);

            return Files.createDirectories(newFolderPath);
        } catch (IOException e) {
            this.window.addOutput("Error creating " + newFolder + ". Terminating", true);
            terminate();
        }
        return null;
    }

    public void importImages(){
        this.window.setLabel("Downloading Images...");
        
    }

    public void terminate(){
        System.exit(0);
    }
}
