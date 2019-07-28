package org.szvsszke.vitezlo2018.map.handler;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import org.szvsszke.vitezlo2018.data.repository.BaseMappingRepository;
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint;
import org.szvsszke.vitezlo2018.presentation.map.marker.CheckpointIconSource;
import org.szvsszke.vitezlo2018.presentation.map.marker.CheckpointMarkerFactory;
import org.szvsszke.vitezlo2018.presentation.map.marker.MarkerHandler;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**Class for managing check point drawing onto the map.
 * 
 * @author Gabor Tatrai
 * */
// TODO split this tasks responsibilities
@Deprecated
public class CheckpointHandler {

	private CheckpointMarkerFactory mMarkerFactory;
	private MarkerHandler mMarkerHandler;

	private Activity mParent;
	private GoogleMap mMap;

	/**Instantiate CheckpointDrawer objects.
	 * @param parent the parent*/
	public CheckpointHandler (Activity parent) {
		mParent = parent;

		// TODO obviously, these should be injected, ideally somewhere else
		mMarkerHandler = new MarkerHandler();
		CheckpointIconSource iconSource = new CheckpointIconSource(new IconGenerator(mParent));
		BaseMappingRepository<String, BitmapDescriptor> iconRepository = new BaseMappingRepository<>(iconSource);
		mMarkerFactory = new CheckpointMarkerFactory(iconRepository);

	}
	
	public void setMap(GoogleMap map) {
		mMap = map;
	}

	public void drawCheckpoints(final List<Checkpoint> checkPoints) {
		List<MarkerOptions> markerOptions = new ArrayList<>();
		for(Checkpoint checkpoint: checkPoints) {
			markerOptions.add(mMarkerFactory.create(checkpoint));
		}

		mMarkerHandler.addMarkers(mMap, markerOptions);
	}
	
	public void remove() {
		mMarkerHandler.removeMarkers();
	}

}
