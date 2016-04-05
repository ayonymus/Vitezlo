package org.szvsszke.vitezlo.map.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;

/**
 * This class models a gpx track. Represents a subset of the 
 * gpx format.	private ArrayList<Integer> elevation;
 * **/
public class Track {
	
	private String trackName;
	private String trackDescription;
	
	private ArrayList<LatLng> trackPoints;
	private ArrayList<Integer> elevation;
	private ArrayList<Double> accuracy;
	private ArrayList<Long> time;
	private HashMap<Integer, String> trackPointNames;
	
	/**The default null constructor.*/
	public Track () {}
	
	/**
	 * Use fields
	 * *
	 * @param trackName	name of gps track
	 * @param trackDescription	description of track
	 * @param trackPoints	list of trackpoints
	 * @param trackPointDescriptions	list of description.
	 * @param elev elevation for each trackPoint
	 * @param accuracy of gps at points
	 * @param time of record
	 */
	public Track(String trackName, String trackDescription,
			ArrayList<LatLng> trackPoints, HashMap<Integer, String> tpNames,
			ArrayList<Integer> elev, ArrayList<Double> accuracy,
			ArrayList<Long> time) {
		this.trackName = trackName;
		this.trackDescription = trackDescription;
		this.trackPoints = trackPoints;
		this.trackPointNames = tpNames;
		this.elevation = elev;
		this.accuracy = accuracy;
		this.time = time;
	}



	/**
	 * @return the trackName
	 */
	public String getTrackName() {
		return trackName;
	}

	/**
	 * @param trackName the trackName to set
	 */
	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	/**
	 * @return the trackDescription
	 */
	public String getTrackDescription() {
		return trackDescription;
	}

	/**
	 * @param trackDescription the trackDescription to set
	 */
	public void setTrackDescription(String trackDescription) {
		this.trackDescription = trackDescription;
	}

	/**
	 * @return the trackPoints
	 */
	public ArrayList<LatLng> getTrackPoints() {
		return trackPoints;
	}

	/**
	 * @param trackPoints the trackPoints to set
	 */
	public void setTrackPoints(ArrayList<LatLng> trackPoints) {
		this.trackPoints = trackPoints;
	}
	
	public void setTrackNames (HashMap<Integer, String> trackPointNames) {
		this.trackPointNames = trackPointNames;
	}
	
	public HashMap<Integer, String>  getTrackPointNames (){
		return trackPointNames;
	}

	/**
	 * @return the elevation
	 */
	public ArrayList<Integer> getElevation() {
		return elevation;
	}

	/**
	 * @param elevation the elevation to set
	 */
	public void setElevation(ArrayList<Integer> elevation) {
		this.elevation = elevation;
	}
	
	/**
	 * @return the accuracy
	 */
	public ArrayList<Double> getAccuracy() {
		return accuracy;
	}

	/**
	 * @param accuracy the accuracy to set
	 */
	public void setAccuracy(ArrayList<Double> accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * @return the time
	 */
	public ArrayList<Long> getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(ArrayList<Long> time) {
		this.time = time;
	}
}
