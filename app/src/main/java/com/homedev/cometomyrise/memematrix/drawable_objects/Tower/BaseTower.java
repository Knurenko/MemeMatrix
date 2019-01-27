package com.homedev.cometomyrise.memematrix.drawable_objects.Tower;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.homedev.cometomyrise.memematrix.DrawView;
import com.homedev.cometomyrise.memematrix.drawable_objects.Creep.BaseCreep;
import com.homedev.cometomyrise.memematrix.drawable_objects.Primitives.DrawableObject;
import com.homedev.cometomyrise.memematrix.drawable_objects.Projectile.BaseProjectile;

import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class BaseTower extends DrawableObject implements Runnable {

    private static final int TEMP_ATTACK_DELAY = 200;
    //          CONSTANTS
    //-------------------------------------------------------------//
    private static final int TOWER_RANGE = 300;
    private Callbacks mContainer;

    //          VARIABLES
    //-------------------------------------------------------------//
    private boolean isReady, creepInRange;
    private ConcurrentHashMap<BaseCreep, BaseCreep> targets;
    private ConcurrentHashMap<BaseCreep, BaseCreep> targetsInsideRange;
    private Thread sensorThread;
    //draw paints
    private Paint greenPaint, redPaint;

    //          CONSTRUCTOR
    //-------------------------------------------------------------//
    public BaseTower(Bitmap picture, int positionX, int positionY) {
        super(picture, positionX, positionY);
        init();
    }

    public BaseTower(Bitmap picture) {
        super(picture);
        init();
    }

    @Override
    public void drawObjectOnCanvas(Canvas canvas) {
        super.drawObjectOnCanvas(canvas);
        additionDrawing(canvas);
    }

    //          METHODS
    //-------------------------------------------------------------//

    @Override
    public void drawObjectOnCanvas(Canvas canvas, int size) {
        super.drawObjectOnCanvas(canvas, size);
        additionDrawing(canvas);
    }

    private void additionDrawing(Canvas canvas){
        if (isClicked) {
            //if creep inside range -> RED, else GREEN circle
            Paint paint = creepInRange? redPaint : greenPaint ;
            //todo convert point to rect
            canvas.drawPoint((float)posX, (float)posY, paint);
            canvas.drawCircle(posX,posY,TOWER_RANGE, paint);
        }
    }

    private void init(){
        greenPaint = greenPaint();
        redPaint = redPaint();

        isClicked = false;
        isReady = false;
        creepInRange = false;

        sensorThread = new Thread(this);

        targetsInsideRange = new ConcurrentHashMap<>();
        targets = new ConcurrentHashMap<>();
    }

    private Paint redPaint(){
        Paint redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(5);
        redPaint.setStyle(Paint.Style.STROKE);
        return redPaint;
    }

    private Paint greenPaint(){
        Paint greenPaint = new Paint();
        greenPaint.setColor(Color.GREEN);
        greenPaint.setStrokeWidth(3);
        greenPaint.setStyle(Paint.Style.STROKE);
        return greenPaint;
    }

    public void setContainer(DrawView container){
        mContainer = container;
    }

    @Override
    public void run() {
        BaseCreep currentTarget = null;


        while (isReady) {
            if (!targets.isEmpty()) {
                for (BaseCreep creep : targets.values()) {
                    //calc circle formula
                    int distance = rangeToTower(creep);
                    if (distance <= sqr(TOWER_RANGE)) {


                        //-------------------------TRUE-----------------------------//
                        creepInRange = true;
                        //add creep to list
                        targetsInsideRange.put(creep,creep);

                        //set current target & attack
                        //if current_target != null

                        if (currentTarget == null) {
                            //set target, next iteration will attack
                            currentTarget = creep;
                        }
                        //else if current target is OUT_OF_RANGE
                        else if (!targetsInsideRange.contains(currentTarget)) {
                            //find nearest from IN_RANGE_LIST & then attack
                            currentTarget = getNearestCreep(targetsInsideRange);
                        } else {
                            //target is not null so it can be fired by projectile

                            //attack(new projectile)
                            BaseProjectile projectile = createProjectile();
                            projectile.setPosition(posX, posY);
                            projectile.setTarget(creep);

                            mContainer.addProjectileToHashMap(projectile);

                            //attack performed, time to DELAY

                            try {
                                Thread.sleep(TEMP_ATTACK_DELAY);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                        //----------------------END TRUE----------------------------//
                    } else {

                        //------------------------FALSE-----------------------------//
                        if (targetsInsideRange.values().isEmpty()){
                            creepInRange = false;
                            currentTarget = null;
                        }

                        //try delete if consist
                        if (targetsInsideRange.values().contains(creep)){
                            try {
                                currentTarget = null;
                                targetsInsideRange.remove(creep, creep);
                            } catch (ConcurrentModificationException e){
                                e.printStackTrace();
                            }
                        }
                        //--------------------END FALSE-----------------------------//
                    }
                }
                //end for
            } else {
                creepInRange = false;
                targetsInsideRange.clear();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //end while
    }

    private BaseProjectile createProjectile(){
        int[] colors = new int[100];
        for (int i=0 ; i<100; i++){
            colors[i] = Color.rgb(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));
        }
        Bitmap projPic = Bitmap.createBitmap(colors,10,10, Bitmap.Config.ARGB_4444);
        return new BaseProjectile(projPic);
    }

    private int sqr(int val) {return val*val;}

    private int rangeToTower(BaseCreep creep) {return sqr(posX - creep.posX) + sqr(posY - creep.posY);}

    private BaseCreep getNearestCreep(ConcurrentHashMap<BaseCreep, BaseCreep> creeps) {
        int minDist = TOWER_RANGE;
        BaseCreep toReturn = null;
        for (BaseCreep target : creeps.values()) {
            if (rangeToTower(target) < minDist) {
                minDist = rangeToTower(target);
                toReturn = target;
            }
        }
        return toReturn;
    }

    //method to start thread and set tower sensor_enabled
    public void activate(ConcurrentHashMap<BaseCreep, BaseCreep> creeps) {
        targets = creeps;
        isReady = true;
        sensorThread.start();

    }

    public void deactivate() {
        isReady = false;
        boolean retry = true;
        while (retry) {
            try {
                sensorThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // keep trying again'n'again
            }
        }
        targetsInsideRange.clear();
        targets.clear();
    }

    //          INTERFACE
    //-------------------------------------------------------------//
    public interface Callbacks {
        void addProjectileToHashMap(BaseProjectile projectile);
    }

}
