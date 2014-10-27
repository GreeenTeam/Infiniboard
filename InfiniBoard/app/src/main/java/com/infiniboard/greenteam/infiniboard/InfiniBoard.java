package com.infiniboard.greenteam.infiniboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;


// The Implementation of the Sliding Menu Layout was Developed with the use of a Reference Material
// Brandon Caruso used the source developed by GitHub User jaylamont as a reference.
// https://github.com/jaylamont/AndroidFlyOutMenuDemo.git

public class InfiniBoard extends Activity {

    LayeredContainer root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the root to look at the layout specified in activity_infini_board
        root = (LayeredContainer) this.getLayoutInflater().inflate(R.layout.activity_infini_board,null);
        setContentView(root);

        final Button menu_button = (Button) findViewById(R.id.menu_button);
        menu_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                root.clearMenus();

                Button colorChange = (Button) findViewById(R.id.color);
                Button mainPaste = (Button) findViewById(R.id.main_paste);

                final Board mainBoard = (Board) findViewById(R.id.board);

                if (mainBoard.currentMarker.getID() == 4) {
                    colorChange.setAlpha((float) .5);
                    colorChange.setEnabled(false);
                }else {
                    colorChange.setAlpha((float) 1.0);
                    colorChange.setEnabled(true);
                }

                if (!mainBoard.selector.hasClipboard()) {
                    mainPaste.setAlpha((float) .5);
                    mainPaste.setEnabled(false);
                }else {
                    mainPaste.setAlpha((float) 1.0);
                    mainPaste.setEnabled(true);
                }

                BackButton backButton = (BackButton) findViewById(R.id.back_button);
                backButton.setVisibility(View.VISIBLE);

                if (!mainBoard.inSelector) {
                    FrameLayout mainBubbleMenu = (FrameLayout) findViewById(R.id.bubble_menu_main);
                    System.out.println("LONG CLICK -- SHOW BUBBLE MENU");

                    mainBubbleMenu.setVisibility(View.VISIBLE);
                }else{
                    FrameLayout selectmenu = (FrameLayout) findViewById(R.id.bubble_menu_select_1);
                    System.out.println("IN SELECT MODE -- LONG CLICK -- SHOW BUBBLE MENU");
                    selectmenu.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });


      final View intro = findViewById(R.id.intro);
        intro.setOnTouchListener(new View.OnTouchListener() {
           @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               intro.setVisibility(View.GONE);
               return false;
            }
        });

        final View board = findViewById(R.id.board);
        board.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                root.clearMenus();
                return false;
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.infini_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggleMenu(View v){
        root.toggleMenu();
    }
    public void changeMarkerTo(View v){
        root.changeMarkerTo(v);
    }
    public void showMainMenu(View v){
        root.showMainMenu(v);
    }
    public void showMainMenuMore1(View v){
        root.showMainMenuMore1(v);
    }
    public void showBoardMenu(View v){
        root.showBoardMenu(v);
    }
    public void showAnchorMenu(View v){
        root.showAnchorMenu(v);
    }
    public void setSelectMode(View v){root.setSelectMode();}
    public void showSelectMenu1(View v){ root.showSelectMenu1(v); }
    public void showSelectMenu2(View v){
        root.showSelectMenu2(v);
    }
    public void showSelectMenu3(View v){
        root.showSelectMenu3(v);
    }
    public void showColorMenu(View v){
        root.showColorMenu(v);
    }
    public void showSizeMenu(View v){
        root.showSizeMenu(v);
    }
    public void moveSelection(View v){
        root.moveSelection(v);
    }
    public void scaleSelection(View v){
        root.scaleSelection(v);
    }
    public void rotateSelection(View v){
        root.rotateSelection(v);
    }
    public void colorizeSelection(View v){
        root.colorizeSelection(v);
    }
    public void pasteSelection(View v){
        root.pasteSelection(v);
    }
    public void copySelection(View v){
        root.copySelection(v);
    }
    public void cutSelection(View v){
        root.cutSelection(v);
    }
    public void eraseSelection(View v){
        root.eraseSelection(v);
    }
    public void exportSelection(View v){
        root.exportSelection(v);
    }
    public void cancelSelection(View v){
        root.cancelSelection(v);
    }
    public void createNewAnchor(View v){
        root.createNewAnchor(v);
    }
    public void showAnchorGoToMenu(View v){
        root.showAnchorGoToMenu(v);
    }
    public void showBoardGoToMenu(View v){
        root.showBoardGoToMenu(v);
    }
    public void createBoard(View v){
        root.createBoard(v);
    }
    public void changeBoardProp(View v){
        root.changeBoardProp(v);
    }
    public void exportBoard(View v){
        root.exportBoard(v);
    }
    public void deleteBoard(View v){
        root.deleteBoard(v);
    }
    public void goToPreviousMenu(View v){
        root.goToPreviousMenu(v);
    }
}
