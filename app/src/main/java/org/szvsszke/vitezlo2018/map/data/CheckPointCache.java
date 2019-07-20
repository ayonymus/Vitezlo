package org.szvsszke.vitezlo2018.map.data;

import java.io.IOException;
import java.util.HashMap;

import org.szvsszke.vitezlo2018.map.model.Waypoint;
import org.szvsszke.vitezlo2018.utilities.Utilities;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

@Deprecated
public class CheckPointCache extends AbstractDataCache<HashMap<String, Waypoint>>{
	
	private static final String TAG = CheckPointCache.class.getName();

	public CheckPointCache(Activity parent) {
		super(parent);
	}

	@Override
	public void loadToMemory() {
		if (mCache == null) {
			AsyncTask<String, Integer, HashMap<String, Waypoint>> loader =
					new AsyncTask<String, Integer, HashMap<String, Waypoint>>() {
			
				@Override
				protected HashMap<String, Waypoint> doInBackground(
						String... params) {
					HashMap<String, Waypoint> loaded = null;
					try {
						loaded = Utilities.checkPointListToMap( 
								XMLTools.parseWaypoints(mParent.getResources()
											.getAssets().open(FilePath.FILE_CHECKPOINTS_GPX)));
					} catch (IOException e) {
						Log.e(TAG, "descriptions could not be parsed: " + FilePath.FILE_HIKE_DESCRIPTIONS_XML);
						e.printStackTrace();
					}
					return loaded;
				}
				
				@Override
				protected void onPostExecute(HashMap<String, Waypoint> result) {
					mCache = result;
					super.onPostExecute(result);
					notifyListener(result);
				}

			};
			
			loader.execute("");
		}
		else {
			notifyListener(mCache);
		}
		
	}

}
