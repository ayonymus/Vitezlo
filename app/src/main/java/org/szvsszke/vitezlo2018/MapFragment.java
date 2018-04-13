package org.szvsszke.vitezlo2018;

import java.util.ArrayList;

import org.szvsszke.vitezlo2018.gpslogger.GpsDatabase;
import org.szvsszke.vitezlo2018.gpslogger.GpsLoggerService;
import org.szvsszke.vitezlo2018.gpslogger.GpsLoggerServiceReplyHandler.HandleReply;
import org.szvsszke.vitezlo2018.map.MapDecorator;
import org.szvsszke.vitezlo2018.map.MapPreferences;
import org.szvsszke.vitezlo2018.map.data.AbstractDataCache.DataLoadedListener;
import org.szvsszke.vitezlo2018.map.data.DescriptionsCache;
import org.szvsszke.vitezlo2018.map.data.UserPathCache;
import org.szvsszke.vitezlo2018.map.model.Track;
import org.szvsszke.vitezlo2018.map.model.TrackDescription;
import org.szvsszke.vitezlo2018.map.overlay.InfoBox;
import org.szvsszke.vitezlo2018.map.overlay.MapControlBox;
import org.szvsszke.vitezlo2018.map.overlay.MapControlBox.MapControlListener;
import org.szvsszke.vitezlo2018.utilities.Utilities;

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

/**
 * This fragment is responsible for loading data and displaying it
 * overlaying a map. 
 * */
public class MapFragment extends Fragment implements MapControlListener, 
		HandleReply {

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
	private UserPathCache mUserPaths;

	//view variables
	private boolean isBoxExpanded = true;
	
	private boolean isLoggerRunning;
	private boolean mUpdaterRunning = false;

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
		
		mUserPaths = new UserPathCache(getActivity());

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
			// check if user path database has any entries
			if (GpsDatabase.getInstance(getActivity()).isEmpty()) {
				mControlBox.enableUserHikeButton(false);
			}
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
		stopUpdater();
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
	
	private void setCurrentUserPath(int pathNr) {
		Log.d(TAG, "setCurrentUserPath");
		mMapPrefs.setSelectedUserPathIndex(pathNr);
		if (mUserPaths.acquireUserPathNames() == null) {
			return;
		};
		final String pathName = mUserPaths.acquireUserPathNames().get(pathNr);
		mUserPaths.acquireTrack(pathName);
		
		if (mUserPaths.acquireTrack(pathName) == null) {
			mUserPaths.setTrackLoadedListener(new DataLoadedListener<Track>() {
	
				@Override
				public void onDataLoaded(Track loaded) {
					mMapDecorator.drawUserPath(loaded);
					updateUserInfo(loaded);
				}			
			});
		} else {
			mMapDecorator.drawUserPath(mUserPaths.acquireTrack(pathName));
			updateUserInfo(mUserPaths.acquireTrack(pathName));
		}
		getActivity().setTitle(pathName);
		mInfoBox.setTitle(pathName);
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
	
	private void updateUserInfo(Track userPath) {
		long duration = userPath.getTime().get(userPath.getTime().size() -1)
				- userPath.getTime().get(0);
		double length = Utilities.calculateTrackLength(
				userPath.getTrackPoints());
		Double accuracy = Utilities.calculateAverageAccuracy(userPath);
		
		String[] userInfo = new String[]{
			Utilities.roundedSpeedInKMH(duration, length),
			Utilities.distanceToKMorM(length),
			Utilities.millisToHMS(duration),
			accuracy.intValue() + " m",
			"" + userPath.getTrackPoints().size()
		};

		mInfoBox.setTitle(userPath.getTrackName());
		mInfoBox.addItems(getResources().getString(R.string.user_path),
				getResources().getStringArray(R.array.user_path_short),
				userInfo);
	}

	private void setUserPathSpinner() {
		Log.d(TAG, "setUserPathSpinner");
		if (mUserPaths.acquireUserPathNames() != null) {
			mInfoBox.setupSpinner(mUserPaths.acquireUserPathNames(), 
					new UserPathSpinnerListener(), 0);
		}
		else {
			mUserPaths.setPathNamesLoadedListener(
					new DataLoadedListener<ArrayList<String>>() {
						@Override
						public void onDataLoaded(ArrayList<String> loaded) {
							mInfoBox.setupSpinner(mUserPaths.acquireUserPathNames(),
									new UserPathSpinnerListener(), 0);
						}
			});
		}
	}

	@Override
	public void displayPreferenceChanged() {
		Log.d(TAG, "displayPreferenceChanged");
		mMapDecorator.decorate(mDescriptions.getDescription(
				mMapPrefs.getSelectedTrackIndex()));
		if  (mMapPrefs.isUserPathEnabled()) {
			showUserPathInfo();
			if (isLoggerRunning) {
				startUserInfoUpdater();
			}
		}
		else {
			stopUpdater();
			showTrackInfo();
			mMapDecorator.removeUserPath();
		}
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

	private void showUserPathInfo() {
		Log.d(TAG, "showUserPathInfo");
		setUserPathSpinner();
		setCurrentUserPath(0);
	}
	
	private void startUserInfoUpdater() {
		Log.d(TAG, "startUserInfoUpdater");
		mUserPaths.alwaysQuery(true);
		mUpdaterRunning = true;
		mInfoBox.lockSpinner(true);
		
		mUserPaths.setTrackLoadedListener(
				new DataLoadedListener<Track>() {
			@Override
			public void onDataLoaded(Track loaded) {
				updateUserInfo(loaded);								
			}							
		});
		
		Runnable updater = new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "userInfo updater thread started");
				while (mUpdaterRunning) {
					//updateUserInfo();
					if (mUserPaths.acquireUserPathNames() != null) {
						mUserPaths.acquireTrack(
								mUserPaths.acquireUserPathNames().get(0));
					}
					try {
						Thread.sleep(TEN_SECONDS);
					} catch (InterruptedException e) {
						Log.e(TAG, "updater thread interrupted", e);
					}
				}				
			}
		};
		Thread updaterThread = new Thread(updater);
		updaterThread.start();
	}
	
	private void stopUpdater() {
		Log.d(TAG, "stopUpdater");
		if (mUpdaterRunning) {
			mUpdaterRunning = false;
			mInfoBox.lockSpinner(false);
			mUserPaths.alwaysQuery(false);
			mUserPaths.removeTrackLoadedListener();
		}
	}

	private class TrackSpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			Log.d(TAG, "onItemSelected " + position);
			setCurrentTrack(position);
			if (!isLoggerRunning) {
				updateTrackInfo();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {}		
	}
	
	private class UserPathSpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {

			setCurrentUserPath(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {}
	}

	@Override
	public void handleReply(int stateCode, String logName, long chronoBaseTime) {
		Log.d(TAG, "handleServiceReply");
		if(stateCode == GpsLoggerService.RUNNING) {
			isLoggerRunning = true;
			mControlBox.enableUserHikeButton(true);
		}
	}
}
