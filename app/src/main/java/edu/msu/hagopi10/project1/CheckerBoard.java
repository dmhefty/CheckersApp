package edu.msu.hagopi10.project1;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.app.AlertDialog;
import java.util.Random;
import android.os.Bundle;
import android.content.DialogInterface;


import java.util.ArrayList;

/**
 * This class represents our checkerboard.
 */
public class CheckerBoard {

    /**
     * Most recent relative X touch when dragging
     */
    private float lastRelX;

    /**
     * Most recent relative Y touch when dragging
     */
    private float lastRelY;

    private Paint textPaint;
    /**
     * Paint for filling the area the checkerboard is in
     */
    private Paint fillPaint;

    /**
     * Paint for outlining the area the checkerboard is in
     */
    private Paint outlinePaint;
    

    /**
     * The size of the checkerboard in pixels
     */
    private int checkerSize;

    /**
     * How much we scale the checkerboard pieces
     */
    private float scaleFactor;

    /**
     * Left margin in pixels
     */
    private int marginX;

    /**
     * Top margin in pixels
     */
    private int marginY;

    /**
     * Collection of checkerboard pieces
     */
    public ArrayList<CheckerPiece> pieces = new ArrayList<CheckerPiece>();

    /**
     * This variable is set to a piece we are dragging. If
     * we are not dragging, the variable is null.
     */
    private CheckerPiece dragging = null;

    /**
     * Percentage of the display width or height that
     * is occupied by the checkerboard.
     */
    final static float SCALE_IN_VIEW = 0.9f;

    /**
     * Random number generator
     */
    private static Random random = new Random();

    /**
     * The name of the bundle keys to save the checkerboard
     */
    private final static String LOCATIONS = "checkerboard.locations";
    private final static String IDS = "checkerboard.ids";

    public CheckerBoard(Context context) {

        // Create paint for filling the area the checkerboard will
        // be solved in.
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(0xffcccccc);

        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setColor(0xccccffcc);


        // Load the checkerboard pieces
        pieces.add(new CheckerPiece(context, R.drawable.spartan_green, 0.259f, 0.238f));
    }
}