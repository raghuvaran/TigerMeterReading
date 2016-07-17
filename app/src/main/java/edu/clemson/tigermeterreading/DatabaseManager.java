package edu.clemson.tigermeterreading;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by rchowda on 7/13/2016.
 */
public class DatabaseManager {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TMR.db";
    public static final String TABLE_NAME_TYPE = "type";
    public static final String TABLE_NAME_METERS = "meters";
    public static final String TABLE_NAME_ROUTE = "routes";
    public static final String TABLE_NAME_READINGS = "readings";


    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";


    private static final String SQL_CREATE_TYPE =
            "CREATE TABLE " + TABLE_NAME_TYPE + " (" +
                    "typeID" + " INTEGER PRIMARY KEY autoincrement not null," +
                    "typeName "+ TEXT_TYPE  +
                    ")";

    private static final String SQL_CREATE_METERS =
            "CREATE TABLE " + TABLE_NAME_METERS + " (" +
                    "mID" + " INTEGER PRIMARY KEY autoincrement not null," +
                    "Number "+ INTEGER_TYPE  + COMMA_SEP +
                    "typeID "+ INTEGER_TYPE  + COMMA_SEP +
                    "facName "+ TEXT_TYPE  + COMMA_SEP +
                    "Serial "+ TEXT_TYPE  + COMMA_SEP +
                    "units "+ TEXT_TYPE  + COMMA_SEP +
                    "digits "+ INTEGER_TYPE  +
                    ")";

    private static final String SQL_CREATE_ROUTE =
            "CREATE TABLE " + TABLE_NAME_ROUTE + " (" +
                    "routeID" + " INTEGER PRIMARY KEY autoincrement not null," +
                    "routeNumber "+ INTEGER_TYPE  + COMMA_SEP +
                    "routeSequence "+ INTEGER_TYPE  + COMMA_SEP +
                    "mID "+ INTEGER_TYPE  +
                    ")";

    private static final String SQL_CREATE_READINGS =
            "CREATE TABLE " + TABLE_NAME_READINGS + " (" +
                    "rID" + " INTEGER PRIMARY KEY autoincrement not null," +
                    "mID "+ INTEGER_TYPE  + COMMA_SEP +
                    "timeStamp "+ TEXT_TYPE  + COMMA_SEP +
                    "readingValue "+ INTEGER_TYPE  + COMMA_SEP +
                    "notes "+ TEXT_TYPE  +
                    ")";

    private static final String SQL_DELETE_TYPE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_TYPE;
    private static final String SQL_DELETE_METERS =
            "DROP TABLE IF EXISTS " + TABLE_NAME_METERS;
    private static final String SQL_DELETE_READINGS =
            "DROP TABLE IF EXISTS " + TABLE_NAME_READINGS;
    private static final String SQL_DELETE_ROUTE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_ROUTE;

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;


    public DatabaseManager(Context ctx) {
        context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        public static String DB_FILEPATH ;
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TYPE);
            db.execSQL(SQL_CREATE_METERS);
            db.execSQL(SQL_CREATE_ROUTE);
            db.execSQL(SQL_CREATE_READINGS);

            db.execSQL("PRAGMA foreign_keys=ON;");
        }

        /**
         * Upgrade Database version drop all tables and recreate
         *
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over

            db.execSQL(SQL_DELETE_TYPE);
            db.execSQL(SQL_DELETE_METERS);
            db.execSQL(SQL_DELETE_READINGS);
            db.execSQL(SQL_DELETE_ROUTE);

            onCreate(db);
        }

        /**
         * onDowngrade Method
         *
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        /**
         * onConfigure for setting foreign key constraints
         *
         * @param database
         */
        public void onConfigure(SQLiteDatabase database) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                database.setForeignKeyConstraintsEnabled(true);
            } else {
                database.execSQL("PRAGMA foreign_keys=ON");
            }
        }
    }

    public DatabaseManager open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        return this;
    }

    public void close() {
        db.setTransactionSuccessful();
        db.endTransaction();
        DBHelper.close();


    }

    /**
     * Fetches all the UNIQUE route number available in the routes table
     * @return  List of route numbers
     */
    public List<Integer> getRoutes(){
        Cursor cursor;
        List<Integer> routes = new ArrayList<Integer>();

        cursor = db.rawQuery("SELECT DISTINCT routeNumber FROM routes ORDER BY routeNumber ASC",null);

        while(cursor.moveToNext()){
            routes.add(cursor.getInt(0));
        }
        cursor.close();
        return routes;
    }

    public List<Integer> getRouteSeq(int route){
        Cursor cursor;
        List<Integer> sequence = new ArrayList<>();

        cursor = db.rawQuery("SELECT DISTINCT routeSequence FROM routes WHERE routeNumber = "+route,null);

        while(cursor.moveToNext()){
            sequence.add(cursor.getInt(0));
        }
        cursor.close();
        return sequence;
    }

    public int getMeterId(int routeNumber, int routeSequence){
        int mID = -1;
        Cursor cursor;

        cursor = db.rawQuery("SELECT DISTINCT mID FROM routes WHERE routeNumber = "+routeNumber+" AND routeSequence = "+routeSequence,null);
        while(cursor.moveToNext()){
            mID = cursor.getInt(0);
        }
        if(cursor.getCount() > 1 ) mID = -1;
        cursor.close();
        return mID;
    }

    public Meter getMeter(int meterID){
        Cursor cursor;

        cursor = db.rawQuery("SELECT  digits, Number, typeName, facName, Serial, units FROM meters , type WHERE mID = "+meterID+" AND type.typeID = meters.typeID ",null);

        cursor.moveToFirst();
        Meter meter = new Meter(meterID,cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));

        return meter;

    }

    public int getMeterReading(int mID){
        Cursor cursor;
        int reading;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-1);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String startDate = year+"-"+String.format("%02d",month)+"-01";
        String endDate = year+"-"+String.format("%02d",month)+"-"+lastDay;
        Log.d("StartDate",startDate);
        Log.d("EndDate",endDate);

        cursor = db.rawQuery("SELECT mID, timeStamp, readingValue, notes FROM readings WHERE timeStamp between '"+startDate+"' and '"+endDate+"' AND mID = "+mID+" order by timeStamp DESC",null);

        if(cursor.isAfterLast()) {
            reading = -1;
        }else{
            cursor.moveToFirst();
            reading = cursor.getInt(2);
        }
        cursor.close();
        return reading;


    }


}
