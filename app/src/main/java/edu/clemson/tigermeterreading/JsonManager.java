package edu.clemson.tigermeterreading;

import android.content.SharedPreferences;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rchowda on 7/24/2016.
 */
public class JsonManager {

    DatabaseManager databaseManager;




    SharedPreferences sharedPreferences;

    public JsonManager(DatabaseManager databaseManager, SharedPreferences sharedPreferences) {
        this.databaseManager = databaseManager;
        this.sharedPreferences = sharedPreferences;
    }

    public JSONObject unSyncedData(){
        int synced_mID = sharedPreferences.getInt("synced_mID",-1);
        int synced_rID = sharedPreferences.getInt("synced_rID",-1);
        int synced_routeID = sharedPreferences.getInt("synced_routeID",-1);
        int synced_typeID = sharedPreferences.getInt("synced_typeID",-1);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        //Query un-synced rows from local database and put them in jsonArray
        try {
            jsonObject.put("type",toJsonObject(databaseManager.queryResult("SELECT * FROM type WHERE typeID > "+synced_typeID)));
            jsonObject.put("meters",toJsonObject(databaseManager.queryResult("SELECT * FROM meters WHERE mID > "+synced_mID)));
            jsonObject.put("routes",toJsonObject(databaseManager.queryResult("SELECT * FROM routes WHERE routeID > "+synced_routeID)));
            jsonObject.put("readings",toJsonObject(databaseManager.queryResult("SELECT * FROM readings WHERE rID > "+synced_rID)));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }





    private JSONArray toJsonObject(Cursor cursor){

        JSONArray jsonArray = new JSONArray();


        try {
            while(cursor.moveToNext()){
                JSONObject jsonObject = new JSONObject();
                for (int i =0; i < cursor.getColumnCount(); i++) {
                    jsonObject.put(cursor.getColumnName(i),cursor.getString(i));
                }
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
