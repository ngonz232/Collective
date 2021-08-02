package com.example.collective.adapters;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collective.R;
import com.example.collective.fragments.EventDetailedFragment;
import com.example.collective.models.Event;
import com.parse.ParseUser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder>  {

    // Declaring our variables used
    public static final String TAG = EventsAdapter.class.getName();
    private List<Event> events;
    private List<Event> eventsFiltered;
    private Context context;

    // Our constructor for the adapter taking in the context and list of events
    public EventsAdapter(List<Event> events, Context context) {
        this.events = events;
        this.eventsFiltered = events;
        this.context = context;
    }

    // Main ViewHolder Class
    class ViewHolder extends RecyclerView.ViewHolder {

        // Declaring our variables
        View itemView;
        TextView Location;
        TextView eventName;
        TextView Description;
        TextView Organizer;
        ImageView eventImage;

        // ViewHolder containing each item which our view will contain
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            Location = itemView.findViewById(R.id.Location);
            eventName = itemView.findViewById(R.id.eventName);
            Description = itemView.findViewById(R.id.Description);
            Organizer = itemView.findViewById(R.id.Organizer);
            eventImage = itemView.findViewById(R.id.eventImage);

        }

        public void bind(Event event) {
            // Our bind method sets the value for each item in our view obtained from an event
            Location.setText(event.getLocation());
            eventName.setText(event.geteventName());
            Description.setText(event.getDescription());
            Organizer.setText(event.getOrganizer());
            Glide.with(context).load(event.getImage().getUrl()).centerCrop()
                    .transform(new RoundedCornersTransformation(30, 10)).into(eventImage);

            /* This OnClickListener makes the whole event item clickable and takes us to the event
            detailed view
             */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventDetailedFragment fr = new EventDetailedFragment();
                    Bundle args = new Bundle();
                    fr.setArguments(args);
                    FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, fr);
                    fragmentTransaction.addToBackStack("DetailedActivity").commit();
                    args.putParcelable("event", event);
                }
            });

            // Declares and initializes register button
            Button register = itemView.findViewById(R.id.eventRegister);

            // Sets OnClickListener for the button
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* This calls a boolean function from the events model and passes it the
                    current logged in user that clicked on the event
                     */
                    boolean isCurrentUserRegistered = event.isUserRegistered(ParseUser
                            .getCurrentUser());

                    // Checks if the function returns true/ user is registered
                    if (isCurrentUserRegistered) {
                        // Calls unregister method from event model and passes it the current user
                        // Try/catch to handle exceptions
                        try {
                            event.unregister(ParseUser.getCurrentUser());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        /* Sets the button view back its regular layout color and "Register!" text
                        instead of "Unregister!" given that we have unregistered the user above
                         */
                        register.setText(R.string.not_registered);
                        register.setBackgroundTintList(ContextCompat
                                .getColorStateList(context, R.color.button_black));
                        /* Persists changes to database by saving event model with registration
                         changes */
                        event.saveInBackground();

                    /* If the function returns false/ user not registered, we call the register
                    function from the events model passing it the current user
                     */
                    } else {
                        Log.e("TAG", "registering user");
                        event.register(ParseUser.getCurrentUser());
                        // Sets button layout to the "Unregister!" view once the user is registered
                        register.setBackgroundTintList(ContextCompat
                                .getColorStateList(context, R.color.unregister));
                        register.setText(R.string.unregister);
                          /* Persists changes to database by saving event model with registration
                         changes */
                        event.saveInBackground();


                    }
                }
            });

        }
    }

    // Inflating our view
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_eventpost, parent, false);
        return new ViewHolder(v);
    }

    // Binding the event at each position with all the data
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = eventsFiltered.get(position);
        holder.bind(event);
    }

    // Default ViewHolder method to return the amount of events in the list
    @Override
    public int getItemCount() {
        return eventsFiltered.size();
    }

    // Filter method for our search bar
    public Filter getFilter() {
        return new Filter() {
            // Gets the user inputted text from the search field into a string
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                /* If the user has not inputted any text to filter then the eventsFiltered array is
                equal to the standard events array list
                 */
                if (charString.isEmpty()) {
                    eventsFiltered = events;
                } else {
                    /* If the user has entered a search, initialize a new ArrayList to store the
                    filtered results
                     */
                    ArrayList<Event> filteredList = new ArrayList<>();
                    // Loop over each event in the original database list and compare to our search
                    for (Event item : events) {

                        // Name match condition based on what the user entered in the search field
                        if (item.geteventName().toLowerCase().contains(charString.toLowerCase())) {
                            // If the the match is found the item is added to our new filteredList
                            filteredList.add(item);
                        }
                    }
                    // The list of events shown will now be equal only to our filtered results
                    eventsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = eventsFiltered;
                return filterResults;
            }

            // Method to publish the results and notify the adapter that the shown dataset changed
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                eventsFiltered = (ArrayList<Event>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }


}
