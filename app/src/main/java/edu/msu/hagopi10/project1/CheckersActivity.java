package edu.msu.hagopi10.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckersActivity extends AppCompatActivity {
    public int score1 = 12;
    public int score2 = 12;
    Player Player1 = new Player(1, score1);
    Player Player2 = new Player(2, score2);
    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkers);

        CheckersView view = (CheckersView) findViewById(R.id.view);
        view.SetupGame();

        TextView playerName = (TextView)findViewById(R.id.player1);
        //playerName.setText(MainActivity.nameS1);
        playerName.setText("Player1");

        TextView p2Name = (TextView)findViewById(R.id.player2);
        //p2Name.setText(MainActivity.nameS2);
        p2Name.setText("Player2");

        // announce first turn
        Toast toast=Toast.makeText(getApplicationContext(),MainActivity.nameS1+ "'s Turn", Toast.LENGTH_SHORT);
        toast.setMargin(50,50);
        toast.show();

    }



    public void EndTurn(View view) {
        int ctr1 = 0;
        int ctr2 = 0;
        CheckersView view1 = (CheckersView)findViewById(R.id.view);
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
            TextView p2Name = (TextView)findViewById(R.id.player2);
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
            TextView playerName = (TextView)findViewById(R.id.player1);
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

        view1.board.switchTurn(view1);
        // inform the players whose turn it is
        String player = view1.board.getActivePlayer() == 1 ? Player1.GetName1() : Player2.GetName2();

        Toast toast=Toast.makeText(view.getContext(),player + "'s Turn", Toast.LENGTH_SHORT);
        toast.setMargin(50,50);
        toast.show();

    }

    public void ResetDB(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference boardStateRef = rootRef.child("boardState");

        boardStateRef.child("activePlayers").child("Player1").setValue("None");
        boardStateRef.child("activePlayers").child("Player2").setValue("None");
        boardStateRef.child("lastMove").setValue("None");
        boardStateRef.child("turn").setValue(0);

    }

    public void Forfeit(View view) {
        ResetDB();
        Intent intent = new Intent(this, EndGame.class);
        startActivity(intent);
    }


}