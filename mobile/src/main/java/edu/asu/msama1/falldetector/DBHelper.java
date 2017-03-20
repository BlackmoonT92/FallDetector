package edu.asu.msama1.falldetector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright 2017 Mitikaa Sama,
 *
 * The Instructor and the Arizona State University
 * has the right to build and evaluate the software package
 * for the purpose of determining the grade and program assessment.
 *
 * Purpose: Masters Applied Project
 *
 * @author Mitikaa Sama on 3/10/17.
 *
 * This class is used to access the database for accelerometer and gyroscope norm values
 */
public class DBHelper extends SQLiteOpenHelper {

    private static String TAG = "DBHelper";

    /**
     * Database Name
     */
    private static final String DB_NAME = Environment.getExternalStorageDirectory() + File.separator + "data.db";

    /**
     * Instance of DBHelper class to ensure singleton pattern
     */
    private static DBHelper dbHelper;

    /**
     * Table name to store Accelerometer norm values
     */
    private final String TABLE_ACCEL = "Accel";

    /**
     * Table name to store Gyroscope norm values
     */
    private final String TABLE_GYRO = "Gyro";

    /**
     * Column to store timestamp for Accelerometer norm
     */
    private String ACCEL_COL2 = "Timestamp";

    /**
     * Column to store timestamp for Gyroscope norm
     */
    private String GYRO_COL2 = "Timestamp";

    /**
     * Column to store Accelerometer norm value
     */
    private String ACCEL_COL3 = "normA";

    /**
     * Column to store Gyroscope norm value
     */
    private String GYRO_COL3 = "normG";

    /**
     * SQLiteDatabase instance
     */
    private static SQLiteDatabase db = null;

    /**
     * Current application context
     */
    private Context mContext;

    /**
     * private constructor to follow singleton pattern such that only one instance of database is created
     * @param context : current application context
     */
    private DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }

    /**
     * method to get instance of DBHelper class
     * @param context : current application context
     */
    public static DBHelper getInstance(Context context) throws IOException, SQLiteException {
        Log.i(TAG, DB_NAME);
        if (dbHelper == null) {
            dbHelper = new DBHelper(context.getApplicationContext());
            db = dbHelper.getWritableDatabase();
        }
        return dbHelper;
    }

    /**
     * method to create two tables: one to store accelerometer norm values and another to store gyroscope norm values
     */
    public void onCreateTable() throws SQLiteException {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ACCEL + " ("
                + " Timestamp text, "
                + " normA real)" );

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_GYRO + " ("
                + " Timestamp text, "
                + " normG real)" );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * method to insert accelerator norm value into table
     * table also stores current timestamp
     * @param normA : acceleration norm value
     */
    public boolean insertAccelNorm(float normA) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCEL_COL2,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        contentValues.put(ACCEL_COL3, normA);
        long result = db.insert(TABLE_ACCEL, null, contentValues);
        //result is -1 in case of error while inserting new row else returns row number
        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * method to insert gyroscope norm value into table
     * table also stores current timestamp
     * @param normG : gyroscope norm value
     */
    public boolean insertGyroNorm(float normG) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(GYRO_COL2,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        contentValues.put(GYRO_COL3, normG);
        long result = db.insert(TABLE_GYRO, null, contentValues);
        //result is -1 in case of error while inserting new row else returns row number
        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * fetches accelerometer and gyroscope norm values along with their timestamps to display on GraphView
     * @return GraphValues : the values are used to plot line graph
     */
    public GraphValues getGraphValues() {
        Cursor cursorA = null, cursorG = null;

        //get accelerometer norm readings from table
        String selectQuery1 = "SELECT  * FROM " + TABLE_ACCEL + " ORDER BY TimeStamp DESC LIMIT 10";
        //get gyroscope norm readings from table
        String selectQuery2 = "SELECT  * FROM " + TABLE_GYRO + " ORDER BY TimeStamp DESC LIMIT 10";

        try {
            //execute raw query for accelerometer
            cursorA = db.rawQuery(selectQuery1, null);

            //execute raw query for gyroscope
            cursorG = db.rawQuery(selectQuery2, null);

            //if no values found
            if (cursorA == null || cursorG == null){
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //GraphValues object to be returned for plotting graph
        GraphValues graphValues = new GraphValues();

        //stores Accelerometer norm values
        float[] normA = new float[10];

        //stores Gyroscope norm values
        float[] normG = new float[10];
        int i = 0;

        try {
            //put the data from database into accelerometer norm array
            if (cursorA.moveToFirst()) {
                do {
                    // get the data into array
                    normA[i] = cursorA.getFloat(cursorA.getColumnIndex(ACCEL_COL3));
                    i++;
                    cursorA.moveToNext();

                } while (i < 10);
            }
            cursorA.close();

            i = 0;
            //put the data from database into gyroscope norm array
            if (cursorG.moveToFirst()) {
                do {
                    // get the data into array
                    normG[i] = cursorG.getFloat(cursorG.getColumnIndex(GYRO_COL3));
                    i++;
                    cursorG.moveToNext();

                } while (i < 10);
            }
            cursorG.close();

        }catch(Exception e){
            e.printStackTrace();
        } finally {
            //set accelerometer and gyroscope norm arrays
            graphValues.setNormA(normA);
            graphValues.setNormG(normG);
        }

        //return values to be plotted on graph
        return graphValues;
    }
}
