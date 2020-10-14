package edu.msu.hagopi10.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name2);
    }

    public void onStartGame(View view) {
        Intent intent = new Intent(this, CheckersActivity.class);
        startActivity(intent);
    }
}