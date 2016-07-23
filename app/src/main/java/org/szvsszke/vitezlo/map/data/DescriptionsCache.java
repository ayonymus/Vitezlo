package org.szvsszke.vitezlo.map.data;

import java.io.IOException;
import java.util.ArrayList;

import org.szvsszke.vitezlo.map.model.TrackDescription;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class DescriptionsCache extends AbstractDataCache<ArrayList<TrackDescription>>{
	
	private static final String TAG = DescriptionsCache.class.getName();

	public DescriptionsCache(Activity parent) {
		super(parent);
	}

	@Override
	public void loadToMemory() {
		if (mCache == null) {
			AsyncTask<String, Integer, ArrayList<TrackDescription>> loader = 
					new AsyncTask<String, Integer, ArrayList<TrackDescription>>() {
				
				@Override
				protected ArrayList<TrackDescription> doInBackground(
						String... params) {
					ArrayList<TrackDescription> descs = null;
					try {
						descs = XMLTools.parseHikeDescriptions(
										mParent.getResources().getAssets()
										.open(FilePath.FILE_HIKE_DESCRIPTIONS_XML));
					} catch (IOException e) {
						Log.e(TAG, "descriptions could not be parsed: " 
					+ FilePath.FILE_HIKE_DESCRIPTIONS_XML, e);
					}
					return descs;
				}

				@Override
				protected void onPostExecute(ArrayList<TrackDescription> descriptions) {
					mCache = descriptions;
					notifyListener(mCache);
					super.onPostExecute(descriptions);					
				}
			};			
			loader.execute("");
		}
		else {
			notifyListener(mCache);
		}
		
	}
	
	/**
	 * @param index the id of the description
	 * @return the hike description corresponding to the index*/
	public TrackDescription getDescription(int index) {
		TrackDescription desc = null;
		if (mCache != null) {
			desc = mCache.get(index);
		}
		return desc;
	}
	
	/**
	 * Creates a String array of the available hike names.
	 * 
	 *  @return the array of names. May be null if cache is not ready.
	 * */	
	public ArrayList<String> getNames() {
		Log.d(TAG, "getNames");
		if (mCache == null) {
			Log.d(TAG, "getNames: cache is null");
			return null;
		}
		
		ArrayList<String>  names = new ArrayList<String> ();
		for(TrackDescription desc : mCache) {
			names.add(desc.getName());
			Log.v(TAG, desc.getName());
		}
		return names;
	}
}
