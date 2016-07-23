package org.szvsszke.vitezlo.map;

import java.util.List;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Helper class for drawing and removing polylines; 
 * */

public class LineDrawer {
	private static final String TAG = LineDrawer.class.getName();
	
	private static final int DEFAULT_LINE_WIDTH = 5;
	private static final int DEFAULT_Z_INDEX = 10; 
	private static final int ARGB_GREEN = 
			Color.argb(255, 100, 255, 100);
	
	private Polyline mPath;
	private GoogleMap mMap;
	
	private int mColor = ARGB_GREEN;
	private float mZIndex = DEFAULT_Z_INDEX;
	private int mWidth = DEFAULT_LINE_WIDTH;
	
	/**@param map should not be null.*/
	public LineDrawer(GoogleMap map) {
		mMap = map;
	}
	/**
	 * Draw a line on the map defined my a list of latlng and color.
	 * Does nothing if the GoogleMap provided to the constructor is null.
	 * @param lineCoordinates a list of LatLng's representing the line
	 * @param center moves the camera so that the path is in the center
	 * */
	public void drawPath(List<LatLng> lineCoordinates, boolean center) {
		Log.v(TAG, "drawPath");
		if (mMap == null) {
			Log.d(TAG, "drawPath: GoogleMap is still null!");
			return;
		}
		removePath();
		if (lineCoordinates != null) {
			//draw track with poylines
			PolylineOptions lineOptions = new PolylineOptions();
			lineOptions.geodesic(true)			
			.addAll(lineCoordinates)
			.color(mColor)
			.zIndex(mZIndex)
			.width(mWidth);
			mPath = mMap.addPolyline(lineOptions);
			if (center) {
				centerToTrack(lineCoordinates);
			}
		}
		else {
			Log.e(TAG, "drawPath: linecoordinates is null!");
		}
	}
	
	/**Removes the path drawn from the map.*/
	public void removePath() {
		if (mPath != null) {
			mPath.remove();
		}
	}
	
	private void centerToTrack(List<LatLng> lineCoordinates) {
    	double latLow = lineCoordinates.get(0).latitude;
    	double latHi = lineCoordinates.get(0).latitude;
    	double lonLow = lineCoordinates.get(0).longitude;
    	double lonHi = lineCoordinates.get(0).longitude;
    	
    	for (LatLng coordinate : lineCoordinates) {
    		if (coordinate.latitude < latLow) {
    			latLow = coordinate.latitude;
    		}
    		else if (coordinate.latitude > latHi) {
    			latHi = coordinate.latitude;
    		}
    		
    		if (coordinate.longitude < lonLow) {
    			lonLow = coordinate.longitude;
    		}
    		else if (coordinate.longitude > lonHi) {
    			lonHi = coordinate.longitude;
    		}
    	}
		
		mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
			new LatLngBounds(new LatLng(latLow, lonLow), 
    				new LatLng(latHi, lonHi)), 80));
		
	}
	
	public void setZIndex(float mZIndex) {
		this.mZIndex = mZIndex;
	}
	
	public void setWidth(int mWidth) {
		this.mWidth = mWidth;
	}
	public void setColor(int color) {
		mColor = color;
		
	}
	
	
}	
