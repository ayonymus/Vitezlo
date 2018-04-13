package org.szvsszke.vitezlo2018.map.data;

import java.io.IOException;
import java.util.HashMap;

import org.szvsszke.vitezlo2018.map.model.Track;
import org.szvsszke.vitezlo2018.map.model.TrackDescription;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

/**Loads and stores Tracks*/
public class TrackCache extends AbstractDataCache<HashMap<TrackDescription, Track>>{

	private static final String TAG = TrackCache.class.getName();
	
	private DataLoadedListener<Track> mTrackListener;
	
	public TrackCache(Activity parent) {
		super(parent);
		mCache = new HashMap<TrackDescription, Track> ();
	}

	@Override
	public void loadToMemory() {
		Log.d(TAG, "loadToMemory is not used here");		
	}
	
	/**
	 * Finds a path and returns it from cache or loads it from xml.
	 * If path is loaded from cache the updateMap is called after the 
	 * separate loading thread finished.
	 * 
	 * @param trackDescription hikeDescription that contains a reference to the path.
	 * @return the latlng list representation of the hike.
	 * */	
	public Track acqureTrack(final TrackDescription trackDescription) {
		Log.d(TAG, "acquireTrack");
		
		if (mCache.containsKey(trackDescription)) {
			return mCache.get(trackDescription);
		}
		// start an async task for loading a hike
		AsyncTask<String, Integer, Track> loader = 
				new AsyncTask<String, Integer, Track>() {
					
			@Override					
			protected Track doInBackground(String... params) {
				Track hike = null;
				try {
					hike = XMLTools.parseGPX(getAssetStream(
							FilePath.PATH_TO_ROUTES + 
							trackDescription.getRouteFileName()));
				} catch (XmlPullParserException e) {
					Log.e(TAG, trackDescription.getRouteFileName() + 
							" could not be parsed", e);
				} catch (IOException e) {
					Log.e(TAG, "ioerror", e);
				}
				return hike;
				
				}
				
				@Override
				protected void onPostExecute(Track result) {
					super.onPostExecute(result);
					if(result == null) {
						Log.e(TAG, "Track could not be loaded: " + trackDescription.getName());

					} else {
						mCache.put(trackDescription, result);
						notifyListener(result);
					}
				}
		};
				
		loader.execute(new String[]{});
		
		return null;
	}
		
	protected void notifyListener(Track loaded) {
		if (mTrackListener != null) {
			Log.d(TAG, "notifyListener");
			mTrackListener.onDataLoaded(loaded);
		} else {
			Log.d(TAG, "notifyListener: listener not set!");
		}
	}
		
	public void setTrackLoadedListener(DataLoadedListener<Track> listener) {
		Log.d(TAG, "setTrackLoadedListener");
		mTrackListener = listener;
		if (mTrackListener == null) {
			Log.e(TAG, "Lisener is still null after set!");
		}
	}
}
