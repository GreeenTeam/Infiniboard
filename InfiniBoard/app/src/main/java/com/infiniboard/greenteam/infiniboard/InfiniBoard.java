package com.infiniboard.greenteam.infiniboard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


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
                System.out.println("LONG CLICK -- SHOW BUBBLE MENU");
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


}
