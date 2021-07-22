package com.example.collective.adapters;


import android.content.Context;
import android.location.Location;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collective.R;
import com.example.collective.databinding.ItemEventpostBinding;
import com.example.collective.models.Event;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class eventsAdapter extends RecyclerView.Adapter<eventsAdapter.ViewHolder> {

    private List<Event> events;
    private Context context;

    public eventsAdapter(List<Event> events, Context context) {
        this.events = events;
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView Location;
        TextView eventName;
        TextView Description;
        TextView Organizer;
        ImageView eventImage;
//        ItemEventpostBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            Location = itemView.findViewById(R.id.Location);
            eventName =  itemView.findViewById(R.id.eventName);
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
            .transform(new RoundedCornersTransformation(30,10)).into(eventImage);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_eventpost, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
