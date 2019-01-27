package com.homedev.cometomyrise.memematrix.Scene;

import android.annotation.SuppressLint;

/**<p>Singleton class for handle camera motions</p>*/
public class CameraUtils {

    private static final float MAX_ZOOM = 5.0f;
    private static  float MIN_ZOOM = 0.2f;
    @SuppressLint("StaticFieldLeak")
    private static CameraUtils instance;
    //variables to save translocation coordinates
    private int mX;
    private int mY;
    private int screenBorderX, screenBorderY;
    private int maxCameraMovingDistanceX, maxCameraMovingDistanceY;
    //variable to save zoom level
    private float mZoom;

    // blank CameraUtils instance;
    private CameraUtils() {
        //no shift
        mX = 0;
        mY = 0;
        //no zoom
        mZoom = 1.0f;

        screenBorderX = 0;
        screenBorderY = 0;
    }

    /**
     *  <p>Method to get instance of current camera settings.</p>
     *  <strong>
     *      <p>NOTICE: if you're calling this method first time</p>
     *      <p>don't forget about {@link #setBorders(int, int)} method to calculate minimal zoom value correctly</p>
     *  </strong>
     *   */
    public static CameraUtils init() {
        if (instance == null) {
            instance = new CameraUtils();
        }
        return instance;
    }

    /**
     * increase zoom by DELTA_ZOOM times
     * @param deltaZoom float number to increase or decrease current zoom level
     */
    public void zoom(float deltaZoom){
        mZoom *= deltaZoom;
        if (mZoom < MIN_ZOOM){
            mZoom = MIN_ZOOM;
        } else if (mZoom > MAX_ZOOM){
            mZoom = MAX_ZOOM;
        }

        if (mX < maxCameraMovingDistanceX){
            mX = maxCameraMovingDistanceX;
        }
        if (mY < maxCameraMovingDistanceY) {
            mY = maxCameraMovingDistanceY;
        }
    }

    /**
     * Method to move 2d camera vision by delta_X & delta_Y.
     * @param dx Integer number that the camera is moved on along the horizontal axis
     * @param dy Integer number that the camera is moved on along the vertical axis
     */
    public void move(int dx, int dy) {
        final int oldX = mX, oldY = mY;

        maxCameraMovingDistanceX = (int) (screenBorderX  - (GameField.SIZE_HORIZONTAL*mZoom));
        maxCameraMovingDistanceY = (int) (screenBorderY  - (GameField.SIZE_VERTICAL*mZoom));

        mX += dx;
        mY += dy;

        if (mX>0 || mX<maxCameraMovingDistanceX) {
            mX = oldX;
        }
        if (mY>0 || mY<maxCameraMovingDistanceY) {
            mY = oldY;
        }
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    public float getZoom() {
        return mZoom;
    }
    public void setBorders(int borderX, int borderY) {
        screenBorderX = borderX; screenBorderY = borderY;

        final float horizontalDifference = (float) borderX / GameField.SIZE_HORIZONTAL;
        final float verticalDifference = (float) borderY / GameField.SIZE_VERTICAL;

        //MIN_ZOOM = bigger diff; (to avoid empty space on display)
        MIN_ZOOM = horizontalDifference > verticalDifference ? horizontalDifference : verticalDifference ;
    }
}