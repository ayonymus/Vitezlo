package org.szvsszke.vitezlo2018.map;

import org.szvsszke.vitezlo2018.adapter.CustomInfoWindowAdapter;
import org.szvsszke.vitezlo2018.map.handler.CheckpointHandler;
import org.szvsszke.vitezlo2018.map.handler.SightsHandler;
import org.szvsszke.vitezlo2018.map.handler.TouristPathsHandler;
import org.szvsszke.vitezlo2018.map.handler.TrackHandler;
import org.szvsszke.vitezlo2018.map.model.Track;
import org.szvsszke.vitezlo2018.map.model.TrackDescription;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * This class is responsible for drawing lines and markers onto the google map, 
 * and for providing the necessary data for these operations.
 * */
public class MapDecorator {
	
	private static final String TAG = MapDecorator.class.getName();
	
	private static final LatLng DEFAULT_POS = 
			new LatLng(48.409291847117601, 20.724328984580993);
	private static final int USR_PATH_WIDTH = 8;
	private static final int USR_Z_INDEX = 20;
	private Activity mParent;
	 	
	private GoogleMap mMap;
	private MapView mMapView;
	
	private boolean isMapReady = false;
	
	private CheckpointHandler mCheckpoints;	
	private TrackHandler mTracks;
	private TouristPathsHandler mTouristPaths;
	private SightsHandler mSights;
	
	private LineDrawer mUserPathDrawer;
	
	private MapPreferences mMapPrefs;
	
	private TrackDescription mLastTrack;
	
	public MapDecorator(Activity parent, MapView view, MapPreferences prefs){
		mParent = parent;
		mMapView = view;
		mMapPrefs = prefs;
		
		mTracks = new TrackHandler(mParent);
		mTracks.setLineColor(mMapPrefs.getTrackColor());
		setupMapIfNeeded();
		
		mCheckpoints = new CheckpointHandler(mParent);
		mTouristPaths = new TouristPathsHandler(mParent);
		mSights = new SightsHandler(mParent);
	}
	
	/**
	 * This method sets up the default view, the listeners and adapters.
	 * */
	
	public void setupMapIfNeeded() {		
		Log.d(TAG, "setupMapIfNeeded");
		if (mMap == null) {
			isMapReady = false;
            mMap = mMapView.getMap();            
            MapsInitializer.initialize(mParent);
            mMap.setMapType(mMapPrefs.getMapType());
            
	        if(mMap == null) {
	        	Log.e(TAG, "mMap is null after getView!");
	        } else {
	            
		        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mParent));
		        mMap.setOnMarkerClickListener(new MarkerClickListener());
		        mMap.setMyLocationEnabled(true);
	            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
	            	
		            @Override
		            public void onMapLoaded() {
		                isMapReady = true;
		                Log.i(TAG, "map is ready");		                
		                mTracks.setMap(mMap);
		                mCheckpoints.setMap(mMap);
		                mTouristPaths.setMap(mMap);
		                mSights.setMap(mMap);
		                mUserPathDrawer = new LineDrawer(mMap);
		                decorate(mLastTrack);
		                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
		                		DEFAULT_POS, 14.0f) );
		            }
		        });
	        }
		}
	}
	
	/**Decorates the map based on the preferences object provided in the 
	 * constructor.
	 * @param description the Track to display.*/
	public void decorate(TrackDescription description) {
		Log.d(TAG, "decorate");	
		if (description != null) {
			mLastTrack = description;
		}
		if (isMapReady) {
			if (mLastTrack != null) {
				displayTrack(mLastTrack);
				displayCheckpoints(mLastTrack);
			}
			displaySights();
			displayTouristPaths();
			if (mMap.getMapType() != mMapPrefs.getMapType()) {
				mMap.setMapType(mMapPrefs.getMapType());
			}
		}
		
	}
	
	/**
	 * Draws the userPath onto the map. 
	 * 
	 * @param userPath the user path to draw or remove. If null is passed
	 * the line is removed from the map.
	 * 
	 * */
	public void drawUserPath(Track userPath) {
		Log.d(TAG, "drawUserPath");
		if (mUserPathDrawer == null) {
			Log.d(TAG, "mUserPathDrawer is null");
			return;
		}
		mUserPathDrawer.setColor(mMapPrefs.getUserPathColor());
		mUserPathDrawer.setWidth(USR_PATH_WIDTH);
		mUserPathDrawer.setZIndex(USR_Z_INDEX);
		mUserPathDrawer.drawPath(userPath.getTrackPoints(), mMapPrefs.isCenterToTrack());
	}

	/**Removes the user path if it is drawn.*/
	public void removeUserPath() {
		if (mUserPathDrawer != null) {
			mUserPathDrawer.removePath();
		}
	}

	private void displayTrack(TrackDescription description) {
		Log.d(TAG, "displayTrack");
		
		if (mMapPrefs.isHikeEnabled()) {
			mTracks.displayTrack(description, mMapPrefs.isCenterToTrack());
		}
		else {			
			mTracks.remove();			
		}
	}
	
	
	private void displayCheckpoints(TrackDescription description) {
		Log.d(TAG, "displayCheckpoints");
		
		if (mMapPrefs.areCheckpointsEnabled()) {
			mCheckpoints.drawCheckpoints(description);
		}
		else{			
			mCheckpoints.remove();
		}
	}
	
	private void displayTouristPaths() {
		if (mMapPrefs.areTouristPathsEnabled()) {
			mTouristPaths.draw();
		}
		else {
			mTouristPaths.remove();
		}
	}
	
	private void displaySights() {
		if (mMapPrefs.areSightsEnabled()) {
			mSights.draw();
		}
		else {
			mSights.remove();
		}
	}

	public class MarkerClickListener implements OnMarkerClickListener {
	
		@Override
		public boolean onMarkerClick(Marker marker) {
			// tourist mark is clicked return immediately
			if(marker.getTitle().contentEquals(TouristPathsHandler.TROUSIT_MARK)) {
				return true;
			}
			
	        //get the map container height
			LatLng markerLatLng = new LatLng(marker.getPosition().latitude,
			        marker.getPosition().longitude);
			
			if (!marker.getTitle().startsWith("EP")) {
				marker.showInfoWindow();
				
			} else {
				marker.showInfoWindow();
				CameraUpdate center = CameraUpdateFactory.newLatLng(markerLatLng);
				mMap.animateCamera(center);
			}
			return true;
		}
	}	
}