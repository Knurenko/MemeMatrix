package com.homedev.cometomyrise.memematrix.drawable_objects.Primitives;

import android.graphics.Bitmap;
import android.graphics.Canvas;

@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public abstract class DrawableObject extends BasePlacingObject {

    protected boolean isClicked;
    private Bitmap picture;
    private boolean isMoving;

    /**
     * constructor of <strong>all drawable objects</strong>
     * @param picture Bitmap image to draw
     * @param positionX position X on the screen or game field
     * @param positionY position Y on the screen or game field
     */
    public DrawableObject(Bitmap picture, int positionX, int positionY){
        super(positionX, positionY);
        this.picture = picture;
        isMoving = false;
    }

    public DrawableObject(Bitmap picture){
        this.picture = picture;
        isMoving = false;
    }

    public void drawObjectOnCanvas(Canvas canvas){
        if (isMoving) updatePosition();
        canvas.drawBitmap(picture, posX, posY, null);
    }

    public void drawObjectOnCanvas(Canvas canvas, int size){
        if (isMoving) updatePosition();
        canvas.drawBitmap(picture, posX - size/2, posY - size/2, null);
    }

    //for moving objects
    public void updatePosition(){}

    void setMoving(){
        isMoving = true;
    }

    public void onClick(){}
    public void setClicked(boolean clicked){isClicked = clicked;}
}
