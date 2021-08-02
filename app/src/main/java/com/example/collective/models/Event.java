package com.example.collective.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Declaring our Parse class
@ParseClassName("Event")
public class Event extends ParseObject {

    // Keys to our Parse columns
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


    // Empty constructor required for Parse
    public Event() {
    }

    // Constructor for our Event object model
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

    // Getters for our required object properties in Parse
    public ParseUser getUser() {
        return getParseUser(KEY_AUTHOR);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public String getAuthor() {
        return getString(KEY_AUTHOR);
    }

    public Integer getnumVolunteers() {
        return  getInt(KEY_NUM_VOLUNTEERS);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public String getdesiredSkills() {
        return getString(KEY_DESIRED_SKILLS);
    }

    public String geteventName() {
        return getString(KEY_EVENTNAME);
    }

    public String getOrganizer() {
        return getString(KEY_ORGANIZER);
    }

    public String getLocation() {
        return getString(KEY_LOCATION);
    }

    public String getDate() {
        return getString(KEY_DATE);
    }

    // Setters for our object properties in Parse

    public void setUser(ParseUser user) {
        put(KEY_AUTHOR, user);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public void setAuthor(String author) {
        put(KEY_AUTHOR, author);
    }

    public void setnumVolunteers(int numVolunteers) {
        put(KEY_NUM_VOLUNTEERS, numVolunteers);
    }

    public void setDate(String Date) {
        put(KEY_DATE, Date);
    }

    public void setOrganizer(String Organizer) {
        put(KEY_ORGANIZER, Organizer);
    }

    public void setLocation(String Location) {
        put(KEY_LOCATION, Location);
    }

    public void seteventName(String eventName) {
        put(KEY_EVENTNAME, eventName);
    }

    public void setdesiredSkills(String desiredSkills) {
        put(KEY_DESIRED_SKILLS, desiredSkills);
    }

    // Gets registeredUsers JSONArray from database
    public JSONArray registeredUsers() { return getJSONArray(KEY_REGISTERED_USERS); }

    // Setter for the registeredUsers JSONArray
    public void setregisteredUsers(JSONArray registeredUsers) { put(KEY_REGISTERED_USERS,
            registeredUsers); }

    // Boolean function to check if the user exists in the "registeredUsers" array of the event
    public boolean isUserRegistered(ParseUser user) {

        // Initializes the array to what we get from the Parse database
        if (registeredUsers() != null) {
            JSONArray usersRegistered = registeredUsers();

            // "For" loop iterates each item of the array
            for (int i = 0; i < usersRegistered.length(); i++) {

                // Initializing our userPointer
                JSONObject userPointer = null;

                // Try/Catch handling exceptions
                try {

                    /* Sets the userPointer to the one stored at the particular index of our
                    registeredUsers JSONArray
                     */
                    userPointer = usersRegistered.getJSONObject(i);

                    /* If any of the userPointers in the registeredUsers
                    JSONArray matches that of our current user passed to this function then
                    it returns true as the user is registered
                     */
                    if (userPointer.getString("objectId").equals(user.getObjectId())) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        // Else if the user is not in the array of registeredUsers return "false"
        return false;
    }

    /* Method to add the userPointer of the current user to the registeredUsers Array
   called in the EventsAdapter upon clicking the register button (if the user is not registered)
    */
    public void register(ParseUser currentUser) {
        add(KEY_REGISTERED_USERS, currentUser);
    }

    // Method called from the EventsAdapter to unregister user
    public void unregister(ParseUser currentUser) throws JSONException {

        // Initializes the array to what we get from the Parse database
        JSONArray registeredUsers = registeredUsers();

        // Creates new JSONArray if empty
        if(registeredUsers == null) {
            registeredUsers = new JSONArray();
        }

        /* For loop iterates entire JSONArray to remove the passed userPointer from the
       registeredUsers array
         */
        for (int i = 0; i < registeredUsers.length(); i++) {
            JSONObject userPointer = registeredUsers.getJSONObject(i);
            if (userPointer.getString("objectId").equals(currentUser.getObjectId())) {
                registeredUsers.remove(i);
                setregisteredUsers(registeredUsers);
            }
        }
    }

}



