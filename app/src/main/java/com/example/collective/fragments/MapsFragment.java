package com.example.collective.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.collective.R;
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

public class MapsFragment extends Fragment {

    // Declaring our variables
    private static final String TAG = MapsFragment.class.getName();
    private GoogleMap mMap;
    private List<Event> allEvents;

    // Implementing the Google Maps
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            // Initializing our map and enabling all user/gesture controls
            mMap = googleMap;
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);

            queryEvents();

            // Default Camera View for Map
            LatLng miamiView = new LatLng(25.76, -80.19);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(miamiView));
            mMap.animateCamera( CameraUpdateFactory.zoomTo( 10.0f ) );
        }
    };

    // Inflating the map layout
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    // Adding all map data to the view
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
            super.onCreate(savedInstanceState);
            allEvents = new ArrayList<>();

        }
    }

    // Querying our database to get the events
    public void queryEvents() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.findInBackground((events, e) -> {
            query.addDescendingOrder("createdAt");
            if (e != null) {
                Log.e(TAG, "Issue with getting events", e);
                return;
            }

            // Save received events to list
            allEvents.addAll(events);

            /* Try/catch for our call to the method that renders the events on the map in case
            the address is invalid
             */
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

    public void addEventsToMap() throws IOException {
        // Geocoder to convert the string address to a GeoLocation
        Geocoder gc = new Geocoder(getContext());
        // For loop iterating through each event's address
        for (Event item : allEvents) {
            // Storing the address for each event into the list
            List<Address> geoResults = gc.getFromLocationName(item.getLocation(), 1);

            if (geoResults.size()>0) {
                // Getting the address from the list
                Address address = geoResults.get(0);
                // Converting it into a GeoLocation
                LatLng eventLocation = new LatLng(address.getLatitude(), address.getLongitude());
                /* Rendering the marker for the GeoLocation on the map with the event's name as the
                title
                 */
                mMap.addMarker(new MarkerOptions().position(eventLocation).title(item.geteventName()));

            }
        }
    }

}



