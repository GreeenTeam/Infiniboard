package com.infiniboard.greenteam.infiniboard;
import android.widget.Button;
/**
 * Created by Justin and Nick on 10/10/14.
 */
public class Marker {
    private int color;
    private int defaultSize;
    private int defaultColor;
    private int strokeWidth;
    private int id; // ADDED BY BRANDON FOR COLOR CHANGING OF BUTTONS


    public Marker(){
        strokeWidth = 10;
        color = 0xFF000000;
    }
    public Marker(int startColor){
        strokeWidth = 10;
        color = startColor;
    }
    public Marker(int startColor,int i){
        strokeWidth = defaultSize = 10;

        color = defaultColor = startColor;
        id = i;
    }

    public Marker(int startColor,int i,int size){
        strokeWidth = defaultSize = size;
        color = defaultColor = startColor;
        id = i;
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

    public int getDefaultSize(){
        return defaultSize;
    }
    public int getDefaultColor(){
        return defaultColor;
    }

    public int getColor(){
        return color;
    }

    public int getID(){
        return id;
    }
}
