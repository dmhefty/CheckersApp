package edu.msu.hagopi10.project1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int  ENTER_NAMES = 1;
    public static final String NAME1 = "Player1";
    public static final String NAME2= "Player2";
    public static String nameS1;
    public static String nameS2;
    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static String REFRESH_DATA_INTENT = "edu.msu.hagopi10.project1.refreshdata";
    public static String MESSAGE_FROM = "edu.msu.hagopi10.project1.messagefrom";
    public static String MESSAGE_BODY = "edu.msu.hagopi10.project1.messagebody";
    private DataUpdateReceiver dataUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dataUpdateReceiver != null) unregisterReceiver(dataUpdateReceiver);
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(MainActivity.REFRESH_DATA_INTENT);
        registerReceiver(dataUpdateReceiver, intentFilter);
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

    public void onNewUser(View view) {
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivity(intent);
    }

    public void onStartLogIn(View view) {
        final List<User> userList = new ArrayList<>();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child("users");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> list = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User tempUser = new User(ds.getKey(), (String) ds.getValue());
                    userList.add(tempUser);
                }

                logInUser(userList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        userRef.addListenerForSingleValueEvent(valueEventListener);

    }


    public void onStartCheckersActivity() {

        Intent intent1 = new Intent();
        EditText editText1 = findViewById(R.id.usernameTextBox);
        EditText editText2 = findViewById(R.id.passwordTextBox);
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

    private void logInUser (List<User> userList){
        String username = ((EditText) findViewById(R.id.usernameTextBox)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordTextBox)).getText().toString();

        final User user = new User(username, password);

        boolean successfulLogin = false;
        for(User person  :  userList){
            if (person.username.equals(user.username) && person.password.equals(user.password)){
                onStartCheckersActivity();
                successfulLogin = true;
            }
        }

        if(!successfulLogin){
            Toast toast=Toast.makeText(getApplicationContext(),
                    "Username and Password combination not accepted. Try again.",
                    Toast.LENGTH_SHORT);
            toast.setMargin(50,50);
            toast.show();
        }
        else{
            Toast toast=Toast.makeText(getApplicationContext(),
                    "Login Success!! Enjoy your game.",
                    Toast.LENGTH_SHORT);
            toast.setMargin(50,50);
            toast.show();
        }

    }

    //
    // subclass
    //

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainActivity.REFRESH_DATA_INTENT)) {
                // Do stuff - maybe update my view based on the changed DB contents
                String from = intent.getStringExtra(MESSAGE_FROM);
                String body = intent.getStringExtra(MESSAGE_BODY);

                Toast toast = new Toast(context);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}