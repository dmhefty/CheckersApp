package edu.msu.hagopi10.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onStartCheckers(View view) {
        Intent intent = new Intent(this, NameActivity.class);
        startActivity(intent);
    }


}