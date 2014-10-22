package com.infiniboard.greenteam.infiniboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.content.ContentResolver;
import android.content.Context;

import java.io.IOException;
import java.lang.System;
import java.sql.SQLOutput;
import java.io.*;

public class Selector {
//Allows for the selection and manipulation of a rectangular area on the Board
//To Implement:
      //Grab selection area (part of constructor ?)
      //Scale/Move/Rotate
      //Save selection
      //Change colour of selection
      //Cut/Copy/Paste

//Variables
    private Board currentBoard;
    private Bitmap selection;
    private int currentX;
    private int currentY;

//Constructor
    public Selector(int x, int y, int width, int height){
        //Create a selection bitmap using the coordinates fed to the method
        //See: Bitmap.createBitmap(Bitmat source, int x, int y, int width, int height)
        //http://developer.android.com/reference/android/graphics/Bitmap.html

        setCurrentX(x);
        setCurrentY(y);
        //set width/height?
    }

    public Selector(){
    }



//Getters and Setters
    public Board getCurrentBoard(){
        return currentBoard;
    }

    public Bitmap getSelection(){
        return selection;
    }

    public int getCurrentX(){
        return currentX;
    }

    public int getCurrentY(){
        return currentY;
    }

    public void setCurrentX(int x){
       currentX = x;
    }

    public void setCurrentY(int y){
        currentY = y;
    }

//Copy
    public Bitmap copySelection(){
        //Saves selection to 'clipboard' to be pasted later
        //See: Bitmap.copy
        return selection;
    }

//Cut
    public Bitmap cutSelection(){
        //First, Call copySelection()
        //Overwrites area where selection is in currentBoard with board colour (white)
        //See: Bitmap.eraseColor(int c)
        //http://developer.android.com/reference/android/graphics/Canvas.html
        return selection;
    }

//Paste
    public void pasteSelection(int x, int y){
        //Pastes selection at a specified location
        //Over-writes what is below
        //See: Canvas.drawBitmap
        //http://developer.android.com/reference/android/graphics/Canvas.html

    }
//Scale
    public Bitmap scaleSelection(int size){
        //Scales selection to specified size
        //See: Bitmap.createScaledBitmap
        return selection;
    }

/*//Move
    public Bitmap moveSelection(int x, int y){
        //Cuts selection, pastes it at specified co-ordinates
        //***Do we need to have this be a 'drag to' operation or just a call of cut/paste?***
    }*/

//Rotate
    public Bitmap rotateSelection(int degree){
        //Rotates the selection by a specified degree
        //See: Bitmap.createBitmap (the one that uses a Matrix)
        return selection;
    }


//Save Selection
    public void saveSelection(String name, Context context){
        //Please add  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
        //to manifest
        //Saves selection (as a bitmap) on the device
        //Code Referenced: http://stackoverflow.com/questions/16861753/android-saving-bitmap-to-phone-storage

        Bitmap temp = Bitmap.createBitmap(selection.getWidth(),selection.getHeight(),selection.getConfig());
        temp.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(selection,0f,0f,null);
        canvas.setBitmap(temp);
        MediaStore.Images.Media.insertImage(context.getContentResolver(), temp , "testImage", "testImage");

    }

//Change colour of Selection
    public void changeColour(int color){
        //Changed the colour of all the paint in the selection
        //with the specified ARGB color

            for (int x = 0; x < selection.getWidth(); x++){
                for (int y = 0; y < selection.getHeight(); y++){
                    selection.getPixel(x, y);
                    //Checks to see if the current pixel is not white, if so, changes it to color
                    if (selection.getPixel(x, y) != 0) {
                        selection.setPixel(x, y, color);
                    }
                }
            }

    }

    public void setSelection(Bitmap b){
        selection = b;
    }



}
