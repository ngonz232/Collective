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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collective.R;
import com.example.collective.databinding.ActivityCreateEventBinding;
import com.example.collective.databinding.ActivityCreateEventBinding;
import com.example.collective.models.events;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.util.Set;

import static com.example.collective.databinding.ActivityCreateEventBinding.bind;
import static com.example.collective.databinding.ActivityCreateEventBinding.inflate;

public class createEvent extends AppCompatActivity {
    // Initializing binding
    private ActivityCreateEventBinding binding;
    // Creating variables for buttons and textviews
    Button createEvent;
    Button uploadBtn;
    Button takeBtn;
    EditText location;
    EditText enterName;
    EditText description;
    EditText enternumVolunteers;
    EditText enterDate;
    EditText enterOrganizerName;
    // Variable for image file
    private File photoFile;
    // TAG for error logs
    private final String TAG = "createEvent";
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
        enternumVolunteers = binding.enterNumVolunteers;
        enterDate = binding.enterDate;
        enterOrganizerName = binding.enterOrganizerName;

        // OnClickListener for camera action button
        takeBtn.setOnClickListener(v -> launchCamera());
        createEvent.setOnClickListener(v -> saveEvent());
    }

    private File getPhotoFileUri() {

        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + "photo.jpg");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ImageView takenPhoto = binding.takenPhoto;
                takenPhoto.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void launchCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri();

        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void emptyTextFields(){
        binding.enterName.setText("");
        binding.enterDate.setText("");
        binding.description.setText("");
        binding.enterOrganizerName.setText("");
        binding.location.setText("");
        binding.enterNumVolunteers.setText("");
    }

    private void resetPhoto(){
        photoFile.delete();
        Drawable myDrawable = getResources().getDrawable(R.drawable.ic_baseline_add_a_photo_24);
        binding.takenPhoto.setImageDrawable(null);
        binding.takenPhoto.setImageDrawable(myDrawable);
    }

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

        events.saveInBackground(e -> {

            if (e != null)
                Log.e(TAG, "Error while saving post", e);
            return;
        });
        Toast.makeText(this, "Event Listed!", Toast.LENGTH_LONG).show();
        emptyTextFields();
        resetPhoto();

    }
}

