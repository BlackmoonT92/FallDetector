package edu.asu.msama1.falldetector;

/**
 * Created by Mitikaa on 3/12/17.
 *
 * This file contains constants used in some parts of the application (mainly for fetching street addresses from latitutde and longitude)
 * References:
 * https://developer.android.com/training/location/display-address.html
 */
public final class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
}