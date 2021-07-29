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
import com.example.collective.adapters.EventsAdapter;
import com.example.collective.databinding.FragmentProfileActivityBinding;
import com.example.collective.models.Event;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends Fragment {

    private static final String TAG = ProfileActivity.class.getName();
    private FragmentProfileActivityBinding binding;
    private ParseUser user;
    private List<Event> allEvents;
    private EventsAdapter adapter;
    private File photoFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentProfileActivityBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = ParseUser.getCurrentUser();
        allEvents= new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        adapter = new EventsAdapter(allEvents,getContext());
        Button Logout = binding.btnLogout;
        binding.rvEvents.setLayoutManager(layoutManager);
        binding.rvEvents.setAdapter(adapter);

        queryPosts(0);

        binding.rvEvents.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(allEvents.size());
            }
        });

        binding.ivProfileImage.setOnClickListener(v -> launchCamera());
        ParseFile photo = user.getParseFile("profilePicture");
        if (photo != null) {
            Glide.with(this).load(photo.getUrl()).circleCrop().into(binding.ivProfileImage);
        }
        binding.Username.setText(user.getUsername());
        binding.fullNameProfile.setText(ParseUser.getCurrentUser().getString("fullName"));
        binding.profileSwipe.setOnRefreshListener(() -> {
            allEvents.clear();
            queryPosts(0);
            binding.profileSwipe.setRefreshing(false);
        });
        binding.profileSwipe.setColorSchemeResources(android.R.color.holo_blue_bright);

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

    private void launchCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri();

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

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

            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            binding.ivProfileImage.setImageBitmap(takenImage);
            user.put("photo", new ParseFile(photoFile));
            user.saveInBackground();
        } else {

        }
    }

    private File getPhotoFileUri() {

        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + "photo.jpg");
    }

    public void queryPosts(int skip) {


        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.setSkip(skip);
        query.setLimit(20);
        query.whereEqualTo(Event.KEY_AUTHOR, ParseUser.getCurrentUser());
//        query.whereContains(Event.KEY_REGISTERED_USERS, String.valueOf(ParseUser.getCurrentUser()));
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
