package com.theironyard.charlotte;

/**
 * Created by jenniferchang on 8/25/16.
 */
public class Song {
    static int id = 0;
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
        this.id = id++;
    }
}
