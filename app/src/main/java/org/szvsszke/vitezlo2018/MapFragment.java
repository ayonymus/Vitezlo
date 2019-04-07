package org.szvsszke.vitezlo2018;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.MapView;

import org.szvsszke.vitezlo2018.map.MapDecorator;
import org.szvsszke.vitezlo2018.map.MapPreferences;
import org.szvsszke.vitezlo2018.map.data.AbstractDataCache.DataLoadedListener;
import org.szvsszke.vitezlo2018.map.data.DescriptionsCache;
import org.szvsszke.vitezlo2018.map.model.Track;
import org.szvsszke.vitezlo2018.map.model.TrackDescription;
import org.szvsszke.vitezlo2018.map.overlay.InfoBox;
import org.szvsszke.vitezlo2018.map.overlay.MapControlBox;
import org.szvsszke.vitezlo2018.map.overlay.MapControlBox.MapControlListener;
import org.szvsszke.vitezlo2018.utilities.Utilities;

import java.util.ArrayList;

/**
 * This fragment is responsible for loading data and displaying it
 * overlaying a map. 
 * */
public class MapFragment extends Fragment implements MapControlListener {

	private static final String TAG = MapFragment.class.getName();
	private static final long TEN_SECONDS = 10000;
	
	private MapView mMapView;
	private MapDecorator mMapDecorator;
	private MapPreferences mMapPrefs;

	private RelativeLayout mInfoContainer;
	private LinearLayout mControlContainer;

	private InfoBox mInfoBox;
	
	private MapControlBox mControlBox;

	private DescriptionsCache mDescriptions;

	//view variables
	private boolean isBoxExpanded = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
					Bundle savedInstanceState) {		
		Log.v(TAG, "onCreate");
		View inflatedView = inflater.inflate(R.layout.fragment_map, container,
				false);
		
		mMapPrefs = new MapPreferences(
				PreferenceManager.getDefaultSharedPreferences(getActivity()));		
		mMapView = (MapView) inflatedView.findViewById(R.id.mapView);
		mMapView.onCreate(savedInstanceState);

		mMapDecorator = new MapDecorator(getActivity(), mMapView, mMapPrefs);
		
		mDescriptions = new DescriptionsCache(getActivity());

        mInfoContainer = (RelativeLayout) 
        		inflatedView.findViewById(R.id.infoBoxHolder);
        
        if (mMapPrefs.isInfoboxEnabled()) {
        	setupInfobox(inflater);
        }
        else {
        	mInfoContainer.setVisibility(View.GONE);
        }
        
        showTrackInfo();
        
		mControlContainer = (LinearLayout) inflatedView.findViewById(
				R.id.controlBoxHolder);
		if (mMapPrefs.isControlBoxEnabled()) {			
			// setup control box
			mControlBox = new MapControlBox(mMapPrefs);	
			mControlBox.onCreateView(inflater, mControlContainer);
			mControlBox.setListener(this);		


			mControlBox.enableUserHikeButton(false);

		}
		else {
			mControlContainer.setVisibility(View.GONE);
		}
		return inflatedView;
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();		
		mMapView.onPause();
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		mMapView.onResume();
		mMapDecorator.decorate(mDescriptions.getDescription(
				mMapPrefs.getSelectedTrackIndex()));
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		mMapView.onDestroy();
		mMapPrefs.setInfoboxLocked(mInfoBox.isSpinnerLocked());
		mMapPrefs.saveMapPreferences();
	};
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}
	
	private void setCurrentTrack(final int trackNr) {
		Log.d(TAG, "setCurrentTrack");
		mMapPrefs.setSelectedTrackIndex(trackNr);
		if (mDescriptions.acquireData() != null) {
			mMapDecorator.decorate(mDescriptions.getDescription(trackNr));
			getActivity().setTitle(mDescriptions.getDescription(trackNr).getName());
			mInfoBox.setTitle(mDescriptions.getDescription(trackNr).getName());
		}
		else {
			mDescriptions.setDataLoadedListener(
					new DataLoadedListener<ArrayList<TrackDescription>>() {
				@Override
				public void onDataLoaded(ArrayList<TrackDescription> loaded) {
					mMapDecorator.decorate(mDescriptions.getDescription(trackNr));
					getActivity().setTitle(
							mDescriptions.getDescription(trackNr).getName());
					mInfoBox.setTitle(mDescriptions.getDescription(trackNr).getName());
				}
			});
		}
	}
	
	private void setupInfobox(LayoutInflater inflater) {
		Log.d(TAG, "setupInfoBox");
		mInfoBox = new InfoBox(getActivity());
	    mInfoBox.onCreateView(inflater, mInfoContainer);  
	    OnClickListener expandCollapse = new OnClickListener() {
	    
	    	@Override
	    	public void onClick(View v) {
	    		isBoxExpanded = !isBoxExpanded;
				mInfoBox.expandInfoBox(isBoxExpanded);									
			}
		};
		mInfoBox.setOnClickListenerForContainer(expandCollapse);
		mInfoBox.lockSpinner(mMapPrefs.isInfoboxLocked());
	}

	private void setTrackSpinner() {
		Log.d(TAG, "setTrackSpinner");
		if (mDescriptions.acquireData() != null) {
			Log.v(TAG, "description data not null");
			mInfoBox.setupSpinner(mDescriptions.getNames(), 
				new TrackSpinnerListener(), 
				mMapPrefs.getSelectedTrackIndex());
		}
		else {
			Log.v(TAG, "description data null, set listener");
			mDescriptions.setDataLoadedListener( 
				new DataLoadedListener<ArrayList<TrackDescription>>() {

					@Override
					public void onDataLoaded(
							ArrayList<TrackDescription> loaded) {
						Log.v(TAG, "mDescriptions onDataLoaded");
						mInfoBox.setupSpinner(mDescriptions.getNames(), 
								new TrackSpinnerListener(), 
								mMapPrefs.getSelectedTrackIndex());
					}
			});
		}
	}

	@Override
	public void displayPreferenceChanged() {
		Log.d(TAG, "displayPreferenceChanged");
		mMapDecorator.decorate(mDescriptions.getDescription(
				mMapPrefs.getSelectedTrackIndex()));

		showTrackInfo();
		mMapDecorator.removeUserPath();
	}
	
	private void showTrackInfo() {
		Log.d(TAG, "showTrackInfo");
		setTrackSpinner();
		updateTrackInfo();
	}

	private void updateTrackInfo() {
		Log.d(TAG, "updateTrackInfo");
		//setCurrentTrack(mMapPrefs.getSelectedTrackIndex());
		TrackDescription desc = mDescriptions.getDescription(
				mMapPrefs.getSelectedTrackIndex());
		
		if(desc!= null) {
			mInfoBox.addItems(desc.getName(), 
				getResources().getStringArray(R.array.hike_info),
				desc.getPublicData());
		} else {
			mDescriptions.setDataLoadedListener(
					new DataLoadedListener<ArrayList<TrackDescription>>() {

						@Override
						public void onDataLoaded(
								ArrayList<TrackDescription> loaded) {
							TrackDescription desc = mDescriptions.getDescription(
									mMapPrefs.getSelectedTrackIndex());
							mInfoBox.addItems(desc.getName(), 
									getResources().getStringArray(R.array.hike_info),
									desc.getPublicData());
						}
			});
		}
	}

	private class TrackSpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			Log.d(TAG, "onItemSelected " + position);
			setCurrentTrack(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {}		
	}

}
