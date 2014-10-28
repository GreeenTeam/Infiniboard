package com.infiniboard.greenteam.infiniboard;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.provider.MediaStore;
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
    Selector selector = new Selector(this);
    boolean inMoveMode = false;
    boolean inPasteMode = false;

    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        //initializes the four markers for the tray
        tray[0] = new Marker(0xFF000000,0);
        tray[1] = new Marker(0xFF009150,1);
        tray[2] = new Marker(0xFF0070BB,2);
        tray[3] = new Marker(0xFFDA2C43,3);
        tray[4] = new Marker(0xFFffffff,4,45);
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
        //get the coordinates of the touch event.
        float eventX = event.getX();
        float eventY = event.getY();
        if(inSelector){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPaint.setColor(0xFF000000);
                    drawPaint.setStrokeWidth(5);
                    drawPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
                    drawPath.moveTo(eventX, eventY);
                    originX = eventX;
                    originY = eventY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.reset();
                    if((originX < eventX) && (originY < eventY)) {
                        drawPath.addRect(originX, originY, eventX, eventY, Path.Direction.CW);
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    drawPath.reset();
                    if((originX < eventX) && (originY < eventY)) {
                        selector.setSelection(Bitmap.createBitmap(canvasBitmap, (int) originX, (int) originY, (int) (eventX - originX), (int) (eventY - originY)));
                        selector.setCurrentX((int) originX);
                        selector.setCurrentY((int) originY);
                    }
                    drawPaint.setPathEffect(null);
                    break;
                default:
                    return false;
            }
        }else if (inMoveMode) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    //place selection down
                    addSelectionToCanvas(((int)eventX - (selector.getWidth()/2)), ((int)eventY - (selector.getHeight()/2)));
                    break;
                case MotionEvent.ACTION_UP:
                    //end move mode
                    inMoveMode = false;
            }
        }else if (inPasteMode) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    //place selection down
                    selector.pasteSelection(((int)eventX - (selector.clipboard.getWidth()/2)), ((int)eventY - (selector.clipboard.getHeight()/2)));
                    break;
                case MotionEvent.ACTION_UP:
                    //end move mode
                    inPasteMode = false;
            }
        }else{
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //set a new starting point
                    drawPaint.setColor(currentMarker.getColor());
                    drawPaint.setStrokeWidth(currentMarker.getStrokeWidth());
                    drawCanvas.drawPoint(eventX, eventY, drawPaint);
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
    //Put the below in the Board class

    public Canvas getCanvas() {
        return drawCanvas;
    }

    public void setCanvas(Canvas n) {
        drawCanvas = n;
    }
    //this will be called when the user selects a different marker
    public void changeMarker(int m){
        if(m==4) {
            drawPaint.setAntiAlias(false);
        }else{
            drawPaint.setAntiAlias(true);
        }
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
        drawCanvas.drawBitmap(selector.getReplacement(), originX, originY, null);
        drawCanvas.drawBitmap(selector.getSelection(),x,y,null);
        originX = x;
        originY = y;
        invalidate();
    }

    public void saveImage (String name,String description, Context context){
        //Please add  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
        //to manifest
        //Saves selection (as a bitmap) on the device
        //Code Referenced: http://stackoverflow.com/questions/16861753/android-saving-bitmap-to-phone-storage

        Bitmap temp = Bitmap.createBitmap(canvasBitmap.getWidth(),canvasBitmap.getHeight(),canvasBitmap.getConfig());
        temp.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(canvasBitmap,0f,0f,null);
        canvas.setBitmap(temp);
        MediaStore.Images.Media.insertImage(context.getContentResolver(), temp , name, description);

    }

}
