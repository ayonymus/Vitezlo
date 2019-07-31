package org.szvsszke.vitezlo2018.map;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.szvsszke.vitezlo2018.adapter.CustomInfoWindowAdapter;
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint;
import org.szvsszke.vitezlo2018.domain.entity.Sight;
import org.szvsszke.vitezlo2018.map.handler.TouristPathsHandler;
import org.szvsszke.vitezlo2018.map.handler.TrackHandler;
import org.szvsszke.vitezlo2018.map.model.TrackDescription;
import org.szvsszke.vitezlo2018.presentation.map.marker.CheckpointHandler;
import org.szvsszke.vitezlo2018.presentation.map.marker.SightsHandler;

import android.app.Activity;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;

/**
 * This class is responsible for drawing lines and markers onto the google map, 
 * and for providing the necessary data for these operations.
 * TODO too many responsibilities, cut smaller
 * */
public class MapDecorator {
	
	private static final String TAG = MapDecorator.class.getName();

	private static final LatLng DEFAULT_POS = 
			new LatLng(48.409291847117601, 20.724328984580993);

	private Activity mParent;

	private GoogleMap mMap;
	private MapView mMapView;
	
	private boolean isMapReady = false;

	private TrackHandler mTracks;
	private TouristPathsHandler mTouristPaths;

	private LineDrawer mUserPathDrawer;

	private MapPreferences mMapPrefs;

	private TrackDescription mLastTrack;

	private CheckpointHandler checkpointHandler;
	private SightsHandler sightsHandler;

	@Inject
	public MapDecorator(CheckpointHandler checkpointHandler, SightsHandler sightsHandler){
		this.checkpointHandler = checkpointHandler;
		this.sightsHandler = sightsHandler;
	}

	// TODO remove context dependency
	public void init(Activity parent, MapView view, MapPreferences prefs) {
        mParent = parent;
        mMapView = view;

        mMapPrefs = prefs;

        mTracks = new TrackHandler(mParent);
        mTracks.setLineColor(mMapPrefs.getTrackColor());
        setupMapIfNeeded();


        mTouristPaths = new TouristPathsHandler(mParent);
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
		                mTouristPaths.setMap(mMap);
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
			}
			displayTouristPaths();
			if (mMap.getMapType() != mMapPrefs.getMapType()) {
				mMap.setMapType(mMapPrefs.getMapType());
			}
		}
		
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
	
	public void markCheckpoints(@NonNull List<Checkpoint> checkpoints) {
		if (mMapPrefs.areCheckpointsEnabled()) {
			if (mMap != null) {
				checkpointHandler.showCheckpoints(mMap, checkpoints);
			}
		}
		else{
			checkpointHandler.hideCheckpoints();
		}
	}

	public void markSights(@NonNull List<Sight> sights) {
        if (mMapPrefs.areSightsEnabled()) {
            if(mMap != null) {
                sightsHandler.showSights(mMap, sights);
            }
        }
        else {
            sightsHandler.hideSights();
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