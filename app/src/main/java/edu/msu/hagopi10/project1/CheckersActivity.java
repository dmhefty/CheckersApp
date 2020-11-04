package edu.msu.hagopi10.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CheckersActivity extends AppCompatActivity {
    public int score1 = 12;
    public int score2 = 12;
    Player Player1 = new Player(1,score1);
    Player Player2 = new Player(2,score2);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkers);

        TextView playerName = (TextView)findViewById(R.id.player1);
        playerName.setText(MainActivity.nameS1);

        TextView p2Name = (TextView)findViewById(R.id.player2);
        p2Name.setText(MainActivity.nameS2);

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
        }

        view1.board.switchTurn(view1);
        // inform the players whose turn it is
        String player = view1.board.getActivePlayer()  == 1 ? Player1.GetName1() : Player2.GetName2();

        Toast toast=Toast.makeText(view.getContext(),player + "'s Turn", Toast.LENGTH_SHORT);
        toast.setMargin(50,50);
        toast.show();

    }

    public void Forfeit(View view) {
        /*if (board.getActivePlayer() == 1) {

        }
        */
        Intent intent = new Intent(this, EndGame.class);
        startActivity(intent);




    }


}