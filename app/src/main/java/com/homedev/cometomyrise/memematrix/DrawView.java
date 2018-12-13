package com.homedev.cometomyrise.memematrix;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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

}
