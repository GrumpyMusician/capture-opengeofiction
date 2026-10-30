package com.captureogf;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ImageHandler {
    private double x1, y1, x2, y2;
    private int zoom, numRows, numCols;
    private String url;
    private Path baseDir, destDir, tempDir;
    private ProgressWindow window = new ProgressWindow();
    public record Point(int x, int y) {};

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
            tempDir = destDir.resolve("Tiles");
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

        window.progress(0);
        window.report("Building cookies");
        HttpClient session = HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                double requestInterval = 0.25;
                long nextRequestTime = System.currentTimeMillis();
                int maxRetries = 5;

                int xTileMax = getXTile(x1);
                int xTileMin = getXTile(x2);
                int yTileMin = getYTile(y1);
                int yTileMax = getYTile(y2);

                HashMap<Point, BufferedImage> tiles = new HashMap<>();
                int totalTiles = ((xTileMax - xTileMin) + 1) * ((yTileMax - yTileMin) + 1);
                int numTilesDownloaded = 0;
                
                window.progress(1, totalTiles); // cookies into account
                
                for (int i = xTileMin; i <= xTileMax; i++) {
                    for (int j = yTileMin; j <= yTileMax; j++) {
                        window.notify("Retrieving (" + i + ", " + j + ")");
                        Path imagePath = tempDir.resolve(i + "_" + j + ".png");
                        if (Files.exists(imagePath)){
                            numTilesDownloaded++;
                        } else {
                            boolean flag = true;
                            for (int k = 0; k < maxRetries; k++){
                                try {
                                    long now = System.currentTimeMillis();
                                    if (now < nextRequestTime){
                                        Thread.sleep(nextRequestTime - now);
                                    }

                                    url = "https://tile.opengeofiction.net/ogf-carto/" + zoom + "/" + i + "/" + j + ".png";
                                    
                                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", "OGF-Extractor").timeout(Duration.ofSeconds(15)).GET().build();
                                    
                                    Random rand = new Random();
                                    nextRequestTime += requestInterval + rand.nextDouble() * 0.04 - 0.02;

                                    session.send(request, HttpResponse.BodyHandlers.ofFile(imagePath));
                                    
                                    numTilesDownloaded ++;
                                    flag = false;
                                    break;
                                } catch(IOException e) {
                                    window.notify("Failed to retrieve (" + i + ", " + j + "). Retrying");
                                }
                            }
                            if (flag){
                                window.report("Failed to retrieve (" + i + ", " + j + ") after " + maxRetries + " failed attempts.");
                            }
                        }
                        window.progress(numTilesDownloaded);
                        if (Files.exists(imagePath)){
                            try {
                                tiles.put(new Point(i, j), ImageIO.read(new File(imagePath.toString())));
                            } catch (IOException e){
                                window.report("Error loading image: " + e.getMessage());
                            }
                        }
                    }
                }

                int tileDimen = 256;
                int tilesPerChunkX = Math.floorDiv(xTileMax - xTileMin + 1, numCols);
                int tilesPerChunkY = Math.floorDiv(yTileMax - yTileMin + 1, numRows);

                System.out.println(xTileMax + ", " + xTileMin);
                
                int remainderX = (xTileMax - xTileMin + 1) % numCols;
                int remainderY = (yTileMax - yTileMin + 1) % numRows;

                for (int i = 0; i < numRows; i++){
                    for (int j = 0; j < numCols; j++){
                        int chunkWidthTiles = tilesPerChunkX + ((j == numCols - 1 && remainderX > 0) ? 1 : 0) * remainderX;
                        int chunkHeightTiles = tilesPerChunkY + ((i == numRows - 1 && remainderY > 0) ? 1 : 0) * remainderY;

                        BufferedImage chunk = new BufferedImage(chunkWidthTiles, chunkHeightTiles, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2d = chunk.createGraphics();
                        g2d.setColor(new Color(255, 255, 255));
                        g2d.fillRect(0, 0, chunkWidthTiles, chunkHeightTiles);
                        g2d.dispose();

                        for (int di = 0; di < chunkWidthTiles; di++){
                            for (int dj = 0; dj < chunkHeightTiles; dj++){
                                int chunkX = xTileMin + j * tilesPerChunkX + di;
                                int chunkY = yTileMin + i * tilesPerChunkY + dj;
                            }
                        }
                    }
                }

                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    window.setLabel("Downloads complete.");
                } catch (Exception e) {
                    window.setLabel("Download failed.");
                    terminate("Download failed: " + e);
                    System.out.println(e);
                }
            }
        };

        worker.execute();
    }

    private int getXTile(double lon){
        return (int) ((lon + 180.0) / 360.0 * ((int) Math.pow(2, zoom)));
    }

    private int getYTile(double lat){
        return (int) ((1.0 - Math.log(Math.tan(Math.toRadians(lat)) + (1 / Math.cos(Math.toRadians(lat)))) / Math.PI) / 2.0 * ((int) Math.pow(2, zoom)));
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
