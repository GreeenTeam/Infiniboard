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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.graphics.Color;
import android.app.Dialog;
import android.content.DialogInterface;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LayeredContainer extends LinearLayout {

    // There will be two views one for the Marker Tray and One for the Board

    private View markerTray;
    private View board;
    private View mainMenuMore1;
    private View mainBubbleMenu;
    private View selectBubbleMenu1;
    private View selectBubbleMenu2;
    private View selectBubbleMenu3;
    private View anchorBubbleMenu;
    private View boardBubbleMenu;
    private BackButton backButton;
    private Board mainBoard;

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
        mainBoard =  (Board) findViewById(R.id.board);
        mainBubbleMenu = this.findViewById(R.id.bubble_menu_main);
        mainMenuMore1 = this.findViewById(R.id.bubble_menu_main_sub);
        selectBubbleMenu1 = this.findViewById(R.id.bubble_menu_select_1);
        selectBubbleMenu2 = this.findViewById(R.id.bubble_menu_select_2);
        selectBubbleMenu3 = this.findViewById(R.id.bubble_menu_select_3);
        anchorBubbleMenu = this.findViewById(R.id.bubble_menu_anchors);
        boardBubbleMenu = this.findViewById(R.id.bubble_menu_boards);
        backButton = (BackButton) this.findViewById(R.id.back_button);
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

    public void goRight(View v){
        System.out.println("Go RIGHT");
        mainBoard.goRightSubBoard();
    }

    public void goLeft(View v){
        System.out.println("Go LEFT");
        mainBoard.goLeftSubBoard();
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
        mainMenuMore1.setVisibility(View.GONE);
        backButton.clearPastMenus();
        backButton.setVisibility(View.GONE);
        backButton.setEnabled(false);
        backButton.setAlpha((float) .50);
    }

    public void setSelectMode (){
        mainBoard.inSelector = true;
        mainBubbleMenu.setVisibility(View.GONE);
        clearMenus();
    }


    // The following are the implementations of all the onClick Methods for the buttons in the Radial Menu
    public void showMainMenu(View v){
        mainMenuMore1.setVisibility(View.GONE);
        backButton.addMenu(mainMenuMore1.getId());
        mainBubbleMenu.setVisibility(View.VISIBLE);
    }

    public void showMainMenuMore1(View v){
        mainBubbleMenu.setVisibility(View.GONE);
        Button expand = (Button) findViewById(R.id.expandboard);
        if(!mainBoard.canExpand()) {
            expand.setAlpha(.5f);
            expand.setEnabled(false);
        }else{
            expand.setAlpha(1f);
            expand.setEnabled(true);
        }
        backButton.addMenu(mainBubbleMenu.getId());
        mainMenuMore1.setVisibility(View.VISIBLE);
    }

    public void showBoardMenu(View v){
        mainMenuMore1.setVisibility(View.GONE);
        backButton.addMenu(mainMenuMore1.getId());
        Button deleteButton = (Button) findViewById(R.id.delete_board);
        if(mainBoard.canDelete()){
            deleteButton.setEnabled(true);
            deleteButton.setAlpha(1f);
        }else{
            deleteButton.setEnabled(false);
            deleteButton.setAlpha(.5f);
        }
        Button gotoButton = (Button) findViewById(R.id.go_to_board);
        if(mainBoard.canGoTo()){
            gotoButton.setEnabled(true);
            gotoButton.setAlpha(1f);
        }else{
            gotoButton.setEnabled(false);
            gotoButton.setAlpha(.5f);
        }
        boardBubbleMenu.setVisibility(View.VISIBLE);
    }
    public void showAnchorMenu(View v){
        mainMenuMore1.setVisibility(View.GONE);
        Button goToAnchor = (Button) findViewById(R.id.go_to_anchor);
        if(mainBoard.anchors.isEmpty()) {
            goToAnchor.setAlpha(.5f);
            goToAnchor.setEnabled(false);
        }else{
            goToAnchor.setAlpha(1f);
            goToAnchor.setEnabled(true);
        }
        backButton.addMenu(mainMenuMore1.getId());
        anchorBubbleMenu.setVisibility(View.VISIBLE);
    }


    public void showSelectMenu1(View v){
        selectBubbleMenu3.setVisibility(View.GONE);
        backButton.addMenu(selectBubbleMenu3.getId());
        mainBoard.inSelector = true;
        selectBubbleMenu1.setVisibility(View.VISIBLE);
    }

    public void showSelectMenu2(View v){
        selectBubbleMenu1.setVisibility(View.GONE);
        backButton.addMenu(selectBubbleMenu1.getId());
        mainBoard.inSelector = true;
        selectBubbleMenu2.setVisibility(View.VISIBLE);
    }

    public void showSelectMenu3(View v){
        selectBubbleMenu2.setVisibility(View.GONE);
        backButton.addMenu(selectBubbleMenu2.getId());
        mainBoard.inSelector = true;;
        selectBubbleMenu3.setVisibility(View.VISIBLE);
    }
    public void showColorMenu(View v) {
        clearMenus();
        System.out.println("SHOW COLOR MENU");
        colorDialog();
    }
    public void showSizeMenu(View v){
        clearMenus();
        System.out.println("SHOW SIZE MENU");
        sizeDialog();
    }
    public void moveSelection(View v){
        clearMenus();
        System.out.println("MOVE SELECTION");
        mainBoard.selector.moveSelection();
        mainBoard.inSelector = false;
    }
    public void scaleSelection(View v){
        clearMenus();
        System.out.println("SCALE SELECTION");

        scaleDialog();
        mainBoard.inSelector = false;
    }
    public void rotateSelection(View v){
        clearMenus();
        System.out.println("ROTATE SELECTION");
        rotateDialog();
        mainBoard.inSelector = false;
    }
    public void colorizeSelection(View v){
        clearMenus();
        colorizeDialog();
        System.out.println("SHOW COLOR MENU THE COLORIZE SELECTION");
        mainBoard.inSelector = false;
    }
    public void pasteSelection(View v){
        clearMenus();

        System.out.println("PASTE SELECTION");
        mainBoard.inPasteMode = true;
        mainBoard.inSelector = false;
    }
    public void copySelection(View v){
        clearMenus();
        mainBoard.selector.copySelection();
        System.out.println("COPY SELECTION");
        mainBoard.inSelector = false;
    }
    public void cutSelection(View v){
        clearMenus();
        mainBoard.selector.cutSelection();
        mainBoard.addSelectionToCanvas(mainBoard.selector.getCurrentX(),mainBoard.selector.getCurrentY());
        invalidate();
        System.out.println("CUT SELECTION");
        mainBoard.inSelector = false;
    }
    public void eraseSelection(View v){
        // DONE - BRANDON CARUSO  WE MIGHT WHAT TO RETHINK RIGHT NOW ITS LIKE COLORIZING ALL TO WHITE
        clearMenus();
        mainBoard.selector.eraseSelection();
        mainBoard.addSelectionToCanvas(mainBoard.selector.getCurrentX(),mainBoard.selector.getCurrentY());
        invalidate();
        System.out.println("ERASE SELECTION");
        mainBoard.inSelector = false;
    }
    public void exportSelection(View v){
        clearMenus();
        selectionToImageDialog();
        System.out.println("EXPORT SELECTION");
        mainBoard.inSelector = false;
    }
    public void cancelSelection(View v){
        clearMenus();
        System.out.println("CANCEL SELECTION");
        mainBoard.inSelector = false;
    }
    public void createNewAnchor(View v){
        clearMenus();
        System.out.println("CREATE NEW ANCHOR");
        createAnchorDialog();
    }
    public void showAnchorGoToMenu(View v){
        clearMenus();
        viewAnchorsDialog();
        System.out.println("SHOW ANCHOR GO TO");
    }
    public void showBoardGoToMenu(View v){
        // THIS MIGHT BE MOVED UP
        clearMenus();
        viewBoardsDialog();
        System.out.println("SHOW BOARD GO TO");
    }
    public void createBoard(View v){
        // THIS MIGHT BE MOVED UP
        clearMenus();
        createBoardDialog();
        System.out.println("CREATE BOARD");
        invalidate();
    }
    public void changeBoardProp(View v){
        // THIS MIGHT BE MOVED UP
        clearMenus();

        System.out.println("CHANGE BOARD PROPERTIES");
        editBoardDialog();

    }
    public void exportBoard(View v){
        // THIS MIGHT BE MOVED UP
        clearMenus();
        boardToImageDialog();
        System.out.println("CHANGE BOARD PROPERTIES");
    }
    public void deleteBoard(View v){
        // THIS MIGHT BE MOVED UP
        clearMenus();
        confirmDelete();
        System.out.println("CHANGE BOARD PROPERTIES");
    }

    public void expandBoard(View v){
        // THIS MIGHT BE MOVED UP
        clearMenus();
        mainBoard.expandBoard();
        System.out.println("CHANGE BOARD PROPERTIES");
    }

    public void showHelp(View v){
        // THIS MIGHT BE MOVED UP
        clearMenus();
        mainBoard.helpDialog();
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
        sizeBar.setProgress(mainBoard.getCurrentMarker().getStrokeWidth() - 10);
        brushSizeGraphic.getLayoutParams().height = brushSizeGraphic.getLayoutParams().width = mainBoard.getCurrentMarker().getStrokeWidth();
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
                sizeBar.setProgress(mainBoard.getCurrentMarker().getDefaultSize()-10);
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

    public void rotateDialog(){

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());


        dialog.setContentView(R.layout.dialog_rotate);
        dialog.setTitle("Rotate Current Selection");

        final Board mainBoard = (Board) findViewById(R.id.board);
        final SeekBar rotateBar = (SeekBar) dialog.findViewById(R.id.degree_bar);
        final TextView percent = (TextView) dialog.findViewById(R.id.degree_text);
        dialog.show();

        rotateBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String degree = Integer.toString(rotateBar.getProgress() * 15);
                percent.setText( degree + (char)0x00B0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        } );
        Button declineButton = (Button) dialog.findViewById(R.id.cancel_rotate_change);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });



        Button changeRotate = (Button) dialog.findViewById(R.id.change_rotate);
        // if decline button is clicked, close the custom dialog
        changeRotate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mainBoard.selector.rotateSelection(rotateBar.getProgress()*15f);
                dialog.dismiss();
            }
        });
    }

    public void scaleDialog(){

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());


        dialog.setContentView(R.layout.dialog_scale);
        dialog.setTitle("Scale Current Selection");

        final Board mainBoard = (Board) findViewById(R.id.board);
        final SeekBar scaleBar = (SeekBar) dialog.findViewById(R.id.scale_bar);
        final TextView percent = (TextView) dialog.findViewById(R.id.percent_text);
        dialog.show();

        scaleBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                percent.setText((scaleBar.getProgress()+1)+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        } );
        Button declineButton = (Button) dialog.findViewById(R.id.cancel_scale_change);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });



        Button changeScale = (Button) dialog.findViewById(R.id.change_scale);
        // if decline button is clicked, close the custom dialog
        changeScale.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mainBoard.selector.scaleSelection(((scaleBar.getProgress()+1)/100.00f));
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
        colorPicker.setOldCenterColor(mainBoard.getCurrentMarker().getColor());
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

        Button resetButton = (Button) dialog.findViewById(R.id.reset_color_change);

        resetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPicker.setColor(mainBoard.getCurrentMarker().getDefaultColor());
            }
        });

        Button changeColor = (Button) dialog.findViewById(R.id.change_color);
        // if decline button is clicked, close the custom dialog
        changeColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainBoard.getCurrentMarker().getID() != 4) {
                    int newColor = colorPicker.getColor();
                    mainBoard.changeMarkerColor(newColor);
                    changeButtonColor(mainBoard.getCurrentMarker().getID());
                    dialog.dismiss();
                }else{
                    System.out.println("CAN'T CHANGE ERASER COLOR");
                    dialog.dismiss();
                }
            }
        });
    }

    public void colorizeDialog(){

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file

        dialog.setContentView(R.layout.dialog_colorize);
        dialog.setTitle("Change Color of Selection");

        final Board mainBoard = (Board) findViewById(R.id.board);
        final ColorPicker colorPicker = (ColorPicker) dialog.findViewById(R.id.picker);
        final SVBar svBar = (SVBar) dialog.findViewById(R.id.svbar);
        colorPicker.setShowOldCenterColor(false);
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
                mainBoard.selector.changeColour(colorPicker.getColor());
                mainBoard.addSelectionToCanvas(mainBoard.selector.getCurrentX(),mainBoard.selector.getCurrentY());
                invalidate();
                dialog.dismiss();
            }
        });
    }

    public void viewBoardsDialog(){

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file

        dialog.setContentView(R.layout.dialog_view_boards);
        dialog.setTitle("Go To a Different Board");


        final ArrayList<String> names = new ArrayList<String>();
        final ListView listView = (ListView) dialog.findViewById(R.id.listView);
        try {


            String filename = "/boards/board_names.txt";
            File file = new File(getContext().getFilesDir(), filename);

            // if file doesnt exists, then create it
            Scanner s = new Scanner(file);

            while (s.hasNext()){
                names.add(s.nextLine().replace('_',' '));
            }

            System.out.println("");

        } catch (IOException e) {
            e.printStackTrace();
        }

        listView.setAdapter(new ArrayAdapter<String>(dialog.getContext(),android.R.layout.simple_list_item_1,android.R.id.text1, names));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                System.out.println(parent.getItemAtPosition(position).toString());
                if(!mainBoard.getName().equals(parent.getItemAtPosition(position).toString().replace(' ', '_'))){
                    mainBoard.saveYourSelf();
                    mainBoard.loadBoardByName(parent.getItemAtPosition(position).toString().replace(' ','_'));
                    mainBoard.goToSubBoard(0);
                }

                dialog.dismiss();
            }

        });

        dialog.show();
        Button declineButton = (Button) dialog.findViewById(R.id.cancel_goto_board);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
    }

    public void viewAnchorsDialog(){

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file

        dialog.setContentView(R.layout.dialog_view_anchors);
        dialog.setTitle("Go To a Different Anchor");

        ListView listView = (ListView) dialog.findViewById(R.id.listView);

        final ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < mainBoard.anchors.size(); ++i) {
            names.add(mainBoard.anchors.get(i).getAnchorName());
        }

        listView.setAdapter(new ArrayAdapter<String>(dialog.getContext(),android.R.layout.simple_list_item_1,android.R.id.text1, names));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                mainBoard.anchors.get(names.indexOf(parent.getItemAtPosition(position))).goToAnchor();
                dialog.dismiss();
            }

        });

        dialog.show();
        Button declineButton = (Button) dialog.findViewById(R.id.cancel_goto_anchor);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
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

    public void boardToImageDialog(){

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file

        dialog.setContentView(R.layout.dialog_board_image);
        dialog.setTitle("Save Entire Board as Image:");

        Button export = (Button) dialog.findViewById(R.id.export);
        final EditText name = (EditText) dialog.findViewById(R.id.image_name);
        final EditText description = (EditText) dialog.findViewById(R.id.image_description);
        name.setText(mainBoard.getName()+"_IMAGE");
        description.setText(mainBoard.getDescription());
        dialog.show();
        Button declineButton = (Button) dialog.findViewById(R.id.cancel_export);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });


        // if decline button is clicked, close the custom dialog
        export.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mainBoard.saveImage(name.getText().toString(),description.getText().toString(),getContext());
                dialog.dismiss();
            }
        });
    }

    public void editBoardDialog(){

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file



        dialog.setContentView(R.layout.dialog_edit_board);
        final Board mainBoard = (Board) findViewById(R.id.board);
        final EditText name = (EditText) dialog.findViewById(R.id.board_name);
        final EditText description = (EditText) dialog.findViewById(R.id.board_description);
        final TextView date = (TextView) dialog.findViewById(R.id.date_created);
        dialog.setTitle("Your Board's Information");
        name.setText(mainBoard.getName().replace('_',' '));
        description.setText(mainBoard.getDescription());
        date.setText("Date Created: " + mainBoard.getDateCreated());
        final String previousName = mainBoard.getName();
        String previousDescription = mainBoard.getDescription();
        dialog.show();
        Button declineButton = (Button) dialog.findViewById(R.id.cancel_edit);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });

        Button saveChanges = (Button) dialog.findViewById(R.id.save_edit);
        // if decline button is clicked, close the custom dialog
        saveChanges.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mainBoard.setName(name.getText().toString().replace(' ','_'));
                mainBoard.setDescription(description.getText().toString());
                mainBoard.changeBoardDir(previousName);
                dialog.dismiss();
            }
        });
    }
    public void selectionToImageDialog(){

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file

        dialog.setContentView(R.layout.dialog_select_image);
        dialog.setTitle("Save Selection as Image:");



        dialog.show();
        Button declineButton = (Button) dialog.findViewById(R.id.cancel_export);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });

        Button export = (Button) dialog.findViewById(R.id.export);
        final EditText name = (EditText) dialog.findViewById(R.id.selection_name);
        final EditText description = (EditText) dialog.findViewById(R.id.selection_description);
        // if decline button is clicked, close the custom dialog
        export.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mainBoard.selector.saveSelection(name.getText().toString(),description.getText().toString(),getContext());
                dialog.dismiss();
            }
        });
    }

    public void createAnchorDialog(){

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file

        dialog.setContentView(R.layout.dialog_new_anchor);
        dialog.setTitle("Create An Anchor:");



        dialog.show();
        Button declineButton = (Button) dialog.findViewById(R.id.cancel_anchor);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });

        Button create = (Button) dialog.findViewById(R.id.create);
        final EditText name = (EditText) dialog.findViewById(R.id.anchor_name);
        // if decline button is clicked, close the custom dialog
        create.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mainBoard.createNewAnchor(name.getText().toString());
                dialog.dismiss();
            }
        });
    }

    public void createBoardDialog(){

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file

        dialog.setContentView(R.layout.dialog_new_board);
        dialog.setTitle("Create An New Board:");



        dialog.show();
        Button declineButton = (Button) dialog.findViewById(R.id.cancel);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });

        Button create = (Button) dialog.findViewById(R.id.create);
        final EditText n = (EditText) dialog.findViewById(R.id.board_name);
        final EditText d = (EditText) dialog.findViewById(R.id.board_description);
        // if decline button is clicked, close the custom dialog
        create.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!n.getText().toString().equals("")) {
                    mainBoard.saveYourSelf();
                    mainBoard.createNewBoard(n.getText().toString(), d.getText().toString());
                    mainBoard.goToSubBoard(0);
                    dialog.dismiss();
                } else{
                    TextView n =(TextView) dialog.findViewById(R.id.name_board_text);
                    n.setText("Name Your Board:   Must provide a name!!");
                }
            }
        });
    }

    public void confirmDelete(){

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file

        dialog.setContentView(R.layout.dialog_delete);

        dialog.show();
        Button confirm = (Button) dialog.findViewById(R.id.delete);
        // if decline button is clicked, close the custom dialog
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                mainBoard.deleteBoard();
                dialog.dismiss();
            }
        });
        Button declineButton = (Button) dialog.findViewById(R.id.cancel);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
    }

    public void goToPreviousMenu(View v){
        mainBubbleMenu.setVisibility(View.GONE);
        selectBubbleMenu1.setVisibility(View.GONE);
        selectBubbleMenu2.setVisibility(View.GONE);
        selectBubbleMenu3.setVisibility(View.GONE);
        anchorBubbleMenu.setVisibility(View.GONE);
        boardBubbleMenu.setVisibility(View.GONE);
        mainMenuMore1.setVisibility(View.GONE);
        if(backButton.getSize()-1 <= 0) {
            backButton.setAlpha(.5f);
            backButton.setEnabled(false);
            View menu =  findViewById(backButton.getLastMenu());
            menu.setVisibility(View.VISIBLE);
        }else {
            View menu = findViewById(backButton.getLastMenu());
            menu.setVisibility(View.VISIBLE);
        }
    }


}
