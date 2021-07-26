package com.example.collective.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collective.R;
import com.example.collective.Utils;
import com.example.collective.activities.event_detail;
import com.example.collective.models.Event;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class eventsAdapter extends RecyclerView.Adapter<eventsAdapter.ViewHolder>  {

    private List<Event> events;
    private List<Event> eventsFiltered;
    private Context context;

    public eventsAdapter(List<Event> events, Context context) {
        this.events = events;
        this.eventsFiltered = events;
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView Location;
        TextView eventName;
        TextView Description;
        TextView Organizer;
        ImageView eventImage;

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
            Location.setText(event.getLocation());
            eventName.setText(event.geteventName());
            Description.setText(event.getDescription());
            Organizer.setText(event.getOrganizer());
            Glide.with(context).load(event.getImage().getUrl()).centerCrop()
                    .transform(new RoundedCornersTransformation(30, 10)).into(eventImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, event_detail.class);
                        intent.putExtra("event", event);
                        context.startActivity(intent);
                    }
                }
            });

            Button register = itemView.findViewById(R.id.eventRegister);

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isCurrentUserRegistered = event.isUserRegistered(ParseUser.getCurrentUser());
                    if (isCurrentUserRegistered) {
                        try {
                            event.unregister(ParseUser.getCurrentUser());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        register.setText(R.string.not_registered);
                        register.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.button_black));
                        event.saveInBackground();

                    } else {
                        Log.e("TAG", "registering user" );
                        event.register(ParseUser.getCurrentUser());
                        register.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.unregister));
                        register.setText(R.string.unregister);
                        event.saveInBackground();



                    }
                }
            });


        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_eventpost, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = eventsFiltered.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventsFiltered.size();
    }



    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    eventsFiltered = events;
                } else {
                    ArrayList<Event> filteredList = new ArrayList<>();
                    for (Event item : events) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (item.geteventName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }

                    eventsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = eventsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                eventsFiltered = (ArrayList<Event>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
