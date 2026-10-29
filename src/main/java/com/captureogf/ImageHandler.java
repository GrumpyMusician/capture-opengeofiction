package com.captureogf;

public class ImageHandler {
    private double x1, y1, x2, y2;
    private int zoom, numRows, numCols;
    private String destination, url;

    public ImageHandler(double x1, double y1, double x2, double y2, int zoom, int numRows, int numCols, String destination, String url){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.zoom = zoom;
        this.numRows = numRows;
        this.numCols = numCols;
        this.destination = destination;
        this.url = url;
    }

     
}
