package com.infiniboard.greenteam.infiniboard;

/**
 * Created by Brandon on 10/6/14.
 */

/* The Implementation of the Sliding Menu Layout was Developed with the use of a Reference Material
 * Brandon Caruso referenced the source developed by GitHub User jaylamont.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LayeredContainer extends LinearLayout {

    // There will be two views one for the Marker Tray and One for the Board

    private View markerTray;
    private View board;


    protected int traySize = 50;

    public enum MenuState {
        CLOSED, OPEN
    };

    // Position information attributes
    protected int currentContentOffset = 0;
    protected MenuState menuCurrentState = MenuState.CLOSED;

    // Various Constructors
    public LayeredContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LayeredContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LayeredContainer(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();

        //MarkerTray is the first Child Specified in activity_infini_board
        markerTray = this.getChildAt(0);

        //Board is the second Child Specified in activity_infini_board
        board = this.getChildAt(1);

        //MarkerTray is originally not visible
        markerTray.setVisibility(View.GONE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom){
        if(changed){
            this.calculateChildDimensions();
        }

        markerTray.layout( left, top - (this.getHeight() - traySize), right, bottom);
        board.layout(left , top - currentContentOffset, right , bottom - currentContentOffset);
    }

    public void toggleMenu() {
        //Opens <--> Closes Menu
        if (menuCurrentState == MenuState.CLOSED) {
            markerTray.setVisibility(View.VISIBLE);
            currentContentOffset = markerTray.getLayoutParams().height;
            board.offsetTopAndBottom(- currentContentOffset);
            menuCurrentState = MenuState.OPEN;
            System.out.println("Menu Opened");
        }else if (menuCurrentState == MenuState.OPEN){
            board.offsetTopAndBottom(currentContentOffset);
            currentContentOffset = 0;
            menuCurrentState = MenuState.CLOSED;
            markerTray.setVisibility(View.GONE);
            System.out.println("Menu Closed");
        }

        //redraw on the screen
        this.invalidate();
    }

    public void changeMarkerTo (View v) {
        int id = v.getId();
        Board mainBoard = (Board) this.findViewById(R.id.board);
        if( id == R.id.button_1){
            mainBoard.changeMarker(0);
            System.out.println("Marker to Button 1 Color");
        }else if ( id == R.id.button_2){
            mainBoard.changeMarker(1);
            System.out.println("Marker to Button 2 Color");
        }else if ( id == R.id.button_3){
            mainBoard.changeMarker(2);
            System.out.println("Marker to Button 3 Color");
        }else if ( id == R.id.button_4){
            mainBoard.changeMarker(3);
            System.out.println("Marker to Button 4 Color");
        }else if ( id == R.id.button_E){
            mainBoard.changeMarker(4);
            System.out.println("Marker to Eraser");
        }
        // Changes Current Marker to Color Passed
        System.out.println("Marker Now Changed");
        this.toggleMenu();
    }


    private void calculateChildDimensions() {
        board.getLayoutParams().height = this.getHeight();
        board.getLayoutParams().width = this.getWidth();

        markerTray.getLayoutParams().width = this.getWidth();
        markerTray.getLayoutParams().height = traySize;
    }




}
