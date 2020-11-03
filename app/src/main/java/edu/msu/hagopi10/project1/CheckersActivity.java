package edu.msu.hagopi10.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
    }

    public void EndTurn(View view) {
        if (score1 <= 0) {
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
        else if (score2 <= 0) {
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
    }

    public void Forfeit(View view) {
        /*if (board.getActivePlayer() == 1) {

        }
        */
        Intent intent = new Intent(this, EndGame.class);
        startActivity(intent);




    }


}