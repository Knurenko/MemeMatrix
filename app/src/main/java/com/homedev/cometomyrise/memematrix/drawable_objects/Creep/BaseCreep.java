package com.homedev.cometomyrise.memematrix.drawable_objects.Creep;

import android.graphics.Bitmap;

import com.homedev.cometomyrise.memematrix.drawable_objects.Primitives.MovingObject;

public class BaseCreep extends MovingObject {

    public BaseCreep(Bitmap pic){
        super(pic);
        //test
        setContactRadius(40);
        setSpeed(10);
    }
}
