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

    private float xCoordinate;

    private float yCoordinate;

    /**
     * Most recent relative X touch when dragging
     */
    private float lastRelX;

    //gameSize = int(minDim) * SCALE_IN_VIEW;

    //blockSize = gameSize /8
    /**
     * Most recent relative Y touch when dragging
     */
    private float lastRelY;

    /**
     * Collection of checkerboard pieces
     */
    public ArrayList<CheckerPiece> pieces = new ArrayList< >();

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

    /**
     * Paint color we will use to draw dark checker squares
     */
    private int darkSquare = 0xff779455;

    /**
     * Paint color we will use to draw light checker squares
     */
    private int lightSquare = 0xffebebd0;


    public CheckerBoard(Context context) {

        // Create paint for filling the area the checkerboard will
        // be solved in.
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(0xffcccccc);

        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setColor(0xccccffcc);


        // Load the checkerboard pieces

        // Load green pieces
        for(int i = 0; i<12; i++){
            pieces.add(new CheckerPiece(context, R.drawable.spartan_green, i));
        }

        // Load white pieces
        for(int i = 0; i<12; i++){
            pieces.add(new CheckerPiece(context, R.drawable.spartan_white, 31-i));
        }

    }

    public void draw(Canvas canvas){
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        // Determine the minimum of the two dimensions
        int minDim = wid < hit ? wid : hit;

        checkerSize = (int)(minDim * SCALE_IN_VIEW);

        // Compute the margins so we center the puzzle
        marginX = (wid - checkerSize) / 2;
        marginY = (hit - checkerSize) / 2;
        //
        // Draw the outline of the puzzle
        //

        scaleFactor = (float)checkerSize;

        canvas.drawRect(marginX, marginY,
                marginX + checkerSize, marginY + checkerSize, fillPaint);

        for(int i = 0; i < 8; i++){
            drawColumn(canvas, i);
        }

        for(CheckerPiece piece : pieces){
            piece.draw(canvas, marginX, marginY, checkerSize, scaleFactor);
        }

    }

/*
        if (dragging != null) {
            dragging.draw_dragging(canvas, blockSize, xCoordinate, yCoordinate);
        }
        */


    /*
        public draw_dragging(canvas, blockSize, xCoordinate, yCoordinate) {
            canvas.save();

            canvas.translate(i, i)

                    canvas.scale()
        }
    */
    public void drawColumn(Canvas canvas, int columnIndex){
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        // Determine the minimum of the two dimensions
        int minDim = wid < hit ? wid : hit;

        int puzzleSize = (int)(minDim * SCALE_IN_VIEW);

        // Compute the margins so we center the puzzle
        int marginX = (wid - puzzleSize) / 2;
        int marginY = (hit - puzzleSize) / 2;

        for(int i = 0; i< 8; i++){
             int squareCenterXOffset = marginX + columnIndex * puzzleSize/8;
             int squareCenterYOffset = marginY + i * puzzleSize/8;

             if ((i + columnIndex) % 2 == 0){
                 fillPaint.setColor(darkSquare);
             }
             else{
                 fillPaint.setColor(lightSquare);
             }

            canvas.drawRect(squareCenterXOffset, squareCenterYOffset,
                    squareCenterXOffset + puzzleSize/8, squareCenterYOffset + puzzleSize/8, fillPaint);
        }
    }

    private boolean onTouched(float x, float y) {

        // Check each piece to see if it has been hit
        // We do this in reverse order so we find the pieces in front
        for(int p=pieces.size()-1; p>=0;  p--) {
            if(pieces.get(p).hit(x, y, checkerSize, scaleFactor)) {
                // We hit a piece!

                //dragging = pieces.get(pieces.size()-1);
                dragging = pieces.get(p);
                lastRelX = x;
                lastRelY = y;
                //PuzzlePiece t = pieces.get(pieces.size()-1);
                //pieces.set(p, t);
                //pieces.set(pieces.size()-1, dragging);
                //pieces.add(pieces.size(), dragging);
                //pieces.remove(p);
                return true;
            }
        }

        return false;
    }


    /*
     * Handle a touch event from the view.
     * @param view The view that is the source of the touch
     * @param event The motion event describing the touch
     * @return true if the touch is handled.
    *
    */

    public boolean onTouchEvent(View view, MotionEvent event) {

        // Convert an x,y location to a relative location in the
        // puzzle.
        //
        xCoordinate = event.getX();
        yCoordinate = event.getY();

        float relX = (event.getX() - marginX) / checkerSize;
        float relY = (event.getY() - marginY) / checkerSize;

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                return onTouched(relX, relY);

            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_CANCEL:
                return onReleased(view, relX, relY);

            case MotionEvent.ACTION_MOVE:
                // If we are dragging, move the piece and force a redraw
                if(dragging != null) {
                    //check if valid
                    //dragging.dx = 500;
                    dragging.move(relX - lastRelX, relY - lastRelY);
                    //dragging.move(lastRelX, lastRelY);
                    lastRelX = relX;
                    lastRelY = relY;

                    dragging.move(5,5);
                    view.invalidate();
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * Handle a release of a touch message.
     * @param x x location for the touch release, relative to the puzzle - 0 to 1 over the puzzle
     * @param y y location for the touch release, relative to the puzzle - 0 to 1 over the puzzle
     * @return true if the touch is handled
     */
    private boolean onReleased(View view, float x, float y) {

        dragging.move(lastRelX, lastRelY);
        if(dragging != null) {
            dragging = null;
            return true;
        }

        return false;
    }


}


// test!!!!!