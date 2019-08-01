package org.szvsszke.vitezlo2018.map.overlay;

import org.szvsszke.vitezlo2018.R;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the InfoBox overlay
 * */
public class InfoBox {
	
	private static final String TAG = InfoBox.class.getName();

	private View mBaseView;
	private Activity mParentActivity;
	
	// layout handle
	private RelativeLayout mContainer;
	private LinearLayout mContent;
	
	private TextView mTitle;
	private Spinner mSpinner;
	private ArrayAdapter<String> mAdapter;

	private ArrayList<TextView> mLabels;
	private ArrayList<TextView> mContents;
	
	// images
	private ImageView expnadCollapse;
	private ImageView locked;
	private ImageView unLocked;
	
	// state variables
	private boolean isInfoExpanded = true;
	private boolean isSpinnerLocked = false;
	private boolean isLockEnabled = true;

	// constructor for the overlay
	public InfoBox(Activity parentActivity) {
		mParentActivity = parentActivity;
	}
	
	public void onCreateView(LayoutInflater inflater, ViewGroup container) { 

		mBaseView = inflater.inflate(R.layout.info_box, null);
		mContainer =  
				(RelativeLayout) mBaseView.findViewById(R.id.infoBoxContainer);
		mContainer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				expandInfoBox(!isInfoExpanded);
			}
		});
		mSpinner = (Spinner) mBaseView.findViewById(R.id.hikeSpinner);
		
		mContent = (LinearLayout) mBaseView.findViewById(R.id.infoBoxContents);
		// create title view
		mTitle = (TextView) mBaseView.findViewById(R.id.infoBoxTitle);
		mTitle.setVisibility(View.GONE);
				
		mLabels = new ArrayList<TextView>();
		mContents = new ArrayList<TextView>();
		
		expnadCollapse = (ImageView) mBaseView.findViewById(R.id.imageViewCollapseExpand);
		
		// lock/unlock spinner
		locked = (ImageView) mBaseView.findViewById(R.id.imageViewLock);
		unLocked = (ImageView) mBaseView.findViewById(R.id.imageViewUnlock);
		LockListener listener = new LockListener();
		locked.setOnClickListener(listener);
		unLocked.setOnClickListener(listener);
		locked.setVisibility(View.GONE);
		container.addView(mBaseView);
	}
	
	/**
	 * Sets the @param title of the infoBox 
	 * */
	public void setTitle(String title) {
		mTitle.setText(title);
	}
	
	/**Clear all labels and texts*/
	public void resetInfoBox() {		
		mTitle.setText("");
		mContent.removeAllViews();
	}
	/**
	 * Adds a line of statistics to the info box
	 * @param label of the info
	 * @param content the info itself
	 * */
	public void addStatLine(String label, String content) {				
		// inflate info line layout
		RelativeLayout layout = (RelativeLayout) mParentActivity
				.getLayoutInflater().inflate(R.layout.info_box_item, null);
		
		// find textViews
		TextView labelTV = (TextView)layout.findViewById(R.id.textViewInfoLabel);
		TextView contentTV = (TextView)layout.findViewById(R.id.textViewInfoContent);
		// set text
		labelTV.setText(label);
		contentTV.setText(content);
		// add layout to mInfoBoxContent
		mContent.addView(layout);
		// keep a reference to the content
		mLabels.add(labelTV);
		mContents.add(contentTV);
	}
	
	/**
	 * Makes corresponding textView GONE or VISIBLE
	 * @param label of textView
	 * @param isVisible 
	 * */
	public void showTextView(int line, boolean isVisible) {
		if (isVisible) {
			if (mLabels.get(line) != null) {
				mLabels.get(line).setVisibility(View.VISIBLE);
			}
			mContents.get(line).setVisibility(View.VISIBLE);			
		} else {
			mContents.get(line).setVisibility(View.GONE);
			if (mLabels.get(line) != null) {
				mLabels.get(line).setVisibility(View.GONE);
			}
		}
	}
	
	/**Updates the content of a line, including the label.
	 * @param lineNR which line
	 * @param label the new label
	 * @param content the new content
	 * */
	public void updateLine (int lineNR, String label, String content) {
		if (lineNR < mLabels.size()) {
			mLabels.get(lineNR).setText(label);
			mContents.get(lineNR).setText(content);
		} else {
			Log.d(TAG, "No such line: " + lineNR);
		}
	}
	
	/**
	 * Add a series of label/content pairs to the screen. 
	 * @param title title to display when locked
	 * @param labels must be the same length as content
	 * @param contents to display
	 * */
	public void addItems(String title, String[] labels, String [] contents) {
		if (labels.length != contents.length) {
			Log.e(TAG, "addItems : Paramteres don't match");
		}
		else {
			mTitle.setText(title);
			for (int i = 0; i < labels.length; i++) {
				if (mLabels.size() <= i) {
					addStatLine(labels[i], contents[i]);
				}
				else {
					updateLine(i, labels[i], contents[i]);
					mLabels.get(i).setVisibility(View.VISIBLE);
					mContents.get(i).setVisibility(View.VISIBLE);
				}
			}
			// remove extra labels
			if (labels.length < mLabels.size()) {
				for (int i = labels.length; i < mLabels.size(); i++) {
					mLabels.get(i).setVisibility(View.GONE);
					mContents.get(i).setVisibility(View.GONE);
				}
			}
		}
	}
	
	/**
	 * Expands or collapses the infobox.
	 * @param expand true to expand, false to collapse
	 * */
	public void expandInfoBox(boolean expand) {
		if(expand) {
			mContent.setVisibility(View.VISIBLE);			
			expnadCollapse.setImageDrawable(mBaseView.getResources().getDrawable
					(R.drawable.ic_action_collapse));
			
			if(isSpinnerLocked) {
				locked.setVisibility(View.VISIBLE);
				mTitle.setVisibility(View.VISIBLE);
			}
		}
		else {
			mContent.setVisibility(View.GONE);
			expnadCollapse.setImageDrawable(mBaseView.getResources().getDrawable
					(R.drawable.ic_action_expand));
			
			if(isSpinnerLocked) {
				locked.setVisibility(View.GONE);
				mTitle.setVisibility(View.GONE);
			}
		}
		isInfoExpanded = expand;
	}


	/**
	 * Sets a listener for the container.
	 * @param listener an action listener for the container
	 * */
	public void setOnClickListenerForContainer (OnClickListener listener) {
		mContainer.setOnClickListener(listener);
	}

	/**
	 * @retrun true if locked, false if unlocked
	 * */
	public boolean isSpinnerLocked () {
		return isSpinnerLocked;
	}
	
	/**
	 * Locs or unlocks the spinner. 
	 * @param lock true to lock spinner
	 * */
	public void lockSpinner(boolean lock) {
		if(isSpinnerLocked != lock) {
			isSpinnerLocked = lock;
			
			if (lock) {
				mSpinner.setVisibility(View.GONE);
				unLocked.setVisibility(View.GONE);			
				mParentActivity.setTitle(mTitle.getText());
				// don't show lock icon unless expanded in order
				// to take up less space
				if (isInfoExpanded) {
					locked.setVisibility(View.VISIBLE);
					mTitle.setVisibility(View.VISIBLE);
				}
				else {
					mTitle.setVisibility(View.GONE);				
				}
			}
			else {
				mSpinner.setVisibility(View.VISIBLE);
				unLocked.setVisibility(View.VISIBLE);
				locked.setVisibility(View.GONE);
				mTitle.setVisibility(View.GONE);
			}
		}
	}
	
	/**
	 * if spinner lock, unlock it. If spinner unlocked, lock it.
	 * */
	public void switchLock() {
		// only allow switch if lock is enabled
		if(isLockEnabled) {
			lockSpinner(!isSpinnerLocked);
		}
	}
	
	// listens for the two lock icons
	private class LockListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switchLock();
			
		}	
	}
	
	/**
	 * Sets up the spinner contents.
	 * @param names that are presented on the spinner
	 * @param listener for selected items
	 * @param selected the currently selected item
	 * */
	public void setupSpinner(List<String> names, OnItemSelectedListener listener, int selected) {
		Log.d(TAG, "setupSpinner");		
		
		if (mAdapter == null) {
			mAdapter = new ArrayAdapter<>(mParentActivity,
					R.layout.info_box_spinner_item, R.id.textViewTrackName, names);
			mSpinner.setAdapter(mAdapter);
		}
		mSpinner.setOnItemSelectedListener(listener);
		mSpinner.setSelection(selected);
	}

}
