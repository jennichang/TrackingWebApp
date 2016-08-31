package com.theironyard.charlotte;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.h2.store.Data;
import org.h2.store.DataReader;
import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static java.util.Comparator.comparing;

public class Main {

    static HashMap<String, User> usersMap = new HashMap<>();

    public static void createTables(Connection conn) throws SQLException { // create users and songs table.  will join through u.id = s.userId
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute
                ("CREATE TABLE IF NOT EXISTS songs (id IDENTITY, userId INT, name NVARCHAR, artist NVARCHAR, album NVARCHAR, genre NVARCHAR, year INT)");
    }


    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn); // establish connection and create the 2 tables

        Spark.get(
                "/", // relative path (homepage)
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    String songId = request.queryParams("songId");
                    User userObj = DataRepository.selectUser(conn, name); // this will have their name and password - create a user object

                    HashMap m = new HashMap<>(); // model hashmap

                    if (userObj == null) {
                        return new ModelAndView(m, "login.html");
                    } else {
                        m.put("username", name);
                        m.put("songsList", DataRepository.selectUserSongs(conn, name)); // use selectusersongs
                        // method to spit out an arraylist of all their songs (as objects)
                        m.put("songId", songId);
                        return new ModelAndView(m, "home.html");
                    }
                }),
                new MustacheTemplateEngine()
        );


        Spark.post(
                "/login",
                ((request, response) -> {
                    String name = request.queryParams("loginName");
                    String password = request.queryParams("password");
                    User userObj = DataRepository.selectUser(conn, name);

                    if ((userObj == null) || (userObj != null && userObj.getPassword().equals(password))) {
                        if (userObj == null) {
                            DataRepository.insertUser(conn, name, password); // if user is null, insert that user into users table
                        }
                        Session session = request.session();
                        session.attribute("username", name);
                        response.redirect("/");
                    } else {
                        if (userObj.getPassword() != password) {
                            response.redirect("/");
                        }
                    }
                    return "";
                })
        );


        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );


        Spark.post(
                "/create-song",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    User userObj = DataRepository.selectUser(conn, name);
                    if (userObj == null) {
                        throw new Exception("User is not logged in");
                    }

                    int userId = userObj.getId(); // will need to get the user ID in order to join users table and songs table.
                    // instead of changing songs.userId to songs.username, joining on strings more costly
                    String songName = request.queryParams("songName");
                    String songArtist = request.queryParams("songArtist");
                    String songAlbum = request.queryParams("songAlbum");
                    String songGenre = request.queryParams("songGenre");
                    int songYear = Integer.valueOf(request.queryParams("songYear"));

                    DataRepository.insertSong(conn, userId, songName, songArtist, songAlbum, songGenre, songYear); // take song and add it to songs table

                    response.redirect("/");
                    return "";
                })
        );


        Spark.get("/edit/:songId",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    int songId = Integer.valueOf(request.params("songId")); // needed request.params and not request.queryParams
                    User userObj = DataRepository.selectUser(conn, name);
                    int userId = userObj.getId();

                    Song songEdit = DataRepository.selectUserSingleSong(conn, songId, userId);

                    String songName = songEdit.getName();
                    String songArtist = songEdit.getArtist();
                    String songAlbum = songEdit.getAlbum();
                    String songGenre = songEdit.getGenre();
                    int songYear = songEdit.getYear();

                    HashMap p = new HashMap<>();

                    p.put("username", name); // still need to figure out how to just pass the object and reference in html
                    p.put("songName", songName);
                    p.put("songId", songId);
                    p.put("songArtist", songArtist);
                    p.put("songAlbum", songAlbum);
                    p.put("songGenre", songGenre);
                    p.put("songYear", songYear);
                    return new ModelAndView(p, "edit.html");

                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/edit-song/:songId",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    int id = Integer.valueOf(request.params("songId"));
                    User userObj = DataRepository.selectUser(conn, name);

                    int userId = userObj.getId();
                    String songName = request.queryParams("songName");
                    String songArtist = request.queryParams("songArtist");
                    String songAlbum = request.queryParams("songAlbum");
                    String songGenre = request.queryParams("songGenre");
                    int songYear = Integer.valueOf(request.queryParams("songYear"));

                    DataRepository.editSong(conn, userId, id, songName, songArtist, songAlbum, songGenre, songYear);

                    String referrer = request.headers("Referrer");
                    response.redirect(referrer != null ? referrer : "/");
                    return "";
                })

        );


        Spark.get("/delete/:songId",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    int songId = Integer.valueOf(request.params("songId"));
                    User userObj = DataRepository.selectUser(conn, name);

                    int userId = userObj.getId();

                    Song songObj = DataRepository.selectUserSingleSong(conn, songId, userId);

                    String songName = songObj.getName();
                    String songArtist = songObj.getArtist();
                    String songAlbum = songObj.getAlbum();
                    String songGenre = songObj.getGenre();
                    int songYear = songObj.getYear();

                    HashMap d = new HashMap<>();

                    d.put("username", name); // need to figure out how to pass just object
                    d.put("songName", songName);
                    d.put("songId", songId);
                    d.put("songArtist", songArtist);
                    d.put("songAlbum", songAlbum);
                    d.put("songGenre", songGenre);
                    d.put("songYear", songYear);
                    return new ModelAndView(d, "delete.html");

                }),
                new MustacheTemplateEngine()
        );


        Spark.post(
                "/delete-song/:songId",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    int id = Integer.valueOf(request.params("songId"));
                    User userObj = DataRepository.selectUser(conn, name);

                    int userId = userObj.getId();

                    DataRepository.deleteSong(conn, userId, id);

                    String referrer = request.headers("Referrer");
                    response.redirect(referrer != null ? referrer : "/");
                    return "";
                })
        );

        Spark.post(
                "/No-delete/:songId",
                ((request, response) -> {

                    response.redirect("/");
                    return "";
                })
        );

        Spark.get(
                "/search",
                ((request, response) -> {

                    String search = request.queryParams("songSearch"); // get the string of what they searched for

                    HashMap s = new HashMap<>();
                    s.put("searchSong", DataRepository.searchSong(conn, search)); // use my search method and put in hashmap

                    return new ModelAndView(s, "search.html");

                }),
                new MustacheTemplateEngine()
        );

    }
}


