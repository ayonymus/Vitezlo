package org.szvsszke.vitezlo2018.map.data;

import java.io.IOException;
import java.util.ArrayList;

import org.szvsszke.vitezlo2018.map.model.Track;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class TouristPathCache extends AbstractDataCache<ArrayList<Track>>{

	private static final String TAG = TouristPathCache.class.getName();
	
	public TouristPathCache(Activity parent) {
		super(parent);
	}

	@Override
	public void loadToMemory() {
		Log.d(TAG, "loadToMemory");
		if (mCache == null) {
			AsyncTask<String, Integer, ArrayList<Track>> loader = 
					new AsyncTask<String, Integer, ArrayList<Track>>() {
				
				@Override
				protected ArrayList<Track> doInBackground(String... params) {
					Log.d(TAG, "read in tourist paths");
					String [] pathFileNames = getTouristPathFileNames();
					ArrayList<Track> paths = new ArrayList<Track>();
					
					for (String fileName : pathFileNames) {
						Track path = null;
						try {
							Log.d(TAG, "use pullParser");
							path = XMLTools.parseGPX(getAssetStream(
									FilePath.PATH_TO_TOURIST_PATHS + "/" 
											+ fileName));
							paths.add(path);
							
						} catch (Exception e) {
							Log.e(TAG, "error parisng " + fileName, e);							
							
						}	
					}
					Log.d(TAG, "parsed "+ paths.size()+ " out of " 
					+ pathFileNames.length + " files");
					return paths;
				}
				
				@Override
				protected void onPostExecute(ArrayList<Track> result) {
					super.onPostExecute(result);
					mCache = result; 
					notifyListener(mCache);
				}
			};			
			loader.execute("");			
		}
		else {
			notifyListener(mCache);
		}
	}

	private String[] getTouristPathFileNames() {
		Log.d(TAG, "getFileNames");
		String [] list = null;
		try {
			list = mParent.getAssets()
					.list(FilePath.PATH_TO_TOURIST_PATHS);		
		}catch (IOException e){
			e.printStackTrace();
		}
		return list;
	}
	

}
