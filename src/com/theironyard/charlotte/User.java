package com.theironyard.charlotte;

import java.util.ArrayList;

/**
 * Created by jenniferchang on 8/25/16.
 */
public class User {
    String name;
    private String password;
    ArrayList<Song> songsList = new ArrayList<>();

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
