package com.homedev.cometomyrise.memematrix.drawable_objects.Primitives;

import android.graphics.Bitmap;

public abstract class MovingObject extends DrawableObject {

    private int speed;
    private int contactRadius;

    private BasePlacingObject mTarget;


    protected MovingObject(Bitmap picture, int posX, int posY) {
        super(picture, posX, posY);
        setMoving();
        contactRadius = 0;
    }

    protected MovingObject(Bitmap picture){
        super(picture);
        setMoving();
        contactRadius = 0;
    }

    public BasePlacingObject getTarget(){return mTarget; }

    public void setTarget(BasePlacingObject target){
        mTarget = target;
    }

    protected void setSpeed(int speed){
        this.speed = speed;
    }

    /**
     * set integer value of contact radius
     * @param radius value in points, needed to reach target
     */
    protected void setContactRadius(int radius){
        contactRadius = radius;
    }

    public boolean isTargetReached(){
        return ((posX >= mTarget.posX - contactRadius && posX <= mTarget.posX + contactRadius) && (posY >= mTarget.posY - contactRadius && posY <= mTarget.posY + contactRadius));
    }

    @Override
    public void updatePosition() {
        double angle = Math.atan2(mTarget.posY - posY, mTarget.posX - posX);

        posX += speed * Math.cos(angle);
        posY += speed * Math.sin(angle);
    }
}
