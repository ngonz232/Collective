package com.example.collective;

import android.app.Application;

import com.example.collective.models.Event;
import com.parse.Parse;
import com.parse.ParseObject;


// Parse client connection file
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Registering our Event model with parse
        ParseObject.registerSubclass(Event.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
    }
}