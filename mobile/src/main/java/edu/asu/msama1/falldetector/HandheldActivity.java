package edu.asu.msama1.falldetector;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.ArrayList;

/**
 * Created by Mitikaa on 11/14/16.
 */

public class HandheldActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>, ActivityCompat.OnRequestPermissionsResultCallback {

    public static String TAG = "HandheldActivity";

    public static ArrayList<ArrayList<String>> readings = new ArrayList<ArrayList<String>>();
    private LocationActivity locationActivity;
    protected LocationManager locationManager;
    private SMSActivity smsActivity;
    private MainActivity mainActivity;

    //double g = 9.81;
    double latitude;
    double longitude;
    String phoneNumber;
    String message;

    TextView latitudeTextView, longitudeTextView;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handheld);
        Intent i = getIntent();
        Log.i(TAG, "Activity started");
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).addApi(Awareness.API).build();

        latitudeTextView = (TextView) findViewById(R.id.latitude);
        longitudeTextView = (TextView) findViewById(R.id.longitude);

        getLocation();

        //send SMS with location
        prepareAndSendMessage();

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

    public void displayLocationCoordinates(){
        latitudeTextView.setText(String.valueOf(latitude));
        longitudeTextView.setText(String.valueOf(longitude));
    }

    public void getLocation(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationActivity = new LocationActivity(HandheldActivity.this);
        latitude = locationActivity.getLatitude();
        longitude = locationActivity.getLongitude();
        Log.d(TAG, "LocationReadings: " + latitude + ", " + longitude);
        displayLocationCoordinates();
        Toast.makeText(this, "LocationReadings: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
    }

    public void prepareAndSendMessage(){
        mainActivity = new MainActivity();
        message = mainActivity.getMessage();

        //send message to first contact
        phoneNumber = mainActivity.getPhoneNumber1();
        smsActivity = new SMSActivity(HandheldActivity.this, phoneNumber, latitude, longitude, message);
        Log.i(TAG, "SMS prepared");
        smsActivity.sendSMSMessge();

        //send message to second contact
        phoneNumber = mainActivity.getPhoneNumber2();
        smsActivity = new SMSActivity(HandheldActivity.this, phoneNumber, latitude, longitude, message);
        Log.i(TAG, "SMS prepared");
        smsActivity.sendSMSMessge();

        //send message to third contact
        phoneNumber = mainActivity.getPhoneNumber3();
        smsActivity = new SMSActivity(HandheldActivity.this, phoneNumber, latitude, longitude, message);
        Log.i(TAG, "SMS prepared");
        smsActivity.sendSMSMessge();
    }

    public void playAlertTone(MediaPlayer mediaPlayer){
        mediaPlayer.setVolume(1.0f, 1.0f); //set full volume
        mediaPlayer.setLooping(true); //will play the alert tone continuously
        mediaPlayer.start();
    }

    public void stopAlertTone(MediaPlayer mediaPlayer){
        mediaPlayer.stop();
        mediaPlayer.prepareAsync();
    }

    @Override
    protected void onDestroy() {
        Log.d("OnDestroy: ", readings.toString());
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

}
