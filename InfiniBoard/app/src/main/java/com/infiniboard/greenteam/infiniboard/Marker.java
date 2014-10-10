package com.infiniboard.greenteam.infiniboard;

/**
 * Created by Justin and Nick on 10/10/14.
 */
public class Marker {
    int color;
    int strokeWidth;
    public Marker(){
        strokeWidth = 10;
        color = 0xFF000000;
    }
    public Marker(int startColor){
        strokeWidth = 10;
        color = startColor;
    }

    public void setColor(int newColor){
        color = newColor;
    }

    public void setStrokeWidth(int newStrokeWidth){
        strokeWidth = newStrokeWidth;
    }

    public int getStrokeWidth(){
        return strokeWidth;
    }

    public int getColor(){
        return color;
    }
}
