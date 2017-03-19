package edu.asu.msama1.falldetector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Mitikaa on 2/17/17.
 *
 * References:
 * http://clover.studio/2016/08/09/getting-current-location-in-android-using-location-manager/
 * https://developer.android.com/reference/android/location/LocationManager.html
 * https://developer.android.com/training/location/change-location-settings.html
 */
public class LocationActivity  implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static String TAG = "LocationActivity";

    /**
     * Current application context
     */
    private final Context mContext;

    /**
     * LocationManager provides access to the system location services
     */
    protected LocationManager locationManager;

    /**
     * Location object to store current location coordinates
     */
    protected Location location;

    /**
     * Stores latitude coordinate
     */
    double latitude = 0;

    /**
     * Stores longitude coordinate
     */
    double longitude = 0;

    /**
     * Indicates the minimum distance between location updates
     * evey 10 meters
     */
    private static final long MINIMUM_DISTANCE_FOR_UPDATES = 10;

    /**
     * Indicates the minimum time interval between location updates
     * every 60 seconds
     */
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000 * 60;

    /**
     * Permission variable to store access for "android.permission.ACCESS_FINE_LOCATION"
     */
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    /**
     * Permission variable to store access for "android.permission.ACCESS_COARSE_LOCATION"
     */
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;

    /**
     * Constructor
     * @param mContext
     */
    public LocationActivity(Context mContext){
        super();
        this.mContext = mContext;
        Log.i(TAG, "longitude: " + longitude);
        Log.i(TAG, "latitude: " + latitude);
        //get current location
        getLocation();
        Log.i(TAG, "longitude: " + longitude);
        Log.i(TAG, "latitude: " + latitude);
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /**
     * Location getter
     * Method to check if user has granted access to ACCESS_FINE_LOCATION and then use GPS to get current coordinates
     * @return location
     */
    public Location getLocation() {
        try{
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            //true if the provider exists and is enabled
            boolean checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            //if provider is not available, inform user
            if (!checkGPS && !checkNetwork) {
                Log.e(TAG, "No Service Provider Available");
            } else {
                //else get the location coordinates
                if (checkNetwork) {
                    //Checks whether the app has a given permission and whether the app op that corresponds to this permission is allowed.
                    if ((ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            && (ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }

                    //Register for location updates using the named provider
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MINIMUM_TIME_BETWEEN_UPDATES,
                                MINIMUM_DISTANCE_FOR_UPDATES,
                                this);
                    if (locationManager != null) {
                        //Get the location indicating the data from the last known location fix obtained from the given provider
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }

                    if (location != null) {
                        Log.i(TAG, "Fetching new coordinates ");
                        //extract latitude and longitude values from the location
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                    } catch (SecurityException e){
                        Log.d(TAG, e.toString());
                    }

                }
            }

            //if GPS provider is available
            if (checkGPS) {
                Toast.makeText(mContext, "GPS Available", Toast.LENGTH_SHORT).show();
                if (location == null) {
                    try {
                        //Register for location updates using the named provider
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MINIMUM_TIME_BETWEEN_UPDATES,
                                MINIMUM_DISTANCE_FOR_UPDATES,
                                this);
                        if (locationManager != null) {
                            //Get the location indicating the data from the last known location fix obtained from the given provider
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }

                        if (location != null) {
                            //extract latitude and longitude values from the location
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    } catch (SecurityException e){
                        Log.d(TAG, e.toString());
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return location;
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
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLocation();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLocation();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    /**
     * Removes all location updates for the specified LocationListener
     * This method will disable any future istener updates
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationManager.removeUpdates((android.location.LocationListener) this);
            return;

        }
    }

    /**
     * Latitude value getter
     * @return latitude
     */
    public double getLatitude() {
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    /**
     * Longitude value getter
     * @return longitude
     */
    public double getLongitude() {
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    /**
     * Latitude setter
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Longitude setter
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Location setter
     * @param location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

}
