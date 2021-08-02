package com.example.collective.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.collective.R;
import com.example.collective.Utils;
import com.example.collective.databinding.FragmentCreateEventBinding;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class CreateEvent extends Fragment {

    // Initializing binding
    private @NonNull
    FragmentCreateEventBinding binding;
    // Creating variables for buttons and TextViews
    Button createEvent;
    Button uploadBtn;
    Button takeBtn;
    EditText location;
    EditText enterName;
    EditText description;
    EditText enterNumVolunteers;
    EditText enterDate;
    EditText enterOrganizerName;
    // Variable for image file
    private File photoFile;
    // TAG for error logs
    private static final String TAG = CreateEvent.class.getName();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Creating the view with all of our items
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Binding buttons and TextViews
        createEvent = binding.createEvent;
        uploadBtn = binding.uploadBtn;
        takeBtn = binding.takeBtn;
        location = binding.location;
        enterName = binding.enterName;
        description = binding.description;
        enterNumVolunteers = binding.enterNumVolunteers;
        enterDate = binding.enterDate;
        enterOrganizerName = binding.enterOrganizerName;

        // OnClickListener for camera action button
        takeBtn.setOnClickListener(v -> launchCamera());
        // OnClickListener for the "Create Event" button
        createEvent.setOnClickListener(v -> saveEvent());
        // OnClickListener for the "Select Image" button
        uploadBtn.setOnClickListener(v -> imagePicker());
    }

    // Inflating our view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentCreateEventBinding.inflate(inflater);
        return binding.getRoot();

    }

    // This method is called when the camera intent activity is done
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // By this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // Load the taken image into a preview
                ImageView takenPhoto = binding.eventPhoto;
                takenPhoto.setImageBitmap(takenImage);
            }
            /* Else if condition in case the user opts to select an image from the gallery instead
            of using the camera
             */
        } else if (requestCode == Utils.RESULT_LOAD_IMAGE) {
            if (resultCode == RESULT_OK) {
                // Check that we have permission to access storage
                verifyStoragePermissions(getActivity());
                // Retrieve Uri of selected image
                Uri selectedImage = data.getData();
                // Creates our file path based on the user's cursor selection
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                // Binds and loads the user selected image into our image preview
                ImageView uploadedPhoto = binding.eventPhoto;
                uploadedPhoto.setImageURI(selectedImage);
                // Loads our newly selected image into the photoFile to be later stored in Parse
                photoFile = new File(picturePath);
            } else {
                // Notification toast for missing image
                Toast.makeText(getContext(), "Picture wasn't taken nor uploaded!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    // Method to launch camera intent and store the taken picture
    private void launchCamera() {
        // Create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a reference variable to the file for future access
        photoFile = getPhotoFileUri();

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(),
                "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // Checks if there is an app to handle the intent
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Starts the intent to take the photo
            startActivityForResult(intent, Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
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
        // Returns the file
        return new File(mediaStorageDir.getPath() + File.separator + "photo.jpg");
    }

    // Method to start an intent for the user to select an image from the internal gallary storage
    public void imagePicker() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, Utils.RESULT_LOAD_IMAGE);

        } catch (Exception exp) {
            Log.i("Error", exp.toString());
        }

    }


    /* Method to check if required fields are filled by the user
    Otherwise, it returns an error message to the user on the unfilled TextField
     */
    public boolean checkValidation() {

        // Nested "if" statements checking each field and file before proceeding
        if (enterName.length() <= 0) {
            enterName.requestFocus();
            enterName.setError("Enter Event Name");
            return false;

        } else if (photoFile == null) {
            // Toast to notify the user no image has been selected
            Toast.makeText(getContext(), "Please Take or Upload a Picture!", Toast.LENGTH_LONG).show();
            return false;

        } else if (enterNumVolunteers.length() <= 0) {
            enterNumVolunteers.requestFocus();
            enterNumVolunteers.setError("Enter Number of Volunteers Needed");
            return false;

        } else if (enterDate.length() <= 0) {
            enterDate.requestFocus();
            enterDate.setError("Enter Event Date");
            return false;

        } else if (enterOrganizerName.length() <= 0) {
            enterOrganizerName.requestFocus();
            enterOrganizerName.setError("Enter Organizer's Name");
            return false;

        } else if (location.length() <= 0) {
            location.requestFocus();
            location.setError("Enter Full Address");
            return false;

        } else if (description.length() <= 0) {
            description.requestFocus();
            description.setError("Enter Description");
            return false;

        } else {
            return true;
        }
    }

    // Saves all entered information into Parse and saves in background
    private void saveEvent() {
        /* "If" statement checks if our earlier field validation method returns true before saving
        content to Parse
         */
        if (checkValidation()) {

            // Intializes our Geocoder and its List to get GeoPoints from the user entered Address
            Geocoder gc = new Geocoder(getContext());
            List<Address> geoResults = null;

            // Adds the address to the array of geoResults
            try {
                geoResults = gc.getFromLocationName(location.getText().toString(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* If there is an address store then get the GeoLocation for that address and store it in
            the LatLng variable "locationGeoPoint"
             */
            if (geoResults.size() > 0) {
                Address address = geoResults.get(0);
                LatLng locationGeoPoint = new LatLng(address.getLatitude(), address.getLongitude());


                ParseObject Event = ParseObject.create("Event");
                Event.put("eventName", binding.enterName.getText().toString());
                Event.put("Date", binding.enterDate.getText().toString());
                Event.put("Description", binding.description.getText().toString());
                Event.put("Organizer", binding.enterOrganizerName.getText().toString());
                Event.put("Location", binding.location.getText().toString());
                Event.put("desiredSkills", binding.desiredSkills.getText().toString());
                Event.put("numVolunteers", Integer.parseInt(binding.enterNumVolunteers.getText().toString()));
                Event.put("author", ParseUser.getCurrentUser());
                Event.put("image", new ParseFile(photoFile));
                Event.put("LocationGeoPoint", locationGeoPoint);

                // Log message method if there is an error saving the posts
                Event.saveInBackground(e -> {
                    Log.e(TAG, "saveEvent4 ");

                    if (e != null)
                        Log.e(TAG, "Error while saving post", e);
                    return;
                });

                // If our method reaches this point, we use a Toast for the successful save
                Toast.makeText(getContext(), "Event Listed!", Toast.LENGTH_LONG).show();

                // Calls for reset methods after the post is completed
                emptyTextFields();
                resetPhoto();

                // Intent to go back to the main feed after creating an event post
                HomeFeed fr = new HomeFeed();
                FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, fr);

                // If the address does not have a valid GeoLocation show the user a prompt
            } else if (geoResults.size() <= 0){
                Toast.makeText(getContext(), "Invalid address!", Toast.LENGTH_LONG).show();

            }
        }
    }

        // This resets all text fields after a post is completed
        private void emptyTextFields() {
            binding.enterName.setText("");
            binding.enterDate.setText("");
            binding.description.setText("");
            binding.enterOrganizerName.setText("");
            binding.location.setText("");
            binding.enterNumVolunteers.setText("");
        }

        // Resets Photo after post is completed
        private void resetPhoto() {
            photoFile.delete();
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_baseline_add_a_photo_24);
            binding.eventPhoto.setImageDrawable(null);
            binding.eventPhoto.setImageDrawable(myDrawable);
        }

    }




