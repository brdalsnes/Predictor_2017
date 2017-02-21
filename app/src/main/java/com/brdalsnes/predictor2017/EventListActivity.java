package com.brdalsnes.predictor2017;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EventListActivity extends Activity {

    ListView list;
    EventAdapter eventAdapter;

    private ArrayList<Event> eventList = new ArrayList<>();
    private ArrayList<Event> categoryList = new ArrayList<>();
    private ArrayList<Event> searchList = new ArrayList<>();

    @InjectView(R.id.categoriesSpinner) Spinner categoriesSpinner;
    @InjectView(R.id.change_activity_text) TextView change_activity_text;
    @InjectView(R.id.search_bar) EditText search_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_event_list);
        ButterKnife.inject(this);

        eventList = (ArrayList<Event>) getIntent().getSerializableExtra("eventList");
        categoryList = (ArrayList<Event>)eventList.clone();
        searchList = (ArrayList<Event>)eventList.clone();

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
                searchList = (ArrayList<Event>)categoryList.clone();
                onResume();
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //Search
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSearch(search_bar.getText().toString());
                onResume();
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        eventAdapter = new EventAdapter(EventListActivity.this, searchList);
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

    public void updateSearch(String search){
        searchList.clear();
        for(int i = 0; i < categoryList.size(); i++){
            if(categoryList.get(i).getStatement().toLowerCase().contains(search.toLowerCase())){
                searchList.add(categoryList.get(i));
            }
        }
    }

    public void sortProbability(){
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                if(lhs.getProbability() == rhs.getProbability()){
                    return 0;
                }
                return lhs.getProbability() > rhs.getProbability() ? -1 : 1;
            }
        });
    }
}
