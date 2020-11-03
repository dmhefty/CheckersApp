package edu.msu.hagopi10.project1;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import android.os.Bundle;

public class EndGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

    }

    public void Resign(View view) {


        AlertDialog.Builder builder =
                new AlertDialog.Builder(view.getContext());

        // Parameterize the builder
        builder.setTitle(R.string.gameover);
        builder.setMessage(R.string.forfeit_message);
        builder.setPositiveButton(android.R.string.ok, null);
        // Create the dialog box and show it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    public void New_Game(View view) {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}