package org.szvsszke.vitezlo2018;

import org.szvsszke.vitezlo2018.gpslogger.GpsDatabase;
import org.szvsszke.vitezlo2018.gpslogger.GpsLoggerConstants;
import org.szvsszke.vitezlo2018.gpslogger.GpsLoggerService;
import org.szvsszke.vitezlo2018.gpslogger.GpsLoggerServiceReplyHandler;
import org.szvsszke.vitezlo2018.gpslogger.GpsLoggerServiceReplyHandler.HandleReply;
import org.szvsszke.vitezlo2018.gpslogger.IGpsLoggerServiceMessages;
import org.szvsszke.vitezlo2018.preferences.AbstractPreferenceFragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class GpsLoggerPrefernceFragment extends AbstractPreferenceFragment implements HandleReply{
	
	private final String TAG = getClass().getName();
	
	private static final int MIN_SELECTABEL_DIST = 0;
	private static final int MAX_SELECTABLE_DIST = 200;
	private static final String METER = "m"; 
	
	private static final int MIN_TIME = 1;
	private static final int MAX_TIME = 300;
	private static final String SECOND = "s";
	
	private static final int MIN_BATT = 10;
	private static final int MAX_BATT = 90;
	private static final String PERCENT = "%";
	
    private GpsLoggerServiceReplyHandler mReplyHandler;
    private IGpsLoggerServiceMessages mMessages;
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	// acquire reference to messenger from activity
    	try {
            mMessages = ((IGpsLoggerServiceMessages) activity);
            //test if successful
            if(mMessages == null) {
            	Log.e(TAG, "onAttach, messenger refernce is null!"); 
            }
            
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */
        	castException.printStackTrace();
        }
    	mReplyHandler = new GpsLoggerServiceReplyHandler(this);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View layout = super.onCreateView(inflater, container, savedInstanceState);
		setTitle(R.string.logger_prefs);
		
		addNumberSelectorItem(GpsLoggerConstants.PREF_MIN_DISTANCE_BETWEEN_FIX,
				R.string.min_distance_between_fix, R.string.min_distance_between_fix, 
				GpsLoggerConstants.DEF_MIN_DISTANCE, 
				MIN_SELECTABEL_DIST, MAX_SELECTABLE_DIST, METER);
		
		addNumberSelectorItem(GpsLoggerConstants.PREF_MIN_TIME_BETWEEN_FIX, 
				R.string.min_time_between_fix, R.string.min_time_expl, 
				GpsLoggerConstants.FIVE_SECONDS, MIN_TIME, MAX_TIME, SECOND);
		/*
		addCategory(R.string.auto_stop_logger);
		
		addNumberSelectorItem(GpsLoggerConstants.PREF_BATTERY_LIMIT, 
				R.string.battery_limit, R.string.battery_limit, 
				GpsLoggerConstants.DEF_BATTERY_LIMIT, MIN_BATT, MAX_BATT, PERCENT);
		
		addTimePickerItem(GpsLoggerConstants.PREF_TIMEOUT, R.string.logger_timeout,
				GpsLoggerConstants.DEF_GPS_TIMEOUT, 0);
		*/
		addResetPreferencesItem();
		
		// reset gps database
		addAgreeDeclineItem(R.string.delete_user_hikes_title, 
				R.string.delete_user_hikes_desc, 
				R.string.delete_user_hikes_reassure, 
				new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mMessages.requestStatus(mReplyHandler);
						dialog.dismiss();
					}
				}, R.string.ok, 
				new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}, R.string.cancel);
		return layout;
	}
	
	@Override
	public void onStop() {		
		
		if (havePreferencesChanged()) {
			Log.d(TAG, "preferences changed, notify logger");
			mMessages.restartService();
		}
		super.onStop();
	}

	@Override
	public void handleReply(int stateCode, String logName, long chronoBaseTime) {
		// can't delete data if logger is running
		if(stateCode == GpsLoggerService.RUNNING) {
			Toast.makeText(getActivity(), 
					R.string.logger_must_be_stopped,
					Toast.LENGTH_LONG).show();
		}
		else {
			//reset database
			GpsDatabase gdb = GpsDatabase.getInstance(getActivity()
					.getApplicationContext());
			gdb.resetDatabase();
			//notify user
			Toast.makeText(getActivity(), 
					R.string.delete_user_hikes_success,
					Toast.LENGTH_SHORT).show();
		}
		
	}
}
