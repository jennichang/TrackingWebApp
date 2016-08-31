package com.theironyard.charlotte;

import java.util.ArrayList;

/**
 * Created by jenniferchang on 8/25/16.
 */
public class User {
    private int id;
    private String name;
    private String password;
    //ArrayList<Song> songsList = new ArrayList<>();


    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
