package org.szvsszke.vitezlo.gpslogger;

import org.szvsszke.vitezlo.utilities.Utilities;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * @brief This class inserts location data into the location database
 * 		  when the Location of device changes.
 * 
 * @author Gabor Tatrai
 * 
 * */
public class GpsLocationListener implements LocationListener{

	private final String TAG = getClass().getName();
	
	private final String mLogName;
	private Location mLastLocation;
	
	// members 
	private GpsDatabase mDB;
	/**
	 * Constructor
	 * */
	public GpsLocationListener(Context context, String logName) {
		Log.d(TAG, "constructor");
		mDB = GpsDatabase.getInstance(context);
		mLogName = logName;		
		Log.d(TAG, "names in database: " + mDB.getPathNames().size());
	}
	
	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged, lat: " + location.getLatitude() +
				" lon: " + location.getLongitude() + " ");
		//location.get
		//deliver location to the database handler.
		// check if distance is greater than the accuracy
		if (mLastLocation != null) {
			if (Utilities.distanceBetweenCoordinates(location, mLastLocation)
					>= location.getAccuracy()) {
				mDB.insertSingleLocation(location, mLogName);
			}
			
		} else {
			mDB.insertSingleLocation(location, mLogName);
		}
		mLastLocation = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "onProviderDisabled");
		// do nothing
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(TAG, "onProviderEnabled");
		// do nothing
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(TAG, "onStatusChanged");
		// do nothing
		
	}
	
	// for testing purposes
	public GpsDatabase getDB() {
		return mDB;
	}
}
