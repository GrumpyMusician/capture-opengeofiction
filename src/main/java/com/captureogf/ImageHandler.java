package com.captureogf;

import javax.swing.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageHandler {
    private double x1, y1, x2, y2;
    private int zoom, numRows, numCols;
    private String url;
    private Path baseDir, destDir, tempDir;
    private ProgressWindow window = new ProgressWindow();

    public ImageHandler(double x1, double y1, double x2, double y2, int zoom, int numRows, int numCols, String destDir, String url){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.zoom = zoom;
        this.numRows = numRows;
        this.numCols = numCols;
        this.baseDir = Path.of(destDir);
        this.url = url;

        //System.out.println(x1 + ", " + y1 + ", " + x2 + ", " + y2);
    }

    public void createWindow(){
        JFrame frame = new JFrame("Progress");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(window);
        frame.pack();
        frame.setSize(500,400);
        frame.setVisible(true);
    }

    public void createDestination() {
        window.setLabel("Setting up directories...");
        window.progress(0, 3);

        try {
            String baseDirName = "CaptureOGF Output";
            String destDirName = baseDirName;
            
            window.notify("Checking if directory " + destDirName + " exists");
            
            int i = 1;
            while (checkDirExists(baseDir, destDirName)) {
                window.report("Directory " + destDirName + " already exists");
                destDirName = baseDirName + " " + i;
                window.notify("Checking if directory " + destDirName + " exists");
                i++;
            }
            window.progress(1);

            window.report("Directory " + destDirName + " will be used");
            window.notify("Creating directory " + destDirName);
            
            destDir = baseDir.resolve(destDirName);
            Files.createDirectory(destDir);
            window.progress(2);

            window.notify("Creating directory for tiles");
            tempDir = destDir.resolve("Temporary Tiles");
            Files.createDirectory(tempDir);
            window.progress(3);
            window.buffer();
        } catch (IOException e) {
            terminate(e.getMessage());
        }
    }

    private boolean checkDirExists(Path baseDir, String childDir){
        return Files.isDirectory(baseDir.resolve(childDir));
    }

    public void importImages(){
        window.setLabel("Downloading Images...");
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Download images and perform filesystem operations here.
                return null;
            }
        };

        worker.execute();
    }

    public void terminate(String message){
        window.setLabel("Error");
        window.notify("ERROR " + message);
        window.report("Terminating program");
        Timer timer = new Timer(5000, e -> {
            System.exit(0);
        });
        timer.setRepeats(false);
        timer.start();
    }
}
