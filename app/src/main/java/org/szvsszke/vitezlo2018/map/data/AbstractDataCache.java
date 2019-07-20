package org.szvsszke.vitezlo2018.map.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;

@Deprecated
public abstract class AbstractDataCache<E> {
	
	protected Activity mParent;
	protected ArrayList<DataLoadedListener<E>> mListener;
	
	protected E mCache;
	protected boolean mLoadingStarted = false;
	
	
	public AbstractDataCache(Activity parent) {
		mParent = parent;
	}
	
	/**Returns the data stored in the cache, or bull
	 * if data has not been cached.*/
	public E acquireData() {
		//prevent starting multiple async tasks
		if (mCache == null && !mLoadingStarted) {
			loadToMemory();
			mLoadingStarted = true;
		}
		return mCache;
	}
	
	/**
	 * Sets callback for data.
	 * @param listener to set. Immediately called if data is already loaded.*/
	public void setDataLoadedListener(DataLoadedListener<E> listener) {
		if (mListener == null) {
			mListener = new ArrayList<AbstractDataCache.DataLoadedListener<E>>();
		}
		mListener.add(listener);
		
		if (mCache != null) {
			listener.onDataLoaded(mCache);
		}
	}
	
	/**
	 * Removes a listener from the cache.
	 * @param listener to remove
	 * */
	public void removeListener(DataLoadedListener<E> listener) {
		if (mListener != null) {
			mListener.remove(listener);
		}
	}
	
	protected void notifyListener(E loaded) {		
		if (mListener != null) {
			for (DataLoadedListener<E> listener : mListener) {
				listener.onDataLoaded(loaded);
			}
		}
	}
	
	/**call the notify method!*/
	abstract public void loadToMemory();
	
	
	/**
	 * Helper method for acquiring an input stream to an asset.
	 * @param pathToFile: asset's address.
	 * */
	protected InputStream getAssetStream (String pathToFile) {
		InputStream is = null;
		try {
			is = mParent.getResources().getAssets()
			.open(pathToFile);
		} catch (IOException e){
			e.printStackTrace();			
		}
		return is; 
	}

	public interface DataLoadedListener<E> {
		public void onDataLoaded(E loaded);
	}
}
