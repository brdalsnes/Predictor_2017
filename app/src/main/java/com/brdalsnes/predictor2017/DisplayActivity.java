package com.brdalsnes.predictor2017;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DisplayActivity extends Activity {

    private DatabaseReference database;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<Event> eventList = new ArrayList<>();
    private ArrayList<Event> categoryList = new ArrayList<>();

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
        setContentView(R.layout.activity_display);
        ButterKnife.inject(this);

        eventList = (ArrayList<Event>) getIntent().getSerializableExtra("eventList");
        categoryList = (ArrayList<Event>)eventList.clone();

        database = FirebaseDatabase.getInstance().getReference();

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

        //Go to predictions
        change_activity_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayActivity.this, EventListActivity.class);
                intent.putExtra("eventList", eventList);
                startActivity(intent);
            }
        });

        //Two new events
        dunno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResume();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Pick two random events
        int[] indexes = getTwoRandomIndexes(categoryList.size());
        final Event event1 = categoryList.get(indexes[0]);
        final Event event2 = categoryList.get(indexes[1]);

        statement1.setText(event1.getStatement());
        statement2.setText(event2.getStatement());


        final long ONE_MEGABYTE = 1024 * 1024;
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://predictor2017-b6577.appspot.com");
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
        chosenEvent.addYes();
        //Calculate new probability
        double probability = calculateProbability(chosenEvent.getYes(), chosenEvent.getNo());
        //Update database
        database.child("Events").child(chosenEvent.getName()).child("yes").setValue(chosenEvent.getYes());
        database.child("Events").child(chosenEvent.getName()).child("probability").setValue(probability);

        //Event not chosen
        otherEvent.addNo();
        //Calculate
        probability = calculateProbability(otherEvent.getYes(), otherEvent.getNo());
        //Update
        database.child("Events").child(otherEvent.getName()).child("no").setValue(otherEvent.getNo());
        database.child("Events").child(otherEvent.getName()).child("probability").setValue(probability);

        onResume();
    }

    public double calculateProbability(Long yes, Long no){
        final double a = 0.2; //Formula constant, higher for more price change

        double b = a*(yes + no) + 64;
        return (Math.exp(yes/b)/(Math.exp(yes/b) + Math.exp(no/b))) * 100; //Magic formula, LMSR
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
