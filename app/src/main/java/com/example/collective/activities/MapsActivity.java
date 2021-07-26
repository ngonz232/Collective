package com.example.collective.activities;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.example.collective.R;
import com.example.collective.databinding.ActivityMapsBinding;
import com.example.collective.models.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity.java";
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private List<Event> allEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allEvents = new ArrayList<>();
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void queryEvents() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.findInBackground((events, e) -> {
            query.addDescendingOrder("createdAt");
            query.setLimit(20);
            if (e != null) {
                Log.e(TAG, "Issue with getting events", e);
                return;
            }

            // Save received events to list
            allEvents.addAll(events);

            try {
                addEventsToMap();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we add a marker with its title for every Event within the "Event" class in our database
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        queryEvents();

//         Add a marker in Sydney and move the camera
        LatLng miamiView = new LatLng(25.76, -80.19);
        mMap.addMarker(new MarkerOptions()
                .position(miamiView)
                .title("Miami"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(miamiView));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 10.0f ) );
    }

    public void addEventsToMap() throws IOException {
        Geocoder gc = new Geocoder(this);
        for (Event item : allEvents) {
            List<Address> geoResults = gc.getFromLocationName(item.getLocation(), 1);

            if (geoResults.size()>0) {
                Address address = geoResults.get(0);
                LatLng eventLocation = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(eventLocation).title(item.geteventName()));
        }
    }

}}