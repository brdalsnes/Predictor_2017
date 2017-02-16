package com.brdalsnes.predictor2017;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EventListActivity extends Activity {

    ListView list;
    EventAdapter eventAdapter;

    private ArrayList<Event> eventList = new ArrayList<>();
    private ArrayList<Event> categoryList = new ArrayList<>();


    @InjectView(R.id.categoriesSpinner) Spinner categoriesSpinner;
    @InjectView(R.id.change_activity_text) TextView change_activity_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_event_list);
        ButterKnife.inject(this);

        eventList = (ArrayList<Event>) getIntent().getSerializableExtra("eventList");
        categoryList = (ArrayList<Event>)eventList.clone();

        //Dropdown customization
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_arrays, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        categoriesSpinner.setAdapter(adapter);

        //Handle spinner for category choice
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        updateCategory("All");
                        break;
                    case 1:
                        updateCategory("Politics");
                        break;
                    case 2:
                        updateCategory("Economics");
                        break;
                    case 3:
                        updateCategory("Sports");
                        break;
                    case 4:
                        updateCategory("Entertainment");
                        break;
                    case 5:
                        updateCategory("ST");
                        break;
                }
                onResume();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Go to events
        change_activity_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventListActivity.this, DisplayActivity.class);
                intent.putExtra("eventList", eventList);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Create list
        list = (ListView) findViewById(R.id.listView);
        eventAdapter = new EventAdapter(EventListActivity.this, categoryList);
        list.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();
    }

    public void updateCategory(String category){
        categoryList.clear();
        if(category.equals("All")){
            categoryList = (ArrayList<Event>)eventList.clone();
            return;
        }
        for(int i = 0; i < eventList.size(); i++){
            if(eventList.get(i).getCategory().equals(category)){
                categoryList.add(eventList.get(i));
            }
        }
    }
}
