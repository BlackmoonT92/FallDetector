package edu.asu.msama1.falldetector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Copyright 2017 Mitikaa Sama,
 *
 * The Instructor and the Arizona State University
 * has the right to build and evaluate the software package
 * for the purpose of determining the grade and program assessment.
 *
 * Purpose: Masters Applied Project
 *
 * @author Mitikaa Sama on 2/21/17.
 *
 * This class is used to prepare and send text messages to the contacts registered by the user
 *
 * References:
 * https://www.tutorialspoint.com/android/android_sending_sms.htm
 * https://developer.android.com/reference/android/telephony/SmsManager.html
 */
public class SMSActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    public static String TAG = "SMSActivity";

    /**
     * Permission variable to store access for "android.permission.REQUEST_SEND_SMS"
     */
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 3;

    /**
     * Current application context
     */
    private final Context mContext;

    /**
     * Phone number to send message to
     */
    private String phoneNumber;

    /**
     * Text message b ody to be sent to the emergency contacts in case on incident
     */
    private String message;

    /**
     * Constructor
     * @param mContext : application context
     * @param phoneNumber : COntact number too which text message has to be sent
     * @param latitude : latitude of incident
     * @param longitude : longitude of incident
     * @param message : text message body to be sent
     */
    public SMSActivity(Context mContext, String phoneNumber, double latitude, double longitude, String message){
        this.mContext = mContext;
        if(phoneNumber==null){
            this.phoneNumber = "6023734290";
        }
        this.message = message;
    }

    /**
     * This method checks for permission from user to send text messages and if permitted, sends the message to the specified phoneNumber
     * @param phoneNumber
     */
    public void sendSMSMessge(String phoneNumber){
        if(ActivityCompat.checkSelfPermission((Activity)mContext, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity)mContext,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
        try{
            Log.i(TAG, "Text message to be sent: " + message);
            Log.i(TAG, "Phone #: " + phoneNumber);
            //Manages SMS operations - sending text message in this case
            SmsManager smsManager = SmsManager.getDefault();
            //sends a text based message
            smsManager.sendTextMessage(phoneNumber, null, this.message, null, null);
            Toast.makeText(mContext, "SMS Sent", Toast.LENGTH_LONG).show();
        } catch (Exception e){
            //display toast to user in case of failure of sending a message
            Toast.makeText(mContext, "Failed to send SMS", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * Callback for the result from requesting permissions
     * @param requestCode : code by which callback is requested
     * @param permissions : requested permissions
     * @param grantResults : results for requested permission
     */
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
                    sendSMSMessge(phoneNumber);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    /**
     * Getter for phone number to which text message is to be sent
     * @return phoneNumber : number to which text message will be sent
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Setter for phone number to which text message is to be sent
     * @param phoneNumber : number to which text message will be sent
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Getter for the text message body to be sent
     * @return message : text message to be sent
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter for the text message body to be sent
     * @param message : text message to be sent
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
