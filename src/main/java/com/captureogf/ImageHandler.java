package com.captureogf;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.ByteArrayInputStream;

import java.time.Duration;

import java.util.concurrent.CancellationException;
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

    
    public Path createDestination(String newFolder) {
        window.setLabel("Setting up directories...");
        window.updateProgress(0, 2);

        Path newFolderPath = destination.resolve(newFolder);

        try {
            window.addOutput("Checking for existing folder " + newFolder, true);

            if (Files.exists(newFolderPath)) {
                window.addOutput("Existing folder found.", true);
                window.addOutput("Removing existing folder " + newFolder, true);

                try (var paths = Files.walk(newFolderPath)) {
                    for (Path path : paths
                            .sorted(Comparator.reverseOrder())
                            .toList()) {
                        Files.delete(path);
                    }
                }
            } else {
                window.addOutput("No existing folder found.", true);
            }

            window.updateProgress(1);

            window.addOutput("Creating " + newFolder, true);
            Files.createDirectories(newFolderPath);

            window.addOutput("Folder created successfully.\n", true);
            window.updateProgress(2);

            return newFolderPath;

        } catch (IOException e) {
            window.addOutput(
                    "Error creating " + newFolder + ": " + e.getMessage(),
                    true
            );
            return null;
        }
    }

    public void importImages(){
        this.window.setLabel("Downloading Images...");

    }

    public void terminate(){
        System.exit(0);
    }
}
