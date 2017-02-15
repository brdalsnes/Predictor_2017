package com.brdalsnes.predictor2017;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * Created by Bjornar on 05/08/2015.
 */
public class EventAdapter extends BaseAdapter implements View.OnClickListener {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Activity activity;
    private ArrayList<Event> eventList = new ArrayList<>();
    private static LayoutInflater inflater = null;
    public Event tempEvent = null;


    public EventAdapter(Activity activity, ArrayList<Event> eventList){
        this.activity = activity;
        this.eventList = eventList;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder{  //Holder class for xml-elements
        ImageView eventImageView;
        TextView eventStatement;
        TextView probabilityTextView;

    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item, null);

            holder = new ViewHolder();
            holder.eventStatement = (TextView)convertView.findViewById(R.id.statement);
            holder.eventImageView = (ImageView)convertView.findViewById(R.id.image);
            holder.probabilityTextView = (TextView)convertView.findViewById(R.id.probability);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        if(eventList.size() == 0){
            holder.eventStatement.setText("No Data");
        }
        else{
            tempEvent = null;
            tempEvent = eventList.get(position);

            //Set names/values for display
            holder.eventStatement.setText(tempEvent.getStatement());

            String probabilityText = round(tempEvent.getProbability(),1) + "%";
            holder.probabilityTextView.setText(probabilityText);
            holder.probabilityTextView.setTextColor(getColor(tempEvent.getProbability()));

            //Image things
            final long ONE_MEGABYTE = 1024 * 1024;
            final StorageReference storageReference = storage.getReferenceFromUrl("gs://predictor2017-b6577.appspot.com");
            StorageReference imageRef = storageReference.child(tempEvent.getImage() + ".jpg");

            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    DisplayMetrics dm = new DisplayMetrics();
                    activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

                    holder.eventImageView.getLayoutParams().height = 200;
                    holder.eventImageView.getLayoutParams().width = 200;
                    holder.eventImageView.setImageBitmap(bitImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

            //Set clicks
            holder.eventImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openEvent(position);
                }
            });
            holder.eventStatement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openEvent(position);
                }
            });
            holder.probabilityTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openEvent(position);
                }
            });
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        Log.i("CustomAdapter", "=====Row button clicked=====");
    }

    private class OnItemClickListener implements View.OnClickListener{
        private int position;

        public OnItemClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Log.i("CustomAdapter", "=====Row button clicked=====");

            MainActivity sct = (MainActivity)activity;
            //sct.onItemClick(position);
        }
    }


    public void openEvent(int position){

    }

    public int getColor(double power) {
        float HSV[] = new float[3];
        HSV[0] = (float)power;
        HSV[1] = (float)0.9;
        HSV[2] = (float)0.9;

        return Color.HSVToColor(HSV);
    }

    public static double round(Double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);

        return (double) tmp / factor;
    }
}