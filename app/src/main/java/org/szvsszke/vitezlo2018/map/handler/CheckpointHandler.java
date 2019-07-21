package org.szvsszke.vitezlo2018.map.handler;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.maps.android.ui.IconGenerator;

import org.szvsszke.vitezlo2018.R;
import org.szvsszke.vitezlo2018.data.repository.CheckpointRepository;
import org.szvsszke.vitezlo2018.domain.Checkpoint;
import org.szvsszke.vitezlo2018.framework.localdata.checkpoint.CheckpointLoader;
import org.szvsszke.vitezlo2018.framework.localdata.checkpoint.GpxCheckpointMapper;
import org.szvsszke.vitezlo2018.map.MarkerDrawer;
import org.szvsszke.vitezlo2018.map.model.TrackDescription;

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

	private MarkerDrawer mMarkers;

	private ArrayList<BitmapDescriptor> mBitmapCache;
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
	}
	
	@Override
	public void setMap(GoogleMap map) {	
		super.setMap(map);
		mMarkers = new MarkerDrawer(mMap);
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
		if (mMarkers != null) {
			mMarkers.removeMarkers();
		}
	}
	
    private void markCheckPoints(TrackDescription description, Map<String, Checkpoint> checkpoints) {
    	remove();
    	if (mMap == null ) {
    		mPending = description;
    		return;
    	}
	    
	    // create a list of waypoints for displaying them in the proper order ??
	    List<Checkpoint> checkPoints = new ArrayList<Checkpoint>();
	    	
	    for(int i = 0; i < description.getCheckPointIDs().length; i++) {
	
	    	Checkpoint cp = checkpoints.get(description.getCheckPointIDs()[i]);
	    	
	    	if(cp != null) {
		    	checkPoints.add(cp);
		    	
	    	}
	    }
	    	    
	    if (mMarkers != null) {	    
		    mMarkers.drawMarkers(checkPoints,
		    		getCheckpointBitmaps(checkpoints.size()));
	    }
    }
    
    private ArrayList<BitmapDescriptor> getCheckpointBitmaps(int chekPoints) {
    	if (mBitmapCache == null) {
    		//generate Bitmaps for all checkpoints
    		ArrayList<BitmapDescriptor> bitmaps = new ArrayList<BitmapDescriptor>();    		
    		IconGenerator iconGenerator = new IconGenerator(mParent);
    		iconGenerator.setStyle(IconGenerator.STYLE_WHITE);    		
    		iconGenerator.setContentRotation(90);

    		bitmaps.add(BitmapDescriptorFactory.fromBitmap(
    				iconGenerator.makeIcon(mParent.getString(R.string.start_finish))));
    		
    		for (int i = 1; i < chekPoints; i++) {
    			
    			bitmaps.add(BitmapDescriptorFactory.fromBitmap(
    					iconGenerator.makeIcon(mParent.getString(R.string.cp) + i)));
    		}
    		mBitmapCache = bitmaps;
    	}    	
    	return mBitmapCache;
    }

	@Override
	public void prepare() { }
}
