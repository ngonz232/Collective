package com.example.collective.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Event")
public class Event extends ParseObject {

    public static final String KEY_NUM_VOLUNTEERS = "numVolunteers";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_DATE = "Date";
    public static final String KEY_ORGANIZER = "Organizer";
    public static final String KEY_LOCATION = "Location";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_EVENTNAME = "eventName";

    public Event() {
    }

    public Event(ParseUser user, String Description,
                 ParseFile image, String numVolunteers, String Date, String Organizer, String Location,
                 String author, String eventName) {
        setUser(user);
        setDescription(Description);
        setImage(image);
        setAuthor(author);
        seteventName(eventName);
        setDate(Date);
        setnumVolunteers(numVolunteers);
        setOrganizer(Organizer);
        setLocation(Location);
    }



    public ParseUser getUser() {
        return getParseUser(KEY_AUTHOR);
    }

    public void setUser(ParseUser user) {
        put(KEY_AUTHOR, user);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public String getAuthor() {
        return getString(KEY_AUTHOR);
    }

    public void setAuthor(String author) {
        put(KEY_AUTHOR, author);
    }

    public String getnumVolunteers() {
        return getString(KEY_NUM_VOLUNTEERS);
    }

    public void setnumVolunteers(String numVolunteers) {
        put(KEY_NUM_VOLUNTEERS, numVolunteers);
    }

    public String getDate() {
        return getString(KEY_DATE);
    }

    public void setDate(String Date) {
        put(KEY_DATE, Date);
    }

    public String getOrganizer() {
        return getString(KEY_ORGANIZER);
    }

    public void setOrganizer(String Organizer) {
        put(KEY_ORGANIZER, Organizer);
    }

    public String getLocation() {
        return getString(KEY_LOCATION);
    }

    public void setLocation(String Location) {
        put(KEY_LOCATION, Location);
    }

    public String geteventName() {
        return getString(KEY_EVENTNAME);
    }

    public void seteventName(String eventName) {
        put(KEY_EVENTNAME, eventName);
    }

}



