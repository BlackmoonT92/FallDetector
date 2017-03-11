package edu.asu.msama1.falldetector;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class Wearable extends Activity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private SensorManager sensorManager;

    private TextView AccelerometerX;
    private TextView AccelerometerY;
    private TextView AccelerometerZ;
    private TextView GyroscopeX;
    private TextView GyroscopeY;
    private TextView GyroscopeZ;
    double g = 9.81;
    //double squareA = 9.81;
    //double squareG = 0.0;

    private float aXValue, aYValue, aZValue, gXValue, gYValue, gZValue;

    Node mNode; // the connected device to send the message to
    String nodeId; //Node  ID for connected device to which message has to be sent
    GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    public static long CONNECTION_TIME_OUT_MS = 10000;
    public static String TAG = "WearListActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Inside onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_wearable);
        AccelerometerX = (TextView) findViewById(R.id.accelX);
        AccelerometerY = (TextView) findViewById(R.id.accelY);
        AccelerometerZ = (TextView) findViewById(R.id.accelZ);
        GyroscopeX = (TextView) findViewById(R.id.gyroX);
        GyroscopeY = (TextView) findViewById(R.id.gyroY);
        GyroscopeZ = (TextView) findViewById(R.id.gyroZ);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //register sensor manager
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), sensorManager.SENSOR_DELAY_NORMAL);

        retrieveDeviceNode();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        Log.d(TAG, "Inside getGoogleApiClient");
        return new GoogleApiClient.Builder(context)
                .addApi(com.google.android.gms.wearable.Wearable.API)
                .build();
    }

    private void retrieveDeviceNode() {
        final GoogleApiClient client = getGoogleApiClient(this);
        Log.d(TAG, "Inside retrieveDeviceNode");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Inside run for retrieveDeviceNode");
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        com.google.android.gms.wearable.Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }


    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            new CountDownTimer(1000, 1000) {
                public void onFinish() {
                    getAccelerometerReadings(sensorEvent);
                }

                public void onTick(long millisUntilFinished) {

                }
            }.start();
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //1 second countdown, 1 second interval
            new CountDownTimer(1000, 1000) {
                public void onFinish() {
                    getGyroscopeReadings(sensorEvent);
                }

                public void onTick(long millisUntilFinished) {

                }
            }.start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void getAccelerometerReadings(SensorEvent sensorEvent) {
        //Log.d(TAG, "Inside getAccelerometerReadings");

        float[] accelerometerValues = sensorEvent.values;
        this.aXValue = accelerometerValues[0];
        this.aYValue = accelerometerValues[1];
        this.aZValue = accelerometerValues[2];

        AccelerometerX.setText("" + aXValue);
        AccelerometerY.setText("" + aYValue);
        AccelerometerZ.setText("" + aZValue);

        sendMessage("AccelerometerReadingChanged "+aXValue+" "+aYValue+" "+aZValue);
    }

    public void getGyroscopeReadings(SensorEvent sensorEvent) {
        //Log.d(TAG, "Inside getGyroscopeReadings");

        float[] gyroscopeValues = sensorEvent.values;
        this.gXValue = gyroscopeValues[0];
        this.gYValue = gyroscopeValues[1];
        this.gZValue = gyroscopeValues[2];

        GyroscopeX.setText("" + gXValue);
        GyroscopeY.setText("" + gYValue);
        GyroscopeZ.setText("" + gZValue);

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
     * Send message to mobile handheld
     */
    private void sendMessage(final String Key) {
        Log.d(TAG, "Inside sendMessage");
        final GoogleApiClient client = getGoogleApiClient(this);
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Inside run for sendMessage");
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
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
        client2.connect();
        AppIndex.AppIndexApi.start(client2, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client2, getIndexApiAction());
        client2.disconnect();
    }
}
