package edu.asu.msama1.falldetector;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.Arrays;

/**
 * Copyright 2017 Mitikaa Sama,
 *
 * The Instructor and the Arizona State University
 * has the right to build and evaluate the software package
 * for the purpose of determining the grade and program assessment.
 *
 * Purpose: Masters Applied Project
 *
 * @author Mitikaa Sama on 3/10/17.
 *
 * This class is used to plot line graph to show accelerometer and gyroscope norm readings
 * It uses last 10 records stored in the database and displays the values in form of a line graph
 */
public class GraphActivity extends AppCompatActivity {

    private static String TAG = "GraphActivity";

    /**
     * X-axis scale for the graph
     */
    String[] verLabels = new String[]{"100", "90", "80", "70", "60","50", "40", "30", "20", "10", "0"};

    /**
     * Y-axis scale for the graph
     */
    String[] horLabels = new String[]{"0", "2", "4", "6", "8", "10", "12", "14"};

    /**
     * array of accelerometer norm values to be plooted on the graph
     */
    float[] normA = new float[10];

    /**
     * array of gyroscope norm values to be plotted on the graph
     */
    float[] normG = new float[10];

    /**
     * Frame Layout for graph view
     */
    FrameLayout fLayout;

    /**
     * Graph view to display change in accelerometer and gyroscope norm readings
     */
    GraphView graphView;

    /**
     * Will help in passing messages and runnable objects
     */
    Handler handler;

    /**
     * Instance of DbHelper to access database and tables
     * Will be used to fecth acceerometer and gyroscope data from respective tables
     */
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        //after start activity requested from MainActivity
        Intent i = getIntent();
        Log.i(TAG, "Activity started");

        //action bar to change settings of the app
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //initialize frame layout
        fLayout  = (FrameLayout) findViewById(R.id.frame);

        //initialize graph view
        graphView = new GraphView(this, normA, normG , "Accelerometer and Gyroscope Data", horLabels, verLabels, GraphView.LINE);

        //add graph view to the frame layout
        fLayout.addView(graphView);

        //initialize handler
        handler = new Handler();

        try {
            dbHelper = DBHelper.getInstance(GraphActivity.this);
            dbHelper.onCreateTable();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Causes the Runnable r to be added to the message queue
        //used when you want to do operations in UI thread
        handler.post(mUpdate);
    }

    /**
     * updates the graph view with new values fetched from the database
     */
    private Runnable mUpdate = new Runnable() {
        public void run() {
            //get graph values from database
            GraphValues graphValues = dbHelper.getGraphValues();
            if(graphValues != null) {
                //set graphview values with accelerometer and gyroscope norm arrays
                graphView.setValues(graphValues.getNormA(), graphValues.getNormG());
                Log.i(TAG, "Inside run " + Arrays.toString(graphValues.getNormA()) + ", " + Arrays.toString(graphValues.getNormG()));

            } else {
                //if no values fetched from database, display graph with accelerometer and gyroscope array values as 0
                graphView.setValues(new float[]{0}, new float[]{0});
            }
            //forces the whole view to draw
            graphView.invalidate();

            //delay the runnable by 1 second
            handler.postDelayed(this, 1000);
        }

    };

    /**
     * This method handles action button items
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                //start new intent to display page to change preference settings
                //start main activity
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return true;

            case R.id.action_graph:
                //currently on graph activity, so do nothing
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
