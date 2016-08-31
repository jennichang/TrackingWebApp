package com.theironyard.charlotte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by jenniferchang on 8/30/16.
 */
public class DataRepository {

    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
        // Insert user prepared statement, returns void.  This will take in the new user's name and password and insert into the users table
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id, name, password);
        }
        return null;
        // Select user execute query - returns a user object.  Takes in the username and spits out users table (id, username and password)
    }

    public static void insertSong(Connection conn, int userId, String name, String artist, String album, String genre, int year) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO songs VALUES (NULL, ?, ?, ?, ?, ?, ?)");
        stmt.setInt(1, userId);
        stmt.setString(2, name);
        stmt.setString(3, artist);
        stmt.setString(4, album);
        stmt.setString(5, genre);
        stmt.setInt(6, year);
        stmt.execute();
        // insert song prepared statement, returns nothing.  inserts into songs table the song created by /create-song
    }

    public static Song selectUserSingleSong(Connection conn, int songId, int userId) throws SQLException { // using aliases in the queries don't seem to work?
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM songs JOIN users ON songs.userId = users.id AND users.id = ? WHERE songs.id = ?");
        stmt.setInt(1, userId);
        stmt.setInt(2, songId);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("songs.id");
            String name = results.getString("songs.name");
            String artist = results.getString("songs.artist");
            String album = results.getString("songs.album");
            String genre = results.getString("songs.genre");
            int year = results.getInt("songs.year");
            return new Song(id, name, artist, album, genre, year);
        }
        return null;
        // Spitting out a single song based on user and song -- will use this when I fill in fields for edit and delete forms.
        // Returns a new Song object.
    }


    public static ArrayList<Song> selectUserSongs(Connection conn, String username) throws SQLException {
        ArrayList<Song> userSongs = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM songs JOIN users ON songs.userId = users.id AND users.name = ?");
        stmt.setString(1, username);
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = results.getInt("songs.id");
            String name = results.getString("songs.name");
            String artist = results.getString("songs.artist");
            String album = results.getString("songs.album");
            String genre = results.getString("songs.genre");
            int year = results.getInt("songs.year");
            Song song = new Song(id, name, artist, album, genre, year); // while there are results, will create a new Song object for each result
            userSongs.add(song); // then add it to the userSongs arraylist
        }
        return userSongs;
        // spitting out all the user's songs in an arraylist.
    }

    public static void editSong(Connection conn, int userId, int id, String name, String artist, String album, String genre, int year) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE songs SET name = ? , artist = ?, album = ?, genre = ?, year = ? WHERE id = ? AND userId = ?");
        stmt.setString(1, name);
        stmt.setString(2, artist);
        stmt.setString(3, album);
        stmt.setString(4, genre);
        stmt.setInt(5, year);
        stmt.setInt(6, id);
        stmt.setInt(7, userId);
        stmt.execute();
    } // editing existing song, returns nothing.  Takes in the userId and song Id and updates the record in songs table

    public static void deleteSong(Connection conn, int userId, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM songs WHERE userId = ? AND id = ?");
        stmt.setInt(1, userId);
        stmt.setInt(2, id);
        stmt.execute();
    } //  deletes the specific song from the songs table, based on the userId and song Id

    public static ArrayList<Song> searchSong(Connection conn, String nameSearch) throws SQLException {
        PreparedStatement preparedStatement =
                conn.prepareStatement("SELECT * FROM songs WHERE LOWER(name) LIKE LOWER(?)"); //fixed case sensitivity issue this time around.
        preparedStatement.setString(1, "%" + nameSearch + "%"); // the like function is tricky cannot put in the prepared statement % or single quote
        ResultSet results = preparedStatement.executeQuery();
        ArrayList<Song> songsSearchList = new ArrayList<>();
        while (results.next()) {
            int id = results.getInt("songs.id");
            String name = results.getString("songs.name");
            String artist = results.getString("songs.artist");
            String album = results.getString("songs.album");
            String genre = results.getString("songs.genre");
            int year = results.getInt("songs.year");
            songsSearchList.add(new Song(id, name, artist, album, genre, year));
        }
        return songsSearchList;
    }


}
