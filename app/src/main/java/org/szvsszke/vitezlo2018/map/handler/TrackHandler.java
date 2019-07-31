package org.szvsszke.vitezlo2018.map.handler;

import org.szvsszke.vitezlo2018.map.LineDrawer;
import org.szvsszke.vitezlo2018.map.data.TrackCache;
import org.szvsszke.vitezlo2018.map.data.AbstractDataCache.DataLoadedListener;
import org.szvsszke.vitezlo2018.map.model.Track;
import org.szvsszke.vitezlo2018.domain.entity.Description;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

public class TrackHandler extends AbstractMapItemHandler implements DataLoadedListener<Track>{
	
	private static final String TAG = TrackHandler.class.getName();
	
	private static final int LINE_WIDTH = 8;
	private static final float Z_INDEX = 0;
	private static final int ARGB_GREEN = 
			Color.argb(255, 100, 255, 100);
	
	private int mLineColor = ARGB_GREEN;
	private TrackCache mTracks;
	private LineDrawer mDrawer;
	private Track mDisplay;
	private boolean mCenter;

	public TrackHandler(Activity activity) {
		super(activity);
		mTracks = new TrackCache(activity);
	}
	
	/**
	 * @param track description of track to display.
	 * */
	public void displayTrack (Description track, boolean center) {
		Log.d(TAG, "displayTrack");
		mCenter = center;
		mTracks.setTrackLoadedListener(TrackHandler.this);
		remove();
		if (mTracks.acqureTrack(track) != null) {
			draw(mTracks.acqureTrack(track), center);
		}
	}

	private void draw(Track track, boolean center) {
		Log.d(TAG, "draw");
		if (mDrawer != null) {
			mDrawer.drawPath(track.getTrackPoints(), center);
		}
		else {
			Log.d(TAG, "PathDrawer has not been initialized yet!");
			mDisplay = track;
		
		}
	}

	@Override
	public void remove() {
		Log.d(TAG, "remove");
		if (mDrawer != null) {
			mDrawer.removePath();
		}
	}
	
	/**Not implemented*/
	@Override
	public void prepare() {}
	
	@Override
	public void setMap(GoogleMap map) {
		super.setMap(map);
		Log.d(TAG, "setMap");
		setupDrawer(map);
	}

	private void setupDrawer(GoogleMap map) {
		Log.d(TAG, "setupDrawer");
		mDrawer = new LineDrawer(map);
		mDrawer.setWidth(LINE_WIDTH);
		mDrawer.setZIndex(Z_INDEX);
		mDrawer.setColor(mLineColor);
		if (mDisplay != null) {
			draw(mDisplay, mCenter);
		}
		mDisplay = null;
	}

	@Override
	public void onDataLoaded(Track loaded) {
		Log.v(TAG, "data loaded");
		draw(loaded, mCenter);
	}
	
	public void setLineColor(int color) {
		Log.d(TAG, "setLineColor");
		mLineColor = color;
	}

}
