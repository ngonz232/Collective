package com.example.collective.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collective.EndlessRecyclerViewScrollListener;
import com.example.collective.Utils;
import com.example.collective.activities.LoginActivity;
import com.example.collective.adapters.EventsProfileAdapter;
import com.example.collective.databinding.FragmentProfileFragmentBinding;
import com.example.collective.models.Event;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    // Declaring variables for our ProfileFragment view
    private static final String TAG = ProfileFragment.class.getName();
    private FragmentProfileFragmentBinding binding;
    private ParseUser user;
    private List<Event> allEvents;
    private EventsProfileAdapter adapter;
    private File photoFile;

    // Inflating our view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentProfileFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    // Creating the view with all the data
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = ParseUser.getCurrentUser();
        allEvents= new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        adapter = new EventsProfileAdapter(allEvents,getContext());
        Button Logout = binding.btnLogout;
        binding.rvEvents.setLayoutManager(layoutManager);
        binding.rvEvents.setAdapter(adapter);

        queryEventsAuthor(0);
        queryEventsRegistered(0);

        // ScrollListener for user to be able to see all events endlessly
        binding.rvEvents.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryEventsAuthor(allEvents.size());
                queryEventsRegistered(allEvents.size());
            }
        });

        // OnClickListener for method launching the camera for the user to upload a profile picture
        binding.ivProfileImage.setOnClickListener(v -> launchCamera());
        ParseFile photo = user.getParseFile("profilePicture");
        if (photo != null) {
            Glide.with(this).load(photo.getUrl()).circleCrop().into(binding.ivProfileImage);
        }
        // Binding our data
        binding.Username.setText(user.getUsername());
        binding.fullNameProfile.setText(ParseUser.getCurrentUser().getString("fullName"));
        // RefreshListener allowing the user to swipe to load new events
        binding.profileSwipe.setOnRefreshListener(() -> {
            // Clearing current events and fetching latest from the database
            allEvents.clear();
            /* Two separate queries in case the user is only one of the two criterias
             (Registered/Author) but not both
             */
            queryEventsAuthor(0);
            queryEventsRegistered(0);
            binding.profileSwipe.setRefreshing(false);
        });
        // Sets the color for our refresh loading symbol
        binding.profileSwipe.setColorSchemeResources(android.R.color.holo_blue_bright);

        // OnClickListener for the Logout button clearing out the user and fragments
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();

                Intent i = new Intent(getContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

    }

    // Launching camera intent for the user to take a profile picture
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Getting the Uri for the image
        photoFile = getPhotoFileUri();

        // Wrapping the Uri into a file
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // Start the intent if we have an app package to handle it
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (photoFile == null) {
                Toast.makeText(getContext(), "There is no image!", Toast.LENGTH_SHORT).show();
                return;
            }
            // By this point we have the camera photo on disk
            // Load the taken image into the Profile ImageView
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            binding.ivProfileImage.setImageBitmap(takenImage);
            // Load the image as the new user profile picture in Parse
            user.put("photo", new ParseFile(photoFile));
            // Persist the changes to user in Parse
            user.saveInBackground();
        } else {

        }
    }

    // Returns the File for a photo stored on the disk given the filename photo.jpg
    private File getPhotoFileUri() {
        // Gets a safe storage directory for the photos
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Creates the storage directory for the photo if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + "photo.jpg");
    }

    // Querying our database for all events in which the current user matches the "Author" column
    public void queryEventsAuthor(int skip) {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.setSkip(skip);
        query.setLimit(20);
        query.whereEqualTo(Event.KEY_AUTHOR, ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.findInBackground((posts, e) -> {

            if (e != null) {
                Log.e(TAG, "Issue with getting events", e);
                return;
            }
            allEvents.addAll(posts);
            adapter.notifyDataSetChanged();
        });
    }

    // Querying our database for all events in which the current user matches the "registered" column
    public void queryEventsRegistered(int skip) {

        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.setSkip(skip);
        query.setLimit(20);
        /* Querying where user is not the author to avoid duplicate events as we have already added
        events where the user is the author in the prior query. This only returns events the user
        is registered for
         */
        query.whereNotEqualTo(Event.KEY_AUTHOR, ParseUser.getCurrentUser());
        query.whereContains(Event.KEY_REGISTERED_USERS, String.valueOf(ParseUser.getCurrentUser()));
        query.addDescendingOrder("createdAt");
        query.findInBackground((posts, e) -> {

            if (e != null) {
                Log.e(TAG, "Issue with getting events", e);
                return;
            }
            allEvents.addAll(posts);
            adapter.notifyDataSetChanged();
        });
    }
}
