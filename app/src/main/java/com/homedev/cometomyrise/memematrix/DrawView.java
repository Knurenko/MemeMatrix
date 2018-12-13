package com.homedev.cometomyrise.memematrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.homedev.cometomyrise.memematrix.moving_objects.Emoji;



/**
 * The feature of the SurfaceView class is that it provides a separate Canvas for drawing,
 * actions with which should be carried out in a separate application thread.
 */
public class DrawView extends SurfaceView implements SurfaceHolder.Callback {

    //--------------------------------------------------------//
    //                  C O N S T A N T S                     //
    //--------------------------------------------------------//


    //--------------------------------------------------------//
    //                  V A R I A B L E S                     //
    //--------------------------------------------------------//

    // thread to get canvas, using surfaceHolder.lockCanvas
    private DrawThread mDrawThread;

    //variable to store image
    private Bitmap mEmojiPic;
    //test emoji object item
    private Emoji mObj;

    //screen bounds
    private int mScreenBoundX;
    private int mScreenBoundY;

    //--------------------------------------------------------//
    //                 C O N S T R U C T O R                  //

    public DrawView(Context context) {
        super(context);
        getHolder().addCallback(this);

        //init thread
        mDrawThread = new DrawThread(this);
    }
    //--------------------------------------------------------//


    //--------------------------------------------------------//
    //                 Surface view callbacks                 //

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //drawing thread start
        mDrawThread.setRunning(true);
        mDrawThread.start();

        //init screen corners
        mScreenBoundX = this.getWidth();
        mScreenBoundY = this.getHeight();

        //load pic
        mEmojiPic = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.emoji_rofl_openeye),
                mScreenBoundX / 10,      // size of emoji items will be x10 smaller than width of screen
                mScreenBoundX / 10,
                false
        );

        //init emoji obj (for test) todo replace by array soon
        mObj = new Emoji(mEmojiPic);
        mObj.setPosX(mScreenBoundX / 2);
        mObj.setPosY(50);
        mObj.setSpeed(2);
        mObj.setQueuePos(Emoji.QueuePos.first);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //leave empty
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //drawing thread end
        mDrawThread.interrupt();
    }
    //--------------------------------------------------------//

    //--------------------------------------------------------//
    //  A L L   M A G I C   G O E S    B E L O W   (drawing)  //

    public void drawPicsOnCanvas(Canvas canvas) {
        drawBackground(canvas);

        mObj.drawItemOnCanvas(canvas);
        if (mObj.getPosY() >= mScreenBoundY) mObj.setPosY(50);
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        // to do: add some background
    }
    //--------------------------------------------------------//


}
