package com.mtullier.travelapp_v2.ui.plan;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.mtullier.travelapp_v2.R;

public class PlanFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        //creates the button that adds an event
        Button btn = view.findViewById(R.id.event_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCalendarEvent();
            }
        });

        return view;
    }

    //opens the intent to create an event in the calendar of the user's choice
    private void addCalendarEvent() {
        Intent i = new Intent(Intent.ACTION_EDIT);
        i.setType("vnd.android.cursor.item/event");
        i.putExtra("allDay", true);
        i.putExtra(CalendarContract.Events.TITLE, "Your Event");
        startActivity(i);
    }
}