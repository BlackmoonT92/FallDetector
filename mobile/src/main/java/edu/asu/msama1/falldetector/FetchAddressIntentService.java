package edu.asu.msama1.falldetector;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Copyright 2017 Mitikaa Sama,
 *
 * The Instructor and the Arizona State University
 * has the right to build and evaluate the software package
 * for the purpose of determining the grade and program assessment.
 *
 * Purpose: Masters Applied Project
 *
 * @author Mitikaa Sama on 3/12/17.
 *
 * This class uses latitude and longitude readings to fetch street address, called reverse geocoding
 * It handles asynchronous requests
 *
 * References:
 * https://developer.android.com/training/location/display-address.html
 */
public class FetchAddressIntentService extends IntentService {

    public static String TAG = "FetchAddressIntentService";

    /**
     * Used for receiving a callback result
     */
    protected ResultReceiver mReceiver;

    /**
     * Creates an IntentService.  Invoked by subclass's constructor.
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService(String name) {
        super(name);
    }

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public FetchAddressIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        if (mReceiver == null) {
            Log.e(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }

        //Geocoder object to handle reverse geocoding
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);

        //list of addresses fetched using latitude and longitude coordinates
        List<Address> addresses = null;

        try {
            //accepts a latitude and longitude and returns a list of addresses
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this, we need to get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found. Display error message
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            //used to send results back to the requesting activity - in this case send a failure message
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }

            Log.i(TAG, getString(R.string.address_found));
            //used to send results back to the requesting activity - in this case send a success message along with address found
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }
    }

    /**
     * Method to send the results back to the requesting activity
     * @param resultCode : code to indicate success or failure
     * @param message : message to be sent to requesting activity
     */
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        //send resultcode and message to requesting activity
        mReceiver.send(resultCode, bundle);
    }

}
