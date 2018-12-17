package com.homedev.cometomyrise.memematrix;

import android.graphics.Canvas;
import android.view.SurfaceHolder;


/**
 * Thread for drawing pics on Canvas from SurfaceView
 */
public class DrawThread extends Thread {

    //const to hold how much milliseconds will be in one frame
    private static final byte MILLIS_IN_FRAME = 30;
    //surface holder instance for lock_n_draw & unlock_n_post canvas
    private final SurfaceHolder surfaceHolder;
    //boolean thread_isRunning flag
    private boolean running = false;
    //drawView instance
    private DrawView mDrawView;

    //variable to calculate elapsed time
    private long prevTime;


    //-------- C O N S T R U C T O R ----------//
    DrawThread(DrawView container) {
        this.mDrawView = container;
        surfaceHolder = mDrawView.getHolder();
    }
    //-----------------------------------------//

    void setRunning(boolean run) {
        running = run;
    }

    //all cycled manipulations will be calc by method below
    @Override
    public void run() {
        Canvas canvas;
        while (running) {

            // calc elapsed time
            long now = System.currentTimeMillis();
            long elapsedTime = now - prevTime;

            //draw frame each 30 milliseconds
            if (elapsedTime > MILLIS_IN_FRAME) {

                //set new time calc point
                prevTime = now;
                canvas = null;
                try {
                    // lock buffer to draw into canvas
                    canvas = surfaceHolder.lockCanvas(null);

                    synchronized (surfaceHolder) {
                        // here will be drawing ALL THINGS on canvas
                        // all things = massive of items with .drawOnCanvas(canvas) method
                        // COMPRESSED MAGIC!! BEWARE!
                        mDrawView.drawPicsOnCanvas(canvas);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        // release buffer & post result
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    /**
     * method to stop thread
     */
    public void interrupt() {
        boolean retry = true;

        //cycled thread killing
        setRunning(false);
        while (retry) {
            try {
                this.join();
                retry = false;
            } catch (InterruptedException e) {
                // keep trying again'n'again
            }
        }
    }
}