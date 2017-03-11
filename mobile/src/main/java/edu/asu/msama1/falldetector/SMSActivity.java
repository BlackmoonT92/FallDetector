package edu.asu.msama1.falldetector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by Mitikaa on 2/21/17.
 *
 * Reference: Followed tutorial on https://www.tutorialspoint.com/android/android_sending_sms.htm
 */

public class SMSActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    public static String TAG = "SMSActivity";
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 3;

    private final Context mContext;
    String phoneNumber;
    double latitude, longitude;
    String message;

    public SMSActivity(Context mContext, String phoneNumber, double latitude, double longitude, String message){
        this.mContext = mContext;
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message + "Location of Incident:" + latitude +", " +longitude;
    }

    public void sendSMSMessge(){
        if(ActivityCompat.checkSelfPermission((Activity)mContext, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity)mContext,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(this.phoneNumber, null, this.message, null, null);
            Toast.makeText(mContext, "SMS Sent", Toast.LENGTH_LONG).show();

        } catch (Exception e){
            Toast.makeText(mContext, "Failed to send SMS", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    sendSMSMessge();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
