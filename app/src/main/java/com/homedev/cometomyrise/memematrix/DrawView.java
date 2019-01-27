package com.homedev.cometomyrise.memematrix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.homedev.cometomyrise.memematrix.Scene.CameraUtils;
import com.homedev.cometomyrise.memematrix.Scene.GameField;
import com.homedev.cometomyrise.memematrix.drawable_objects.Creep.BaseCreep;
import com.homedev.cometomyrise.memematrix.drawable_objects.Primitives.BasePlacingObject;
import com.homedev.cometomyrise.memematrix.drawable_objects.Primitives.DrawableObject;
import com.homedev.cometomyrise.memematrix.drawable_objects.Projectile.BaseProjectile;
import com.homedev.cometomyrise.memematrix.drawable_objects.Tower.BaseTower;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The feature of the SurfaceView class is that it provides a separate Canvas for drawing,
 * actions with which should be carried out in a separate application thread.
 */
public class DrawView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, BaseTower.Callbacks {

    //--------------------------------------------------------//
    //                  C O N S T A N T S                     //
    //--------------------------------------------------------//
    private static final int CELL_SIDE = 50;

    private static final BasePlacingObject startPos = new BasePlacingObject(200, GameField.SIZE_VERTICAL/2);
    private static final BasePlacingObject endPos = new BasePlacingObject(GameField.SIZE_HORIZONTAL-200, GameField.SIZE_VERTICAL/2);


    //--------------------------------------------------------//
    //                  V A R I A B L E S                     //
    //--------------------------------------------------------//

    // thread to get canvas, using surfaceHolder.lockCanvas
    private DrawThread mDrawThread;
    //variable to store image
    private Bitmap mEmojiSimplePic;
    private Bitmap mEmojiMatrixPic;
    private static final byte JUST_TOUCH = 0;
    private static final byte TOUCH_MOVE_CAMERA = 1;
    private static final byte TOUCH_ZOOM = 2;
    public Context mContext;
    //using concurrentHashMap to avoid ConcurrentModificationException
    private ConcurrentHashMap<BaseCreep, BaseCreep> mCreepMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<BaseProjectile, BaseProjectile> mProjectileMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<BaseTower, BaseTower> mTowerMap = new ConcurrentHashMap<>();
    //camera instance
    private CameraUtils mCamera;
    private GameField mField;
    //prepare to camera actions!
    private int lastTouchedX, lastTouchedY;
    private ScaleGestureDetector mScaleDetector;
    private byte touchMode = 0;

    //--------------------------------------------------------//
    //                 C O N S T R U C T O R                  //

    @SuppressLint("ClickableViewAccessibility")
    public DrawView(Context context) {
        super(context);
        getHolder().addCallback(this);
        this.setOnTouchListener(this);

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
        //screen bounds
        int screenBoundX = this.getWidth();
        final int screenBoundY = this.getHeight();

        mCamera = CameraUtils.init();
        mCamera.setBorders(screenBoundX, screenBoundY);
        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleListener());
        mField = new GameField();


        //load pic
        mEmojiSimplePic = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.emoji_rofl_openeye),
                screenBoundX / 10,      // size of emoji items will be x10 smaller than width of screen
                screenBoundX / 10,
                false
        );
        mEmojiMatrixPic = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.emoji_matrix),
                screenBoundX / 10,      // size of emoji items will be x10 smaller than width of screen
                screenBoundX / 10,
                false
        );

        //todo temporary code here, delete soon
        BaseTower towerLeft = new BaseTower(mEmojiSimplePic);
        towerLeft.setContainer(this);
        towerLeft.setPosition(700, 930);
        towerLeft.activate(mCreepMap);
        mTowerMap.put(towerLeft, towerLeft);

        BaseTower towerRight = new BaseTower(mEmojiSimplePic);
        towerRight.setContainer(this);
        towerRight.setPosition(1300, 1070);
        towerRight.activate(mCreepMap);
        mTowerMap.put(towerRight, towerRight);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //leave empty
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //drawing thread end
        mDrawThread.kill();

        for (BaseTower tower : mTowerMap.values()){
            tower.deactivate();
        }
    }
    //--------------------------------------------------------//

    //--------------------------------------------------------//
    //  A L L   M A G I C   G O E S    B E L O W   (drawing)  //

    public void drawPicsOnCanvas(Canvas canvas) {

        drawBackground(canvas);

        for (BaseCreep creep : mCreepMap.values()){
            if (creep.isTargetReached() && creep.getTarget() == endPos){
                mCreepMap.remove(creep);
            } else if (creep.isTargetReached() && creep.getTarget() == startPos){
                creep.setTarget(endPos);
            } else {
                creep.drawObjectOnCanvas(canvas, CELL_SIDE);
            }
        }

        for (BaseProjectile projectile : mProjectileMap.values()){
            if (projectile.isTargetReached()){
                mProjectileMap.remove(projectile);
            } else {
                projectile.drawObjectOnCanvas(canvas);
            }
        }

        for (BaseTower tower : mTowerMap.values()){
            tower.drawObjectOnCanvas(canvas, CELL_SIDE*2);
        }
    }

    private void drawBackground(Canvas canvas) {
        canvas.translate(mCamera.getX(), mCamera.getY());
        canvas.scale(mCamera.getZoom(), mCamera.getZoom());

        canvas.drawColor(Color.BLACK);

        mField.drawBackGround(canvas);
    }
    //--------------------------------------------------------//

    //--------------------------------------------------------//
    //        SINGLE  &  MULTIPLE    TOUCH    LISTENER        //

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        mScaleDetector.onTouchEvent(event);

        final int action = event.getAction();
        final int deltaX, deltaY;

        switch (action & MotionEvent.ACTION_MASK) {

            ///////////////////////////////////////

            case MotionEvent.ACTION_DOWN: {
                touchMode = JUST_TOUCH;

                lastTouchedX = (int) event.getX();
                lastTouchedY = (int) event.getY();
            }
            break;

            ///////////////////////////////////////

            case MotionEvent.ACTION_UP: {
                if (touchMode == JUST_TOUCH){
                    final float x = (event.getX() - mCamera.getX())/mCamera.getZoom();
                    final float y = (event.getY() - mCamera.getY())/mCamera.getZoom();

                    if (!isObjectsClicked((int) x, (int) y))
                    spawnCreepAtPoint((int) x, (int) y);
                }
            }
            break;

            ///////////////////////////////////////
            case MotionEvent.ACTION_MOVE: {
                //touchMode= TOUCH_MOVE_CAMERA;

                deltaX = (int) event.getX() - lastTouchedX;
                deltaY = (int) event.getY() - lastTouchedY;

                lastTouchedX = (int) event.getX();
                lastTouchedY = (int) event.getY();

                mCamera.move(deltaX, deltaY);

                if (deltaX>0 || deltaY>0) touchMode = TOUCH_MOVE_CAMERA;
            }

            ///////////////////////////////////////

            default:
                break;
        }
        return true;
    }

    private boolean isObjectsClicked(final int x, final int y){
        final int bound = 40;
        final List<DrawableObject> objectList = new ArrayList<>();
        objectList.addAll(mTowerMap.values());
        objectList.addAll(mCreepMap.values());

        boolean toReturn = false;

        for (DrawableObject item : objectList){
            final boolean xInRange = (item.posX >= x-bound && item.posX <= x+bound);
            final boolean yInRange = (item.posY >= y-bound && item.posY <= y+bound);
            if (xInRange && yInRange){
                item.onClick();
                item.setClicked(true);
                toReturn = true;
            } else {
                item.setClicked(false);
            }
        }
        return toReturn;
    }

    @Override
    public void addProjectileToHashMap(BaseProjectile projectile) {
        mProjectileMap.put(projectile, projectile);
    }

    //todo delete soon and rework
    //temporary spawn creep at touched position code
    private void spawnCreepAtPoint(int x, int y) {
        BaseCreep creep = new BaseCreep(mEmojiMatrixPic);
        creep.setPosition(x, y);
        creep.setTarget(startPos);
        mCreepMap.put(creep, creep);
    }
    //--------------------------------------------------------//

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            touchMode = TOUCH_ZOOM;

            mCamera.zoom(detector.getScaleFactor());
            return true;
        }
    }

}
