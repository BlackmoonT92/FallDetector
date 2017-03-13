package edu.asu.msama1.falldetector;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.IOException;

/**
 * Created by Mitikaa on 11/14/16.
 */

public class WearableListener extends WearableListenerService {

    public static String TAG = "WearableListener";
    public String nodeId;
    double g = 9.81;
    double normA = 0.0, normG = 0.0;
    long lastInsertedTimeStamp = System.currentTimeMillis();

    @Override
    public void onMessageReceived(MessageEvent messageEvent){
        nodeId = messageEvent.getSourceNodeId();
        String event = messageEvent.getPath();

        String[] message = event.split(" ");

        if(message[0].contains("Accelero")){
            double aX = Double.parseDouble(message[1]);
            double aY = Double.parseDouble(message[2]);
            double aZ = Double.parseDouble(message[3]);
            normA = aX * aX + aY * aY + aZ * aZ;
            normA = Math.sqrt(normA);
        }

        if(message[0].contains("Gyro")){
            double gX = Double.parseDouble(message[1]);
            double gY = Double.parseDouble(message[2]);
            double gZ = Double.parseDouble(message[3]);
            normG = gX * gX + gY * gY + gZ * gZ;
            normG = Math.sqrt(normG);
        }

        if (isFall(normA, normG)) {
            Log.i(TAG, "normA: "+normA+" normG: "+normG);
            Intent i = new Intent(this, HandheldActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        }

        try {
            long currentTimeStamp = System.currentTimeMillis();
            if((currentTimeStamp - lastInsertedTimeStamp) > 1000) {
                DBHelper dbHelper = DBHelper.getInstance(WearableListener.this);
                dbHelper.onCreateTable();
                dbHelper.insertAccelNorm((float) normA);
                dbHelper.insertGyroNorm((float) normG);
                lastInsertedTimeStamp = currentTimeStamp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isFall(double normA, double normG) {
        if ((normA / g > 6) && (true)) {
          return true;
        } else return false;
    }

    public double getNormA() {
        return normA;
    }

    public void setNormA(double normA) {
        this.normA = normA;
    }

    public double getNormG() {
        return normG;
    }

    public void setNormG(double normG) {
        this.normG = normG;
    }
}
