package com.infiniboard.greenteam.infiniboard;

import android.util.AttributeSet;
import android.widget.Button;
import android.content.Context;
import java.util.Stack;
import java.util.jar.Attributes;

/**
 * Created by Brandon on 10/22/14.
 */
public class BackButton extends Button {

    private Stack<Integer> s = new Stack<Integer>();


    public BackButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BackButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackButton(Context context) {
        super(context);
    }

    public void addMenu(int i){
        this.setAlpha(1f);
        this.setEnabled(true);
        s.push(i);
    }

    public int getLastMenu(){
            return s.pop();
    }

    public void clearPastMenus(){
        this.setAlpha(.5f);
        this.setEnabled(false);
        s.clear();
    }

    public boolean hasAPast(){
        return s.empty();
    }
    public int getSize(){
        return s.size();
    }
}
