package com.example.collective.adapters;


import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collective.R;

public class eventsAdapter extends RecyclerView.ViewHolder{
    EditText location;
    EditText enterName;
    EditText description;
    EditText enternumVolunteers;
    EditText enterDate;
    EditText enterOrganizerName;
    ImageView eventImage;

    public eventsAdapter(View itemView) {
        super(itemView);

    }
}
