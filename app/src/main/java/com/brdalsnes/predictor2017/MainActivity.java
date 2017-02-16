package com.brdalsnes.predictor2017;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends Activity {

    private DatabaseReference database;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private ArrayList<Event> eventList = new ArrayList<Event>();


    @InjectView(R.id.main_layout) RelativeLayout main_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        database = FirebaseDatabase.getInstance().getReference();

        Log.i("TAG", "Hi!");
        //Make list of all events
        database.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("TAG", "Finally, I made it!!");
                eventList.clear();
                for( DataSnapshot eventSnapshot: dataSnapshot.getChildren()) {
                    Event event = new Event();
                    String key = eventSnapshot.getKey();
                    event.setName(key);
                    event.setImage(eventSnapshot.child("image").getValue(String.class));
                    event.setStatement(eventSnapshot.child("state").getValue(String.class));
                    event.setCategory(eventSnapshot.child("cat").getValue(String.class));
                    event.setProbability(eventSnapshot.child("probability").getValue(Double.class));
                    event.setYes(eventSnapshot.child("yes").getValue(Long.class));
                    event.setNo(eventSnapshot.child("no").getValue(Long.class));
                    eventList.add(event);
                    Log.i("Length", eventList.size() + "");
                }

                sortProbability(); //Sort by highest probability

                main_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                        intent.putExtra("eventList", eventList);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("ERROR", "data");

            }
        });
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
