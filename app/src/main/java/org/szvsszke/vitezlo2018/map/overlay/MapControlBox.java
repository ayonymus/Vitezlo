package org.szvsszke.vitezlo2018.map.overlay;

import org.szvsszke.vitezlo2018.R;
import org.szvsszke.vitezlo2018.map.MapPreferences;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;

/**
 * Class for encapsulating the map view manipulating icons.
 * 
 * */
@Deprecated
public class MapControlBox {
	
	private static final String TAG = MapControlBox.class.getName();

	private ImageView mMapTypeSwitch;
	private ImageView mPathEnabled;
	private ImageView mTouristPathEnabled;
	private ImageView mSightsEnabled;
	private ImageView mUserHikeEnabled;	
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
		mMapTypeSwitch = (ImageView)
				mBaseView.findViewById(R.id.imageViewMapTypeSwitch);
		mMapTypeSwitch.setOnClickListener(listener);
		mPathEnabled = (ImageView) 
				mBaseView.findViewById(R.id.imageViewHikeEnabled);
		mPathEnabled.setOnClickListener(listener);
		mSightsEnabled = (ImageView)
				mBaseView.findViewById(R.id.imageViewSightsEnabled);
		mSightsEnabled.setOnClickListener(listener);
		mTouristPathEnabled = (ImageView) 
				mBaseView.findViewById(R.id.imageViewTourstPathEnabled);	
		mTouristPathEnabled.setOnClickListener(listener);
		mUserHikeEnabled = (ImageView)
				mBaseView.findViewById(R.id.imageViewUserHikeEnabled);
		mUserHikeEnabled.setOnClickListener(listener);
		setViewControlImages();
	}
	
	/**
	 * Helper method for setting the correct images for the view control "buttons"
	 * */
	private void setViewControlImages() {
		// images are enabled by default in the xml
		if (!mPrefs.isHikeEnabled()) {
			mPathEnabled.setImageResource(R.drawable.path_disabled);
		}
		
		if (!mPrefs.areTouristPathsEnabled()) {
			mTouristPathEnabled.setImageResource(R.drawable.tpath_disabled);
		}
		
		if (!mPrefs.areSightsEnabled()) {
			mSightsEnabled.setImageResource(R.drawable.sights_disabled);
		}
		
		if (!mPrefs.isUserPathEnabled()) {
			mUserHikeEnabled.setImageResource(R.drawable.my_hike_disabled);
		} else {
			mUserHikeEnabled.setImageResource(R.drawable.my_hike);
		}
	}
	
	/**
	 * Handles view control item clicks.
	 * */
	private class ViewControlListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			switch(v.getId()) {
			case R.id.imageViewMapTypeSwitch:
				Log.d(TAG, "maptype icon clicked");
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
				break;
			
			case R.id.imageViewHikeEnabled:
				Log.d(TAG, "hike icon clicked");
				mPrefs.setHikeEnabled(!mPrefs.isHikeEnabled());
				
				if(mPrefs.isHikeEnabled()) {
					mPathEnabled.setImageResource(R.drawable.path_enabled);
				} else {
					mPathEnabled.setImageResource(R.drawable.path_disabled);
				}				
				break;
				
			case R.id.imageViewTourstPathEnabled:
				Log.d(TAG, "toursit path icon clicked");
				mPrefs.setTouristPathsEnabled(!mPrefs.areTouristPathsEnabled());
				if(mPrefs.areTouristPathsEnabled()) {
					mTouristPathEnabled.setImageResource(R.drawable.tpath_enabled);
				}
				else {
					mTouristPathEnabled.setImageResource(R.drawable.tpath_disabled);
				}				
				break;
			
			case R.id.imageViewSightsEnabled:
				Log.d(TAG, "sights icon clicked");
				mPrefs.setSightsEnabled(!mPrefs.areSightsEnabled());
				if(mPrefs.areSightsEnabled()) {
					mSightsEnabled.setImageResource(R.drawable.sights_enabled);
				}else {
					mSightsEnabled.setImageResource(R.drawable.sights_disabled);
				}
				break;
			
			case R.id.imageViewUserHikeEnabled:
				Log.d(TAG, "user hike icon clicked");
				mPrefs.setUserPathEnabled(!mPrefs.isUserPathEnabled());
				if (mPrefs.isUserPathEnabled()) {
					mUserHikeEnabled.setImageResource(R.drawable.my_hike);
				}
				else {
					mUserHikeEnabled.setImageResource(R.drawable.my_hike_disabled);
					
				}
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
	
	/**Enables or disables the User Hike button.
	 * 
	 * @param enable true to show button, false to hide */
	public void enableUserHikeButton (boolean enable) {
		if (enable) {
			mUserHikeEnabled.setVisibility(View.VISIBLE);
		}
		else {
			mUserHikeEnabled.setVisibility(View.GONE);
		}
	}
	
	public interface MapControlListener {
		public void displayPreferenceChanged();
	}
}
