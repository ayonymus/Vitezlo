package org.szvsszke.vitezlo2018.map;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.jetbrains.annotations.NotNull;
import org.szvsszke.vitezlo2018.adapter.CustomInfoWindowAdapter;
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint;
import org.szvsszke.vitezlo2018.domain.entity.Sight;
import org.szvsszke.vitezlo2018.domain.entity.Track;
import org.szvsszke.vitezlo2018.domain.preferences.MapStatus;
import org.szvsszke.vitezlo2018.presentation.map.camera.GoogleMapExtensionsKt;
import org.szvsszke.vitezlo2018.presentation.map.line.LineHandler;
import org.szvsszke.vitezlo2018.presentation.map.line.TouristPathHandler;
import org.szvsszke.vitezlo2018.presentation.map.marker.CheckpointHandler;
import org.szvsszke.vitezlo2018.presentation.map.marker.SightsHandler;

import android.app.Activity;
import android.graphics.Color;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;

import static org.szvsszke.vitezlo2018.presentation.map.camera.GoogleMapExtensionsKt.getStatus;
import static org.szvsszke.vitezlo2018.presentation.map.camera.GoogleMapExtensionsKt.switchType;
import static org.szvsszke.vitezlo2018.presentation.map.camera.GoogleMapExtensionsKt.toLatLng;
import static org.szvsszke.vitezlo2018.presentation.map.marker.TouristMarkFactory.TOURIST_MARK;

/**
 * This class is responsible for drawing lines and markers onto the google map, 
 * and for providing the necessary data for these operations.
 * TODO too many responsibilities, refactor
 * */
public class MapDecorator {

	private Activity mParent;

	private GoogleMap mMap;

	private MapView mMapView;
	private boolean isMapReady = false;

	private final CheckpointHandler checkpointHandler;
	private final SightsHandler sightsHandler;
	private final LineHandler trackHandler;
	private final TouristPathHandler touristPathHandler;

	private MapStatus mapStatus = null;

	@Inject
	public MapDecorator(CheckpointHandler checkpointHandler, SightsHandler sightsHandler, LineHandler lineHandler,
			TouristPathHandler touristPathHandler){
		this.checkpointHandler = checkpointHandler;
		this.sightsHandler = sightsHandler;
		this.trackHandler = lineHandler;
		this.touristPathHandler = touristPathHandler;
	}

	// TODO remove context dependency
	public void init(Activity parent, MapView view) {
        mParent = parent;
        mMapView = view;

        setupMapIfNeeded();

    }

	private void setupMapIfNeeded() {
		if (mMap == null) {
			isMapReady = false;
            mMap = mMapView.getMap();            
            MapsInitializer.initialize(mParent);

	        if(mMap != null) {
		        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mParent));
		        mMap.setOnMarkerClickListener(new MarkerClickListener());
		        mMap.setMyLocationEnabled(true);
	            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
	            	
		            @Override
		            public void onMapLoaded() {
		                isMapReady = true;
		                if (mapStatus != null) {
		                	setMapStatus(mMap, mapStatus);
						}
		            }
		        });
	        }
		}
	}

	private void setMapStatus(@NonNull GoogleMap map, @NonNull MapStatus mapStatus) {
		map.setMapType(mapStatus.getMapType());
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(
				toLatLng(mapStatus.getCameraPosition()), mapStatus.getCameraZoom()));
	}

	// TODO accept Track object
	// TODO set color
    public void markTrack(List<LatLng> data, LatLng one, LatLng two) {
		GoogleMapExtensionsKt.centerToArea(mMap, one, two, 100);
		trackHandler.drawLine(mMap, data, Color.argb(255, 100, 255, 100));
    }
	
	public void markCheckpoints(@NonNull List<Checkpoint> checkpoints) {
		checkpointHandler.showCheckpoints(mMap, checkpoints);
	}

	public void hideCheckpoints() {
		checkpointHandler.hideCheckpoints();
	}

	public void markSights(@NonNull List<Sight> sights) {
		sightsHandler.showSights(mMap, sights);
	}

    public void hideSights() {
	    sightsHandler.hideSights();
    }
	
	public void displayTouristPaths(@NonNull List<Track> paths) {
		touristPathHandler.drawPaths(mMap, paths);
	}

	public void hideTouristPaths() {
		touristPathHandler.removePaths();
	}

	public void setMapStatus(@NotNull MapStatus mapStatus) {
		if (isMapReady) {
			setMapStatus(mMap, mapStatus);
		} else {
			this.mapStatus = mapStatus;
		}
	}

	@NotNull
	public MapStatus getMapStatus() {
		return getStatus(mMap);
	}

	public void switchMapType() {
		switchType(mMap);
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