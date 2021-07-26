package com.example.collective.models;

import android.location.Location;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

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
    public static final String KEY_DESIRED_SKILLS = "desiredSkills";
    public static final String KEY_REGISTERED_USERS = "registeredUsers";

    public Event() {
    }

    public Event(ParseUser user, String Description,
                 ParseFile image, Integer numVolunteers, String Date, String Organizer, String Location,
                 String author, String eventName, String desiredSkills, JSONArray registeredUsers) {
        setUser(user);
        setDescription(Description);
        setImage(image);
        setAuthor(author);
        seteventName(eventName);
        setDate(Date);
        setnumVolunteers(numVolunteers);
        setOrganizer(Organizer);
        setLocation(Location);
        setdesiredSkills(desiredSkills);

    }

    public void unregister(ParseUser currentUser) throws JSONException {
        JSONArray registeredUsers = registeredUsers();

        if(registeredUsers == null) {
           registeredUsers = new JSONArray();
        }

        for (int i = 0; i < registeredUsers.length(); i++) {
            JSONObject userPointer = registeredUsers.getJSONObject(i);
            if (userPointer.getString("objectId").equals(currentUser.getObjectId())) {
                registeredUsers.remove(i);
                setregisteredUsers(registeredUsers);
            }
        }
    }

    public void register(ParseUser currentUser) {
        add(KEY_REGISTERED_USERS, currentUser);
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

    public Integer getnumVolunteers() {
        return  getInt(KEY_NUM_VOLUNTEERS);
    }

    public JSONArray registeredUsers() { return getJSONArray(KEY_REGISTERED_USERS); }
    public void setregisteredUsers(JSONArray registeredUsers) { put(KEY_REGISTERED_USERS, registeredUsers); }

    public boolean isUserRegistered(ParseUser user) {
        if (registeredUsers() != null) {
            JSONArray usersRegistered = registeredUsers();

            for (int i = 0; i < usersRegistered.length(); i++) {
                JSONObject userPointer = null;
                try {
                    userPointer = usersRegistered.getJSONObject(i);
                    if (userPointer.getString("objectId").equals(user.getObjectId())) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
        public void setnumVolunteers(int numVolunteers) {
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

    public String getdesiredSkills() {
        return getString(KEY_DESIRED_SKILLS);
    }

    public void setdesiredSkills(String desiredSkills) {
        put(KEY_DESIRED_SKILLS, desiredSkills);
    }

}



