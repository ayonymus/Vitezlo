package org.szvsszke.vitezlo.preferences;

import org.szvsszke.vitezlo.R;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class CheckBoxItem extends AbstractPreferenceItem {
	
	private final String TAG = getClass().getName();
	
	private CheckBox mCheckBox;
	
	private boolean defaultValue;
	
	public CheckBoxItem (LayoutInflater inflater,
			String preferenceName, SharedPreferences preferences, 
			boolean defaultValue, int titleID, int descriptionID) {
		
		super(inflater, R.layout.pref_item_check_box, -1, preferenceName,
				preferences, titleID);
		
		this.defaultValue = defaultValue;
		
		mCheckBox = (CheckBox) mItemView.findViewById(R.id.checkBoxItem);
		mCheckBox.setChecked(mPref.getBoolean(preferenceName, defaultValue));
		
		mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, 
					boolean isChecked) {
				Log.d(TAG, mTVTitle.getText() + "onCheckedChange" );
				savePreference(isChecked);
			}
		});
		
		if (descriptionID != -1) {
			TextView desc = (TextView) mItemView.findViewById(R.id.textViewItemDescription);
			desc.setText(descriptionID);
		}
	}

	@Override
	protected void showPreferenceDialog() {
		// no dialog for this preference
	}
	
	@Override
	public void resetItem() {
		mCheckBox.setChecked(defaultValue);
	}	
}
