package org.szvsszke.vitezlo2018.map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

/**
 * Wrapper class for SharedPreferences for accessing 
 * the map related preferences more easily.
 * 
 * The modified data is not saved to preferences automatically,
 * the savePreferences() method should be called at appropriate time, 
 * eg. onDestroy();
 * 
 * @author Gabor Tatrai
 */
@Deprecated
public class MapPreferences {

	private static final String MAP_TYPE = "map_type";

	private static final String LAST_CAMERA_LAT = "lat";
	private static final String LAST_CAMERA_LON = "lon";
	private static final String LAST_CAMERA_ZOOM = "zoom";

	private static final String IS_INFO_EXTENDED = "infoExtended";
	private static final String IS_INFO_LOCKED = "isHikeLocked";
	
	private static final String SELECTED_TRACK_INDEX = "selectedTrackIndex";

	private int mapType;
	private int mSelectedTrackIndex;
	private double cameraLat;
	private double cameraLon;
	private float cameraZoom;

	private boolean isInfoboxExtended;
	private boolean isInfoboxLocked;

	private SharedPreferences mPrefs;
	
	public MapPreferences (SharedPreferences preferences) {
		mPrefs = preferences;
		loadMapPreferences();
	}
	
	private void loadMapPreferences () {

		mapType = mPrefs.getInt(MAP_TYPE, GoogleMap.MAP_TYPE_NORMAL);
		mSelectedTrackIndex = mPrefs.getInt(SELECTED_TRACK_INDEX, 0);
		cameraLat = mPrefs.getLong(LAST_CAMERA_LAT, 0);
		cameraLon = mPrefs.getLong(LAST_CAMERA_LON, 0);
		cameraZoom = mPrefs.getFloat(LAST_CAMERA_ZOOM, 0);

		isInfoboxExtended = mPrefs.getBoolean(IS_INFO_EXTENDED, true);
		isInfoboxLocked = mPrefs.getBoolean(IS_INFO_LOCKED, false);
		
	}
	
	public void saveMapPreferences() {
		Editor editor = mPrefs.edit();
		editor.putInt(MAP_TYPE, mapType);

		editor.putLong(LAST_CAMERA_LAT, Double.doubleToLongBits(cameraLat));
		editor.putLong(LAST_CAMERA_LON, Double.doubleToLongBits(cameraLon));
		editor.putFloat(LAST_CAMERA_ZOOM, cameraZoom);

		editor.putBoolean(IS_INFO_EXTENDED, isInfoboxExtended);
		editor.putBoolean(IS_INFO_LOCKED, isInfoboxLocked);
		editor.putInt(SELECTED_TRACK_INDEX, mSelectedTrackIndex);

		editor.commit();
	}

	public int getMapType() {
		return mapType;
	}

	public void setMapType(int mapType) {
		this.mapType = mapType;
	}

	public boolean isInfoboxLocked() {
		return isInfoboxLocked;
	}

	public void setInfoboxLocked(boolean isInfoboxLocked) {
		this.isInfoboxLocked = isInfoboxLocked;
	}


}
