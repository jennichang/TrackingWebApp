package com.theironyard.charlotte;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by jenniferchang on 8/30/16.
 */
public class DataRepositoryTest {

    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }
    //test for insert and select user
    @Test
    public void testInsertSelect() throws SQLException {
        Connection conn = startConnection();
        DataRepository.insertUser(conn, "Alice", "password");
        User user = DataRepository.selectUser(conn, "Alice");
        conn.close();
        assertTrue(user != null);
    }

//    // Test for insert and select user songs
    @Test
    public void testUserSongs() throws SQLException {
        Connection conn = startConnection();
        DataRepository.insertUser(conn, "Alice", "");// inserting 2 users bob and alice
        DataRepository.insertUser(conn, "Bob", "");
        User alice = DataRepository.selectUser(conn, "Alice");
        User bob = DataRepository.selectUser(conn, "Bob");
        DataRepository.insertSong(conn, alice.getId(), "songname", "songartist", "songalbum", "songgenre", 2014);
        DataRepository.insertSong(conn, bob.getId(), "songname", "songartist", "songalbum", "songgenre", 2014);
                ArrayList<Song> songs = DataRepository.selectUserSongs(conn, "Alice");
        conn.close();
        assertTrue(songs.size() == 1);
    }

//
    @Test
    public void testSingleSong() throws SQLException {
        Connection conn = startConnection();
        DataRepository.insertUser(conn, "Alice", "");
        User alice = DataRepository.selectUser(conn, "Alice");
        DataRepository.insertSong(conn, alice.getId(), "songname", "songartist", "songalbum", "songgenre", 2014);
        //how do i get the songId from the insertSong, if i'm testing selectusersinglesong? do I have to create another method,
        // just to test this? cause that's what i did
        Song songName = DataRepository.selectSongByName(conn, "songname");
        Song song = DataRepository.selectUserSingleSong(conn, songName.getId(), alice.getId());
        conn.close();
        assertTrue(song != null);
    }

    //test for edit entry  Connection conn, int songId, String username
    @Test
    public void testEditEntry() throws SQLException {
        Connection conn = startConnection();
        DataRepository.insertUser(conn, "Alice", "");
        User alice = DataRepository.selectUser(conn, "Alice");
        DataRepository.insertSong(conn, alice.getId(), "songname", "songartist", "songalbum", "songgenre", 2014);
        Song songName = DataRepository.selectSongByName(conn, "songname");
        DataRepository.editSong(conn, alice.getId(), songName.getId(), "edit", "edit", "edit", "edit", 2016);
        Song testSong = DataRepository.selectUserSingleSong(conn, songName.getId(), alice.getId());
        conn.close();
        assertTrue(testSong.getName().equals("edit"));
    }

    // test for delete entry
    @Test
    public void testDeleteEntry() throws SQLException {
        Connection conn = startConnection();
        DataRepository.insertUser(conn, "Alice", "");
        User alice = DataRepository.selectUser(conn, "Alice");
        DataRepository.insertSong(conn, alice.getId(), "songname", "songartist", "songalbum", "songgenre", 2014);
        DataRepository.insertSong(conn, alice.getId(), "songname2", "songartist2", "songalbum2", "songgenre2", 2014);
        Song songName = DataRepository.selectSongByName(conn, "songname");
        DataRepository.deleteSong(conn, alice.getId(), songName.getId());
        ArrayList<Song> songs = DataRepository.selectUserSongs(conn, "Alice");
        conn.close();
        assertTrue(songs.size() == 1);//since we inserted 2, replies array list size should be 2
    }

}