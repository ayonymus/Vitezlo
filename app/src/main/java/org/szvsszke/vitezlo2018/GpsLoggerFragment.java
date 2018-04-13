package org.szvsszke.vitezlo2018;

import org.szvsszke.vitezlo2018.gpslogger.GpsDatabase;
import org.szvsszke.vitezlo2018.gpslogger.GpsLoggerConstants;
import org.szvsszke.vitezlo2018.gpslogger.GpsLoggerService;
import org.szvsszke.vitezlo2018.gpslogger.GpsLoggerServiceReplyHandler;
import org.szvsszke.vitezlo2018.gpslogger.GpsLoggerServiceReplyHandler.HandleReply;
import org.szvsszke.vitezlo2018.gpslogger.IGpsLoggerServiceMessages;
import org.szvsszke.vitezlo2018.map.model.Track;
import org.szvsszke.vitezlo2018.utilities.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GpsLoggerFragment extends Fragment implements HandleReply {

    // GpsLoggerConstants
    private final String TAG = getClass().getName();

    private static final String WARNING = "logger_warn";

    // ui
    private TextView mTVAVGSpeed, mTVDistance, mTVPointsRecorded,
            mTVAvrgAccuracy, mTVLogName, mTVStatus;

    // chronometervt
    private Chronometer mChronometer;
    private View mTVTime;
    // images as buttons
    private ImageView mPlay;
    private ImageView mPause;

    private LinearLayout mInfoContainer;

    private GpsDatabase mDB;

    private LocationManager mLocManager;

    private String mCurrentLogName = null;
    private boolean isRunning = false;

    private GpsLoggerServiceReplyHandler mReplyHandler;
    private IGpsLoggerServiceMessages mMessages;

    private LayoutInflater inflater;

    private SharedPreferences mPrefs;

    private View elapsed;

    private TextView mTVelapsed;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // acquire reference to messenger from activity
        try {
            mMessages = ((IGpsLoggerServiceMessages) activity);
            //test if successful
            if (mMessages == null) {
                Log.e(TAG, "onAttach, messenger refernce is null!");
            }

        } catch (ClassCastException e) {
            /** The activity does not implement the listener. */
            Log.e(TAG, "onAttach: ", e);
        }
        mReplyHandler = new GpsLoggerServiceReplyHandler(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        View root = inflater.inflate(R.layout.fragment_logger, null);
        //setup labels
        mInfoContainer = (LinearLayout) root.findViewById(R.id.statContainer);

        setUpTextViews(inflater);
        mPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        // setup control buttons
        mPlay = (ImageView) root.findViewById(R.id.imageViewPlay);
        mPause = (ImageView) root.findViewById(R.id.imageViewPause);
        LoggerButtonListener listener = new LoggerButtonListener();
        mPlay.setOnClickListener(listener);
        mPause.setOnClickListener(listener);
        // status bar
        mTVStatus = (TextView) root.findViewById(R.id.textViewStatus);

        mDB = GpsDatabase.getInstance((getActivity().getApplicationContext()));
        mLocManager = (LocationManager) getActivity().getSystemService(
                Context.LOCATION_SERVICE);
        if (PermissionHelper.hasCoarse(getContext())  &&
                PermissionHelper.hasFineLocation(getContext())) {
            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    mPrefs.getInt(GpsLoggerConstants.PREF_MIN_TIME_BETWEEN_FIX, 5000),
                    mPrefs.getInt(GpsLoggerConstants.PREF_MIN_DISTANCE_BETWEEN_FIX, 5),
                    new LocListener());
        }

        // set default state
		handleReply(10, null, 0);
		return root;
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		mMessages.requestStatus(mReplyHandler);
		super.onResume();
		
	};
	
	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		
	};

	private void setUpTextViews(LayoutInflater inflater) {
		mTVAVGSpeed = addStatLine(inflater, R.string.av_speed);
		mTVDistance = addStatLine(inflater, R.string.distance_made);
		mChronometer = addChronometer(inflater);
		//
		elapsed = inflater.inflate(R.layout.info_box_item, null);
		TextView tvLabel = (TextView) elapsed.findViewById(R.id.textViewInfoLabel);
		mTVelapsed = (TextView) elapsed.findViewById(R.id.textViewInfoContent);
		tvLabel.setText(R.string.elapsed_time);	
		//
		mTVPointsRecorded = addStatLine(inflater, R.string.points_recorded);
		mTVAvrgAccuracy = addStatLine(inflater, R.string.gps_accuracy);
		mTVLogName = addStatLine(inflater, R.string.log_as);
	}
	
	/**
	 * Updates the ui depending on the logger service's state.
	 * 
	 * @param state the state of the logger service
	 * */
	@Override
	public void handleReply(int state, String logName, long baseTime) {
		// default state
		switch (state) {
		case GpsLoggerService.RUNNING:
			isRunning = true;
			mTVStatus.setText(R.string.log_state_running);
			mPause.setVisibility(View.VISIBLE);
			mPlay.setVisibility(View.GONE);
			setFieldsFromDatabase(logName);
			mChronometer.setBase(baseTime);
			mChronometer.start();
			break;
		
		case GpsLoggerService.STOPPED:
			isRunning = false;
			mChronometer.stop();
			mTVStatus.setText(R.string.log_state_stopped);
			mPlay.setVisibility(View.VISIBLE);
			mPause.setVisibility(View.GONE);			
			if (DateUtils.isToday(mDB.getLastTimeStamp())) {
				setFieldsFromDatabase(mDB.getLastPathName());
			}
			else {
				setFields(null);
			}			
			break;

		case GpsLoggerService.LOW_BATTERY:
			Log.d(TAG, "low battery");
			break;
			
		default:			
			mTVStatus.setText(R.string.please_wait);
			mPlay.setVisibility(View.GONE);
			mPause.setVisibility(View.GONE);
			break;
		}
		
		if (logName == null) {
			mCurrentLogName = generateLogName();
		}else {
			mCurrentLogName = logName;
		}
		mTVLogName.setText(mCurrentLogName);
	}
	

	/**
	 * Adds a line of satistics to the main view.
	 * @param inflater a layout inflater
	 * @param label for the text view, later it is used as a key for updating
	 * the content text view
	 * @return the TextView of the content that is subject to changes. 
	 * */
	private TextView addStatLine(LayoutInflater inflater, int label) {
		View item = inflater.inflate(R.layout.info_box_item, null);
		
		TextView tvLabel = (TextView) item.findViewById(R.id.textViewInfoLabel);
		TextView tvContent = (TextView) item.findViewById(R.id.textViewInfoContent);
		
		tvLabel.setText(label);
		tvContent.setText(R.string.no_data_);		
		mInfoContainer.addView(item);
		return tvContent;
	}

	private Chronometer addChronometer(LayoutInflater inflater) {
		View item = inflater.inflate(R.layout.info_box_chrono_item, null);
		Chronometer chrono = (Chronometer)item.findViewById(R.id.chronometer1);
		chrono.setBase(SystemClock.elapsedRealtime());
		mInfoContainer.addView(item);
		return chrono;
	}

	private void setFieldsFromDatabase(final String logName) {
		
		if (logName != null) {
			// create a thread for loading from database
			new AsyncTask<String, Integer, Track>() {
	
				@Override
				protected Track doInBackground(String... params) {					
					return mDB.getPathTrack(logName);
						
				}
					
				protected void onPostExecute(Track result) {
					setFields(result);
				};
			}.execute(new String[]{});
		}
	}
	
	private void setFields(Track track) {
		// check if last log is from today
		if (track != null && DateUtils.isToday(
				track.getTime().get(track.getTime().size() - 1))) {
			//set chronometer from database
			if (isRunning) {
				long now = System.currentTimeMillis();
				long start = track.getTime().get(0);
				long nowReal = SystemClock.elapsedRealtime();
				long base = nowReal - (now - start);
				mChronometer.setBase(base);
				elapsed.setVisibility(View.GONE);
				mChronometer.setVisibility(View.VISIBLE);

			}
			else {
				//latest - earliest entry
				long start = track.getTime().get(0);
				long end = track.getTime().get(track.getTime().size() -1 );
				String length = Utilities.millisToHMS(end - start);
				mChronometer.setVisibility(View.GONE);
				mTVelapsed.setText(length);
				elapsed.setVisibility(View.VISIBLE);
			}
			double length = Utilities.calculateTrackLength(
					track.getTrackPoints());		
			String dist = Utilities.distanceToKMorM(length);
			mTVDistance.setText(dist);
			
			long millis = track.getTime().get(track.getTime().size() - 1) 
					- track.getTime().get(0);
			mTVAVGSpeed.setText(Utilities.roundedSpeedInKMH(millis, length));			
			mTVPointsRecorded.setText(" " + track.getTrackPoints().size());
			mTVAvrgAccuracy.setText("" + Double.valueOf(
					Utilities.calculateAverageAccuracy(track)).intValue() + " m");
			mTVLogName.setText(track.getTrackName());
			mCurrentLogName = track.getTrackName();
		}
		else {
			// set defauil value " - "
			mTVAVGSpeed.setText(R.string.no_data_);
			mTVDistance.setText(R.string.no_data_);
			mTVPointsRecorded.setText(R.string.no_data_);
		}		 
	}

	private String generateLogName() {
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		return today.format("%Y-%m-%d_%H-%M-%S");
	}

	
	//handles the button clicks
	private class LoggerButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// handle click event
			switch (v.getId()) {
			case R.id.imageViewPlay:
				if(!mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					Toast.makeText(getActivity(), R.string.gps_needed, Toast.LENGTH_SHORT).show();
					// go to settings to enable gps
					Intent gpsOptionsIntent = new Intent(  
							android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
							startActivity(gpsOptionsIntent);
					break;
				}
				if(mCurrentLogName == null) {
					// shouldnt be null by now
					mCurrentLogName = generateLogName();
				}
				
				View messageView = inflater.inflate(R.layout.warning_tick_box, null);
				final CheckBox box = (CheckBox)messageView.findViewById(R.id.checkBoxDontShowBatteryWarning);
				
				// show alert about battery
				if(!mPrefs.getBoolean(WARNING, false)) {
					//Log.d(TAG, "")
					new AlertDialog.Builder(getActivity())
						.setTitle(R.string.warning)
						.setView(messageView)
						.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// check tick box
								if (box.isChecked()) {
									mPrefs.edit().putBoolean(WARNING, true).commit();
								}
								dialog.dismiss();
								startLog();
							}
						})
						.show();
				}
				else {
					startLog();
				}
				
				break;
			default:
				// stop
				mMessages.requestStopService(mReplyHandler);				
				break;
			}
		}
	}
	
	private void startLog() {
		// check database for last entry
		String lastHikeName = mDB.getLastPathName();
		if (lastHikeName != null &&
				lastHikeName.contentEquals(mCurrentLogName)) {
			
			// prompt user for continue if last log name in database is the same as
			// the one stored in the service
			final TextView question = new TextView(getActivity());
			question.setText(R.string.continue_log_desc);
			new AlertDialog.Builder(getActivity())
				.setTitle(R.string.continue_log_title)
				.setView(question)
				.setPositiveButton(R.string.continue_log, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mMessages.requestStartService(mCurrentLogName, mChronometer.getBase(), mReplyHandler);
						dialog.dismiss();
					}
				})
				.setNegativeButton(R.string.new_log, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//generate a new name
//						mChronometer.setBase(SystemClock.elapsedRealtime());
						mMessages.requestStartService(generateLogName(), SystemClock.elapsedRealtime(), mReplyHandler);								
						dialog.dismiss();
					}
			}).show();
		}
		else {
			mMessages.requestStartService(generateLogName(), SystemClock.elapsedRealtime(), mReplyHandler);
		}
	}
	
	private class LocListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			setFieldsFromDatabase(mCurrentLogName);			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onProviderDisabled(String provider) {}	
	}
}
