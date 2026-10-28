package com.captureogf;

public class Preset {
    private String name;
    private double x1, x2, y1, y2;

    public Preset(String name, double x1, double y1, double x2, double y2){
        this.name = name;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Preset(String name){
        this.name = name;
        this.x1 = 200;
        this.y1 = 200;
        this.x2 = 200;
        this.y2 = 200;
    }

    public String getName(){
        return name;
    }

    public double[] getCoordinates(){
        double coordinates[] = {x1, y1, x2, y2};
        return coordinates;
    }

    public void setCoordinates(int x1, int y1, int x2, int y2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public String toString() {
        return name;
    }
}
