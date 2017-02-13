package com.brdalsnes.predictor2017;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends Activity {

    private DatabaseReference database;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;

    private ArrayList<Event> eventList = new ArrayList<Event>();
    private String category;

    final long ONE_MEGABYTE = 1024 * 1024;
    final StorageReference storageReference = storage.getReferenceFromUrl("gs://predictor2017-b6577.appspot.com");

    @InjectView(R.id.statement1) TextView statement1;
    @InjectView(R.id.image1) ImageView image1;
    @InjectView(R.id.statement2) TextView statement2;
    @InjectView(R.id.image2) ImageView image2;
    @InjectView(R.id.dunno) TextView dunno;
    @InjectView(R.id.event1) LinearLayout layout1;
    @InjectView(R.id.event2) LinearLayout layout2;
    @InjectView(R.id.categoriesSpinner) Spinner categoriesSpinner;
    @InjectView(R.id.change_activity_text) TextView change_activity_text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        database = FirebaseDatabase.getInstance().getReference();

        //Handle spinner for category choice
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
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

        //Go to predictions
        change_activity_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EventListActivity.class);
                intent.putExtra("eventList", eventList);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("TAG", "Hi!");
        //Make list of all events
        database.child("Events").addValueEventListener(new ValueEventListener() {
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
                //Pick two random events
                int[] indexes = getTwoRandomIndexes(eventList.size());
                final Event event1 = eventList.get(indexes[0]);
                final Event event2 = eventList.get(indexes[1]);

                statement1.setText(event1.getStatement());
                statement2.setText(event2.getStatement());

                StorageReference image1Ref = storageReference.child(event1.getImage() + ".jpg");
                StorageReference image2Ref = storageReference.child(event2.getImage() + ".jpg");

                //Display
                //Image1
                image1Ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        image1.setMinimumHeight(dm.heightPixels);
                        image1.setMinimumWidth(dm.widthPixels);
                        image1.setImageBitmap(bm);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
                //Image2
                image2Ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        image2.setMinimumHeight(dm.heightPixels);
                        image2.setMinimumWidth(dm.widthPixels);
                        image2.setImageBitmap(bm);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
                layout1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleClick(event1, event2);
                    }
                });
                layout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleClick(event2, event1);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("ERROR", "data");

            }
        });

        //Buttons
        dunno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });
    }

    public int[] getTwoRandomIndexes(int length){
        int[] indexes = new int[2];
        Random randomizer = new Random();

        indexes[0] = randomizer.nextInt(length);
        indexes[1] = indexes[0];

        //Ensures indexes are different
        while (indexes[1] == indexes[0]){
            indexes[1] = randomizer.nextInt(length);
        }
        return indexes;
    }

    public void handleClick(final Event chosenEvent, final Event otherEvent){
        //Chosen event
        //Calculate new probability
        double probability = calculateProbability(chosenEvent.getYes() + 1, chosenEvent.getNo());
        //Update
        database.child("Events").child(chosenEvent.getName()).child("yes").setValue(chosenEvent.getYes() + 1);
        database.child("Events").child(chosenEvent.getName()).child("probability").setValue(probability);

        //Event not chosen
        //Calculate
        probability = calculateProbability(otherEvent.getYes(), otherEvent.getNo() + 1);
        //Update
        database.child("Events").child(otherEvent.getName()).child("no").setValue(otherEvent.getNo() + 1);
        database.child("Events").child(otherEvent.getName()).child("probability").setValue(probability);
    }

    public double calculateProbability(Long yes, Long no){
        final double a = 0.2; //Formula constant, higher for more price change

        double b = a*(yes + no) + 64;
        return (Math.exp(yes/b)/(Math.exp(yes/b) + Math.exp(no/b))) * 100; //Magic formula, LMSR
    }
}
