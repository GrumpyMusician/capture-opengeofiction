package com.captureogf;

public class Stage {
    // Most useless class ever
    private int stage;

    public Stage(){
        this.stage = 0;
    }

    public boolean isBoundingStage(){
        return this.stage == 0;
    }

    public boolean isConfigStage(){
        return this.stage == 1;
    }

    public boolean isExportStage(){
        return this.stage == 2;
    }

    public void setStageBbox(){
        this.stage = 0;
    }

    public void nextStage(){
        stage++;
        checkStage();
    }

    public void prevStage(){
        stage--;
        checkStage();
    }

    private void checkStage(){
        if (stage < 0 || stage > 2){
            throw new IllegalArgumentException("Invalid stage!");
        } 
    }
}
