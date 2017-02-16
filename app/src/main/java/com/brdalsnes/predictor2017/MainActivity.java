package com.brdalsnes.predictor2017;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private ArrayList<Event> eventList = new ArrayList<Event>();


    @InjectView(R.id.main_layout) RelativeLayout main_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        //Authentification
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out

                }
            }
        };
        auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Make list of all events
        database.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
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
