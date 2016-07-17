package edu.clemson.tigermeterreading;

import android.content.Intent;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MeterReading extends AppCompatActivity {

    DatabaseManager databaseManager;

    TextView routeNumberText, routeSequenceText, typeText, meterNumberText, meterSerialText, facNameText, prevReadingText;
    EditText currReadingText, notes;
    int routeNumber,routeSequence;
    int mID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_reading);

        Intent intent = getIntent();
        routeNumber = intent.getIntExtra("routeNumber",-1);
        routeSequence = intent.getIntExtra("routeSequence",-1);

        databaseManager = new DatabaseManager(getApplicationContext());

        routeNumberText = (TextView) findViewById(R.id.routeNumber);
        routeSequenceText = (TextView) findViewById(R.id.routeSequence);
        typeText = (TextView) findViewById(R.id.typeName);
        meterNumberText = (TextView) findViewById(R.id.meterNumber);
        meterSerialText = (TextView) findViewById(R.id.meterSerial);
        facNameText = (TextView) findViewById(R.id.facName);
        prevReadingText = (TextView) findViewById(R.id.prevReading);
        currReadingText = (EditText) findViewById(R.id.currReading);
        notes = (EditText) findViewById(R.id.notes);

        updateDisplay(routeNumber,routeSequence);
    }

    public void updateDisplay(int route, int sequence){
        int prevReading;

        databaseManager.open();
        mID = databaseManager.getMeterId(route,sequence);
        databaseManager.close();

        if(mID == -1) Toast.makeText(MeterReading.this, "No meters are assigned to this route!", Toast.LENGTH_SHORT).show();

        databaseManager.open();
        Meter meter = databaseManager.getMeter(mID);
        prevReading = databaseManager.getMeterReading(mID);
        databaseManager.close();

        routeNumberText.setText(String.valueOf(route));
        routeSequenceText.setText(String.valueOf(sequence));
        typeText.setText(meter.getType());
        meterNumberText.setText(meter.getNumber());
        meterSerialText.setText(meter.getSerial());
        facNameText.setText(meter.getFacName());
        prevReadingText.setText(String.valueOf(prevReading));

    }
}
