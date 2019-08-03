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
import org.szvsszke.vitezlo2018.domain.entity.Track;
import org.szvsszke.vitezlo2018.presentation.map.camera.GoogleMapExtensionsKt;
import org.szvsszke.vitezlo2018.presentation.map.line.LineHandler;
import org.szvsszke.vitezlo2018.presentation.map.line.TouristPathHandler;
import org.szvsszke.vitezlo2018.presentation.map.marker.CheckpointHandler;
import org.szvsszke.vitezlo2018.presentation.map.marker.SightsHandler;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;

import static org.szvsszke.vitezlo2018.presentation.map.marker.TouristMarkFactory.TOURIST_MARK;

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

	private MapPreferences mMapPrefs;

	private final CheckpointHandler checkpointHandler;
	private final SightsHandler sightsHandler;
	private final LineHandler trackHandler;
	private final TouristPathHandler touristPathHandler;

	@Inject
	public MapDecorator(CheckpointHandler checkpointHandler, SightsHandler sightsHandler, LineHandler lineHandler,
			TouristPathHandler touristPathHandler){
		this.checkpointHandler = checkpointHandler;
		this.sightsHandler = sightsHandler;
		this.trackHandler = lineHandler;
		this.touristPathHandler = touristPathHandler;
	}

	// TODO remove context dependency
	public void init(Activity parent, MapView view, MapPreferences prefs) {
        mParent = parent;
        mMapView = view;

        mMapPrefs = prefs;
        setupMapIfNeeded();

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
		                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
		                		DEFAULT_POS, 14.0f) );
		            }
		        });
	        }
		}
	}

	public void decorate() {
		Log.d(TAG, "decorate");
		if (isMapReady) {
			if (mMap.getMapType() != mMapPrefs.getMapType()) {
				mMap.setMapType(mMapPrefs.getMapType());
			}
		}
	}

	// TODO accept Track object
	// TODO set color
	// TODO center to area
    public void markTrack(List<LatLng> data, LatLng one, LatLng two) {
	    if (mMapPrefs.isHikeEnabled()) {
	    	GoogleMapExtensionsKt.centerToArea(mMap, one, two, 100);
		    trackHandler.drawLine(mMap, data, Color.argb(255, 100, 255, 100));
	    } else {
	    	trackHandler.removeLine();
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
	
	public void displayTouristPaths(@NonNull List<Track> paths) {
		if (mMapPrefs.areTouristPathsEnabled()) {
			touristPathHandler.drawPaths(mMap, paths);
		}
		else {
			touristPathHandler.removePaths();
		}
	}

	public class MarkerClickListener implements OnMarkerClickListener {
	
		@Override
		public boolean onMarkerClick(Marker marker) {
			// tourist mark is clicked return immediately
			if(marker.getTitle().contentEquals(TOURIST_MARK)) {
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