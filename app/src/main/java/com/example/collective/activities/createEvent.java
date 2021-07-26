package com.example.collective.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.collective.R;
import com.example.collective.Utils;
import com.example.collective.databinding.ActivityCreateEventBinding;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;


public class createEvent extends AppCompatActivity {
    // Initializing binding
    private ActivityCreateEventBinding binding;
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
    private final static String TAG = "createEvent";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflating binding and setting content for binding
        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Binding buttons and Textviews
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
        createEvent.setOnClickListener(v -> saveEvent());
    }

    // Returns the File for a photo stored on the disk given the filename photo.jpg
    private File getPhotoFileUri() {
        // Gets a safe storage directory for the photos
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Creates the storage directory for the photo if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory");
        }
        // Returns the file
        return new File(mediaStorageDir.getPath() + File.separator + "photo.jpg");
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
                ImageView takenPhoto = binding.takenPhoto;
                takenPhoto.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to launch camera intent and store the taken picture
    private void launchCamera() {
        // Create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a reference variable to the file for future access
        photoFile = getPhotoFileUri();

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(this,
        "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // Checks if there is an app to handle the intent
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Starts the intent to take the photo
            startActivityForResult(intent, Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
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
        binding.takenPhoto.setImageDrawable(null);
        binding.takenPhoto.setImageDrawable(myDrawable);
    }

    /* Method to check if required fields are filled by the user
    Otherwise, it returns an error message to the user on the unfilled TextField
     */
    public  boolean checkValidation() {

        // Nested "if" statements checking each field and file before proceeding
        if (enterName.length() <= 0) {
            enterName.requestFocus();
            enterName.setError("Enter Event Name");
            return false;

        } else if (photoFile == null) {
            // Toast to notify the user no image has been selected
            Toast.makeText(this, "Please Take or Upload a Picture!", Toast.LENGTH_LONG).show();
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

            // Log message method if there is an error saving the posts
            Event.saveInBackground(e -> {

                if (e != null)
                    Log.e(TAG, "Error while saving post", e);
                return;
            });

            // If our method reaches this point, we use a Toast for the successful save
            Toast.makeText(this, "Event Listed!", Toast.LENGTH_LONG).show();

            // Calls for reset methods after the post is completed
            emptyTextFields();
            resetPhoto();

            // Intent to go back to the main feed after creating an event post
            Intent i = new Intent(createEvent.this, MainActivity.class);
            startActivity(i);
        }
    }
}

