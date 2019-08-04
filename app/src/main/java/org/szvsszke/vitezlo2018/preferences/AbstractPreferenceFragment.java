package org.szvsszke.vitezlo2018.preferences;

import org.szvsszke.vitezlo2018.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

/**
 * Base class for my custom settings fragments.
 * */
@Deprecated
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

	
	public ResetPreferncesItem addResetPreferencesItem(){
		ResetPreferncesItem item = new ResetPreferncesItem(mInflater, mPrefs, 
				this) ;
		mItemContainer.addView(item.getView());
		mItems.add(item);
		return item;
	}
	
	/**Calls reset to all items added to the fragment.*/
	public void resetPreferences() {
		for(AbstractPreferenceItem item : mItems) {
			item.resetItem();
		}
	}
}
