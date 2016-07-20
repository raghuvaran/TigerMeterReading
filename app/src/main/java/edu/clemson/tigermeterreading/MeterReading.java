package edu.clemson.tigermeterreading;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Calendar;
import java.util.List;

public class MeterReading extends AppCompatActivity {

    DatabaseManager databaseManager;

    TextView routeNumberText, routeSequenceText, typeText, meterNumberText, meterSerialText, facNameText, prevReadingText;
    EditText currReadingText, notes;
    int routeNumber,routeSeqPointer;
    List<Integer> routeSequence;
    int mID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_reading);

        Intent intent = getIntent();
        routeNumber = intent.getIntExtra("routeNumber",-1);
        routeSeqPointer = intent.getIntExtra("routeSequence",-1);

        setTitle("Route No: "+routeNumber);

        databaseManager = new DatabaseManager(getApplicationContext());
        databaseManager.open();
        routeSequence = databaseManager.getRouteSeq(routeNumber);
        databaseManager.close();

        routeNumberText = (TextView) findViewById(R.id.routeNumber);
        routeSequenceText = (TextView) findViewById(R.id.routeSequence);
        typeText = (TextView) findViewById(R.id.typeName);
        meterNumberText = (TextView) findViewById(R.id.meterNumber);
        meterSerialText = (TextView) findViewById(R.id.meterSerial);
        facNameText = (TextView) findViewById(R.id.facName);
        prevReadingText = (TextView) findViewById(R.id.prevReading);
        currReadingText = (EditText) findViewById(R.id.currReading);
        notes = (EditText) findViewById(R.id.notes);

        updateDisplay(routeNumber,routeSeqPointer);
    }

    public void updateDisplay(int route, int sequence){
        Reading prevReading, currReading;


        routeSeqPointer = sequence;

        databaseManager.open();
        mID = databaseManager.getMeterId(route,sequence);
        databaseManager.close();

        if(mID == -1) Toast.makeText(MeterReading.this, "No meters are assigned to this route!", Toast.LENGTH_SHORT).show();
        else try {

            databaseManager.open();
            Meter meter = databaseManager.getMeter(mID);

            Calendar calendar = Calendar.getInstance();
            currReading = databaseManager.getMeterReading(mID,calendar);

            calendar.add(Calendar.MONTH,-1);
            prevReading = databaseManager.getMeterReading(mID,calendar);


            databaseManager.close();

            routeNumberText.setText(String.valueOf(route));
            routeSequenceText.setText(String.valueOf(sequence));
            typeText.setText(String.valueOf(meter.getType() + " (" + meter.getUnits() + ")"));
            meterNumberText.setText(meter.getNumber());
            meterSerialText.setText(meter.getSerial());
            facNameText.setText(meter.getFacName());
            prevReadingText.setText(String.valueOf(prevReading.getReading()));

            currReadingText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(meter.getDigits())});

            if(currReading.getReading() == -1){
                currReadingText.setText("");
            }else{
                currReadingText.setText(String.valueOf(currReading.getReading()));
            }

            if(currReading.getNotes() == null) notes.setText("");
            else notes.setText(currReading.getNotes());


        } catch (SQLException e) {
            e.printStackTrace();
        }




    }

    public void onNavigate(View view){

        int index;
        index = routeSequence.indexOf(routeSeqPointer);

        databaseManager.open();
        //int meterId = databaseManager.getMeterId(routeNumber,routeSeqPointer);
        databaseManager.saveCurrReading(mID,Double.valueOf(currReadingText.getText().toString()),notes.getText().toString());
        databaseManager.close();


        if(R.id.nextButton == view.getId()) {
            if(index == routeSequence.size()-1){
                Toast.makeText(MeterReading.this, "You've reached the last meter!", Toast.LENGTH_SHORT).show();
            }else{
                updateDisplay(routeNumber,routeSequence.get(index+1) );
            }

        }

        if(R.id.backButton == view.getId()){
            if(index == 0){
                Toast.makeText(MeterReading.this, "You're on the first meter!", Toast.LENGTH_SHORT).show();
            }else{
                updateDisplay(routeNumber,routeSequence.get(index-1) );
            }
        }
    }

    public void validateBeforeNavigate(final View view){

        if(String.valueOf(currReadingText.getText()).isEmpty()){
                new AlertDialog.Builder(MeterReading.this)
                        .setTitle("Skipping meter")
                        .setMessage("You haven't entered current reading. Do you want to skip this meter?")
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing, return to activity
                            }
                        })
                        .setPositiveButton("Skip", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onNavigate(view);
                            }
                        })
                        .show();
        }else{
            onNavigate(view);
        }


    }
}
