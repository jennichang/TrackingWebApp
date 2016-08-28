package com.theironyard.charlotte;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    static HashMap<String, User> usersMap = new HashMap<>();

    public static void main(String[] args) {

        usersMap.put("Jennifer", new User("Jennifer", "abc")); // this is just to test
        usersMap.put("Alice", new User("Alice", "123")); // this is just to test
        usersMap.put("Charlie", new User("Charlie", "qwe")); // this is just to test

        Spark.get(
                "/", // relative path (homepage)
                ((request, response) -> {
                    Session session = request.session(); // get reference to our current session
                    String name = session.attribute("username"); // get username from session
                    String songId = request.queryParams("songId"); // try to get key replyId and give me its value
                    User userObj = usersMap.get(name); // create a user object and it equals the value in the users hashmap (of that name)

                    HashMap m = new HashMap<>(); // create model hashmap

                    if (userObj == null) { // if the user object is null
                        return new ModelAndView(m, "login.html"); // return the login page
                    }
                    else {
                        m.put("username", name); // pass username to home html template
                        m.put("songsList", userObj.songsList); // why can't this work on line29?
                        m.put("songId", songId);
                        return new ModelAndView(m, "home.html"); //otherwise takes them to messages
                    }
                }),
                new MustacheTemplateEngine()
        );


        Spark.get("/edit/:songId" ,
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

                    HashMap m = new HashMap<>(); // create model hashmap

                    m.put("username", name);
                    m.put("songName", songName);
                    m.put("songId", songId);
                    m.put("songArtist", songArtist);
                    m.put("songAlbum", songAlbum);
                    m.put("songGenre", songGenre);
                    m.put("songYear", songYear);
                        return new ModelAndView(m, "edit.html");

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

                    HashMap m = new HashMap<>(); // create model hashmap

                    m.put("username", name);
                    m.put("songName", songName);
                    m.put("songId", songId);
                    m.put("songArtist", songArtist);
                    m.put("songAlbum", songAlbum);
                    m.put("songGenre", songGenre);
                    m.put("songYear", songYear);
                    return new ModelAndView(m, "delete.html");

                }),
                new MustacheTemplateEngine()
        );


        Spark.post(
                "/login",
                ((request, response) -> {
                    String name = request.queryParams("loginName");  // get name trying to log in as
                    String password = request.queryParams("password"); // get password
                    User userObj = usersMap.get(name); // reference hashmap at that name, key is name value is user object

                    if ((userObj == null) || (userObj != null && usersMap.get(name).getPassword().equals(password))) {
                        // if userObj is null OR not null and password is correct
                        if (userObj == null) { // if user is null
                            userObj = new User(name, password); // create object and put in hashmap
                            usersMap.putIfAbsent(name, userObj); // don't really need put if absent, but...just in case?
                        }
                        Session session = request.session(); // both will - set our session in reference to current users name
                        session.attribute("username", name);
                        response.redirect("/"); // then redirect
                    } else {
                        if (usersMap.get(name).getPassword() != password) { // if password is not correct, do not start a session and just redirect
                            response.redirect("/");
                        }
                    }
                    return "";
                })
        );

        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session(); // have current session
                    session.invalidate(); // invalidate it
                    response.redirect("/"); // redirect
                    return "";
                })
        );

        Spark.post(
                "/create-song",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username"); // getting current session, asking who are you?
                    User userObject = usersMap.get(name); // getting current user
                    if (userObject == null) { // if no user
                        throw new Exception("User is not logged in"); // throw exception
                    }

                    String songName = request.queryParams("songName");
                    String songArtist = request.queryParams("songArtist");
                    String songAlbum = request.queryParams("songAlbum");
                    String songGenre = request.queryParams("songGenre");
                    int songYear = Integer.valueOf(request.queryParams("songYear"));

                    Song songObj = new Song(songName, songArtist, songAlbum, songGenre, songYear); // put that data into new message object

                    userObject.songsList.add(songObj); // add that message object to the message array of that user object

                    response.redirect("/"); //redirect
                    return "";
                })
        );

        Spark.post(
                "/edit-song/:songId",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username"); // getting current session, asking who are you?
                    int id = Integer.valueOf(request.params("songId"));
                    User userObject = usersMap.get(name); // getting current user

                    String songName = request.queryParams("songName");
                    String songArtist = request.queryParams("songArtist");
                    String songAlbum = request.queryParams("songAlbum");
                    String songGenre = request.queryParams("songGenre");
                    int songYear = Integer.valueOf(request.queryParams("songYear"));

                    Song editedSong = new Song(songName, songArtist, songAlbum, songGenre, songYear);

                    if(editedSong.id != id) {
                        editedSong.id = id;
                    }

                    userObject.songsList.set(id, editedSong); // replace old object with new

                    String referrer = request.headers("Referrer");
                    response.redirect(referrer != null ? referrer : "/");
                    return "";
                })

        );

        Spark.post(
                "/delete-song/:songId",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username"); // getting current session, asking who are you?
                    int id = Integer.valueOf(request.params("songId"));
                    User userObject = usersMap.get(name); // getting current user

                    userObject.songsList.remove(id); // minus 1 to remove based on index

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