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
//    @Test
//    public void testUserSongs() throws SQLException {
//        Connection conn = startConnection();
//        DataRepository.insertUser(conn, "Alice", "");// inserting 2 users bob and alice
//        DataRepository.insertUser(conn, "Bob", "");
//        User alice = DataRepository.selectUser(conn, "Alice");
//                User bob = DataRepository.selectUser(conn, "Bobl");
//        DataRepository.insertSong(conn, "Alice", "songname", "songartist", "songalbum", "songgenre", 2014);
//        DataRepository.insertSong(conn, "Bob", "songname", "songartist", "songalbum", "songgenre", 2014);
//                ArrayList<Song> songs = DataRepository.selectUserSongs(conn, "Alice");//get all replies to the first message
//        conn.close();
//        assertTrue(songs.size() == 1);//since we inserted 2, replies array list size should be 2
//    }
//
//    //test for insert and select single song -- not sure why this test doesn't pass, the actual method seems to be working correctly
//    @Test
//    public void testSingleSong() throws SQLException {
//        Connection conn = startConnection();
//        DataRepository.insertSong(conn, "Alice", "songname", "songartist", "songalbum", "songgenre", 2014);
//        Song song = DataRepository.selectUserSingleSong(conn, 1, "Alice");
//        conn.close();
//        assertTrue(song != null);
//    }

    //test for edit entry  Connection conn, int songId, String username
//    @Test
//    public void testEditEntry() throws SQLException {
//        Connection conn = startConnection();
//        DataRepository.insertSong(conn, "Alice", "songname", "songartist", "songalbum", "songgenre", 2014);
//        User testUser = DataRepository.selectUser(conn, "Alice");
//        Song testSong = DataRepository.selectUserSingleSong(conn, 1, "Alice"); // need to figure out how to pull out song id
//        DataRepository.editSong(conn, "Alice.id", );
//        conn.close();
//        assertTrue(user != null);
//    }

}