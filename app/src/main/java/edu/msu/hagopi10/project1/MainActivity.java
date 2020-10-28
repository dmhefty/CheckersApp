package edu.msu.hagopi10.project1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onStartCheckersActivity(View view) {

        Intent intent1 = new Intent();
        EditText editText1 = (EditText) findViewById(R.id.PLAYER1NAME);
        EditText editText2 = (EditText) findViewById(R.id.PLAYER2NAME);
        intent1.putExtra(NAME1, editText1.getText().toString());
        intent1.putExtra(NAME2, editText2.getText().toString());
        Intent intent = new Intent(this, CheckersActivity.class);
        startActivity(intent);
    }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);

        }

}