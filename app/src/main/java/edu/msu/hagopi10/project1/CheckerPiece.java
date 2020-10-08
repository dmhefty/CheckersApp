package edu.msu.hagopi10.project1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import java.util.Random;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

public class CheckerPiece {
    /**
     * THe image for the actual piece.
     */
    private Bitmap piece;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    /**
     * The puzzle piece ID
     */
    private int id;

    /**
     * x location.
     * We use relative x locations in the range 0-1 for the center
     * of the puzzle piece.
     */
    private float x = 0;

    /**
     * y location
     */
    private float y = 0;

    /**
     * x location when the puzzle is solved
     */
    private float finalX;

    /**
     * y location when the puzzle is solved
     */
    private float finalY;

    /**
     * y location when the puzzle is solved
     */
    private Paint textPaint;

    /**
     * We consider a piece to be in the right location if within
     * this distance.
     */
    final static float SNAP_DISTANCE = 0.05f;

    public CheckerPiece(Context context, int id, float finalX, float finalY) {
        this.finalX = finalX;
        this.finalY = finalY;
        this.id = id;

        piece = BitmapFactory.decodeResource(context.getResources(), id);
    }

}
