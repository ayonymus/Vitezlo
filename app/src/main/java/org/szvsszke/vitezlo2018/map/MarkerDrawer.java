package org.szvsszke.vitezlo2018.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.szvsszke.vitezlo2018.map.model.Waypoint;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**Class for drawing markers and caching drawers on the map.
 * 
 * @author Gabor Tatrai*/

public class MarkerDrawer {
	private static final String TAG = MarkerDrawer.class.getName();
	
	private GoogleMap mMap;
	private ArrayList<Marker> mVisibleMarkers;
	
	/**@param map on which the markers should be displayed. 
	 * Map must be ready!*/
	public MarkerDrawer(GoogleMap map) {
		mMap = map;
	}
	/**
	 * Put default markers onto the map
	 * @param waypoints the Waypoint representation of markers
	 * @param icon to be displayed
	 * */
	public void drawMarkers (List<Waypoint> waypoints, 
			BitmapDescriptor icon) {
		Log.d(TAG, "drawMarkers");
		
		mVisibleMarkers = new ArrayList<Marker>();
		for (Waypoint wp : waypoints){
			MarkerOptions mark = new MarkerOptions();
			mark.position(wp.getLatLng())
				.title(wp.getName())
				.snippet(wp.getComment())
				.icon(icon);
				
			Marker m = mMap.addMarker(mark);				
			// store a reference
			mVisibleMarkers.add(m);			
		}
	}
	
	/**
	 * Removes visible markers from map.
	 * */
	public void removeMarkers() {
		if (mVisibleMarkers == null) {
			return;
		}
		for (Marker marker: mVisibleMarkers) {
			marker.remove();
		}
		mVisibleMarkers = null;
	}
}
