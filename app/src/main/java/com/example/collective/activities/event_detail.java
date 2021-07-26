package com.example.collective.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.collective.R;
import com.example.collective.databinding.ActivityEventDetailBinding;
import com.example.collective.models.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class event_detail extends AppCompatActivity implements OnMapReadyCallback  {
//public class event_detail extends AppCompatActivity  {
    private ActivityEventDetailBinding binding;
    private Event event;
    private GoogleMap mMap;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();
        event = getIntent().getParcelableExtra("event");

        binding.eventNameDetailed.setText(event.geteventName());
        binding.organizerDetailed.setText(event.getOrganizer());
        binding.addressDetailed.setText(event.getLocation());
        binding.dateDetailed.setText(event.getDate());
        binding.numVolunteersDetailed.setText(String.valueOf(event.getnumVolunteers()));
        if ((event.getdesiredSkills() != null)) {
            binding.desiredSkillsDetailed.setText(event.getdesiredSkills());
        }
        Glide.with(context).load(event.getImage().getUrl()).centerCrop()
                .transform(new RoundedCornersTransformation(30, 10)).into(binding.eventImageDetailed);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);


    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        try {
            addEventsToMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addEventsToMap() throws IOException {
        Geocoder gc = new Geocoder(this);

        List<Address> geoResults = gc.getFromLocationName(event.getLocation(), 1);

        if (geoResults.size()>0) {
            Address address = geoResults.get(0);
            LatLng eventLocation = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(eventLocation).title(event.geteventName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(eventLocation));
            mMap.animateCamera( CameraUpdateFactory.zoomTo( 10.0f ) );
        }
    }

}