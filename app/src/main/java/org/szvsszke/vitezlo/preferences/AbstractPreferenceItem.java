package org.szvsszke.vitezlo.preferences;

import org.szvsszke.vitezlo.R;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

abstract class AbstractPreferenceItem {

	protected String mPreferenceKey;
	protected SharedPreferences mPref;
	protected int mPreferenceTitleID;
	protected View mItemView;
	protected TextView mTVTitle;
	protected TextView mTVDescription;
	protected LayoutInflater mInflater;
	protected boolean mPrefChanged = false;
	
	/**
	 * @param inflater that inflates the preference item's view
	 * @param layoutID for the item
	 * @param dialogLayoutId -1 if no dialog for the preference
	 * @param preferenceKey key of the preference
	 * @param preferences instance of SharedPreferences
	 * @param titleID title of the item
	 * */
	public AbstractPreferenceItem (LayoutInflater inflater, int layoutID, 
			int dialogLayoutId, String preferenceKey, SharedPreferences preferences, 
			int titleID) {
		
		mItemView = inflater.inflate(layoutID, null);
		mTVTitle = (TextView) mItemView.findViewById(R.id.textViewItemTitle);
		mTVDescription = (TextView) mItemView.findViewById(
				R.id.textViewItemDescription);
		
		mPreferenceKey = preferenceKey;
		mPref = preferences;
		mPreferenceTitleID = titleID;
		mInflater = inflater;
		mTVTitle.setText(titleID);

		if (dialogLayoutId != -1) {			
			mItemView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showPreferenceDialog();
					
				}
			});
		}
	}
	
	public View getView() {
		return mItemView;
	}
	
	/**
	 * Must call super.savePreferene() when overridden!
	 * */
	protected void savePreference(int value) {
		mPref.edit().putInt(mPreferenceKey, value).apply();
		mPrefChanged = true;
	};
	
	protected void savePreference(boolean value) {
		mPref.edit().putBoolean(mPreferenceKey, value).apply();
		mPrefChanged = true;
	}
	
	protected void savePreference(long value) {
		mPref.edit().putLong(mPreferenceKey, value).apply();
		mPrefChanged = true;
	}
	
	protected void savePreference(String value) {
		mPref.edit().putString(mPreferenceKey, value).apply();
		mPrefChanged = true;
	}
	
	
	public boolean hasChanged() {
		return mPrefChanged;
	}
	
	public void setChanged(boolean changed) {
		mPrefChanged = false;
	}
	
	abstract public void resetItem();
	abstract protected void showPreferenceDialog();		
}

