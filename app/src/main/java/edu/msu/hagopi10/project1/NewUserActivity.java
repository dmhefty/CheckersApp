package edu.msu.hagopi10.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
    }

    public void onSave(View view) {
        String username = ((EditText) findViewById(R.id.newUsernameEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.newPasswordEditText)).getText().toString();
        String passConfirmation = ((EditText) findViewById(R.id.confirmPasswordEditText)).getText().toString();

        if (password.length() < 8) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Password is too short. Must be at least 8 characters. Try again.",
                    Toast.LENGTH_SHORT);
            toast.setMargin(50, 50);
            toast.show();
        } else if (!(password.equals(passConfirmation))) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Password doesn't match password confirmation. Try again.",
                    Toast.LENGTH_SHORT);
            toast.setMargin(50, 50);
            toast.show();
        } else {
            // save password
            User newUser = new User(username, password);
            MainActivity.mDatabase.child("users").child(username).setValue(password);

            Toast toast = Toast.makeText(getApplicationContext(),
                    "New User saved.",
                    Toast.LENGTH_SHORT);
            toast.setMargin(50, 50);
            toast.show();

            this.finish();
        }
    }

    public void onBack(View view) {
        this.finish();

    }
}