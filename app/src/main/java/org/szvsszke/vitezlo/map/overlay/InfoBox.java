package org.szvsszke.vitezlo.map.overlay;

import java.util.ArrayList;

import org.szvsszke.vitezlo.R;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
	
	// chronometer
	private Chronometer mChrono;
	private View mChronoContainer;
	
	// state variables
	private boolean isInfoExpanded = true;
	private boolean isSpinnerLocked = false;
	private boolean isLockEnabled = true;
	private boolean isChronoStarted = false;
	
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
		
		mContent = 
				(LinearLayout) mBaseView.findViewById(R.id.infoBoxContents);
		addChronometer(inflater, mContent);
		enableChronometer(false);
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
	 * @param the info itself
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
	
	private void addChronometer(LayoutInflater inflater, LinearLayout container) {
		mChronoContainer = inflater.inflate(R.layout.info_box_chrono_item, null);
		mChrono = (Chronometer)mChronoContainer.findViewById(R.id.chronometer1);
		mChrono.setBase(SystemClock.elapsedRealtime());
		container.addView(mChronoContainer);
		
	}
	
	/**@param true to enable chronometer.*/
	public void enableChronometer(boolean enable) {
		if (enable) {
			mChronoContainer.setVisibility(View.VISIBLE);
		}
		else {
			mChronoContainer.setVisibility(View.GONE);
		}
	}
	
	/**Sets the base time of the chronometer.
	 * @param base for chronometer*/
	public void setChronoBase(long base) {
		mChrono.setBase(base);	
	}
	
	/**Starts the chronometer.*/
	public void startChronometer() {
		mChrono.start();
		isChronoStarted = true;
	}
	/**Stops the chronometer.*/
	public void stopChronometer() {
		mChrono.stop();
		isChronoStarted = false;
	}
	
	/**@return true if chronometer is started.*/
	public boolean isChronoStarted() {
		return isChronoStarted;
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
	
	/**
	 * Updates a line's contents or creates a new line if it doesnt exist
	 * @param lineNR number of line
	 * @param content the content
	 * */
	public void updateContent(int lineNR, String content) {
		// check if textview exists
		if (lineNR < mLabels.size()) {
			mContents.get(lineNR).setText(content);
		}
		else {
			Log.d(TAG, "No such line: " + lineNR);
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
	 * @return if InfoBox is expanded
	 * */
	public boolean isExpanded() {
		return isInfoExpanded;
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
	
	/**
	 * Sets a custom listener for the content part of the infobox, 
	 * replacing the default expand/collapse action.
	 * InfoBox does not contain the expand/collapse arrow and the lock icon.
	 * @param listener for the onclick action
	 * */
	public void setInfoBoxOnClickListener (OnClickListener listener) {
		mContainer.setOnClickListener(listener);
	}
	
	/**
	 * Enables / disables lock
	 * @param enabled => true.
	 * */
	public void enableLock(boolean enabled) {
		// only take action if there is a change
		if (isLockEnabled != enabled) {			
			isLockEnabled = enabled;
			
			if (enabled) {
				lockSpinner(false);		
			}
			else {
				lockSpinner(true);
			}
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
	 * @param lockSpinnner weather the spinner should be locked
	 * */
	public void setupSpinner(ArrayList<String> names, 
			OnItemSelectedListener listener, int selected) {
		Log.d(TAG, "setupSpinner");		
		
		if (mAdapter == null) {
			mAdapter = new ArrayAdapter<String> (mParentActivity, 
					R.layout.info_box_spinner_item, R.id.textViewTrackName, names);
			mSpinner.setAdapter(mAdapter);
		}
		else {
			Log.v(TAG, "update spinner adapter contents");
			mAdapter.clear();
			mAdapter.addAll(names);
			mAdapter.notifyDataSetChanged();
		}
		mSpinner.setOnItemSelectedListener(listener);
		mSpinner.setSelection(selected);
	}
	
	public void setSpinner(Spinner spinner) {
		mSpinner = spinner;
	}
	
	/**
	 * Shows or hides the whole info box
	 * */
	public void setVisible(boolean visible) {
		if (visible) {
			mBaseView.setVisibility(View.VISIBLE);
		} else {
			mBaseView.setVisibility(View.GONE);
		}
	}
	
	public boolean isVisible() {
		if (mBaseView.getVisibility() == View.VISIBLE) {
			return true;
		}
		return false;
	}
}
