package edu.msu.hagopi10.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class CheckersActivity extends AppCompatActivity {
    public int score1 = 12;
    public int score2 = 12;
    Player Player1 = new Player(1, score1);
    Player Player2 = new Player(2, score2);
    public int waitCounter = 0;


    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static String serverToken = "AAAA-TEy6Jk:APA91bHcvy4rOtwUX-hGL-NZA6ONAev4QyLqpGpFPop87TGAJdajF9efj3wyQDxCQg3QJPkk2STn9QWrRl8YlYkQhOVOh2L13KpOGyZzx5wTFoE0lCAO6t-7h7ttxvCrBFh2bJdQN73N";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + serverToken;
    final private String contentType = "application/json";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkers);

        final CheckersView view = findViewById(R.id.view);

        view.SetupGame();
        waitForSetupFinish();

    }



    public void EndTurn(View view) throws InterruptedException {
        CheckersView cView = findViewById(R.id.view);
        if(!cView.board.playerHasMoved){
            Toast toast=Toast.makeText(view.getContext(),
                    "Make a move before ending your turn.",
                    Toast.LENGTH_SHORT);
            toast.setMargin(50,50);
            toast.show();
            return;
        }

        if(cView.board.otherPlayersMove){ // if it is the other player's turn, do nothing.
            return;
        }

        int ctr1 = 0;
        int ctr2 = 0;
        CheckersView view1 = findViewById(R.id.view);
        for(CheckerPiece piece:view1.getBoard().pieces ){
            if(piece.access ==1){
                ctr1++;

            }else{
                ctr2++;
            }
        }
        score1 = ctr1;
        score2 = ctr2;
        if (ctr1<=0) {
            TextView p2Name = findViewById(R.id.player2);
            p2Name.setText(MainActivity.nameS2);

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(view.getContext());

            // Parameterize the builder
            builder.setTitle(R.string.gameover);
            builder.setMessage(Player1.GetName1() + " wins!");
            builder.setPositiveButton(android.R.string.ok, null);
            // Create the dialog box and show it
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            ResetDB();
        }
        else if (ctr2 <= 0) {
            TextView playerName = findViewById(R.id.player1);
            playerName.setText(MainActivity.nameS1);

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(view.getContext());

            // Parameterize the builder
            builder.setTitle(R.string.gameover);
            builder.setMessage(Player2.GetName1() + " wins!");
            builder.setPositiveButton(android.R.string.ok, null);
            // Create the dialog box and show it
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            ResetDB();
        }

        view1.board.sendTurnToDB(view1.board.turnStart, view1.board.turnEnd);

        view1.board.switchTurn(view1);

        TextView tView = findViewById(R.id.player2);
        tView.setText(tView.getText() + Integer.toString(view1.board.getActivePlayer()));
        // inform the players whose turn it is
        //String player = view1.board.getActivePlayer() == 1 ? Player1.GetName1() : Player2.GetName2();


    }



    public void waitForSetupFinish() {
        waitCounter++;
        CheckersView view =  findViewById(R.id.view);
        if(view.hasReset){ // if the dialogue is open let it stay
            finish();
        }
        else if (view.resetOpen){ // if dialogue was opened then closed, end the activity
            refresh(1000);
        }
        else if(waitCounter <= 3 && !view.resetOpen){ //wait three seconds to check if the dialogue has opened
            refresh(1000);
        }
        else{
            TextView player1Name = findViewById(R.id.player1);
            player1Name.setText(view.player1Name);

            TextView player2Name = findViewById(R.id.player2);
            player2Name.setText(view.player2Name);

            // announce first turn
            Toast toast=Toast.makeText(getApplicationContext(),MainActivity.nameS1+ "'s Turn", Toast.LENGTH_SHORT);
            toast.setMargin(50,50);
            toast.show();
        }

    }

    private void refresh(int milliseconds) {

        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                waitForSetupFinish();
            }
        };

        handler.postDelayed(runnable, milliseconds);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CheckersActivity.this, "Request error", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void ResetDB(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference boardStateRef = rootRef.child("boardState");
        Hashtable<String, String> nameDict = new Hashtable<String, String>();
        nameDict.put("name", "None");
        nameDict.put("token", "");

        boardStateRef.child("activePlayers").child("Player1").setValue(nameDict);
        boardStateRef.child("activePlayers").child("Player2").setValue(nameDict);
        ArrayList<String> moves = new ArrayList< >();
        moves.add("-1");
        moves.add("-1");
        boardStateRef.child("lastMove").setValue(moves);
        boardStateRef.child("turn").setValue(0);
    }

    public void Forfeit(View view) {
        ResetDB();
        Intent intent = new Intent(this, EndGame.class);
        startActivity(intent);
    }


}