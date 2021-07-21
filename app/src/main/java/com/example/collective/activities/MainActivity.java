package com.example.collective.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.collective.adapters.eventsAdapter;
import com.example.collective.databinding.ActivityMainBinding;
import com.example.collective.models.Event;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
private static final String TAG = "MainActivity";
private eventsAdapter adapter;

private List<Event> allEvents;

private ActivityMainBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView rvEvents = binding.rvEvents;

        allEvents = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new eventsAdapter(allEvents, this);
        rvEvents.setLayoutManager(layoutManager);
        rvEvents.setAdapter(adapter);
        queryEvents();

    }

    public void queryEvents() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground((events, e) -> {

            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }

            // Save received posts to list and notify adapter of new data
            allEvents.addAll(events);
            adapter.notifyDataSetChanged();
        });
    }


}