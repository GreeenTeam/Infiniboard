package com.infiniboard.greenteam.infiniboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
    private int height = 0;
    private int width = 0;
    private Bitmap replacement;

//Constructor
    public Selector(int x, int y, int width, int height){
        //Create a selection bitmap using the coordinates fed to the method
        //See: Bitmap.createBitmap(Bitmat source, int x, int y, int width, int height)
        //http://developer.android.com/reference/android/graphics/Bitmap.html

        setCurrentX(x);
        setCurrentY(y);
        //set width/height?
    }

    public Selector(Board b){
        currentBoard = b;
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

    public int getHeight(){return height;}

    public int getWidth(){return width;}




//The below goes in the selector class

    //We declare a Bitmap called clipboard
//This will hold the copy of our selection
    public Bitmap clipboard;
    //Copy
    public void copySelection(){
        //The selection will be copied into clipboard
        clipboard = selection.copy(selection.getConfig(), true);
    }

    //Cut
    public void cutSelection(){
        //Calling copySelection in order to copy the selection into clipboard
        copySelection();
        //All the pixels in selection will be set to white
        selection.eraseColor(0xFFffffff);
    }

    //Paste
    public void pasteSelection(int x, int y){
        //We declare a Canvas called newCanvas
        Canvas newCanvas;
        //Here we set newCanvas to the current canvas in our Board
        newCanvas = currentBoard.getCanvas();
        //We then draw the previously copied Bitmap to newCanvas
        //The top left corner of the bitmap is located at x and y
        //Paint is null
        currentBoard.getCanvas().drawBitmap(clipboard, x, y, null);
        //Finally we set the Canvas in Board equal to newCanvas
        currentBoard.setCanvas(newCanvas);
        //currentBoard.addSelectionToCanvas(x,y);
    }

///Scale
    public Bitmap scaleSelection(float scaleFactor) {
        //Scales selection to specified size
        //See: Bitmap.createScaledBitmap

        selection = Bitmap.createScaledBitmap(selection, (int)(width*scaleFactor), (int)(height*scaleFactor), true);
        currentBoard.addSelectionToCanvas((int)(currentX + (width-(width*scaleFactor))/2), (int)(currentY + (height-(height*scaleFactor))/2));
        width = (int)(width*scaleFactor);
        height = (int)(height*scaleFactor);
        return selection;
    }

//Move
    public Bitmap moveSelection(){
        //Cuts selection, pastes it at specified co-ordinates
        //***Do we need to have this be a 'drag to' operation or just a call of cut/paste?***
        currentBoard.inMoveMode = true;
        return selection;
    }

//Rotate
    public Bitmap rotateSelection(float degree){
        //Rotates the selection by a specified degree
        //See: Bitmap.createBitmap (the one that uses a Matrix)
        Matrix matrix = new Matrix();
        matrix.postScale(width, height);
        matrix.setRotate(degree);
        selection = Bitmap.createBitmap(selection, 0, 0, width, height, matrix, true);
        currentBoard.addSelectionToCanvas((currentX - (selection.getWidth()-(width))/2), (currentY - (selection.getHeight()-height)/2));
        width = this.getWidth();
        height = this.getHeight();
        return selection;
    }


//Save Selection
    public void saveSelection(String name,String description, Context context){
        //Please add  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
        //to manifest
        //Saves selection (as a bitmap) on the device
        //Code Referenced: http://stackoverflow.com/questions/16861753/android-saving-bitmap-to-phone-storage

        Bitmap temp = Bitmap.createBitmap(selection.getWidth(),selection.getHeight(),selection.getConfig());
        temp.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(selection,0f,0f,null);
        canvas.setBitmap(temp);
        MediaStore.Images.Media.insertImage(context.getContentResolver(), temp , name, description);

    }

//Change colour of Selection
    public void changeColour(int color){
        //Changed the colour of all the paint in the selection
        //with the specified ARGB color

            for (int x = 0; x < selection.getWidth(); x++){
                for (int y = 0; y < selection.getHeight(); y++){
                    selection.getPixel(x, y);
                    //Checks to see if the current pixel is not white, if so, changes it to color
                    if (selection.getPixel(x, y) != 0 && selection.getPixel(x, y) != (Color.parseColor("#FFFFFF"))) {
                        selection.setPixel(x, y, color);
                    }
                }
            }

    }

    public void eraseSelection(){
        //Changed the colour of all the paint in the selection
        //with White

        selection.eraseColor(0xFFffffff);

    }
    public void setSelection(Bitmap b, int x, int y){
        selection = b;
        height = b.getHeight();
        width = b.getWidth();
        currentX = x;
        currentY = y;
        replacement = Bitmap.createBitmap(b);
        replacement.eraseColor(0xFFffffff);
    }

    public Bitmap getReplacement() { return replacement; }

    public boolean hasClipboard(){
        return clipboard != null;
    }


}
