package edu.msu.hagopi10.project1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;


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

    public int playerNum = 0;

    public String tokenFCM = "";

    public ProgressDialog loadingBox;

    String player1Name = "Player1";
    String player2Name = "Player2";
    public Boolean resetOpen = false;
    public Boolean hasReset = false;

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

    public void SetupGame(){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference boardStateRef = rootRef.child("boardState").child("activePlayers");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CheckersView view1 = findViewById(R.id.view);
                //player1Name = (String) dataSnapshot.child("Player1").child("name").getValue();
                //player2Name = (String) dataSnapshot.child("Player2").child("name").getValue();

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String player = ds.getKey();
                    String name = (String) ds.child("name").getValue();
                    if(player.equals("Player1") && name.equals("None")){
                        boardStateRef.child("Player1").child("name").setValue(MainActivity.nameS1);
                        boardStateRef.child("Player1").child("token").setValue(tokenFCM);
                        view1.board.otherPlayersMove = false;
                        view1.board.hasOtherPlayerConnected = false;
                        view1.board.playerNumber = 1;

                        loadingBox = new ProgressDialog(getContext());
                        loadingBox.setTitle("Waiting");
                        loadingBox.setMessage("Waiting for player2 to join...");
                        loadingBox.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        loadingBox.show();

                        waitForPlayer2();

                        break;
                    }
                    else if(player.equals("Player2") && name.equals("None")){
                        boardStateRef.child("Player2").child("name").setValue(MainActivity.nameS1);
                        boardStateRef.child("Player2").child("token").setValue(tokenFCM);
                        view1.board.otherPlayersMove = true;
                        view1.board.hasOtherPlayerConnected = true;
                        view1.board.playerNumber = 2;
                        // TODO send message to other player to say we have connected; title: checkers ; message: the other player is connected
                        boardStateRef.getParent().child("turn").setValue(1);
                        board.waitForOtherPlayer(view1);

                        break;
                    }
                    else if(player.equals("Player2")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        resetOpen = true;
                        builder.setMessage(R.string.resetDBMessage);
                        // Add the buttons
                        builder.setPositiveButton(R.string.resetDBokay, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ResetDB();
                                hasReset = true;
                            }
                        });

                        // Create the AlertDialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        boardStateRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public void waitForPlayer2() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference boardStateRef = rootRef.child("boardState").child("activePlayers");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("Player2").child("name").getValue();

                if(!name.equals("None")){
                    board.hasOtherPlayerConnected = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        boardStateRef.addListenerForSingleValueEvent(valueEventListener);

        if(!board.hasOtherPlayerConnected){
            refresh( 1000 );
        }
        else{
            board.otherPlayersMove = false;
            if(loadingBox.isShowing())
            {
                loadingBox.dismiss();
            }
        }
    }

    private void refresh(int milliseconds) {

        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                waitForPlayer2();
            }
        };

        handler.postDelayed(runnable, milliseconds);
    }

    public CheckerBoard getBoard() {
        return board;
    }

    public void ResetDB(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference boardStateRef = rootRef.child("boardState");
        Hashtable<String, String> nameDict = new Hashtable<String, String>();
        nameDict.put("name", "None");
        nameDict.put("token", "");

        boardStateRef.child("activePlayers").child("Player1").setValue(nameDict);
        boardStateRef.child("activePlayers").child("Player2").setValue(nameDict);
        ArrayList<String> moves = new ArrayList<>();
        moves.add("-1");
        moves.add("-1");
        boardStateRef.child("lastMove").setValue(moves);
        boardStateRef.child("turn").setValue(0);
    }
}
