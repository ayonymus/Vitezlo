package org.szvsszke.vitezlo.preferences;

import org.szvsszke.vitezlo.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

public class NumberSelectorItem extends AbstractPreferenceItem {
	
	//private final String TAG = getClass().getCanonicalName();
		
	// private members
	private int mMinValue, mMaxValue, mCurrentValue, mDefaultValue;
	private String mUnit;
	private Context mContext;
	private int mDescriptionID;
		
	public NumberSelectorItem(LayoutInflater inflater, 
			String preferenceKey, SharedPreferences preference, 
			int defaultValue, int minValue, int maxValue, String unit,
			int titleID, int descriptionID, Context context) {
		
		
		super(inflater, R.layout.pref_item_simple_two_line, 0, preferenceKey,
				preference, titleID);
		
		// set members
		mMinValue = minValue;
		mMaxValue = maxValue;
		mDefaultValue = defaultValue;
		mUnit = unit;
		mCurrentValue = mPref.getInt(preferenceKey, defaultValue);
		mTVDescription.setText(Integer.toString(mCurrentValue) + (mUnit == null ? " " : mUnit));
		mContext = context;
		
		mDescriptionID = descriptionID;
	}

	@Override
	protected void showPreferenceDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
		dialogBuilder.setTitle(mPreferenceTitleID);
		
		View dialogView;	
		OnClickListener positive;
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
		    // go with NumberPicker
			dialogView = mInflater.inflate(R.layout.pref_item_number_dialog_ics,
					null);
			final NumberPicker numPicker = (NumberPicker) dialogView.findViewById(
					R.id.numberPicker1);
			numPicker.setMaxValue(mMaxValue);
			numPicker.setMinValue(mMinValue);
			numPicker.setValue(mCurrentValue);
			
			positive = new OnClickListener() {
			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					mCurrentValue = numPicker.getValue();
					savePreference(mCurrentValue);
					mTVDescription.setText(mCurrentValue + " " + mUnit);
					
				}
			};
		}
		
		else {
			// go with manual entry
			dialogView = mInflater.inflate(R.layout.pref_item_number_dialog_ginger,
					null);
			final EditText etNum = (EditText) dialogView.findViewById(R.id.editTextNumberEntry); 
			
			positive = new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (etNum.getText().length() != 0) {
						int val = Integer.parseInt(etNum.getText().toString());
						if (val < mMinValue) {
							mCurrentValue = mMinValue;
						}
						if (val > mMaxValue) {
							mCurrentValue = mMaxValue;
						}
						savePreference(mCurrentValue);
						mTVDescription.setText(mCurrentValue + " " + mUnit);
					}
					
				}
			};
		}
		
		TextView descr = (TextView) dialogView.findViewById(R.id.textViewNumberDesc);
		descr.setText(mDescriptionID);
		
		dialogBuilder.setView(dialogView);
		dialogBuilder.setCancelable(true)
			.setPositiveButton(R.string.ok, positive)
			.setNegativeButton(R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});
		AlertDialog alertDialog = dialogBuilder.create();
	    alertDialog.show();
	}

	@Override
	public void resetItem() {		
		mCurrentValue = mDefaultValue;
		savePreference(mDefaultValue);
		mTVDescription.setText(mCurrentValue + " " + mUnit);		
	}
}
