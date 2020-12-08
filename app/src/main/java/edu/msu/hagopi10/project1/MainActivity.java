package edu.msu.hagopi10.project1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private static final int  ENTER_NAMES = 1;
    public static final String NAME1 = "Player1";
    public static final String NAME2= "Player2";
    public static String nameS1;
    public static String nameS2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void HowToPlay(View view) {


        AlertDialog.Builder builder =
                new AlertDialog.Builder(view.getContext());

        // Parameterize the builder
        builder.setTitle(R.string.how_to_play);
        builder.setMessage(R.string.explanation);
        builder.setPositiveButton(android.R.string.ok, null);
        // Create the dialog box and show it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onStartLoad(View view) {
        Intent intent = new Intent(this, LoadActivity.class);
        startActivity(intent);
    }

    public void onStartLogIn(View view) {
        Intent intent = new Intent(this, LogInActivity.class);
        //startActivity(intent);
    }


    public void onStartCheckersActivity(View view) {

        Intent intent1 = new Intent();
        EditText editText1 = (EditText) findViewById(R.id.PLAYER1NAME);
        EditText editText2 = (EditText) findViewById(R.id.PLAYER2NAME);
        nameS1 = editText1.getText().toString();
        nameS2 = editText2.getText().toString();
        intent1.putExtra(NAME1,nameS1);
        intent1.putExtra(NAME2,nameS2);
         Intent intent = new Intent(this, CheckersActivity.class);
        startActivity(intent); 
    }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);

        }

}