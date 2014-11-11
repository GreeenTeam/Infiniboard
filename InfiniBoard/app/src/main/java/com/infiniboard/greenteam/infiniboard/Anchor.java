package com.infiniboard.greenteam.infiniboard;

/**
 * Created by Justin and Brandon on 11/7/14.
 */
public class Anchor {

    private int posX;
    private String name;
    private Board board;

    /*public Anchor(){
        //Empty constructor
        //For creating an Anchor without a position
    }*/

    public Anchor(int x,String n, Board b){
        //Reference
        //On create you can pass in the position of the Anchor
        name = n;
        board = b;
        posX = x;
    }

    public void goToAnchor(){
        //This method will take you to the Anchor's position
        //It will use posX to snap you to that view
        board.goToSubBoard(posX);
    }

    /*public void setAnchorReference(){
        //In case we want to set the reference through a method
    }

    public void setPosX(int x){
        posX = x;
    }*/

    public int getAnchorPosX(){
        return posX;
    }

    public String getAnchorName(){
        return name;
    }

}
