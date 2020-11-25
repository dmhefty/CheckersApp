package edu.msu.hagopi10.project1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;


/**
 * Custom view class for our Checkers.
 */
public class CheckersView extends View {


    public CheckerBoard board;

    /**
     * Paint object we will use to draw a line
     */
    private Paint linePaint;

    /**
     * First touch status
     */
    private final Touch touch1 = new Touch();

    private final Parameters params = new Parameters();

    /**
     * The image bitmap. None initially.
     */
    private final Bitmap imageBitmap = null;
    /**
     * Image drawing scale
     */
    private final float imageScale = 1;

    /**
     * Image left margin in pixels
     */
    private final float marginLeft = 0;

    /**
     * Image top margin in pixels
     */
    private final float marginTop = 0;

    private static class Parameters implements Serializable {
        /**
         * Path to the image file if one exists
         */
        public String imagePath = null;
        /**
         * The current checker type
         */
        public int checker;
        /**
         * X location of hat relative to the image
         */
        public float posX = 0;
        /**
         * Y location of hat relative to the image
         */
        public float posY = 0;

    }

    /**
     * Local class to handle the touch status for one touch.
     * We will have one object of this type for each of the
     * two possible touches.
     */
    private static class Touch {
        /**
         * Touch id
         */
        public int id = -1;

        /**
         * Current x location
         */
        public float x = 0;

        /**
         * Current y location
         */
        public float y = 0;

        /**
         * Previous x location
         */
        public float lastX = 0;

        /**
         * Previous y location
         */
        public float lastY = 0;
        /**
         * Change in x value from previous
         */
        public float dX = 0;

        /**
         * Change in y value from previous
         */
        public float dY = 0;
        /**
         * Copy the current values to the previous values
         */
        public void copyToLast() {
            lastX = x;
            lastY = y;
        }
        /**
         * Compute the values of dX and dY
         */
        public void computeDeltas() {
            dX = x - lastX;
            dY = y - lastY;
        }
    }

    public CheckersView(Context context) {
        super(context);
        init(null, 0);
    }

    public CheckersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CheckersView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        board = new CheckerBoard(getContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return board.onTouchEvent(this, event);
        //return true
    }

    /**
     * Handle movement of the touches
     */
    private void move() {
        // If no touch1, we have nothing to do
        // This should not happen, but it never hurts
        // to check.
        if(touch1.id < 0) {
            return;
        }

        // At least one touch
        // We are moving
        touch1.computeDeltas();


        params.posX += touch1.dX;
        params.posY += touch1.dY;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        board.draw(canvas);
    }

    public boolean hitTest(float x, float y) {
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * Get the positions for the two touches and put them
     * into the appropriate touch objects.
     * @param event the motion event
     */
    private void getPositions(MotionEvent event) {
        for(int i=0;  i<event.getPointerCount();  i++) {

            // Get the pointer id
            int id = event.getPointerId(i);

            // Convert to image coordinates
            float x = (event.getX(i) - marginLeft) / imageScale;
            float y = (event.getY(i) - marginTop) / imageScale;

            if(id == touch1.id) {
                touch1.copyToLast();
                touch1.x = x;
                touch1.y = y;
            }
        }

        invalidate();
    }

    public CheckerBoard getBoard() {
        return board;
    }

}
