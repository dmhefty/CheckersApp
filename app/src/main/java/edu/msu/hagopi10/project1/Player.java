package edu.msu.hagopi10.project1;
import android.provider.Settings;

import java.lang.String;
import java.util.ArrayList;
public class Player {
    //array of pieces a player has
    public ArrayList<CheckerPiece> pieces = new ArrayList<CheckerPiece>();
    // how many pieces the player has left
private float score;
private String name1;
private int playerCtr = 0;

public Player( ArrayList checkers, float score1){
        if(playerCtr == 0) {
            this.score = score1;
            this.pieces = checkers;
            this.name1 = MainActivity.NAME1;
            playerCtr++;
        }
        if(playerCtr ==1){
            this.score = score1;
            this.pieces = checkers;
            this.name1 = MainActivity.NAME2;
        }
    }

}



