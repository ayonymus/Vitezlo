package org.szvsszke.vitezlo2018.map.handler;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;

public abstract class AbstractMapItemHandler {
	
	protected GoogleMap mMap;
	protected Activity mParent;
	
	public AbstractMapItemHandler(Activity activity) {
		mParent = activity;
	}
	
	/**Removes items from the map*/
	abstract public void remove();
	abstract public void prepare();
	
	/**
	 * Sets the googlemap.
	 * @param map that is already prepared and ready!
	 * */
	public void setMap(GoogleMap map) {
		mMap = map;
	}
	
}
