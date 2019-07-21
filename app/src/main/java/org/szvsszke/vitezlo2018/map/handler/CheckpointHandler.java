package org.szvsszke.vitezlo2018.map.handler;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import org.szvsszke.vitezlo2018.data.repository.BaseMappingRepository;
import org.szvsszke.vitezlo2018.data.repository.CheckpointRepository;
import org.szvsszke.vitezlo2018.domain.Checkpoint;
import org.szvsszke.vitezlo2018.framework.localdata.checkpoint.CheckpointLoader;
import org.szvsszke.vitezlo2018.framework.localdata.checkpoint.GpxCheckpointMapper;
import org.szvsszke.vitezlo2018.presentation.map.marker.CheckpointMarkerFactory;
import org.szvsszke.vitezlo2018.map.model.TrackDescription;
import org.szvsszke.vitezlo2018.presentation.map.marker.CheckpointIconSource;
import org.szvsszke.vitezlo2018.presentation.map.marker.MarkerHandler;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.ticofab.androidgpxparser.parser.GPXParser;
import timber.log.Timber;

/**Class for managing check point drawing onto the map.
 * 
 * @author Gabor Tatrai
 * */
// TODO split this tasks responsibilities
@Deprecated
public class CheckpointHandler extends AbstractMapItemHandler {

	private CheckpointRepository mCheckpointRepository;
	private CheckpointMarkerFactory mMarkerFactory;
	private MarkerHandler mMarkerHandler;

	private TrackDescription mPending;
	
	private Activity mParent;
	
	/**Instantiate CheckpointDrawer objects.
	 * @param parent the parent*/
	public CheckpointHandler (Activity parent) {
		super(parent);
		mParent = parent;

		// TODO obviously, these should be injected
		CheckpointLoader checkpointLoader = new CheckpointLoader(mParent.getAssets(),
				new GPXParser(),
				new GpxCheckpointMapper());
		mCheckpointRepository = new CheckpointRepository(checkpointLoader);
		mMarkerHandler = new MarkerHandler();
		CheckpointIconSource iconSource = new CheckpointIconSource(new IconGenerator(mParent));
		BaseMappingRepository<String, BitmapDescriptor> iconRepository = new BaseMappingRepository<>(iconSource);
		mMarkerFactory = new CheckpointMarkerFactory(iconRepository);

	}
	
	@Override
	public void setMap(GoogleMap map) {	
		super.setMap(map);
		if (mPending != null) {
			drawCheckpoints(mPending);
		}
	}
	
	/**
	 * Draws the checkpoints as soon as they are ready.
	 * setMap() method must be called in order to draw the checkpoints!
	 * @param description of hike
	 * */
	public void drawCheckpoints(final TrackDescription description) {
		// TODO remove async task
		AsyncTask<String, Integer, Map<String, Checkpoint>> loader = new AsyncTask<String, Integer, Map<String, Checkpoint>>() {
			@Override
			protected Map<String, Checkpoint> doInBackground(final String... strings) {
				Map data = mCheckpointRepository.getData();
				Timber.v("Checkpoints loaded");
				return data;
			}

			@Override
			protected void onPostExecute(final Map<String, Checkpoint> checkpoints) {
				markCheckPoints(description, checkpoints);
			}
		};
		loader.execute();
	}
	
	@Override
	public void remove() {
		mMarkerHandler.removeMarkers();
	}
	
    private void markCheckPoints(TrackDescription description, Map<String, Checkpoint> checkpoints) {
    	remove();
    	if (mMap == null ) {
    		mPending = description;
    		return;
    	}

	    List<Checkpoint> checkPoints = new ArrayList<>();
	    for(int i = 0; i < description.getCheckPointIDs().length; i++) {
	    	Checkpoint cp = checkpoints.get(description.getCheckPointIDs()[i]);
	    	if(cp != null) {
		    	checkPoints.add(cp);
	    	}
	    }
	    List<MarkerOptions> markerOptions = new ArrayList<>();
	    for(Checkpoint checkpoint: checkPoints) {
	    	markerOptions.add(mMarkerFactory.create(checkpoint));
	    }

	    mMarkerHandler.addMarkers(mMap, markerOptions);
    }

	@Override
	public void prepare() { }
}
