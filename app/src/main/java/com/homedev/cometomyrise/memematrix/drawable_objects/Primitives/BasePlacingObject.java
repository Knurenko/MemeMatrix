package com.homedev.cometomyrise.memematrix.drawable_objects.Primitives;

public class BasePlacingObject {

    public int posX;
    public int posY;

    public BasePlacingObject(int x, int y){
        posX = x;
        posY = y;
    }
    BasePlacingObject(){}

    public void setPosition(int x, int y){
        posX = x;
        posY = y;
    }

}
