package org.szvsszke.vitezlo2018.map.handler;

import java.util.ArrayList;

import org.szvsszke.vitezlo2018.map.MarkerDrawer;
import org.szvsszke.vitezlo2018.map.data.SightsCache;
import org.szvsszke.vitezlo2018.map.data.AbstractDataCache.DataLoadedListener;
import org.szvsszke.vitezlo2018.map.model.Waypoint;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class SightsHandler extends AbstractMapItemHandler implements
		DataLoadedListener<ArrayList<Waypoint>> {
	
	private MarkerDrawer mMarkers;
	private SightsCache mSightCache;
	private boolean isSightMarked;

	public SightsHandler(Activity activity) {
		super(activity);
		mSightCache = new SightsCache(mParent);
	}
	
	@Override
	public void setMap(GoogleMap map) {	
		super.setMap(map);
		mMarkers = new MarkerDrawer(map);
	}

	@Override
	public void remove() {
		if (mMarkers != null) {
			mMarkers.removeMarkers();
		}
		isSightMarked = false;
	}

	@Override
	public void prepare() {
		mSightCache.acquireData();		
	}
	public void draw () {
		if (mSightCache.acquireData() != null) {
			markSights(mSightCache.acquireData());
		}
		else {
			mSightCache.setDataLoadedListener(this);
		}
	}
	
	private void markSights(ArrayList<Waypoint> sights) {
		if (mMarkers != null && !isSightMarked) {
					// draw markers if not marked already
			mMarkers.drawMarkers(sights,
					BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			isSightMarked = true;
		}
	}

	@Override
	public void onDataLoaded(ArrayList<Waypoint> loaded) {
		markSights(loaded);		
	}

}
