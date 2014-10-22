package com.infiniboard.greenteam.infiniboard;

import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.content.Context;
import java.nio.Buffer;

/**
 * Created by Nick on 10/6/2014.
 */

public class Board extends View {
    //set local variables
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private Bitmap canvasBitmap;
    private Canvas drawCanvas;
    public Marker[] tray = new Marker[5];
    public Marker currentMarker;
    float lastEventX;
    float lastEventY;
    boolean inSelector;
    float originX;
    float originY;
    Selector selector = new Selector();

    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        //initializes the four markers for the tray
        tray[0] = new Marker(0xFF000000,0);
        tray[1] = new Marker(0xFF009150,1);
        tray[2] = new Marker(0xFF0070BB,2);
        tray[3] = new Marker(0xFFDA2C43,3);
        tray[4] = new Marker(0xFFffffff,4);
        tray[4].setStrokeWidth(45);
        currentMarker = tray[0];
        setupDrawing();
    }

    //get drawing area setup for interaction
    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setStrokeWidth(currentMarker.getStrokeWidth());
        drawPaint.setStrokeWidth(currentMarker.getStrokeWidth());

        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        //more research needed here
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    //this is called for when the view needs to render its content
    protected void onDraw(Canvas canvas) {
        //this draws the bitmap to the canvas so it stays after the user lifts their finger
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        //this draws the path as the user is touching the screen
        canvas.drawPath(drawPath, drawPaint);
    }

    //This sets up the canvas by first creating the bitmap where the pixels will be saved
    //and from there creating the canvas that will handle the draw calls
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    //These are the instructions to draw, when ever the user is touching
    //the screen this method is called
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        drawPaint.setColor(currentMarker.getColor());
        drawPaint.setStrokeWidth(currentMarker.getStrokeWidth());
        //get the coordinates of the touch event.
        float eventX = event.getX();
        float eventY = event.getY();
        if(inSelector){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    drawPath.moveTo(eventX, eventY);
                    originX = eventX;
                    originY = eventY;
                    break;
                case MotionEvent.ACTION_MOVE:

                    drawPath.reset();
                    drawPath.addRect(originX,originY,eventX,eventY, Path.Direction.CW);
                    break;
                case MotionEvent.ACTION_UP:

                    drawPath.reset();
                    selector.setSelection( Bitmap.createBitmap(canvasBitmap,(int) originX,(int) originY,(int) (eventX-originX) ,(int) (eventY-originY)));
                    selector.setCurrentX((int)originX);
                    selector.setCurrentY((int)originY);
                    break;
                default:
                    return false;
            }
        }else{
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //set a new starting point
                    drawPath.moveTo(eventX, eventY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    //Connect the points
                    //This should be slightly smother than the line to became it uses quad equation
                    drawPath.quadTo(lastEventX, lastEventY, eventX, eventY);
                    break;
                case MotionEvent.ACTION_UP:
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    break;
                default:
                    return false;
            }
        }
        lastEventX = eventX;
        lastEventY = eventY;
        //makes view repaint and call onDraw
        invalidate();
        return true;
    }

    //this will be called when the user selects a different marker
    public void changeMarker(int m){
        currentMarker = tray[m];
    }

    //these will be called when the user changes the properties of an existing marker
    public void changeMarkerColor(int color){
        currentMarker.setColor(color);
    }
    public void changeMarkerWidth(int width){
        currentMarker.setStrokeWidth(width);
    }

    public void addSelectionToCanvas (int x, int y){
        drawCanvas.drawBitmap(selector.getSelection(),x,y,null);
    }

}
