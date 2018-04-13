package org.szvsszke.vitezlo2018.preferences;

import java.util.ArrayList;

import org.szvsszke.vitezlo2018.R;

import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Base class for my custom settings fragments.
 * */
abstract public class AbstractPreferenceFragment extends Fragment{
	
	private LayoutInflater mInflater;
	private SharedPreferences mPrefs;
	private LinearLayout mItemContainer;
	
	private ArrayList<AbstractPreferenceItem> mItems;
	
	protected TextView mTVTitle;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {				
		super.onCreateView(inflater, container, savedInstanceState);
		
		mInflater = inflater;
		mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		View layout = mInflater.inflate(R.layout.pref_fragment, container, false);
		
		mItemContainer = (LinearLayout) layout.findViewById(R.id.preferenceContainer);
		mTVTitle = (TextView) layout.findViewById(R.id.prefTitle);
		mItems = new ArrayList<AbstractPreferenceItem>();
		return layout;
	}
	
	/**
	 * Sets the text of first the line (title).
	 * @param titleID the title of the settings screen 
	 * */
	public void setTitle(int titleID) {
		mTVTitle.setText(titleID);
	}
	
	public void addCategory(int titleID) {
		View v = mInflater.inflate (R.layout.pref_item_simple_two_line, null);
		TextView title = (TextView) v.findViewById(R.id.textViewItemTitle);
		title.setTextAppearance(getActivity(), android.R.style.TextAppearance_Large);
		title.setText(titleID);
		mItemContainer.addView(v);
	}
	
	/**
	 * Factory method for creating a checkbox settings item. The item is 
	 * hooked to the shared preference.
	 * 
	 * @param settinName key in the SharedPreferences.
	 * @param titleID string id of title
	 * @param descriptionID id of description string
	 * @param defaultValue of the item
	 * 
	 * @return the newly created checkbox item hooked to the appropriate
	 * 			preference.
	 * */
	public CheckBoxItem addCheckBoxItem (String settinName, int titleID,
			int descriptionID, boolean defaultValue) {
		CheckBoxItem item = new CheckBoxItem(mInflater, settinName,
				mPrefs, defaultValue, titleID, descriptionID);
		mItemContainer.addView(item.getView());
		mItems.add(item);
		return item;
	}
	
	/**
	 * Factory method for creating a color selector settings item. The item is 
	 * hooked to the shared preference.
	 * 
	 * @param preferenceKey key in the SharedPreferences.
	 * @param titleID string id of title
	 * @param descriptionID id of description string
	 * @param defaultARGBValue of the color
	 * 
	 * @return the newly created ColorSelectorItem item hooked to the appropriate
	 *			preference.
	 * */
	public ColorSelectorItem addColorSelectorItem(String preferenceKey, 
			int titleID, int defaultARGBValue) {
		ColorSelectorItem item = new ColorSelectorItem(mInflater, 
				mPrefs, preferenceKey, defaultARGBValue, titleID, getActivity());
		mItemContainer.addView(item.getView());
		mItems.add(item);
		return item;
	}
	
	
	/**
	 * Factory method for creating a number selector settings item. The item is 
	 * hooked to the shared preference.
	 * 
	 * @param preferenceKey key in the SharedPreferences.
	 * @param titleID string id of title
	 * @param descriptionID id of description string
	 * @param defaultValue of the number
	 * @param minValue the minimum selectable value
	 * @param maxValue the max value selectable
	 * @param the unit of the value
	 * 
	 * @return the newly created ColorSelectorItem item hooked to the appropriate
	 *			preference.
	 * */	
	public NumberSelectorItem addNumberSelectorItem(String preferenceKey, 
			int titleID, int descriptionID, int defaultValue, int minValue, 
			int maxValue, String unit) {
		
		NumberSelectorItem item = new NumberSelectorItem(mInflater, preferenceKey, 
				mPrefs, defaultValue, minValue, maxValue, unit, titleID, 
				descriptionID, getActivity());
		mItemContainer.addView(item.getView());
		mItems.add(item);
		return item;
	}
	
	public TimeSelectorItem addTimePickerItem(String preferenceKey, int titleID,
			int defaultHour, int defaultMinute) {
		TimeSelectorItem item = new TimeSelectorItem(mInflater, 
				R.layout.pref_item_simple_two_line, preferenceKey, mPrefs, 
				titleID, defaultHour, defaultMinute, getActivity()); 
		mItemContainer.addView(item.getView());
		mItems.add(item);
		return item;
	}
	
	public ResetPreferncesItem addResetPreferencesItem(){
		ResetPreferncesItem item = new ResetPreferncesItem(mInflater, mPrefs, 
				this) ;
		mItemContainer.addView(item.getView());
		mItems.add(item);
		return item;
	}
	
	/**
	 * Warning: this item does NOT extend the BasePreferenceItem!
	 * @param layoutID
	 * @param titleID
	 * @param descriptionID
	 * @param reassureID
	 * @param positiveAction
	 * @param positiveTextID
	 * @param negativeAction
	 * @param negativeTextID
	 * @return the item
	 */
	public AgreeDeclineItem addAgreeDeclineItem(
			final int titleID, int descriptionID, final int reassureID, 
			final OnClickListener positiveAction, final int positiveTextID,
			final OnClickListener negativeAction, final int negativeTextID) {
		
		AgreeDeclineItem item = new AgreeDeclineItem(mInflater,
				R.layout.pref_item_simple_two_line,
				titleID, descriptionID, reassureID, positiveAction, 
				positiveTextID, negativeAction, negativeTextID, getActivity());
		
		mItemContainer.addView(item.getView());
		return item;
	}
	
	/**Calls reset to all items added to the fragment.*/
	public void resetPreferences() {
		for(AbstractPreferenceItem item : mItems) {
			item.resetItem();
		}
	}
	
	/**
	 * Checks weather any added items have been changed since the last check.
	 * 
	 *  @return true if changed
	 * */
	public boolean havePreferencesChanged() {
		boolean changed = false;
		if(mItems != null) {
			for(AbstractPreferenceItem item : mItems) {
				if (item.hasChanged()) {
					changed = true;
					item.setChanged(false);
				}
			}
		}
		return changed;
	}
}
