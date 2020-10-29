package edu.msu.hagopi10.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

public class CheckersActivity extends AppCompatActivity {
    public ArrayList<CheckerPiece> pieces1 = new ArrayList<CheckerPiece>();
    public ArrayList<CheckerPiece> pieces2 = new ArrayList<CheckerPiece>();
    public int score1;
    public int score2;
    Player Player1 = new Player(pieces1,score1);
    Player Player2 = new Player(pieces2,score2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkers);
    }



}