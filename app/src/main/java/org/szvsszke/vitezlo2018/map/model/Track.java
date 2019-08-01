package org.szvsszke.vitezlo2018.map.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;

@Deprecated
public class Track {

	private String trackName;

	private String trackDescription;

	private ArrayList<LatLng> trackPoints;

	/**
	 * Use fields
	 * *
	 *
	 * @param trackName              name of gps track
	 * @param trackDescription       description of track
	 * @param trackPoints            list of trackpoints
	 * @param trackPointDescriptions list of description.
	 * @param elev                   elevation for each trackPoint
	 * @param accuracy               of gps at points
	 * @param time                   of record
	 */
	@Deprecated
	public Track(String trackName, String trackDescription,
			ArrayList<LatLng> trackPoints, HashMap<Integer, String> tpNames,
			ArrayList<Integer> elev, ArrayList<Double> accuracy,
			ArrayList<Long> time) {
		this.trackName = trackName;
		this.trackDescription = trackDescription;
		this.trackPoints = trackPoints;
	}

	/**
	 * @return the trackDescription
	 */
	public String getTrackDescription() {
		return trackDescription;
	}

	/**
	 * @return the trackPoints
	 */
	public ArrayList<LatLng> getTrackPoints() {
		return trackPoints;
	}

}
