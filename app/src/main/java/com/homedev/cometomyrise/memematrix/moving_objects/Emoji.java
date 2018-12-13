package com.homedev.cometomyrise.memematrix.moving_objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;


public class Emoji {

    //transparency constants
    private static final int TRANSP_1 = 255;
    private static final int TRANSP_2 = 204;
    private static final int TRANSP_3 = 153;
    private static final int TRANSP_4 = 102;
    private static final int TRANSP_5 = 51;

    //-----------------------------------------//

    //picture
    private Bitmap mPicture;

    //-----------------------------------------//

    //position & size variables
    private int mPosX;
    private int mPosY;
    //only one variable for size, instead height & width, cuz image will be always SQUARED
    private int mSize;

    //-----------------------------------------//
    private QueuePos mQueuePos;
    //speed variable
    private int mSpeed;

    //-----------------------------------------//

    //-------- C O N S T R U C T O R ----------//
    public Emoji(Bitmap picture) {
        mPicture = picture;
    }

    //-----------------------------------------//
    //-----------------------------------------//
    //-----------------------------------------//

    private void updatePosition() {
        //only y coordinate changes, cuz emoji literally drops down
        mPosY += mSpeed;
    }
    //-----------------------------------------//

    public void drawItemOnCanvas(Canvas canvas) {
        updatePosition();

        Paint paint = new Paint();
        paint.setAlpha(mQueuePos.getValue());
        canvas.drawBitmap(mPicture, mPosX, mPosY, paint);
    }

    public int getPosX() {
        return mPosX;
    }

    public void setPosX(int posX) {
        mPosX = posX;
    }

    public int getPosY() {
        return mPosY;
    }

    public void setPosY(int posY) {
        mPosY = posY;
    }

    public int getSize() {
        return mSize;
    }

    /**
     * note: after resetting size it will also rescale image
     */
    public void setSize(int size) {
        mSize = size;
        mPicture = Bitmap.createScaledBitmap(mPicture, mSize, mSize, false);
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    public QueuePos getQueuePos() {
        return mQueuePos;
    }

    public void setQueuePos(QueuePos queuePos) {
        this.mQueuePos = queuePos;
    }

    //enum class for transparency manage
    public enum QueuePos {
        first(TRANSP_1),
        second(TRANSP_2),
        third(TRANSP_3),
        fourth(TRANSP_4),
        fifth(TRANSP_5);

        private int index;

        QueuePos(int index) {
            this.index = index;
        }

        public int getValue() {
            return index;
        }
    }
}
