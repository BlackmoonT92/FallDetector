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
 * Created by Mitikaa on 3/10/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static String TAG = "DBHelper";

    private static final String DB_NAME = Environment.getExternalStorageDirectory() + File.separator + "data.db";
    private static DBHelper dbHelper;
    private final String TABLE_ACCEL = "Accel";
    private final String TABLE_GYRO = "Gyro";
    private String ACCEL_COL2 = "Timestamp";
    private String GYRO_COL2 = "Timestamp";
    private String ACCEL_COL3 = "normA";
    private String GYRO_COL3 = "normG";
    private static SQLiteDatabase db = null;
    private Context mContext;


    private DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }

    //singleton
    public static DBHelper getInstance(Context context) throws IOException, SQLiteException {
        Log.i(TAG, DB_NAME);
        if (dbHelper == null) {
            dbHelper = new DBHelper(context.getApplicationContext());
            db = dbHelper.getWritableDatabase();
        }
        return dbHelper;
    }

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

    public boolean insertAccelNorm(float normA) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCEL_COL2,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        contentValues.put(ACCEL_COL3, normA);
        long result = db.insert(TABLE_ACCEL, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public boolean insertGyroNorm(float normG) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(GYRO_COL2,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        contentValues.put(GYRO_COL3, normG);
        long result = db.insert(TABLE_GYRO, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public GraphValues getGraphValues() {
        Cursor cursorA = null, cursorG = null;
        String selectQuery1 = "SELECT  * FROM " + TABLE_ACCEL + " ORDER BY TimeStamp DESC LIMIT 10";
        String selectQuery2 = "SELECT  * FROM " + TABLE_GYRO + " ORDER BY TimeStamp DESC LIMIT 10";
        try {
            cursorA = db.rawQuery(selectQuery1, null);
            cursorG = db.rawQuery(selectQuery2, null);
            if (cursorA == null || cursorG == null){
                return null;
            }
            //db.setTransactionSuccessful();
        } catch (Exception e) {
            //report problem
            e.printStackTrace();
        }
        GraphValues graphValues = new GraphValues();
        float[] normA = new float[10];
        float[] normG = new float[10];
        int i = 0;

        try {
            //put the data from database into normA array
            if (cursorA.moveToFirst()) {
                do {
                    // get the data into array, or class variable
                    normA[i] = cursorA.getFloat(cursorA.getColumnIndex(ACCEL_COL3));
                    i++;
                    cursorA.moveToNext();

                } while (i < 10);
            }
            cursorA.close();

            i = 0;
            //put the data from database into normG array
            if (cursorG.moveToFirst()) {
                do {
                    // get the data into array, or class variable
                    normG[i] = cursorG.getFloat(cursorG.getColumnIndex(GYRO_COL3));
                    i++;
                    cursorG.moveToNext();

                } while (i < 10);
            }
            cursorG.close();

        }catch(Exception e){
            //report problem
            e.printStackTrace();
        } finally {
            graphValues.setNormA(normA);
            graphValues.setNormG(normG);
        }

        return graphValues;
    }


    public boolean insertAccelValues(float normA) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCEL_COL2,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        contentValues.put(ACCEL_COL3, normA);
        long result = db.insert(TABLE_ACCEL, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public boolean insertGyroValues(float normG) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(GYRO_COL2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        contentValues.put(GYRO_COL3, normG);
        long result = db.insert(TABLE_GYRO, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
}
