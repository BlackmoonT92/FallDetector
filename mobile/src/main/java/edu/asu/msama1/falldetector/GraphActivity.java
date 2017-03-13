package edu.asu.msama1.falldetector;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Mitikaa on 3/10/17.
 */

public class GraphActivity extends AppCompatActivity {

    private static String TAG = "GraphActivity";

    String[] verLabels = new String[]{"100", "90", "80", "70", "60","50", "40", "30", "20", "10", "0"};
    String[] horLabels = new String[]{"0", "2", "4", "6", "8", "10", "12", "14"};

    float[] normA = new float[10];
    float[] normG = new float[10];

    FrameLayout fLayout;
    GraphView graphView;
    Handler handler;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Intent i = getIntent();
        Log.i(TAG, "Activity started");

        fLayout  = (FrameLayout) findViewById(R.id.frame);
        graphView = new GraphView(this, normA, normG , "Accelerometer and Gyroscope Data", horLabels, verLabels, GraphView.LINE);
        fLayout.addView(graphView);
        handler = new Handler();

        try {
            dbHelper = DBHelper.getInstance(GraphActivity.this);
            dbHelper.onCreateTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.post(mUpdate);
    }

    private Runnable mUpdate = new Runnable() {
        public void run() {
            GraphValues graphValues = dbHelper.getGraphValues();
            if(graphValues != null) {
                graphView.setValues(graphValues.getNormA(), graphValues.getNormG());
                Log.i(TAG, "Inside run " + Arrays.toString(graphValues.getNormA()) + ", " + Arrays.toString(graphValues.getNormG()));

            } else {
                graphView.setValues(new float[]{0}, new float[]{0});
            }
            graphView.invalidate();
            handler.postDelayed(this, 1000);
        }

    };

}
