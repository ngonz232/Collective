package com.example.collective.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.collective.R;
import com.example.collective.databinding.FragmentEventDetailedBinding;
import com.example.collective.models.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class EventDetailedFragment extends Fragment implements OnMapReadyCallback {

    // Declaring our variables for the map, event model, and binding
    private FragmentEventDetailedBinding binding;
    private Event event;
    private GoogleMap mMap;

    // Creating our view and setting its content
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            event = bundle.getParcelable("event");
        }

        // Setting all of our event's data
        binding.eventNameDetailed.setText(event.geteventName());
        binding.organizerDetailed.setText(event.getOrganizer());
        binding.addressDetailed.setText(event.getLocation());
        binding.dateDetailed.setText(event.getDate());
        binding.numVolunteersDetailed.setText(String.valueOf(event.getnumVolunteers()));
        binding.descriptionDetailed.setText(event.getDescription());
        if ((event.getdesiredSkills() != null)) {
                binding.desiredSkillsDetailed.setText(event.getdesiredSkills());
        }
        Glide.with(getContext()).load(event.getImage().getUrl()).centerCrop()
                .transform(new RoundedCornersTransformation(30, 10)).into(binding.eventImageDetailed);

        // Initializing the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

    }

    // Inflating our view with all of its binded content
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventDetailedBinding.inflate(inflater);
        return binding.getRoot();
    }


    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Initializing the map variable and enabling all user controls/gestures
        mMap = googleMap;
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        // Adding the Event to the map with a try/catch in case location is invalid
        try {
            addEventToMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Our methods to add the event to the map with a marker
    public void addEventToMap() throws IOException {
        // Initializing Geocoder to convert the event's address to a GeoPoint
        Geocoder gc = new Geocoder(getContext());

        // Initializing a list with the address of our event
        List<Address> geoResults = gc.getFromLocationName(event.getLocation(), 1);

        if (geoResults.size()>0) {
            // Getting the address for our event
            Address address = geoResults.get(0);
            // Creating a GeoPoint using the address's LatLng
            LatLng eventLocation = new LatLng(address.getLatitude(), address.getLongitude());
            // Adding a marker with the newly created LatLng "eventLocation" and the event's name
            mMap.addMarker(new MarkerOptions().position(eventLocation).title(event.geteventName()));
            // Moving and animating the map's camera view to that location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(eventLocation));
            mMap.animateCamera( CameraUpdateFactory.zoomTo( 10.0f ) );
        }
    }


}

