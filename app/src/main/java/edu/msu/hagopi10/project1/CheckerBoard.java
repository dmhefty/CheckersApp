package edu.msu.hagopi10.project1;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * This class represents our checkerboard.
 */
public class CheckerBoard {

    private Paint textPaint;
    /**
     * Paint for filling the area the checkerboard is in
     */
    private final Paint fillPaint;

    /**
     * Paint for outlining the area the checkerboard is in
     */
    private final Paint outlinePaint;

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
     * Most recent relative X touch when dragging
     */
    private float lastRelX;

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
    private static final Random random = new Random();

    /**
     * determines if a player has made their move
     */
    public boolean playerHasMoved = false;
    public int playerNumber = 0;

    public int turnStart = -1;
    public int turnEnd = -1;

    /**
     * The name of the bundle keys to save the checkerboard
     */
    private final static String LOCATIONS = "checkerboard.locations";
    private final static String IDS = "checkerboard.ids";

    /**
     * Paint color we will use to draw dark checker squares
     */
    private final int darkSquare = 0xff779455;

    /**
     * Paint color we will use to draw light checker squares
     */
    private final int lightSquare = 0xffebebd0;

    /**
     * Current player to make a move
     * 1 = player 1
     * 2 = player 2
     * Starts game with player 1
     */
    private int activePlayer = 1;

    public boolean otherPlayersMove = true;
    public boolean hasOtherPlayerConnected = false;
    public int otherPlayerWaitToastCount = 0;

    public long lastDBCheck = 0;

    public ArrayList<Integer> otherPlayerLastMove = new ArrayList<>();

    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public int getActivePlayer() {
        return activePlayer;
    }

    public CheckerBoard(Context context) {

        // Create paint for filling the area the checkerboard will
        // be solved in.
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(0xccccffcc);

        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setColor(0xffcccccc);

        // Load the checkerboard pieces

        //Load green pieces
        for(int i = 0; i<12; i++){
            pieces.add(new CheckerPiece(context, R.drawable.spartan_green, R.drawable.king_green, i, 1));
        }

         //Load white pieces
        for(int i = 0; i<12; i++){
            pieces.add(new CheckerPiece(context, R.drawable.spartan_white, R.drawable.king_white, 31-i, 2));
        }

        //sendTurnToDB(11, 16);

    }

    public void draw(Canvas canvas){
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        // Determine the minimum of the two dimensions
        int minDim = Math.min(hit, wid);

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

        //
        // draw the actual pieces
        //
        for(CheckerPiece piece : pieces){
            piece.draw(canvas, marginX, marginY, checkerSize, scaleFactor);
        }

    }

    public void drawColumn(Canvas canvas, int columnIndex){
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        // Determine the minimum of the two dimensions
        int minDim = Math.min(hit, wid);

        int puzzleSize = (int)(minDim * SCALE_IN_VIEW);

        // Compute the margins so we center the puzzle
        int marginX = (wid - puzzleSize) / 2;
        int marginY = (hit - puzzleSize) / 2;

        for(int i = 0; i< 8; i++){
             int squareCenterXOffset = marginX + columnIndex * puzzleSize/8;
             int squareCenterYOffset = marginY + i * puzzleSize/8;

             if ((i + columnIndex) % 2 == 1){
                 fillPaint.setColor(darkSquare);
             }
             else{
                 fillPaint.setColor(lightSquare);
             }

            canvas.drawRect(squareCenterXOffset, squareCenterYOffset,
                    squareCenterXOffset + puzzleSize/8f, squareCenterYOffset + puzzleSize/8f, fillPaint);
        }
    }

    private boolean onTouched(float x, float y) {

        // Check each piece to see if it has been hit
        // We do this in reverse order so we find the pieces in front
        for(int p=pieces.size()-1; p>=0;  p--) {
            if(pieces.get(p).hit(x, y, checkerSize, SCALE_IN_VIEW, marginX, marginY)
                && activePlayer == pieces.get(p).access) {
                // We hit a piece!

                //dragging = pieces.get(pieces.size()-1);
                dragging = pieces.get(p);
                dragging.isGrabbed = true;
                lastRelX = x;
                lastRelY = y;
                //PuzzlePiece t = pieces.get(pieces.size()-1);
                //pieces.set(p, t);
                //pieces.set(pieces.size()-1, dragging);
                pieces.add(pieces.size(), dragging);
                pieces.remove(p);
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
        if(playerHasMoved) return false;
        if(otherPlayersMove){
            Toast toast=Toast.makeText(view.getContext(),
                    "Wait for other player to move",
                    Toast.LENGTH_SHORT);
            toast.setMargin(50,50);
            toast.show();
            return false;
        }

        float relX = (event.getX()) / (checkerSize + marginX*2);
        float relY = (event.getY()) / (checkerSize + marginY*2);

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
        if(playerHasMoved) {
            // throw toast if no jump is possible
            Toast toast=Toast.makeText(view.getContext(),
                    "Player has already moved. Press done to start next player's turn.",
                    Toast.LENGTH_SHORT);
            toast.setMargin(50,50);
            toast.show();
            return false;
        }

        if(dragging != null) {
            for (int p=pieces.size()-1; p>=0;  p--){
                CheckerPiece test1 = pieces.get(p);
                int proposedIndex = dragging.calculateIndex(marginX, marginY, checkerSize);
                int currentIndex = test1.calculateIndex(marginX, marginY, checkerSize);

                if (dragging.locationIndex == pieces.get(p).locationIndex) {
                    if (dragging.locationIndex == pieces.get(p).locationIndex) {
                        //true
                        if (pieces.get(p) != dragging) {
                            return false;
                        }
                        //pieces.remove(p);
                        //return false;
                    }
                }
            }

            int ogIndex = dragging.locationIndex;
            int potentialIndex = dragging.calculateIndex(marginX,  marginY,  checkerSize);

            turnStart = ogIndex;
            turnEnd = potentialIndex;

            if(dragging.maybeSnap(marginX, marginY, checkerSize)) {
                // We have snapped into place

                // check if there is  a  piece in the way
                boolean locationOccupied = false;
                //int potentialIndex = dragging.calculateIndex(marginX,  marginY,  checkerSize);
                for(CheckerPiece piece : pieces){
                    if(piece.locationIndex == potentialIndex){
                        locationOccupied = true;
                        break;
                    }
                }

                if(!locationOccupied)  {
                    dragging.updateIndex(potentialIndex, marginX, marginY, checkerSize);

                    if(isDone()) {
                        // The puzzle is done

                    }
                    else{
                        playerHasMoved = true;
                    }
                }
                else{
                    // throw toast if not possible
                    Toast toast=Toast.makeText(view.getContext(),
                            "The location you tried to move to is occupied. Please try again.",
                            Toast.LENGTH_SHORT);
                    toast.setMargin(50,50);
                    toast.show();

                }


                view.invalidate();
            }
            else if(!(potentialIndex<0 || potentialIndex>31) ) {
                // check for jump before failure
                //int potentialIndex = dragging.calculateIndex(marginX,  marginY,  checkerSize);
                int potentialJumpee = -1;
                switch(potentialIndex - dragging.locationIndex){

                    case -7:
                        // if not player 1  or is a king, cannot go backwards
                        if(dragging.access == 1 && !dragging.isKing) break;

                        if( (dragging.locationIndex/4)%2 == 0 ){
                            potentialJumpee = -3;
                        }
                        else{
                            potentialJumpee = -4;
                        }
                        break;
                    case -9:
                        // if not player 1  or is a king, cannot go backwards
                        if(dragging.access == 1 && !dragging.isKing) break;

                        // either player 2 or is king

                        if( (dragging.locationIndex/4)%2 == 0 ){
                            potentialJumpee = -4;
                        }
                        else{
                            potentialJumpee = -5;
                        }
                        break;
                    case 7:
                        // if not player 2  or is a king, cannot go forwards
                        if(dragging.access == 2 && !dragging.isKing) break;

                        if( (dragging.locationIndex/4)%2 == 0 ){
                            potentialJumpee = 4;
                        }
                        else{
                            potentialJumpee = 3;
                        }
                        break;
                    case 9:
                        // if not player 1  or is a king, cannot go forwards
                        if(dragging.access == 2 && !dragging.isKing) break;

                        if( (dragging.locationIndex/4)%2 == 0 ){
                            // if player 1, use 4 if player 2 use 5
                            potentialJumpee = 5;
                        }
                        else{
                            // if player 1, use 5 if player 2 use 4
                            potentialJumpee = 4;
                        }
                        break;
                }
                if(!(potentialJumpee == -1)){  // if valid and/or not
                    for(CheckerPiece piece : pieces){
                        if( ( (dragging.locationIndex + potentialJumpee) == piece.locationIndex)
                        && dragging.access != piece.access)
                        {
                            // double check final location is not occupied
                            boolean occupied = false;
                            for(CheckerPiece finalLocationCheck : pieces){
                                if(finalLocationCheck.locationIndex  == potentialIndex){
                                    occupied = true;
                                    break;
                                }
                            }
                            if(occupied){
                                // location is occupied
                                // throw toast if no jump is possible
                                Toast toast=Toast.makeText(view.getContext(),
                                        "The location you tried to jump to is invalid. Please try again.",
                                        Toast.LENGTH_SHORT);
                                toast.setMargin(50,50);
                                toast.show();
                                break;
                            }

                            // kill piece
                            pieces.remove(piece);
                            // move dragging
                            dragging.updateIndex(potentialIndex, marginX, marginY, checkerSize);
                            sendTurnToDB(ogIndex, potentialIndex);
                            playerHasMoved = true;
                            break;
                        }
                        else if( ( (dragging.locationIndex + potentialJumpee) == piece.locationIndex)
                                && dragging.access == piece.access){
                            // Cannot jump your own piece

                            // throw toast if no jump is possible
                            Toast toast=Toast.makeText(view.getContext(),
                                    "Cannot jump your own piece. Please try again.",
                                    Toast.LENGTH_SHORT);
                            toast.setMargin(50,50);
                            toast.show();
                        }
                    }

                }
                else{
                    // impossible move
                    // throw toast if no jump is possible
                    Toast toast=Toast.makeText(view.getContext(),
                            "The move you tried to make is invalid. Please try again.",
                            Toast.LENGTH_SHORT);
                    toast.setMargin(50,50);
                    toast.show();
                }

            }

            // determine if the moved piece needs to be kinged
            int row = dragging.locationIndex/4;
            if(( dragging.access == 1 && row == 7) || (dragging.access == 2 && row == 0)
             && !dragging.isKing){
                dragging.kingify();
            }

            dragging.isGrabbed = false;
            dragging = null;

            view.invalidate();
            return true;
        }

        return false;
    }

    public void executeOtherPlayerTurn(View view, int start, int end){
        CheckerPiece movedPiece = new CheckerPiece(view.getContext(), 0,0,0,0);
        for (CheckerPiece piece : pieces){
            if(piece.locationIndex == start){
                movedPiece = piece;
                break;
            }
        }


        if(movedPiece != null) {

            int ogIndex = movedPiece.locationIndex;
            int potentialIndex = end;



            if (movedPiece.updateIndexForOtherPlayerMove(end, marginX, marginY, checkerSize)) {
                // We have snapped into place

                // check if there is  a  piece in the way
                boolean locationOccupied = false;
                //int potentialIndex = movedPiece.calculateIndex(marginX,  marginY,  checkerSize);
                for (CheckerPiece piece : pieces) {
                    if (piece.locationIndex == potentialIndex) {
                        locationOccupied = true;
                        break;
                    }
                }

                if (!locationOccupied) {
                    movedPiece.updateIndex(potentialIndex, marginX, marginY, checkerSize);

                    if (isDone()) {
                        // The puzzle is done

                    } else {
                        playerHasMoved = true;
                        sendTurnToDB(ogIndex, potentialIndex);
                    }
                } else {
                    // throw toast if not possible
                    Toast toast = Toast.makeText(view.getContext(),
                            "The location you tried to move to is occupied. Please try again.",
                            Toast.LENGTH_SHORT);
                    toast.setMargin(50, 50);
                    toast.show();

                }


                view.invalidate();
            } else if (!(potentialIndex < 0 || potentialIndex > 31)) {
                // check for jump before failure
                //int potentialIndex = movedPiece.calculateIndex(marginX,  marginY,  checkerSize);
                int potentialJumpee = -1;
                switch (potentialIndex - movedPiece.locationIndex) {

                    case -7:
                        // if not player 1  or is a king, cannot go backwards
                        if (movedPiece.access == 1 && !movedPiece.isKing) break;

                        if ((movedPiece.locationIndex / 4) % 2 == 0) {
                            potentialJumpee = -3;
                        } else {
                            potentialJumpee = -4;
                        }
                        break;
                    case -9:
                        // if not player 1  or is a king, cannot go backwards
                        if (movedPiece.access == 1 && !movedPiece.isKing) break;

                        // either player 2 or is king

                        if ((movedPiece.locationIndex / 4) % 2 == 0) {
                            potentialJumpee = -4;
                        } else {
                            potentialJumpee = -5;
                        }
                        break;
                    case 7:
                        // if not player 2  or is a king, cannot go forwards
                        if (movedPiece.access == 2 && !movedPiece.isKing) break;

                        if ((movedPiece.locationIndex / 4) % 2 == 0) {
                            potentialJumpee = 4;
                        } else {
                            potentialJumpee = 3;
                        }
                        break;
                    case 9:
                        // if not player 1  or is a king, cannot go forwards
                        if (movedPiece.access == 2 && !movedPiece.isKing) break;

                        if ((movedPiece.locationIndex / 4) % 2 == 0) {
                            // if player 1, use 4 if player 2 use 5
                            potentialJumpee = 5;
                        } else {
                            // if player 1, use 5 if player 2 use 4
                            potentialJumpee = 4;
                        }
                        break;
                }
                if (!(potentialJumpee == -1)) {  // if valid and/or not
                    for (CheckerPiece piece : pieces) {
                        if (((movedPiece.locationIndex + potentialJumpee) == piece.locationIndex)
                                && movedPiece.access != piece.access) {
                            // double check final location is not occupied
                            boolean occupied = false;
                            for (CheckerPiece finalLocationCheck : pieces) {
                                if (finalLocationCheck.locationIndex == potentialIndex) {
                                    occupied = true;
                                    break;
                                }
                            }
                            if (occupied) {
                                // location is occupied
                                // throw toast if no jump is possible
                                Toast toast = Toast.makeText(view.getContext(),
                                        "The location you tried to jump to is invalid. Please try again.",
                                        Toast.LENGTH_SHORT);
                                toast.setMargin(50, 50);
                                toast.show();
                                break;
                            }

                            // kill piece
                            pieces.remove(piece);
                            // move movedPiece
                            movedPiece.updateIndex(potentialIndex, marginX, marginY, checkerSize);
                            sendTurnToDB(ogIndex, potentialIndex);
                            playerHasMoved = true;
                            break;
                        } else if (((movedPiece.locationIndex + potentialJumpee) == piece.locationIndex)
                                && movedPiece.access == piece.access) {
                            // Cannot jump your own piece

                            // throw toast if no jump is possible
                            Toast toast = Toast.makeText(view.getContext(),
                                    "Cannot jump your own piece. Please try again.",
                                    Toast.LENGTH_SHORT);
                            toast.setMargin(50, 50);
                            toast.show();
                        }
                    }

                } else {
                    // impossible move
                    // throw toast if no jump is possible
                    Toast toast = Toast.makeText(view.getContext(),
                            "The move you tried to make is invalid. Please try again.",
                            Toast.LENGTH_SHORT);
                    toast.setMargin(50, 50);
                    toast.show();
                }

            }

            // determine if the moved piece needs to be kinged
            int row = movedPiece.locationIndex / 4;
            if ((movedPiece.access == 1 && row == 7) || (movedPiece.access == 2 && row == 0)
                    && !movedPiece.isKing) {
                movedPiece.kingify();
            }

            movedPiece.isGrabbed = false;

            view.invalidate();
        }
    }

    /**
     * Determine if the puzzle is done!
     * @return true if puzzle is done
     */
    public boolean isDone() {
        return pieces.isEmpty();
    }

    public void switchTurn(final View view) throws InterruptedException {
        // swap current player
        activePlayer = activePlayer == 1 ? 2 : 1;

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference turnDBRef = rootRef.child("boardState").child("turn");
        turnDBRef.setValue(activePlayer);

        playerHasMoved = false;
        otherPlayersMove = true;
        waitForOtherPlayer(view);
    }

    public void waitForOtherPlayer(View view) {
        otherPlayerWaitToastCount++;
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference boardStateRef = rootRef.child("boardState");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long turnValue = (Long) dataSnapshot.child("turn").getValue();
                if(turnValue == null) return;
                if(turnValue == playerNumber){
                    otherPlayerLastMove = new ArrayList<>();
                    otherPlayerLastMove.add(Integer.parseInt((String) dataSnapshot.child("lastMove").child("0").getValue()));
                    otherPlayerLastMove.add(Integer.parseInt((String) dataSnapshot.child("lastMove").child("1").getValue()));
                    otherPlayersMove = false;
                    playerHasMoved = false;

                    activePlayer = playerNumber;
                }
                else if(turnValue == 0){
                    // TODO other player reset
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        boardStateRef.addListenerForSingleValueEvent(valueEventListener);

        if (otherPlayersMove && (otherPlayerWaitToastCount%4 == 0)){
            Toast toast = Toast.makeText(view.getContext(),
                    "Waiting for other player's move.",
                    Toast.LENGTH_SHORT);
            toast.setMargin(50, 50);
            toast.show();
            refreshForTurn( 1000, view );
            view.invalidate();
        }
        else if(otherPlayersMove){
            refreshForTurn( 1000, view );
        }
        else{
            executeOtherPlayerTurn(view, otherPlayerLastMove.get(0), otherPlayerLastMove.get(1));
            otherPlayerLastMove = new ArrayList<>();
            otherPlayerWaitToastCount = 0;

        }
    }

    public void refreshForTurn(int milliseconds, final View view) {

        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                waitForOtherPlayer(view);
            }
        };

        handler.postDelayed(runnable, milliseconds);
    }

    public void sendTurnToDB(int start, int end){
    ArrayList<String> move = new ArrayList<>();
    move.add(Integer.toString(start));
    move.add(Integer.toString(end));

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    final DatabaseReference turnDBRef = rootRef.child("boardState").child("lastMove");
    turnDBRef.setValue(move);
    }


}
