package edu.msu.hagopi10.project1;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;


/**
 * Custom view class for our Puzzle.
 */
public class NameView extends View {

    public NameView(Context context) {
        super(context);
        init(null, 0);
    }

    public NameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public NameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }



}
