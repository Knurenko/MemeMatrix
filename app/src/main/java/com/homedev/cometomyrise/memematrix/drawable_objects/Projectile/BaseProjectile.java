package com.homedev.cometomyrise.memematrix.drawable_objects.Projectile;

import android.graphics.Bitmap;

import com.homedev.cometomyrise.memematrix.drawable_objects.Primitives.MovingObject;

public class BaseProjectile extends MovingObject {

    public BaseProjectile(Bitmap picture, int posX, int posY) {
        super(picture, posX, posY);
    }

    public BaseProjectile(Bitmap picture) {
        super(picture);

        setSpeed(25);
        setContactRadius(25);
    }
}
