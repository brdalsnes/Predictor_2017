package com.brdalsnes.predictor2017;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.InjectView;

public class EventListActivity extends Activity {

    ListView list;
    EventAdapter eventAdapter;

    private ArrayList<Event> eventList = new ArrayList<>();
    private String category;


    @InjectView(R.id.categoriesSpinner) Spinner categoriesSpinner;
    @InjectView(R.id.change_activity_text) TextView change_activity_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_event_list);

        Bundle extras = getIntent().getExtras();
        eventList = (ArrayList<Event>) extras.getSerializable("eventList");


        //Handle spinner for category choice
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        category = "All";
                        break;
                    case 1:
                        category = "Politics";
                        break;
                    case 2:
                        category = "Economics";
                        break;
                    case 3:
                        category = "Sports";
                        break;
                    case 4:
                        category = "Entertainment";
                        break;
                    case 5:
                        category = "ST";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Go to events
        change_activity_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Create list
        list = (ListView) findViewById(R.id.listView);
        eventAdapter = new EventAdapter(EventListActivity.this, eventList);
        list.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();

    }
}
