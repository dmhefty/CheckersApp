package edu.msu.hagopi10.project1;
import android.provider.Settings;

import java.lang.String;
import java.util.ArrayList;
public class Player {
    //array of pieces a player has
   // public ArrayList<CheckerPiece> pieces = new ArrayList<CheckerPiece>();
    // how many pieces the player has left
private float score;
private String name1;
private String name2;
private int playerCtr = 0;
private int access1 =1;
private int access2 = 2;

public Player( int access, float score1){
        if(playerCtr == 0) {
            this.score = score1;
            this.access1 = access;
            this.name1 = MainActivity.nameS1;
            playerCtr++;
        }
        if(playerCtr ==1){
            this.score = score1;
            this.access2 = access;
            this.name2 = MainActivity.nameS2;
        }
    }

}



