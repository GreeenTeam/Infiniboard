package com.infiniboard.greenteam.infiniboard;
import android.widget.Button;
/**
 * Created by Justin and Nick on 10/10/14.
 */
public class Marker {
    int color;
    int strokeWidth;
    int id; // ADDED BY BRANDON FOR COLOR CHANGING OF BUTTONS


    public Marker(){
        strokeWidth = 10;
        color = 0xFF000000;
    }
    public Marker(int startColor){
        strokeWidth = 10;
        color = startColor;
    }
    public Marker(int startColor,int i){
        strokeWidth = 10;
        color = startColor;
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

    public int getColor(){
        return color;
    }

    public int getID(){
        return id;
    }
}
