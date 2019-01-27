package com.homedev.cometomyrise.memematrix.Scene;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;


public class GameField {

    public static final int SIZE_HORIZONTAL = 2000;
    public static final int SIZE_VERTICAL = 2000;
    private static final int CELL_SIDE = 50;

    public GameField() {}

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    public void drawBackGround(Canvas canvas){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.YELLOW);

        final Path backgroundGrid = new Path();
        backgroundGrid.reset();

        //cells
        //method that draw lines on path, and then path on canvas
        for (int x = 0; x < GameField.SIZE_HORIZONTAL; x += CELL_SIDE) {
            //draw vertical line across full screen height
            backgroundGrid.moveTo(x, 0);
            backgroundGrid.lineTo(x, GameField.SIZE_VERTICAL);
        }
        for (int y = 0; y < GameField.SIZE_VERTICAL; y += CELL_SIDE) {
            backgroundGrid.moveTo(0, y);
            backgroundGrid.lineTo(GameField.SIZE_HORIZONTAL, y);
        }
        canvas.drawPath(backgroundGrid, paint);

        //temporary creep going line creation
        final Paint creepPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        creepPathPaint.setStyle(Paint.Style.STROKE);
        creepPathPaint.setStrokeWidth(5);
        creepPathPaint.setColor(Color.CYAN);

        final Path temporaryPath = new Path();
        temporaryPath.reset();
        temporaryPath.moveTo(200, SIZE_VERTICAL/2);
        temporaryPath.lineTo(SIZE_HORIZONTAL-200, SIZE_VERTICAL/2);
        canvas.drawPath(temporaryPath, creepPathPaint);
    }
}
