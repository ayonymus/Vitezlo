package org.szvsszke.vitezlo.preferences;

import org.szvsszke.vitezlo.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ResetPreferncesItem extends AbstractPreferenceItem {
	
	private final String TAG = getClass().getName();
	
	private AbstractPreferenceFragment mPrefFragment;

	public ResetPreferncesItem(LayoutInflater inflater,
			SharedPreferences preference, AbstractPreferenceFragment prefFragment) {
		super(inflater, R.layout.pref_item_simple_two_line, 
				R.layout.pref_dialog_simple_one_line, null, preference, 
				R.string.reset);
		mPrefFragment = prefFragment;
		mTVDescription.setText(R.string.reset_detail);
	}

	@Override
	protected void showPreferenceDialog() {
		AlertDialog.Builder dialogBuilder = new Builder(mPrefFragment.getActivity());
		dialogBuilder.setTitle(mPreferenceTitleID);
		View dialogView = mInflater.inflate(R.layout.pref_dialog_simple_one_line, null);
		TextView text = (TextView) dialogView.findViewById(R.id.textViewDialogText);
		text.setText(R.string.reset_reassure);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setCancelable(true)
				.setPositiveButton(R.string.ok, 
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// reset preferences
								mPrefFragment.resetPreferences();
								mPrefChanged = true;
								Log.d(TAG, "preferences are reset");
							}
						})
				.setNegativeButton(R.string.cancel, 
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();						
					}
			});
		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}

	@Override
	public void resetItem() {
		// do nothing
		
	}
}
