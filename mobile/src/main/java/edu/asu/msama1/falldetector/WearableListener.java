package edu.asu.msama1.falldetector;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.IOException;

/**
 * Created by Mitikaa on 11/14/16.
 *
 * This class receives message events from the wearable node
 * Lifecycle of this class is managed by Wearable
 *
 * References:
 * http://www.androprogrammer.com/2015/05/android-wear-how-to-send-data-from.html
 * https://developer.android.com/training/wearables/data-layer/index.html
 * https://developer.android.com/training/wearables/data-layer/messages.html
 * https://github.com/petrnalevka/wear/blob/master/watch/mobile/src/main/java/com/urbandroid/wear/WearService.java
 */
public class WearableListener extends WearableListenerService {

    public static String TAG = "WearableListener";

    /**
     * Node  ID for connected device from which message has to be received
     */
    public String nodeId;

    /**
     * Acceleration of gravity
     */
    double g = 9.81;

    /**
     * Accelerometer norm (normA)
     * Gyroscope norm (normG)
     */
    double normA = 0.0, normG = 0.0;

    /**
     * Timestamp of last inserted normA and normG values into the database
     */
    long lastInsertedTimeStamp = System.currentTimeMillis();

    /**
     * This method receives message events from wearable devices
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent){
        //get nodeID of message sender
        nodeId = messageEvent.getSourceNodeId();
        String event = messageEvent.getPath();

        String[] message = event.split(" ");

        //if sensor reading sent is of Accelerometer
        if(message[0].contains("Accelero")){
            //get x-axis, y-axis and z-axis of accelerometer sensor
            double aX = Double.parseDouble(message[1]);
            double aY = Double.parseDouble(message[2]);
            double aZ = Double.parseDouble(message[3]);
            //calculate accelerometer norm
            normA = Math.sqrt(aX * aX + aY * aY + aZ * aZ);
        }

        //if sensor reading sent is of Gyroscope
        if(message[0].contains("Gyro")){
            //get x-axis, y-axis and z-axis of gyroscope sensor
            double gX = Double.parseDouble(message[1]);
            double gY = Double.parseDouble(message[2]);
            double gZ = Double.parseDouble(message[3]);
            //calculate gyroscope norm
            normG = Math.sqrt(gX * gX + gY * gY + gZ * gZ);
        }

        //check if fall occurred and raise an alarm if it did
        if (isFall(normA, normG)) {
            Log.i(TAG, "normA: "+normA+" normG: "+normG);
            Intent i = new Intent(this, HandheldActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        }

        //insert current norm data into the database
        try {
            //get current system time in miliseconds
            long currentTimeStamp = System.currentTimeMillis();
            //insert norm values in the database in intervals of 1 second
            if((currentTimeStamp - lastInsertedTimeStamp) > 1000) {
                //get DBHelper instance
                DBHelper dbHelper = DBHelper.getInstance(WearableListener.this);
                //create tables if they dont exist
                dbHelper.onCreateTable();
                //insert accelerometer norm
                dbHelper.insertAccelNorm((float) normA);
                //insert gyroscope norm
                dbHelper.insertGyroNorm((float) normG);
                //update timestamp
                lastInsertedTimeStamp = currentTimeStamp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to detect the fall using accelerometer and gyroscope norm readings
     * Currently, the fall is detected whenever the accelerometer norm increases a certain threshold
     * More work needs to be done on the fall detection algorithm in future
     * @param normA : Accelerometer norm
     * @param normG : Gyroscope norm
     * @return boolean : true if norms indicate a fall, false if the norms indicate it is not a fall
     */
    private boolean isFall(double normA, double normG) {
        if ((normA / g > 5) && (true)) {
          return true;
        } else return false;
    }

    /**
     * Accelerometer norm getter
     * @return accelerometer norm
     */
    public double getNormA() {
        return normA;
    }

    /**
     * Accelerometer norm setter
     * @param normA : accelerometer norm
     */
    public void setNormA(double normA) {
        this.normA = normA;
    }

    /**
     * Gyroscope norm getter
     * @return gyroscope norm
     */
    public double getNormG() {
        return normG;
    }

    /**
     * Gyroscope norm setter
     * @param normG : gyroscope norm
     */
    public void setNormG(double normG) {
        this.normG = normG;
    }
}
