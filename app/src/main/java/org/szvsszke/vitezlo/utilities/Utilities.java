package org.szvsszke.vitezlo.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.szvsszke.vitezlo.map.model.Track;
import org.szvsszke.vitezlo.map.model.TrackDescription;
import org.szvsszke.vitezlo.map.model.Waypoint;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * This is a collection of functions for calculating 
 * various numbers
 * */
public class Utilities {
	
	// tag for logging
	private static final String TAG = Utilities.class.getName();
	
	/**
	 * @param currentTrack The current hike track
	 * @param currentLocation of the device 
	 * @return the index of the nearest point in the mTrack list 
	 * to the current position	
	*/
	public static int findNearestPointOnCurrentTrack(List<LatLng> currentTrack,
			LatLng currentLocation) {
				
		double distance = Double.MAX_VALUE;
		
		int nearestNodeIndex = 0;
		
		for (int i = 0; i < currentTrack.size(); i++) {
			double d = distanceBetweenCoordinates(currentTrack.get(i),
					currentLocation);
			if (d < distance) {
				nearestNodeIndex = i;
				distance = d;
			}
		}
		
		return nearestNodeIndex;
	}
	
	/**
	 * This function transforms a list of checkpoints into a HashMap of Wayoints
	 * @param checkPointList a list of waypoints with the appropriate id strored in the description
	 * @return a nice mapping of checkpoints
	 * */
	public static HashMap<String, Waypoint> checkPointListToMap (ArrayList<Waypoint> checkPointList) {
		if (checkPointList == null) {
			return null;
		}
		HashMap<String, Waypoint> cpMap = new HashMap<>();
		
		for (Waypoint wp : checkPointList) {
			try {
				String key = wp.getDescription();
				cpMap.put(key, wp);
			}
			catch (Exception e){
				Log.d(TAG, "creating waypointmap error: cannot parse id at waypoint" + wp.toString());
			}
		}
		
		return cpMap;
	}
	
	/** helper method for converting a Location object to LatLng
	 *  
	 * @param location object
	 * @return LatLng from the location
	 */
	public static LatLng locationToLatLng (Location location) {
		return new LatLng(location.getLatitude(), location.getLongitude());
	}
	
	/**
	 * Method for calulating the recorded accuracy of a track.
	 * @param track that has accuracy set
	 * @return the average accuracy of the track.
	 * */
	public static double calculateAverageAccuracy(Track track) {		
		
		// check dta first
		if (track == null || track.getAccuracy() == null) {
			return -1;
		}
		double total = 0;
		
		for(Double acc : track.getAccuracy()) {
			total += acc;
		}
		return total / track.getAccuracy().size();
	}
	
	/**
	 * Fetches the version information from the manifest file.
	 * @param context of the activity
	 * 
	 * @return the versionName stored in the manifest file.
	 * */
	public static String getAppVersion(Context context){ 
		PackageManager packageManager = context.getPackageManager();
		String packageName = context.getPackageName();

		String appVersion = "not available";

		try {
		    appVersion = packageManager.getPackageInfo(packageName, 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
		    e.printStackTrace();
		}
		
		return appVersion;
	}
	
	/**
	 * Create a String out of milliseconds representing a date.
	 * in yyyy.MM.dd format
	 * @param millis date in milliseconds
	 * @return Date in in yyyy.MM.dd format
	 * */
	public static String millisToDate(final long millis) {
        Date resultdate = new Date(millis);
        return SimpleDateFormat.getInstance().format(resultdate);
	}

	/**
	 * @param milliseconds to transform
	 * @return the Hours and Minutes calculated from the milliseconds
	 * */
	public static String millisToHoursMinutes(final long milliseconds) {
		int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
		int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return hours + " : " + (minutes < 10 ? "0": "") + minutes;
	}
	
	/**
	 * @param milliseconds to transform
	 * 
	 * @return String in H : mm : ss format
	 * */
	public static String millisToHMS(long milliseconds) {
		int seconds = (int) ((milliseconds / 1000) % 60);
		int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
		int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return hours + " : " + 
				(minutes < 10 ? "0": "") + minutes + " : " +
				(seconds < 10 ? "0" : "") + seconds;
	}

	/**
	 * @param milliseconds to transform
	 * @return the Hours calculated from the milliseconds
	 * */
	public static double millisToHours(final long milliseconds) {
		return (double) milliseconds / 1000 / 60 / 60;
	}

	/**
	 * Calculates the speed.
	 * @param millis time in milliseconds
	 * @param distanceInMeters distance in meters
	 * @return String of rounded down speed in km/h
	 * */
	public static String roundedSpeedInKMH(final long millis,
			final double distanceInMeters) {
		String l = Double.toString((distanceInMeters / 1000)
				/ millisToHours(millis));
		int dot = l.indexOf(".");
		return l.substring(0, dot + 2) + " km/h";
	}

	/**
	 * Calculates the speed in km/h.
	 * @param millis time in millisecon        SimpleDateFormat sdf = SimpleDateFormat.getInstance().; 
        		//new SimpleDateFormat("yyyy.MM.dd.");ds
	 * @param distanceInMeters distance in meters
	 * @return speed in m/s
	 * */
	public static double speedInKMHDouble(final long millis,
			final double distanceInMeters) {
		return (distanceInMeters / millisToHours(millis)) / 1000;
	}

	/**
	 * @param distance in meters
	 * @return a string of the distance either in m or km
	 * */
	public static String distanceToKMorM(final double distance) {
		if (distance < 0) {
			return null;
		}
		if (distance < 1000) {			
			String l = Double.toString(distance);
			return l.substring(0, l.indexOf(".")) + " m";
		}
		String  s = Double.toString(distance / 1000);			
		return s.substring(0, s.indexOf(".") + 1) + " km";
	}

	/**
	 * Calculates the elapsed time between two dates in hours.
	 * @param start time in milliseconds
	 * @param end time in milliseconds
	 * @return elapsed time in hours
	 */
	public static double elapsedTimeInHours(
			final long start, final long end) {
		long elapsed = end - start;
		return (double) elapsed / 1000 / 60 / 60;
	}

	/**
	 * Method for calculating the distance between two coordinates
	 * on the surface of the earth in meters.
	 * Uses the Harvesine formula.
	 *
	 * @param place1 coordinates of a place
	 * @param place2 coordinates of another place
	 *
	 * @return distance in meters
	 * */
	public static double distanceBetweenCoordinates(
			final LatLng place1, final LatLng place2) {
		int R = 6371000;	// Earths radius
	    double lat1 = place1.latitude;
	    double lat2 = place2.latitude;
	    double lon1 = place1.longitude;
	    double lon2 = place2.longitude;
	    double distanceLat = Math.toRadians(lat2 - lat1);
	    double distanceLon = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(distanceLat / 2) * Math.sin(distanceLat / 2)
	    		+ Math.cos(Math.toRadians(lat1))
	    		* Math.cos(Math.toRadians(lat2))
	    	    * Math.sin(distanceLon / 2) * Math.sin(distanceLon / 2);
	    double c = 2 * Math.asin(Math.sqrt(a));
	    return R * c;
	}
	
	/**
	 * Calculate the distance between two locations.
	 * @param location1 first location
	 * @param location2 second location
	 * 
	 * @return distance in meters
	 * */
    public static double distanceBetweenCoordinates(Location location1,
			Location location2) {
		
		return distanceBetweenCoordinates(
				new LatLng(location1.getLatitude(), location1.getLongitude()),
				new LatLng(location2.getLatitude(), location2.getLongitude()));
	}

	/** Function for calculating the length of a list of LatLngs in meters.
     * @param coordinates of the track
     * @return length of track in meters
	 * */
	public static double calculateTrackLength(final List<LatLng> coordinates) {

		double total = 0;
		for (int i = 0; i < coordinates.size() - 1; i++) {
			total += distanceBetweenCoordinates(coordinates.get(i),
					coordinates.get(i + 1));
		}
		return total;
	}
	
	/**
	 * File copying method.
	 * @param src source file
	 * @param dst destination file
	 * */
	public static void copyFile(File src, File dst) throws IOException {
		
		FileInputStream fis =  new FileInputStream(src);
		FileOutputStream fos =  new FileOutputStream(dst);

	    try {
	        fis.getChannel().transferTo(0, fis.getChannel().size(), 
	        		fos.getChannel());
	    }catch (IOException e) {
	    	
	    	e.printStackTrace();
	    }finally{
	    	if (fis != null) 
	    		fis.close();
	    	if (fos != null) {
	    		fos.close();
	    	}      
	    }
	}
}
