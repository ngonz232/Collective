package com.example.collective.activities;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.collective.EndlessRecyclerViewScrollListener;
import com.example.collective.R;
import com.example.collective.Utils;
import com.example.collective.adapters.eventsAdapter;
import com.example.collective.databinding.ActivityMainBinding;
import com.example.collective.models.Event;
import com.parse.ParseQuery;

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
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        queryEvents(0);

        binding.swipeContainer.setOnRefreshListener(() -> {
            allEvents.clear();
            queryEvents(0);
            binding.swipeContainer.setRefreshing(false);
        });
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright);

        binding.rvEvents.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryEvents(allEvents.size());
            }

        });

    }

    public void queryEvents(int skip) {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.addDescendingOrder("createdAt");
        query.setSkip(skip);
        query.setLimit(20);
        query.findInBackground((events, e) -> {

            if (e != null) {
                Log.e(TAG, "Issue with getting events", e);
                return;
            }

            // Save received events to list and notify adapter of new data
            allEvents.addAll(events);
            adapter.notifyDataSetChanged();

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        ImageView icon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        ImageView icon2 = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        icon.setColorFilter(Color.WHITE);
        icon2.setColorFilter(Color.WHITE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if(allEvents.contains(query)){
                    adapter.getFilter().filter(query);
                } else {
                    Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

   }


