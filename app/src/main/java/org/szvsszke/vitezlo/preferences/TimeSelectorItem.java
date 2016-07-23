package org.szvsszke.vitezlo.preferences;

import java.util.Calendar;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TimePicker;

public class TimeSelectorItem extends AbstractPreferenceItem implements OnTimeSetListener{
	
	private final String TAG = getClass().getName();
	private Context mContext;
	private int mCurrentMin;
	private int mCurrentHour;
	private int mDefaultHour;
	private int mDefaultMinute;
	
	public TimeSelectorItem(LayoutInflater inflater, int layoutID, 
			String preferenceKey, SharedPreferences preference, int titleID,
			int defaultHour, int defaultMinute, Context context) {
		
		super(inflater, layoutID, 0, preferenceKey, preference, titleID);
		Calendar defaultCal = Calendar.getInstance();
		defaultCal.set(Calendar.HOUR_OF_DAY, defaultHour);
		defaultCal.set(Calendar.MINUTE, defaultMinute);
		
		long millis = mPref.getLong(preferenceKey, 0);		
		defaultCal.setTimeInMillis(millis);
		mCurrentMin = defaultCal.get(Calendar.MINUTE);
		mCurrentHour = defaultCal.get(Calendar.HOUR_OF_DAY);
		setDescription(mCurrentHour, mCurrentMin);
		mContext = context;
		mDefaultHour = defaultHour;
		mDefaultMinute = defaultMinute;
	}
	
	private void setDescription(int hour, int minute) {
		String minuteString = minute < 10 ? "0" + minute : "" + minute;
		mTVDescription.setText( hour + " : " + minuteString);
	}
	

	@Override
	public void resetItem() {
		mCurrentHour = mDefaultHour;
		mCurrentMin = mDefaultMinute;
		setDescription(mCurrentHour, mDefaultMinute);
		savePreference();
	}
	
	protected void savePreference() {
		Calendar setTime = Calendar.getInstance();
        setTime.set(Calendar.HOUR_OF_DAY, mCurrentHour);
        setTime.set(Calendar.MINUTE, mCurrentMin);
		super.savePreference(setTime.getTimeInMillis());
	}

	@Override
	protected void showPreferenceDialog() {
		
		TimePickerDialog tpicker = new TimePickerDialog(mContext, this, mCurrentHour, 
				mCurrentMin, true);
		tpicker.setTitle(mTVTitle.getText());
		tpicker.show();
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Calendar setTime = Calendar.getInstance();
        setTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        setTime.set(Calendar.MINUTE, minute);
        Log.d(TAG, "current time" + setTime.getTimeInMillis());        
        mCurrentHour = setTime.get(Calendar.HOUR_OF_DAY);
        mCurrentMin = setTime.get(Calendar.MINUTE);
        setDescription(mCurrentHour, mCurrentMin);
        savePreference();
	}

}
