package edu.asu.msama1.falldetector;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2017 Mitikaa Sama,
 *
 * The Instructor and the Arizona State University
 * has the right to build and evaluate the software package
 * for the purpose of determining the grade and program assessment.
 *
 * Purpose: Masters Applied Project
 *
 * @author Mitikaa Sama on 10/21/16.
 *
 * This class uses device's motion sensors to get Accelerometer and Gyroscope readings and sends it to the handheld device for further processing
 *
 * References:
 * http://www.androprogrammer.com/2015/05/android-wear-how-to-send-data-from.html
 * https://developer.android.com/training/wearables/data-layer/index.html
 * https://github.com/mvyas85/Fall-Alarm-Wearable/tree/master/wear/src/main/java/com/capstone/
 * https://github.com/petrnalevka/wear/tree/master/watch/app/src/main/java/com/urbandroid/wear
 */
public class Wearable extends Activity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static String TAG = "WearListActivity";

    /**
     * Instance of SensorManager
     * It will let users access device's sensors
     */
    private SensorManager sensorManager;

    /**
     * TextView to display X-axis reading of Accelerometer
     */
    private TextView AccelerometerX;

    /**
     * TextView to display Y-axis reading of Accelerometer
     */
    private TextView AccelerometerY;

    /**
     * TextView to display Z-axis reading of Accelerometer
     */
    private TextView AccelerometerZ;

    /**
     * TextView to display X-axis reading of Gyroscope
     */
    private TextView GyroscopeX;

    /**
     * TextView to display Y-axis reading of Gyroscope
     */
    private TextView GyroscopeY;

    /**
     * TextView to display Z-axis reading of Gyroscope
     */
    private TextView GyroscopeZ;

    /**
     * Acceleration of gravity
     */
    double g = 9.81;

    /**
     * Variables to store values for Accelerometer and Gyroscope sensor readings
     * aXValue is X-axis reading of Accelerometer
     * aYValue is Y-axis reading of Accelerometer
     * aZValue is Z-axis reading of Accelerometer
     * gXValue is X-axis reading of Gyroscope
     * gYValue is Y-axis reading of Gyroscope
     * gZValue is Z-axis reading of Gyroscope
     */
    private float aXValue, aYValue, aZValue, gXValue, gYValue, gZValue;

    /**
     * the connected device to send the message to
     */
    private Node mNode;

    /**
     * Node  ID for connected device to which message has to be sent
     */
    private String nodeId;

    /**
     * Maximum time to wait while trying to connect the client to Google Play services
     */
    public static long CONNECTION_TIME_OUT_MS = 10000;

    /**
     * Instance of GoogleApiClient
     * Helps make a connection between the application with Google APIs
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Inside onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_wearable);

        //Initialize TextViews
        AccelerometerX = (TextView) findViewById(R.id.accelX);
        AccelerometerY = (TextView) findViewById(R.id.accelY);
        AccelerometerZ = (TextView) findViewById(R.id.accelZ);
        GyroscopeX = (TextView) findViewById(R.id.gyroX);
        GyroscopeY = (TextView) findViewById(R.id.gyroY);
        GyroscopeZ = (TextView) findViewById(R.id.gyroZ);

        //Registering for SensorService
        //Returns the handle to a system-level service
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Registering SensorServiceListener for Accelerometer and Gyroscope Sensors
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), sensorManager.SENSOR_DELAY_NORMAL);

        //call method to retrieve connected nodes
        retrieveDeviceNode();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * get Wearable API client
     * @param context : current application context
     * @return GoogleApiClient
     */
    private GoogleApiClient getGoogleApiClient(Context context) {
        Log.d(TAG, "Inside getGoogleApiClient");
        return new GoogleApiClient.Builder(context)
                .addApi(com.google.android.gms.wearable.Wearable.API)
                .build();
    }

    /**
     * Retrieve all connected nodes to this device
     * Selecting first found node as the connected node to which data needs to be sent
     */
    private void retrieveDeviceNode() {
        final GoogleApiClient mClient = getGoogleApiClient(this);
        Log.d(TAG, "Inside retrieveDeviceNode");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Inside run for retrieveDeviceNode");
                mClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        com.google.android.gms.wearable.Wearable.NodeApi.getConnectedNodes(mClient).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                mClient.disconnect();
            }
        }).start();
    }

    /**
     * Called when a new sensor event is observed
     * @param sensorEvent : new sensor event
     */
    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {

        //if change in Accelerometer readings
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometerReadings(sensorEvent);
        }

        //if change in Gyroscope readings
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            getGyroscopeReadings(sensorEvent);
        }
    }

    /**
     * Called when accuracy of sensor is changed
     * @param sensor : sensor
     * @param i : new accuracy of sensor
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * Parses accelerometer sensor readings and send message to handheld device
     * @param sensorEvent : new accelerometer sensor event
     */
    public void getAccelerometerReadings(SensorEvent sensorEvent) {
        Log.d(TAG, "Inside getAccelerometerReadings");

        //get Accelerometer readings for x-axis, y-axis and z-axis
        float[] accelerometerValues = sensorEvent.values;
        this.aXValue = accelerometerValues[0];
        this.aYValue = accelerometerValues[1];
        this.aZValue = accelerometerValues[2];

        //set TextView values to new sensor readings
        AccelerometerX.setText("" + aXValue);
        AccelerometerY.setText("" + aYValue);
        AccelerometerZ.setText("" + aZValue);

        //method to send sensor data to Handheld device
        sendMessage("AccelerometerReadingChanged "+aXValue+" "+aYValue+" "+aZValue);
    }

    /**
     * Parses gyroscope sensor readings and send message to handheld device
     * @param sensorEvent : new gyroscope sensor event
     */
    public void getGyroscopeReadings(SensorEvent sensorEvent) {
        Log.d(TAG, "Inside getGyroscopeReadings");

        //get Gyroscope readings for x-axis, y-axis and z-axis
        float[] gyroscopeValues = sensorEvent.values;
        this.gXValue = gyroscopeValues[0];
        this.gYValue = gyroscopeValues[1];
        this.gZValue = gyroscopeValues[2];

        //set TextView values to new sensor readings
        GyroscopeX.setText("" + gXValue);
        GyroscopeY.setText("" + gYValue);
        GyroscopeZ.setText("" + gZValue);

        //method to send sensor data to Handheld device
        sendMessage("GyroscopeReadingChanged "+gXValue+" "+gYValue+" "+gZValue);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Inside onConnected");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Send message to handheld using MessageApi
     * @param Key : message to be set to the handheld device for processing
     */
    private void sendMessage(final String Key) {
        Log.d(TAG, "Inside sendMessage");

        //get instance of wearable api client
        final GoogleApiClient client = getGoogleApiClient(this);
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Inside run for sendMessage");
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    //sends message to handheld
                    com.google.android.gms.wearable.Wearable.MessageApi.sendMessage(client, nodeId, Key, null);
                    client.disconnect();
                }
            }).start();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Wearable Page") // TODO: Define a title for the content shown.
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
}
