package com.infiniboard.greenteam.infiniboard;

/**
 * Created by Brandon on 10/6/14.
 */

/* The Implementation of the Sliding Menu Layout was Developed with the use of a Reference Material
 * Brandon Caruso referenced the source developed by GitHub User jaylamont.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.graphics.Color;
import android.app.Dialog;
import android.content.DialogInterface;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;

public class LayeredContainer extends LinearLayout {

    // There will be two views one for the Marker Tray and One for the Board

    private View markerTray;
    private View board;
    private View mainBubbleMenu;
    private View selectBubbleMenu1;
    private View selectBubbleMenu2;
    private View selectBubbleMenu3;
    private View anchorBubbleMenu;
    private View boardBubbleMenu;


    public Boolean selectMode = false;


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
        markerTray.setVisibility(View.VISIBLE);
        markerTray.setVisibility(View.GONE);

        mainBubbleMenu = this.findViewById(R.id.bubble_menu_main);
        selectBubbleMenu1 = this.findViewById(R.id.bubble_menu_select_1);
        selectBubbleMenu2 = this.findViewById(R.id.bubble_menu_select_2);
        selectBubbleMenu3 = this.findViewById(R.id.bubble_menu_select_3);
        anchorBubbleMenu = this.findViewById(R.id.bubble_menu_anchors);
        boardBubbleMenu = this.findViewById(R.id.bubble_menu_boards);
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
        clearMenus();
        updateButtonColors();
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
        final Board mainBoard = (Board) findViewById(R.id.board);
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



    public void clearMenus(){
        mainBubbleMenu.setVisibility(View.GONE);
        selectBubbleMenu1.setVisibility(View.GONE);
        selectBubbleMenu2.setVisibility(View.GONE);
        selectBubbleMenu3.setVisibility(View.GONE);
        anchorBubbleMenu.setVisibility(View.GONE);
        boardBubbleMenu.setVisibility(View.GONE);
    }



    public void setSelectMode (){
        selectMode = true;
        mainBubbleMenu.setVisibility(View.GONE);
    }


    // The following are the implementations of all the onClick Methods for the buttons in the Radial Menu


    public void showBoardMenu(View v){
        mainBubbleMenu.setVisibility(View.GONE);
        boardBubbleMenu.setVisibility(View.VISIBLE);

    }
    public void showAnchorMenu(View v){
        mainBubbleMenu.setVisibility(View.GONE);
        anchorBubbleMenu.setVisibility(View.VISIBLE);

    }
    public void showSelectMenu1(View v){
        mainBubbleMenu.setVisibility(View.GONE);
        selectBubbleMenu3.setVisibility(View.GONE);
        selectMode = true;
        selectBubbleMenu1.setVisibility(View.VISIBLE);
    }
    public void showSelectMenu2(View v){
        mainBubbleMenu.setVisibility(View.GONE);
        selectBubbleMenu1.setVisibility(View.GONE);
        selectMode = true;
        selectBubbleMenu2.setVisibility(View.VISIBLE);
    }

    public void showSelectMenu3(View v){
        mainBubbleMenu.setVisibility(View.GONE);
        selectBubbleMenu2.setVisibility(View.GONE);
        selectMode = true;
        selectBubbleMenu3.setVisibility(View.VISIBLE);
    }
    public void showColorMenu(View v) {
        mainBubbleMenu.setVisibility(View.GONE);
        System.out.println("SHOW COLOR MENU");


        colorDialog();
    }
    public void showSizeMenu(View v){
        mainBubbleMenu.setVisibility(View.GONE);
        System.out.println("SHOW SIZE MENU");
        sizeDialog();
    }
    public void moveSelection(View v){
        selectBubbleMenu1.setVisibility(View.GONE);
        System.out.println("MOVE SELECTION");
        selectMode = false;

    }
    public void scaleSelection(View v){
        selectBubbleMenu1.setVisibility(View.GONE);
        System.out.println("SCALE SELECTION");
        selectMode = false;
    }
    public void rotateSelection(View v){
        selectBubbleMenu1.setVisibility(View.GONE);
        System.out.println("ROTATE SELECTION");
        selectMode = false;
    }
    public void colorizeSelection(View v){
        selectBubbleMenu1.setVisibility(View.GONE);
        System.out.println("SHOW COLOR MENU THE COLORIZE SELECTION");
        selectMode = false;
    }
    public void pasteSelection(View v){
        selectBubbleMenu2.setVisibility(View.GONE);
        System.out.println("PASTE SELECTION");
        selectMode = false;
    }
    public void copySelection(View v){
        selectBubbleMenu2.setVisibility(View.GONE);
        System.out.println("COPY SELECTION");
        selectMode = false;
    }
    public void cutSelection(View v){
        selectBubbleMenu2.setVisibility(View.GONE);
        System.out.println("CUT SELECTION");
        selectMode = false;
    }
    public void eraseSelection(View v){
        selectBubbleMenu2.setVisibility(View.GONE);
        System.out.println("ERASE SELECTION");
        selectMode = false;
    }
    public void exportSelection(View v){
        selectBubbleMenu3.setVisibility(View.GONE);
        System.out.println("EXPORT SELECTION");
        selectMode = false;
    }
    public void cancelSelection(View v){
        selectBubbleMenu3.setVisibility(View.GONE);
        System.out.println("CANCEL SELECTION");
        selectMode = false;
    }
    public void createNewAnchor(View v){
        anchorBubbleMenu.setVisibility(View.GONE);
        System.out.println("CREATE NEW ANCHOR");

    }
    public void showAnchorGoToMenu(View v){
        anchorBubbleMenu.setVisibility(View.GONE);
        System.out.println("SHOW ANCHOR GO TO");

    }
    public void showBoardGoToMenu(View v){
        // THIS MIGHT BE MOVED UP
        boardBubbleMenu.setVisibility(View.GONE);
        System.out.println("SHOW BOARD GO TO");

    }
    public void createBoard(View v){
        // THIS MIGHT BE MOVED UP
        boardBubbleMenu.setVisibility(View.GONE);
        System.out.println("CREATE BOARD");
    }
    public void changeBoardProp(View v){
        // THIS MIGHT BE MOVED UP
        boardBubbleMenu.setVisibility(View.GONE);
        System.out.println("CHANGE BOARD PROPERTIES");

    }
    public void exportBoard(View v){
        // THIS MIGHT BE MOVED UP
        boardBubbleMenu.setVisibility(View.GONE);
        System.out.println("CHANGE BOARD PROPERTIES");
    }
    public void deleteBoard(View v){
        // THIS MIGHT BE MOVED UP
        boardBubbleMenu.setVisibility(View.GONE);
        System.out.println("CHANGE BOARD PROPERTIES");
    }

    public void sizeDialog(){

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file

        dialog.setContentView(R.layout.dialog_size);
        dialog.setTitle("Change Marker Size");

        final Board mainBoard = (Board) findViewById(R.id.board);
        final ImageView brushSizeGraphic = (ImageView) dialog.findViewById(R.id.brush_size_graphic);
        final SeekBar sizeBar = (SeekBar) dialog.findViewById(R.id.size_bar);
        sizeBar.setProgress(mainBoard.currentMarker.strokeWidth - 10);
        brushSizeGraphic.getLayoutParams().height = brushSizeGraphic.getLayoutParams().width = mainBoard.currentMarker.strokeWidth;
        dialog.show();

        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brushSizeGraphic.requestLayout();
                brushSizeGraphic.getLayoutParams().height = brushSizeGraphic.getLayoutParams().width = i + 10;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        } );
        Button declineButton = (Button) dialog.findViewById(R.id.cancel_size_change);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });

        Button resetButton = (Button) dialog.findViewById(R.id.reset_size_change);

        resetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sizeBar.setProgress(0);
            }
        });

        Button changeSize = (Button) dialog.findViewById(R.id.change_size);
        // if decline button is clicked, close the custom dialog
        changeSize.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mainBoard.changeMarkerWidth( sizeBar.getProgress() + 10 );
                System.out.println("Change Brush Size to: "+ ( sizeBar.getProgress() + 10 )+" px");
                // Close dialog
                dialog.dismiss();
            }
        });
    }

    public void colorDialog(){

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file

        dialog.setContentView(R.layout.dialog_color);
        dialog.setTitle("Change Color of Current Marker");

        final Board mainBoard = (Board) findViewById(R.id.board);
        final ColorPicker colorPicker = (ColorPicker) dialog.findViewById(R.id.picker);
        final SVBar svBar = (SVBar) dialog.findViewById(R.id.svbar);
        colorPicker.setOldCenterColor(mainBoard.currentMarker.getColor());
        colorPicker.addSVBar(svBar);

        dialog.show();
        Button declineButton = (Button) dialog.findViewById(R.id.cancel_color_change);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });

        Button changeColor = (Button) dialog.findViewById(R.id.change_color);
        // if decline button is clicked, close the custom dialog
        changeColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainBoard.currentMarker.getID() != 4) {
                    int newColor = colorPicker.getColor();
                    mainBoard.changeMarkerColor(newColor);
                    changeButtonColor(mainBoard.currentMarker.getID());
                    dialog.dismiss();
                }else{
                    System.out.println("CAN'T CHANGE ERASER COLOR");
                    dialog.dismiss();
                }
            }
        });
    }

    public void changeButtonColor(int id){
        final Board mainBoard = (Board) findViewById(R.id.board);
        Button currentButton;
        if(id == 0){
           currentButton = (Button) markerTray.findViewById(R.id.button_1);
           currentButton.setBackgroundColor(mainBoard.tray[0].getColor());
        }else if (id == 1){
            currentButton = (Button) markerTray.findViewById(R.id.button_2);
            currentButton.setBackgroundColor(mainBoard.tray[1].getColor());
        }else if (id == 2){
            currentButton = (Button) markerTray.findViewById(R.id.button_3);
            currentButton.setBackgroundColor(mainBoard.tray[2].getColor());
        }else if (id == 3){
            currentButton = (Button) markerTray.findViewById(R.id.button_4);
            currentButton.setBackgroundColor(mainBoard.tray[3].getColor());
        }
    }

    public void updateButtonColors(){
        final Board mainBoard = (Board) findViewById(R.id.board);
        Button currentButton;

        currentButton = (Button) markerTray.findViewById(R.id.button_1);
        currentButton.setBackgroundColor(mainBoard.tray[0].getColor());

        currentButton = (Button) markerTray.findViewById(R.id.button_2);
        currentButton.setBackgroundColor(mainBoard.tray[1].getColor());

        currentButton = (Button) markerTray.findViewById(R.id.button_3);
        currentButton.setBackgroundColor(mainBoard.tray[2].getColor());

        currentButton = (Button) markerTray.findViewById(R.id.button_4);
        currentButton.setBackgroundColor(mainBoard.tray[3].getColor());

    }

}
