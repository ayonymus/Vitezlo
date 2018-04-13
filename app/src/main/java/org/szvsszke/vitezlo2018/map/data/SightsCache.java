package org.szvsszke.vitezlo2018.map.data;

import java.util.ArrayList;

import org.szvsszke.vitezlo2018.map.model.Waypoint;

import android.app.Activity;
import android.os.AsyncTask;

public class SightsCache extends AbstractDataCache<ArrayList<Waypoint>> {

	public SightsCache(Activity parent) {
		super(parent);
	}

	@Override
	public void loadToMemory() {
		if(mCache == null) {
			AsyncTask<String , Integer, ArrayList<Waypoint>> loader =
					new AsyncTask<String, Integer, ArrayList<Waypoint>>() {

				
				@Override
				protected ArrayList<Waypoint> doInBackground(
						String... params) {
					ArrayList<Waypoint> loaded = null;
					try {
						loaded = XMLTools.parseWaypoints(mParent.getResources()
								.getAssets().open(FilePath.FILE_SIGHTS_GPX));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					return loaded;
				}
				
				@Override
				protected void onPostExecute(ArrayList<Waypoint> result) {
					mCache = result;
					notifyListener(mCache);
					super.onPostExecute(result);
					
				} 
							
			};			
			loader.execute("");
		}
		else {
			notifyListener(mCache);
		}
	}
	
}
