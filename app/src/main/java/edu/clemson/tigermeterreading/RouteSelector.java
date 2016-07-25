package edu.clemson.tigermeterreading;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class RouteSelector extends AppCompatActivity {

    Spinner routeSelector, routeSeqSelector;
    DatabaseManager databaseManager;
    int routeNumber;
    int routeSequence;

    ArrayAdapter<Integer> routeArrayAdapter;

    ArrayAdapter<Integer> routeSeqArrayAdapter;

    public ArrayAdapter<Integer> getRouteSeqArrayAdapter() {
        return routeSeqArrayAdapter;
    }

    public void setRouteSeqArrayAdapter(ArrayAdapter<Integer> routeSeqArrayAdapter) {
        this.routeSeqArrayAdapter = routeSeqArrayAdapter;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_selector);

        /*
        * Initialize variable once before using them
        **/
        routeNumber = -1;
        routeSequence = -1;

        routeSelector = (Spinner) findViewById(R.id.routeSelectSpinner); //Map spinner to variable
        routeSeqSelector = (Spinner) findViewById(R.id.routeSeqSpinner);

        databaseManager = new DatabaseManager(getApplicationContext()); //Initialize database


        databaseManager.open();
        routeArrayAdapter = new ArrayAdapter<>(RouteSelector.this,android.R.layout.simple_spinner_item, databaseManager.getRoutes());// Array adapter is necessary for spinner
        databaseManager.close();
        routeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSelector.setAdapter(routeArrayAdapter); //Spinner is now set with the array values


        // Onclick listener for route number
        routeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                routeNumber = routeArrayAdapter.getItem(position);
                databaseManager.open();
                final ArrayAdapter<Integer> seqArrayAdapter = new ArrayAdapter<>(RouteSelector.this, android.R.layout.simple_spinner_item,databaseManager.getRouteSeq(routeNumber));
                databaseManager.close();
                seqArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                routeSeqSelector.setAdapter(seqArrayAdapter);
                setRouteSeqArrayAdapter(seqArrayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        routeSeqSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                routeSequence = getRouteSeqArrayAdapter().getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });







    }

    /**
     * Drives to next activity on hitting 'Start Tour' button
     * @param view 'Start Tour' button
     */
    public void onStartTour(View view){
        if(routeNumber != -1 && routeSequence != -1){
            SharedPreferences sharedPreferences = getSharedPreferences("syncStatus",-1);
            databaseManager.open();
            JsonManager jsonManager = new JsonManager(databaseManager,sharedPreferences);
            Log.i("DBSyncOutput",jsonManager.unSyncedData().toString());
            databaseManager.close();

            Intent intent = new Intent(getApplicationContext(), MeterReading.class);
            intent.putExtra("routeNumber",routeNumber);
            intent.putExtra("routeSequence",routeSequence);
            startActivity(intent);
        }
        else{
            Toast.makeText(RouteSelector.this, "Please choose a valid route!", Toast.LENGTH_SHORT).show();
        }
    }
}
