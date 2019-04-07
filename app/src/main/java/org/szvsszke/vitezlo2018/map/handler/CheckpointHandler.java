package org.szvsszke.vitezlo2018.map.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.szvsszke.vitezlo2018.R;
import org.szvsszke.vitezlo2018.map.MarkerDrawer;
import org.szvsszke.vitezlo2018.map.data.AbstractDataCache.DataLoadedListener;
import org.szvsszke.vitezlo2018.map.data.CheckPointCache;
import org.szvsszke.vitezlo2018.map.model.TrackDescription;
import org.szvsszke.vitezlo2018.map.model.Waypoint;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.maps.android.ui.IconGenerator;

/**Class for managing check point drawing onto the map.
 * 
 * @author Gabor Tatrai
 * */

public class CheckpointHandler extends AbstractMapItemHandler {

	private MarkerDrawer mMarkers;
	private CheckPointCache mCheckpoints;
	
	private ArrayList<BitmapDescriptor> mBitmapCache;
	private TrackDescription mPending;
	
	private Activity mParent;
	
	/**Instantiate CheckpointDrawer objects.
	 * @param parent the parent*/
	public CheckpointHandler (Activity parent) {
		super(parent);
		mParent = parent;
		mCheckpoints = new CheckPointCache(mParent);
		
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
		if (mCheckpoints != null) {
			markCheckPoints(description);
		}
		else {			
			mCheckpoints = new CheckPointCache(mParent);
			mCheckpoints.setDataLoadedListener(
					new DataLoadedListener<HashMap<String, Waypoint>>() {

				@Override
				public void onDataLoaded(
					HashMap<String, Waypoint> loaded) {
					markCheckPoints(description);						
				}
			});
		}
	}
	
	@Override
	public void remove() {
		if (mMarkers != null) {
			mMarkers.removeMarkers();
		}
	}
	
    private void markCheckPoints(TrackDescription description) { 	
    	remove();
    	if (mMap == null ) {
    		mPending = description;
    		return;
    	}
	    
	    // create a list of waypoints for displaying it properly
	    List<Waypoint> checkPoints = new ArrayList<Waypoint>();
	    	
	    for(int i = 0; i < description.getCheckPointIDs().length; i++) {
	
	    	Waypoint cp = mCheckpoints.acquireData().get(description.getCheckPointIDs()[i]);
	    	
	    	if(cp != null) {
		    	checkPoints.add(cp);
		    	
	    	}
	    }
	    	    
	    if (mMarkers != null) {	    
		    mMarkers.drawMarkers(checkPoints,
		    		getCheckpointBitmaps(mCheckpoints.acquireData().size()));
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
	public void prepare() {
		mCheckpoints.loadToMemory();
	}
}
