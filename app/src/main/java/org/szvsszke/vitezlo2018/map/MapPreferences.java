package org.szvsszke.vitezlo2018.map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
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
	
	private static final String TAG = MapPreferences.class.getName();
	
	private static final String MAP_TYPE = "map_type";
	private static final String IS_HIKE_ENABLED = "isHikeEnabled";
	private static final String IS_CHECKPOINT_ENABLED = "isCheckpointEnabled";
	private static final String IS_SIGHT_ENABLED = "isSightEnabled";
	private static final String IS_TOURIST_PATH_ENABLED = "isTouristPathEnabled";
	private static final String LAST_CAMERA_LAT = "lat";
	private static final String LAST_CAMERA_LON = "lon";
	private static final String LAST_CAMERA_ZOOM = "zoom";
	public static final String CENTER_TO_TRACK = "centerToTrack";
	
	private static final String IS_INFO_EXTENDED = "infoExtended";
	private static final String IS_INFO_LOCKED = "isHikeLocked";
	
	public static final String IS_CONTROL_ENABLED = "isViewControlEnabled";
	public static final String IS_INFO_ENABLED = "infoEnabled";
	public static final String IS_ZOOM_ENABLED = "isZoomEnabled";
	public static final String TRACK_COLOR = "hikeColor";
	public static final String USER_PATH_COLOR = "userHikeColor";
	
	private static final String SELECTED_TRACK_INDEX = "selectedTrackIndex";
	
	public static final int DEFAULT_USER_HIKE_COLOR  = 
			Color.argb(255, 150, 150, 255);
	public static final int DEFAULT_TRACK_COLOR = Color.argb(255, 0, 150, 0);
	
	private boolean mIsHikeEnabled;
	private boolean mAreCheckpointsEnabled;
	private boolean mIsSightsEnabled;
	private boolean mAreTouristPathsEnabled;
	private int mapType;
	private int mSelectedTrackIndex;
	private double cameraLat;
	private double cameraLon;
	private float cameraZoom;

	private boolean isInfoboxExtended;
	private boolean isInfoboxLocked;
	
	//no setters for these
	private boolean isControlBoxEnabled;
	private boolean isInfoboxEnabled;
	
	private SharedPreferences mPrefs;
	
	public MapPreferences (SharedPreferences preferences) {
		Log.d(TAG, "instantiate");
		mPrefs = preferences;
		loadMapPreferences();
	}
	
	private void loadMapPreferences () {
		Log.d(TAG, "loadMapPreferences");
		
		mIsHikeEnabled = mPrefs.getBoolean(IS_HIKE_ENABLED, true);
		mAreCheckpointsEnabled = mPrefs.getBoolean(IS_CHECKPOINT_ENABLED, true);
		mAreTouristPathsEnabled = mPrefs.getBoolean(
				IS_TOURIST_PATH_ENABLED, false);
		mIsSightsEnabled = mPrefs.getBoolean(IS_SIGHT_ENABLED, false);
		mapType = mPrefs.getInt(MAP_TYPE, GoogleMap.MAP_TYPE_NORMAL);
		mSelectedTrackIndex = mPrefs.getInt(SELECTED_TRACK_INDEX, 0);
		cameraLat = mPrefs.getLong(LAST_CAMERA_LAT, 0);
		cameraLon = mPrefs.getLong(LAST_CAMERA_LON, 0);
		cameraZoom = mPrefs.getFloat(LAST_CAMERA_ZOOM, 0);

		isInfoboxEnabled = mPrefs.getBoolean(IS_INFO_ENABLED, true);
		isInfoboxExtended = mPrefs.getBoolean(IS_INFO_EXTENDED, true);
		isInfoboxLocked = mPrefs.getBoolean(IS_INFO_LOCKED, false);
		
		isControlBoxEnabled = mPrefs.getBoolean(IS_CONTROL_ENABLED, true);
	}
	
	public void saveMapPreferences() {
		Log.d(TAG, "saveMapPreferences");
		Editor editor = mPrefs.edit();
		editor.putBoolean(IS_HIKE_ENABLED, mIsHikeEnabled);
		editor.putBoolean(IS_CHECKPOINT_ENABLED, mAreCheckpointsEnabled);
		editor.putBoolean(IS_SIGHT_ENABLED, mIsSightsEnabled);
		editor.putBoolean(IS_TOURIST_PATH_ENABLED, mAreTouristPathsEnabled);
		editor.putInt(SELECTED_TRACK_INDEX, mSelectedTrackIndex);
		editor.putInt(MAP_TYPE, mapType);

		editor.putLong(LAST_CAMERA_LAT, Double.doubleToLongBits(cameraLat));
		editor.putLong(LAST_CAMERA_LON, Double.doubleToLongBits(cameraLon));
		editor.putFloat(LAST_CAMERA_ZOOM, cameraZoom);

		editor.putBoolean(IS_INFO_EXTENDED, isInfoboxExtended);
		editor.putBoolean(IS_INFO_LOCKED, isInfoboxLocked);

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

	public boolean isControlBoxEnabled() {
		return isControlBoxEnabled;
	}

	public boolean isInfoboxEnabled() {
		return isInfoboxEnabled;
	}

}
