package edu.msu.hagopi10.project1;

public class User {

    public String username;
    public String password;
    public Integer wins;
    public Integer losses;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.wins = 0;
        this.losses = 0;
    }

}