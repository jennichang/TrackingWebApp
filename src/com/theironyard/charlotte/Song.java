package com.theironyard.charlotte;

import java.util.ArrayList;

/**
 * Created by jenniferchang on 8/25/16.
 */
public class Song {
    int id = 0;
    static int songCount = 0;
    String name;
    String artist;
    String album;
    String genre;
    int year;

    public Song(String name, String artist, String album, String genre, int year) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.year = year;
        this.id = songCount++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    @Override
//    public String toString() {
//        return String.format(id + " " + name + " " + artist + " " +  album + " " +  genre + " " +  year + " ");
//    }
}
