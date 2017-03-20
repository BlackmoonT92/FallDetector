package edu.asu.msama1.falldetector;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import static edu.asu.msama1.falldetector.Constants.RESULT_DATA_KEY;
import static edu.asu.msama1.falldetector.Constants.SUCCESS_RESULT;

/**
 * Copyright 2017 Mitikaa Sama,
 *
 * The Instructor and the Arizona State University
 * has the right to build and evaluate the software package
 * for the purpose of determining the grade and program assessment.
 *
 * Purpose: Masters Applied Project
 *
 * @author Mitikaa Sama on 11/14/16.
 *
 * This class handles sending text messages, fetching latitude and longitude, getting street address and raising an alarm in case a fall is detected
 *
 * References:
 * https://developer.android.com/reference/android/os/ResultReceiver.html
 * https://developer.android.com/training/location/retrieve-current.html
 * https://developer.android.com/reference/android/media/MediaPlayer.html
 * https://developer.android.com/reference/android/app/IntentService.html
 * https://developer.android.com/training/location/display-address.html
 */
public class HandheldActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>, ActivityCompat.OnRequestPermissionsResultCallback {

    public static String TAG = "HandheldActivity";

    /**
     * Object to fetch current location coordinates
     */
    private LocationActivity locationActivity;

    /**
     * LocationManager provides access to the system location services
     */
    protected LocationManager locationManager;

    /**
     * Object to prepare and send text messages
     */
    private SMSActivity smsActivity;

    /**
     * Object to fetch contact numbers that the text message has to be sent to
     */
    private MainActivity mainActivity;

    /**
     * Location object to store current location coordinates
     */
    protected Location location;

    /**
     * Object of nested class used to receie result from FetchAddressIntentService
     */
    private AddressResultReceiver mResultReceiver;

    /**
     * To store result after reverse geocoding (i.e. stores street address of the incident)
     */
    private String geoLocation;

    /**
     * Stores last updated latitude coordinate
     */
    private double latitude;

    /**
     * Stores last updated longitude coordinate
     */
    private double longitude;

    /**
     * Used to send messages
     */
    private String phoneNumber;

    /**
     * Message to be sent in case of incident
     */
    private String message;

    /**
     * TextViews to display incident coordinates and street address
     */
    TextView latitudeTextView, longitudeTextView, addressTextView;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handheld);

        //start activity request from WearableListerner class
        //this activity will start ONLY if a fall is detected
        Intent i = getIntent();
        Log.i(TAG, "Activity started");

        //action bar to change settings of the app
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).addApi(Awareness.API).build();

        //initialize AddressResultReciever class with new Handler to handle results from FetchAddressIntentService
        mResultReceiver = new AddressResultReceiver(new Handler());

        //initialize TextViews
        latitudeTextView = (TextView) findViewById(R.id.latitude);
        longitudeTextView = (TextView) findViewById(R.id.longitude);
        addressTextView = (TextView) findViewById(R.id.address);

        //fetch current or last available location
        location = getLocation();

        //method to start intent service
        startIntentService();

        //button to stop alarm after fall is detected
        final Button button = (Button) findViewById(R.id.stopAlert);
        final MediaPlayer mediaPlayer = MediaPlayer.create(HandheldActivity.this, R.raw.emergency_alert);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                stopAlertTone(mediaPlayer);
            }
        });

        //play alarm when fall detected
        playAlertTone(mediaPlayer);
    }

    /**
     * Method will update TextView with fetched latitude and longitude coordinates and the street address
     */
    public void displayLocationCoordinates(){
        latitudeTextView.setText(String.valueOf(latitude));
        longitudeTextView.setText(String.valueOf(longitude));
        addressTextView.setText(geoLocation);
    }

    /**
     * This method uses LocationActivity to get the current location of the handheld device with the help of GPS
     * @return location
     */
    public Location getLocation(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationActivity = new LocationActivity(HandheldActivity.this);
        latitude = locationActivity.getLatitude();
        longitude = locationActivity.getLongitude();
        location = locationActivity.getLocation();
        Log.d(TAG, "LocationReadings: " + latitude + ", " + longitude);
        return location;
    }

    /**
     * Prepare text message to be sent in case of an incident
     */
    public void prepareAndSendMessage(){
        mainActivity = new MainActivity();
        message = mainActivity.getMessage();
        message = message + " Address: "+ geoLocation;

        Log.i(TAG, "Message: " + this.message);

        //send message to first contact
        phoneNumber = mainActivity.getPhoneNumber1();
        smsActivity = new SMSActivity(HandheldActivity.this, phoneNumber, latitude, longitude, message);
        Log.i(TAG, "SMS prepared");
        smsActivity.sendSMSMessge(phoneNumber);

        //send message to second contact
        phoneNumber = mainActivity.getPhoneNumber2();
        smsActivity = new SMSActivity(HandheldActivity.this, phoneNumber, latitude, longitude, message);
        Log.i(TAG, "SMS prepared");
        smsActivity.sendSMSMessge(phoneNumber);

        //send message to third contact
        phoneNumber = mainActivity.getPhoneNumber3();
        smsActivity = new SMSActivity(HandheldActivity.this, phoneNumber, latitude, longitude, message);
        Log.i(TAG, "SMS prepared");
        smsActivity.sendSMSMessge(phoneNumber);
    }

    /**
     * Method to play an alert tone in case an incident has occurred
     * @param mediaPlayer
     */
    public void playAlertTone(MediaPlayer mediaPlayer){
        //set full volume
        mediaPlayer.setVolume(1.0f, 1.0f);
        //will play the alert tone continuously
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    /**
     * Method to stop playing the alert tone
     * @param mediaPlayer
     */
    public void stopAlertTone(MediaPlayer mediaPlayer){
        mediaPlayer.stop();
        mediaPlayer.prepareAsync();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationActivity.stopUsingGPS();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Handheld Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    /**
     * This method starts an asynchronous intent service to fetch the street address using location cooridnates and waits for results
     */
    protected void startIntentService() {
        Log.i(TAG, "Starting intent service for geolocation");
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    /**
     * GeoLocation getter - returns street address of incident
     * @return geolocation
     */
    public String getGeoLocation() {
        return geoLocation;
    }

    /**
     * GeoLocation setter - set sthe street address of incident
     * @param geoLocation
     */
    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
        Log.i(TAG, "Address is: " + this.geoLocation);
    }


    /**
     * This class is used to receive a callback result from FetchAddresssIntentService
     */
    class AddressResultReceiver extends ResultReceiver {
        public String TAG = "AddressResultReceiver";

        /**
         * constructor
         * @param handler
         */
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            Log.i(TAG, "After Receiving Result");
            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(RESULT_DATA_KEY);
            displayAddressOutput(mAddressOutput);

            // Show a toast message if an address was found.
            if (resultCode == SUCCESS_RESULT) {
                Log.i(TAG, "Success Result");
                Toast.makeText(HandheldActivity.this, R.string.address_found, Toast.LENGTH_LONG).show();
            }

        }

        /**
         * displays output and send a text message after receiving street address result
         * @param mAddressOutput
         */
        private void displayAddressOutput(String mAddressOutput) {
            Log.i(TAG, "Inside displayAddressOutput");
            //set fetched street address
            setGeoLocation(mAddressOutput);
            //send text messages to emergency contact list
            prepareAndSendMessage();
            //display coordinates and street address on UI
            displayLocationCoordinates();
        }
    }

    /**
     * This method handles action button items
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //start new intent to display page to change preference settings
                //start main activity
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                return true;

            case R.id.action_graph:
                //start main activity
                Intent intent2 = new Intent(this, GraphActivity.class);
                startActivity(intent2);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Inflate action menu bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }
}
