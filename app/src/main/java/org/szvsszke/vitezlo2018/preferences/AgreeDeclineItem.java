package org.szvsszke.vitezlo2018.preferences;

import org.szvsszke.vitezlo2018.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * @brief Class that is used for triggering a one-time event that
 * 			requires reassurance from the user, such as deleting
 * 			some kind of data.
 * 
 * @author Gabor Tatrai
 * */
public class AgreeDeclineItem {
	
	private final String TAG = getClass().getName();
	
	private View mItemView;
	private TextView mTVTitle, mTVDescription;
	
	/**
	 * @param inflater
	 * @param layoutID
	 * @param titleID
	 * @param descriptionID
	 * @param reassureID
	 * @param positiveAction
	 * @param positiveTextID
	 * @param negativeAction
	 * @param negativeTextID
	 * @param context
	 */
	public AgreeDeclineItem (LayoutInflater inflater, int layoutID,
			final int titleID, int descriptionID, final int reassureID, 
			final OnClickListener positiveAction, final int positiveTextID,
			final OnClickListener negativeAction, final int negativeTextID,
			final Context context) {
		
		mItemView = inflater.inflate(layoutID, null);
		mTVTitle = (TextView) mItemView.findViewById(R.id.textViewItemTitle);
		mTVDescription = (TextView) mItemView.findViewById(
				R.id.textViewItemDescription);
		mTVTitle.setText(titleID);
		mTVDescription.setText(descriptionID);
		
		mItemView.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				showDialog(titleID, reassureID, positiveAction, negativeAction,
						positiveTextID, negativeTextID, context);
			}
		});
	}
	
	/**
	 * @return The inflated view of the object.
	 * */
	public View getView() {
		return mItemView;
	}
	
	/**
	 * @param titleID
	 * @param reassureID
	 * @param positive
	 * @param negative
	 * @param positiveTextID
	 * @param negativeTextID
	 * @param context
	 */
	private void showDialog(int titleID, int reassureID, OnClickListener positive,
			OnClickListener negative, int positiveTextID, int negativeTextID,
			Context context){
		
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(titleID);
		LinearLayout layout = new LinearLayout(context);
		TextView reassure = new TextView(context);
		reassure.setText(reassureID);
		layout.addView(reassure);
		builder.setView(layout)
			.setCancelable(true)
			.setPositiveButton(positiveTextID, positive)
			.setNegativeButton(negativeTextID, negative);
		
		AlertDialog dialog = builder.create();
		dialog.show();		
	}
	
			
}
