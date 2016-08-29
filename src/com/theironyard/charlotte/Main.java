package com.theironyard.charlotte;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static java.util.Comparator.comparing;

public class Main {

    static HashMap<String, User> usersMap = new HashMap<>();

    public static void main(String[] args) {

        usersMap.put("Jennifer", new User("Jennifer", "abc")); // this is just to test
        usersMap.put("Alice", new User("Alice", "123")); // this is just to test
        usersMap.put("Charlie", new User("Charlie", "qwe")); // this is just to test

        Spark.get(
                "/", // relative path (homepage)
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    String songId = request.queryParams("songId");
                    User userObj = usersMap.get(name);

                    HashMap m = new HashMap<>();

                    if (userObj == null) {
                        return new ModelAndView(m, "login.html");
                    }
                    else {
                        m.put("username", name);
                        m.put("songsList", userObj.songsList);
                        m.put("songId", songId);
                        return new ModelAndView(m, "home.html");
                    }
                }),
                new MustacheTemplateEngine()
        );


        Spark.get("/edit/:songId" ,
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    int songId = Integer.valueOf(request.params("songId")); // needed request.params and not request.queryParams

                    User userObj = usersMap.get(name);

                    String songName = userObj.songsList.get(songId).name;
                    String songArtist = userObj.songsList.get(songId).artist;
                    String songAlbum = userObj.songsList.get(songId).album;
                    String songGenre = userObj.songsList.get(songId).genre;
                    int songYear = userObj.songsList.get(songId).year;

                    HashMap p = new HashMap<>(); // create model hashmap

                    p.put("username", name);
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


        Spark.get("/delete/:songId" ,
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    int songId = Integer.valueOf(request.params("songId"));

                    User userObj = usersMap.get(name);

                    String songName = userObj.songsList.get(songId).name;
                    String songArtist = userObj.songsList.get(songId).artist;
                    String songAlbum = userObj.songsList.get(songId).album;
                    String songGenre = userObj.songsList.get(songId).genre;
                    int songYear = userObj.songsList.get(songId).year;

                    HashMap d = new HashMap<>(); // create model hashmap

                    d.put("username", name);
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
                "/login",
                ((request, response) -> {
                    String name = request.queryParams("loginName");
                    String password = request.queryParams("password");
                    User userObj = usersMap.get(name);

                    if ((userObj == null) || (userObj != null && usersMap.get(name).getPassword().equals(password))) {
                        if (userObj == null) {
                            userObj = new User(name, password);
                            usersMap.putIfAbsent(name, userObj);
                        }
                        Session session = request.session();
                        session.attribute("username", name);
                        response.redirect("/");
                    } else {
                        if (usersMap.get(name).getPassword() != password) {
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
                    User userObject = usersMap.get(name);
                    if (userObject == null) { // if no user
                        throw new Exception("User is not logged in");
                    }

                    String songName = request.queryParams("songName");
                    String songArtist = request.queryParams("songArtist");
                    String songAlbum = request.queryParams("songAlbum");
                    String songGenre = request.queryParams("songGenre");
                    int songYear = Integer.valueOf(request.queryParams("songYear"));

                    Song songObj = new Song(songName, songArtist, songAlbum, songGenre, songYear);

                    //Ok - to fix the bug of ids screwing up after deleting an entry.  If the new song created's id is not the max id
                    // I will make it the current maxId + 1. Find the current maxId, and then increment new song ID by 1

                    int maxId = -1;

                    for(int i = 0; i < userObject.songsList.size();i++) {
                        if(userObject.songsList.get(i).id > maxId) {
                            maxId = userObject.songsList.get(i).id;
                        }
                    }

//                    ArrayList<Song> userSongstemp = new ArrayList<Song>();
//                    userSongstemp.add(songObj);
//
//                    String whatAmIDoing = String.valueOf(userSongstemp.stream().max(comparing(Song::getId)).get());
//
//                    int maxId = whatAmIDoing.charAt(0);
//
                    if(songObj.id != maxId+1) {
                        songObj.id = maxId+1;
                    }

                    userObject.songsList.add(songObj);

                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/edit-song/:songId",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    int id = Integer.valueOf(request.params("songId"));
                    User userObject = usersMap.get(name);

                    String songName = request.queryParams("songName");
                    String songArtist = request.queryParams("songArtist");
                    String songAlbum = request.queryParams("songAlbum");
                    String songGenre = request.queryParams("songGenre");
                    int songYear = Integer.valueOf(request.queryParams("songYear"));

                    Song editedSong = new Song(songName, songArtist, songAlbum, songGenre, songYear);

                    if(editedSong.id != id) {
                        editedSong.id = id;
                    } // to fix the bug of ids changing after song is edited...just revert back to original id

                    userObject.songsList.set(id, editedSong);

                    String referrer = request.headers("Referrer");
                    response.redirect(referrer != null ? referrer : "/");
                    return "";
                })

        );

        Spark.post(
                "/delete-song/:songId",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    int id = Integer.valueOf(request.params("songId"));
                    User userObject = usersMap.get(name);

                   //ArrayList<Song> keepSongId = (ArrayList<Song>) userObject.songsList.clone();

                    for(int i = 0; i < userObject.songsList.size(); i++) {
                        if(userObject.songsList.get(i).id == id) {
                            userObject.songsList.remove(i);
                        }
                    }

                    // in order to keep the ids the same, and not have the list shift up...anything before the deleted item stays the same
                    // but anything after has to be shifted by -1
                    for(int i = 0; i < userObject.songsList.size(); i++) {
                        if(userObject.songsList.get(i).id > id) {
                            userObject.songsList.get(i).id = userObject.songsList.get(i).id - 1;
                        }
                        }

                    String referrer = request.headers("Referrer");
                    response.redirect(referrer != null ? referrer : "/");
                    return "";
                })

        );

    }
}











// --> "/update-card/:id"

/*

Card card = user.cardList.stream().filter(c -> c.id == Integer.valueOf(request.params(":id"))).findFirst


 */