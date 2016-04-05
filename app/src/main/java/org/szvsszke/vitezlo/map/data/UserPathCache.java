package org.szvsszke.vitezlo.map.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.szvsszke.vitezlo.gpslogger.GpsDatabase;
import org.szvsszke.vitezlo.map.model.Track;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Stores the values queried from user path database.
 * 
 * @author Gabor Tatrai
 * 
 * */
public class UserPathCache extends AbstractDataCache<HashMap<String, Track>>{
	
	private static final String TAG = UserPathCache.class.getName();
	
	private DataLoadedListener<Track> mListener;
	
	private DataLoadedListener<ArrayList<String>> mPathNamesListener;
	private ArrayList<String> mPathNames;
	
	private boolean mAlwaysQuery;
	
	public UserPathCache(Activity parent) {
		super(parent);
		mCache = new HashMap<String, Track>();	
	}
	
	/**
	 * Not implemented!
	 * */
	@Override
	public void loadToMemory() {
		Log.d(TAG, "Not implemented!");
		mLoadingStarted = false;
	}

	/**
	 * Returns track from memory or returns null and
	 * starts an async task to acquire data from database.
	 * 
	 * If alwayQuery is set to true cache will not be used and data
	 * will always be queried. 
	 * 
	 * @param trackName the name of the user track in the database. */
	public Track acquireTrack(final String trackName) {
		Log.d(TAG, "acquire track: " + trackName);
		if (!mAlwaysQuery && mCache.containsKey(trackName)) {
			if (mCache.get(trackName) != null) {
				return mCache.get(trackName);
			}
		}
		
		AsyncTask<String, Integer, Track> loader = new AsyncTask<String, Integer, Track>() {

			@Override
			protected Track doInBackground(String... params) {
				return GpsDatabase.getInstance(mParent).getPathTrack(trackName);
			}
			
			@Override
			protected void onPostExecute(Track result) {			
				super.onPostExecute(result);
				notifyListener(result);
				if (!mAlwaysQuery) {
					mCache.put(trackName, result);
					notifyListener(result);
				}
			}
		};
		loader.execute("");
		return null;
	}
	
	private void notifyListener(Track loaded) {
		if (mListener != null) {
			mListener.onDataLoaded(loaded);
		}
	}
	
	public void setTrackLoadedListener(
			DataLoadedListener<Track> listener) {
		mListener = listener;
	}
	
	/**
	 * Loads the user path names from the gps database.
	 *  
	 * @return array of path names or null if data has not been loaded yet.
	 * */
	public ArrayList<String> acquireUserPathNames() {
		if (mPathNames != null) {
			return mPathNames;
		}
		// else
		AsyncTask<String, Integer, ArrayList<String>> loader = 
				new AsyncTask<String, Integer, ArrayList<String>>() {

			@Override
			protected ArrayList<String>  doInBackground(String... params) {
				return GpsDatabase.getInstance(mParent).getPathNames();
			}
			
			@Override
			protected void onPostExecute(ArrayList<String>result) {			
				super.onPostExecute(result);
				mPathNames = result;
				notifyPathNamesLoadedListener(mPathNames);
			}
			
		};
		loader.execute("");
		return null;
	}
	
	public void setPathNamesLoadedListener(
			DataLoadedListener<ArrayList<String> > listener) {
		mPathNamesListener = listener;
		if (mPathNames != null) {
			mPathNamesListener.onDataLoaded(mPathNames);
		}
	}
	
	private void notifyPathNamesLoadedListener(ArrayList<String> loaded) {
		if (mPathNamesListener != null) {
			mPathNamesListener.onDataLoaded(loaded);
		}
	}
	
	/**
	 * @param query true to always query database.
	 * */
	public void alwaysQuery(boolean query) {
		mAlwaysQuery = query;
	}
	
	/**
	 * @return true if database contains no items. 
	 * */
	public boolean isDatabaseEmpty() {
		return GpsDatabase.getInstance(mParent).isEmpty();		
	}

	public void removeTrackLoadedListener() {
		mListener = null;		
	}
}