package com.example.collective.adapters;


import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collective.R;
import com.example.collective.fragments.EventDetailedFragment;
import com.example.collective.models.Event;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

// This is a second adapter since we are using a different event itemView for the profile page
public class EventsProfileAdapter  extends RecyclerView.Adapter<EventsProfileAdapter.ViewHolder>  {

    // Declaring variables used
    private List<Event> events;
    private Context context;

    // Constructor for the adapter
    public EventsProfileAdapter(List<Event> events, Context context) {

        this.events = events;
        this.context = context;
    }

    // Our ViewHolder class
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        ImageView eventImage;
        View itemView;

        // Our ViewHolder only containing the image and event name this time
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            eventName = itemView.findViewById(R.id.eventName);
            eventImage = itemView.findViewById(R.id.eventImage);

        }


        public void bind(Event event) {
            // Binding our data for each event
            eventName.setText(event.geteventName());
            Glide.with(context).load(event.getImage().getUrl()).centerCrop()
                    .transform(new RoundedCornersTransformation(30, 10)).into(eventImage);

            // OnClickListener for entire itemView taking us to the "Event Detailed Page"
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
        }
    }

    // Creating and inflating the ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_eventprofile, parent, false);
        return new ViewHolder(v);
    }

    // Binding the event data at each position in the viewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    // Default ViewHolder method returning the size of the events list
    @Override
    public int getItemCount() {
        return events.size();
    }

}
