package edu.msu.hagopi10.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

public class CheckersActivity extends AppCompatActivity {
    public int score1 = 12;
    public int score2 = 12;
    Player Player1 = new Player(1,score1);
    Player Player2 = new Player(2,score2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkers);
    }



}