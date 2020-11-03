package edu.msu.hagopi10.project1;

import android.content.Context;
import android.database.CrossProcessCursorWrapper;
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
     * The piece ID
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
    final static float SNAP_DISTANCE = 0.5f;

    /**
     * What square the piece is in, starts counting from the top, leftmost, square, counts right
     * until the end of the row then loops to the leftmost square on the next row and continues
     * counting
     */
    public int locationIndex;

    /**
     * True when the piece has been picked up to move
     */
    public boolean isGrabbed;


    public CheckerPiece(Context context, int id, int boardIndex) {
        this.locationIndex = boardIndex;
        this.id = id;

        piece = BitmapFactory.decodeResource(context.getResources(), id);

    }

    public void draw(Canvas canvas, int marginX, int marginY,
                     int puzzleSize, float scaleFactor){

        if (!isGrabbed) setLocationFromIndex(locationIndex, marginX, marginY, puzzleSize);

        canvas.save();

        // Convert x,y to pixels and add the margin, then draw
        canvas.translate(x * (puzzleSize + 2*marginX), y * (puzzleSize + 2*marginY));

        scaleFactor = (puzzleSize/8.0f)/(float)Math.min(piece.getWidth(), piece.getWidth());

        // Scale it to the right size
        canvas.scale(scaleFactor, scaleFactor);

        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-piece.getWidth() / 2f, -piece.getHeight() / 2f);

        // Draw the bitmap
        canvas.drawBitmap(piece, 0, 0, null);
        canvas.restore();

    }

    /**
     * Test to see if we have touched a puzzle piece
     * @param testX X location as a normalized coordinate (0 to 1)
     * @param testY Y location as a normalized coordinate (0 to 1)
     * @param puzzleSize the size of the puzzle in pixels
     * @param scaleFactor the amount to scale a piece by
     * @return true if we hit the piece
     */
    public boolean hit(float testX, float testY,
                       int puzzleSize, float scaleFactor, int marginX, int marginY) {

        // Make relative to the location and size to the piece size

        int pX = (int)((testX - x) * (puzzleSize + marginX*2) / scaleFactor) +
                piece.getWidth() / 2;
        int pY = (int)((testY - y) * (puzzleSize + marginY*2) / scaleFactor) +
                piece.getHeight() / 2;

        if(pX < 0 || pX >= piece.getWidth() ||
                pY < 0 || pY >= piece.getHeight()) {
            return false;
        }

        // We are within the rectangle of the piece.
        // Are we touching actual picture?
        return (piece.getPixel(pX, pY) & 0xff000000) != 0;

    }

    /**
     * Move the puzzle piece by dx, dy
     * @param dx x amount to move
     * @param dy y amount to move
     */
    public void move(float dx, float dy) {
        x += dx;
        y += dy;
    }

    public void setLocationFromIndex(int index, int marginX, int marginY, int puzzleSize){
        int xIndex; int yIndex;
        xIndex = index%4;
        yIndex = index/4;

        // Convert x,y to pixels and add the margin, then draw
        if( yIndex%2 == 0 ){
            x = (float) (marginX + xIndex * puzzleSize/4 + puzzleSize/16) / (float) (puzzleSize + 2*marginX);
            y = (float) (marginY + yIndex * puzzleSize/8 + puzzleSize/16) / (float) (puzzleSize + 2*marginY);
        }
        else{
            x = (float) (marginX + xIndex * puzzleSize/4 + puzzleSize * 3/16) / (float) (puzzleSize + 2*marginX);
            y = (float) (marginY + yIndex * puzzleSize/8 + puzzleSize/16) / (float) (puzzleSize + 2*marginY);
        }

    }

    public void calculateIndex(int marginX, int marginY, int puzzleSize) {


        //gets index

        int xIndex; int yIndex;
        int index1 = 18;
        xIndex = index1%4;
        yIndex = index1/4;

        // Convert x,y to pixels and add the margin, then draw
        if( yIndex%2 == 0 ){
            finalX = (float) (marginX + xIndex * puzzleSize/4 + puzzleSize/16) / (float) (puzzleSize + 2*marginX);
            finalY  = (float) (marginY + yIndex * puzzleSize/8 + puzzleSize/16) / (float) (puzzleSize + 2*marginY);

            //xIndex  = marginX + finalX * (puzzleSize + 2*marginX) - puzzleSize/16) / (puzzleSize/4)


            //int row = finalX;
            //int column =
        }
        else{
            finalX = (float) (marginX + xIndex * puzzleSize/4 + puzzleSize * 3/16) / (float) (puzzleSize + 2*marginX);
            finalY = (float) (marginY + yIndex * puzzleSize/8 + puzzleSize/16) / (float) (puzzleSize + 2*marginY);
        }


        //get our index?????
    }


    /**
     * If we are within SNAP_DISTANCE of the correct
     * answer, snap to the correct answer exactly.
     * @return
     */
    public boolean maybeSnap(int marginX, int marginY, int puzzleSize) {


        calculateIndex(marginX, marginY, puzzleSize);

        if(Math.abs(x - finalX) < SNAP_DISTANCE &&
                Math.abs(y - finalY) < SNAP_DISTANCE) {

            x = finalX;
            y = finalY;
            return true;
        }

        return false;
    }

    /**
     * Determine if this piece is snapped in place
     * @return true if snapped into place
     */
    public boolean isSnapped() {
        if (x == finalX && y == finalY) {
            return true;
        }
        return false;
    }
}
