package edu.msu.hagopi10.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

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
        TextView p1Name = (TextView)findViewById(R.id.textView3);
        p1Name.setText(MainActivity.nameS1);

        TextView p2Name = (TextView)findViewById(R.id.textView2);
        p2Name.setText(MainActivity.nameS2);
    }



}