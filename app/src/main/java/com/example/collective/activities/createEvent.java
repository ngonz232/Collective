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
import com.example.collective.databinding.ActivityCreateEventBinding;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;

import static com.example.collective.databinding.ActivityCreateEventBinding.inflate;

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
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;


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
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
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
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
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

    // Saves all entered information into Parse and saves in background
    private void saveEvent() {
        ParseObject events = ParseObject.create("events");
        events.put("eventName", binding.enterName.getText().toString());
        events.put("Date", binding.enterDate.getText().toString());
        events.put("Description", binding.description.getText().toString());
        events.put("Organizer", binding.enterOrganizerName.getText().toString());
        events.put("Location", binding.location.getText().toString());
        events.put("numVolunteers", Integer.parseInt(binding.enterNumVolunteers.getText().toString()));
        events.put("author", ParseUser.getCurrentUser());
        events.put("image", new ParseFile(photoFile));

        // Log message method if there is an error saving the posts
        events.saveInBackground(e -> {

            if (e != null)
                Log.e(TAG, "Error while saving post", e);
            return;
        });

        // If our method reaches this point, we use a Toast for the successful save
        Toast.makeText(this, "Event Listed!", Toast.LENGTH_LONG).show();

        // Calls for reset methods after the post is completed
        emptyTextFields();
        resetPhoto();

    }
}

