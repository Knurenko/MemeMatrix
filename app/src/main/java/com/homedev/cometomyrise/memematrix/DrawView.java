package com.homedev.cometomyrise.memematrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.homedev.cometomyrise.memematrix.moving_objects.Emoji;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The feature of the SurfaceView class is that it provides a separate Canvas for drawing,
 * actions with which should be carried out in a separate application thread.
 */
public class DrawView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    //--------------------------------------------------------//
    //                  C O N S T A N T S                     //
    //--------------------------------------------------------//
    private static final int MAX_SPAWN_TIME_MILLIS = 1000;
    private static final int CELL_SIDE = 40;
    private static final int MAX_SPEED = 8;
    private static final boolean GRID_SHOW = true;


    //--------------------------------------------------------//
    //                  V A R I A B L E S                     //
    //--------------------------------------------------------//

    // thread to get canvas, using surfaceHolder.lockCanvas
    private DrawThread mDrawThread;
    //variable to store image
    private Bitmap mEmojiPic;
    //test emoji object item
    private Random mRandom = new Random();
    //using concurrentHashMap to avoid ConcurrentModificationException
    private ConcurrentHashMap<Emoji, Emoji> mEmojiMap = new ConcurrentHashMap<>();


    private boolean spawnRunning = false;
    //thread for spawn emoji objects
    private Thread spawnThread;

    //screen bounds
    private int screenBoundX;
    private int screenBoundY;

    //--------------------------------------------------------//
    //                 C O N S T R U C T O R                  //

    public DrawView(Context context) {
        super(context);
        getHolder().addCallback(this);

        //init thread
        mDrawThread = new DrawThread(this);
        spawnThread = new Thread(this);
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
        screenBoundX = this.getWidth();
        screenBoundY = this.getHeight();

        //load pic
        mEmojiPic = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.emoji_rofl_openeye),
                screenBoundX / 10,      // size of emoji items will be x10 smaller than width of screen
                screenBoundX / 10,
                false
        );

        //spawn start
        spawnRunning = true;
        spawnThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //leave empty
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //drawing thread end
        mDrawThread.interrupt();
        spawnThread.interrupt();
    }
    //--------------------------------------------------------//

    //--------------------------------------------------------//
    //  A L L   M A G I C   G O E S    B E L O W   (drawing)  //

    public void drawPicsOnCanvas(Canvas canvas) {
        drawBackground(canvas);

        for (Emoji item : mEmojiMap.values()) {
            //cycle across all hash map
            if (item.getPosY() < screenBoundY) {
                item.drawItemOnCanvas(canvas);
            } else {
                mEmojiMap.remove(item);
            }
        }

    }
    //--------------------------------------------------------//

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        if (GRID_SHOW) {
            //path variable to insert grid in
            Path path = new Path();
            path.reset();

            //paint variable to hold drawing config
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            paint.setColor(Color.GREEN);

            //cells
            //method that draw lines on path, and then path on canvas
            for (int x = 0; x < this.getWidth(); x += CELL_SIDE) {
                //draw vertical line across full screen height
                path.moveTo(x, 0);
                path.lineTo(x, this.getHeight());
            }
            for (int y = 0; y < this.getHeight(); y += CELL_SIDE) {
                path.moveTo(0, y);
                path.lineTo(this.getWidth(), y);
            }

            //draw prepared path on canvas
            canvas.drawPath(path, paint);
        }
    }
    //--------------------------------------------------------//

    //--------------------------------------------------------//
    //      M E T H O D    T O    K I L L    T H R E A D      //

    //like in DrawThread
    public void interrupt() {
        boolean retry = true;
        spawnRunning = false;
        while (retry) {
            try {
                spawnThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // keep trying again'n'again
            }
        }
    }
    //--------------------------------------------------------//

    //--------------------------------------------------------//
    //        S P A W N I N G     C O D E     B E L OW        //

    @Override
    public void run() {
        while (spawnRunning) {
            //init first item of array
            Emoji[] items = new Emoji[5];
            items[0] = new Emoji(mEmojiPic);
            items[0].setPosY(0);
            items[0].setPosX(mRandom.nextInt((screenBoundX - mEmojiPic.getWidth())));
            items[0].setSpeed(mRandom.nextInt(MAX_SPEED) + 1);

            //init other 4 items
            for (int i = 1; i < items.length; i++) {
                items[i] = new Emoji(mEmojiPic);
                items[i].setPosX(items[0].getPosX());
                items[i].setPosY((items[i - 1].getPosY() - items[0].getSpeed() * 8) + 3);
                items[i].setSpeed(items[0].getSpeed());
            }

            //set
            items[0].setQueuePos(Emoji.QueuePos.first);
            items[1].setQueuePos(Emoji.QueuePos.second);
            items[2].setQueuePos(Emoji.QueuePos.third);
            items[3].setQueuePos(Emoji.QueuePos.fourth);
            items[4].setQueuePos(Emoji.QueuePos.fifth);

            try {
                //wait'n'add
                Thread.sleep(mRandom.nextInt(MAX_SPAWN_TIME_MILLIS));
                for (Emoji item : items) {
                    //maybe need to put from index 4->1
                    mEmojiMap.put(item, item);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //--------------------------------------------------------//

}
