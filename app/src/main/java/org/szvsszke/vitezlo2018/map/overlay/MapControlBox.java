package org.szvsszke.vitezlo2018.map.overlay;

import com.google.android.gms.maps.GoogleMap;

import org.szvsszke.vitezlo2018.R;
import org.szvsszke.vitezlo2018.map.MapPreferences;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Class for encapsulating the map view manipulating icons.
 * 
 * */
@Deprecated // TODO move functions to vuewModel
public class MapControlBox {

	private ImageView mMapTypeSwitch;
	private View mBaseView;
	
	private MapPreferences mPrefs;
	
	private MapControlListener mListener;
	
	public MapControlBox(MapPreferences prefs) {	
		mPrefs = prefs;		
	}
	
	public void onCreateView(LayoutInflater inflater, ViewGroup container) {
		// setup image buttons
		ViewControlListener listener = new ViewControlListener();
		mBaseView = inflater.inflate(R.layout.control_box, container);
		mMapTypeSwitch = mBaseView.findViewById(R.id.imageViewMapTypeSwitch);
		mMapTypeSwitch.setOnClickListener(listener);
	}
	
	/**
	 * Handles view control item clicks.
	 * */
	private class ViewControlListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			switch (mPrefs.getMapType()){
				case GoogleMap.MAP_TYPE_NORMAL:
					mPrefs.setMapType(GoogleMap.MAP_TYPE_HYBRID);
					break;
				case GoogleMap.MAP_TYPE_HYBRID:
					mPrefs.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
					break;
				case GoogleMap.MAP_TYPE_TERRAIN:
					mPrefs.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					break;
				}

			notifyListener();
		}
	}
	
	public void setListener(MapControlListener listener) {
		mListener = listener;
	}
	
	private void notifyListener() {
		if (mListener != null) {
			mListener.displayPreferenceChanged();
		}
	}
	
	public interface MapControlListener {
		public void displayPreferenceChanged();
	}
}
