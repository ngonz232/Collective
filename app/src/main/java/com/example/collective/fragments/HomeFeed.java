package com.example.collective.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collective.EndlessRecyclerViewScrollListener;
import com.example.collective.R;
import com.example.collective.adapters.EventsAdapter;
import com.example.collective.databinding.FragmentHomeFeedBinding;
import com.example.collective.models.Event;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeFeed extends Fragment {
    // Declaring our variables
    private static final String TAG = HomeFeed.class.getName();
    private EventsAdapter adapter;

    private List<Event> allEvents;
    private Paint p = new Paint();
    private FragmentHomeFeedBinding binding;


    RecyclerView rvEvents;

    // Inflating our view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentHomeFeedBinding.inflate(inflater);
        return binding.getRoot();

    }

    // Creating our view
    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enabling the toolbar widgets (Searchbar and filtering options)
        setHasOptionsMenu(true);
    }

    // Creating our view with the recyclerView and all the data
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvEvents = binding.rvEvents;
        allEvents = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new EventsAdapter(allEvents, getContext());
        rvEvents.setLayoutManager(layoutManager);
        rvEvents.setAdapter(adapter);
        // Intializing our toolbar
        Toolbar myToolbar = view.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        // Disabling the Toolbar title to leave space for our app's logo
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Changing the icon of the menu to be that of a filter icon
        Drawable d = getResources().getDrawable(R.drawable.ic_action_filtericon);
        myToolbar.setOverflowIcon(d);
        // Calling our querying to the database
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
        enableSwipe();
    }

    // Querying our database for list of events
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

    // Inflating our options menu and searchView
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        // Setting a maxWidth so it does not cover our logo
        searchView.setMaxWidth(650);
        // Setting icons and color
        ImageView icon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        ImageView icon2 = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        icon.setColorFilter(Color.WHITE);
        icon2.setColorFilter(Color.WHITE);
        // Textlistener on the SearchView for user input
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (allEvents.contains(query)) {
                    // Calls our filter method passing it the user input to find a match
                    adapter.getFilter().filter(query);
                } else {
                    Toast.makeText(getContext(), "No Match found", Toast.LENGTH_LONG).show();
                }
                return false;

            }

            // Method for when the user changes the text input
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return;
    }

    // Boolean in case the user selects a filter from the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        // Switch case statements calling the method for the filter selected
        switch (item.getItemId()){
            case R.id.filterRecent:
                filterRecent();
                return true;
            case R.id.filterNearest:
                filterNearest();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to remove item from the RecyclerView and from the database
    public void removeItem(int position) {
        Log.d(TAG, "Position: " + position);
        // Gets the position of the event swiped to delete
        Event toBeDeleted = allEvents.get(position);
        // Deletes event from Parse
        toBeDeleted.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Deleted parse object");
                } else {
                    Log.d(TAG, "Couldn't delete parse object");
                }
            }
        });
        // Removes the event from the recyclerView and notifies the adapter
        allEvents.remove(position);
        adapter.notifyItemRemoved(position);
    }

    // Enables swipe to delete on each itemView
    private void enableSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder
                    , RecyclerView.ViewHolder target) {
                return false;
            }

            // Calls the removeItem method on the event swiped passing the postion
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    RecyclerView rvEvents = binding.rvEvents;
                    if (direction == ItemTouchHelper.LEFT) {
                        removeItem(position);
                    }
                }
            }

            // This methods creates the background and UI for the swipe to delete
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Sets each itemView to float with the swiping action
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    // Creates the swipe to delete direction and background color
                    if (dX <= 0.0) {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float)
                                itemView.getTop(), (float) itemView.getRight(),
                                (float) itemView.getBottom());
                        c.drawRect(background, p);
                        // Sets the delete trashcan icon
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float)
                                itemView.getTop() + width, (float) itemView.getRight() - width
                                , (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }

                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvEvents);
    }

    // Method called by the menu option of filtering by recent first
    public void filterRecent() {
        // Clearing the currently loaded events
        allEvents.clear();
        // Querying our database for events sorted by recent date
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.addDescendingOrder("createdAt");
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

    // Method called by the menu option of filtering by nearest first
    public void filterNearest() {
        // Clearing the currently loaded events
        allEvents.clear();
        // Querying our database for events nearest to the user's location
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.whereNear("Location", getCurrentUserLocation());
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

    private ParseGeoPoint getCurrentUserLocation() {

        // Finding currentUser
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Otherwise, return the current user location
        return currentUser.getParseGeoPoint("Location");

    }

}





