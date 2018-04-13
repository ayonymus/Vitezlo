package org.szvsszke.vitezlo2018;

import org.szvsszke.vitezlo2018.map.MapPreferences;
import org.szvsszke.vitezlo2018.preferences.AbstractPreferenceFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @brief Fragment for setting the VitezloMapFragment preferences.
 * */
public class MapPreferncesFragment extends AbstractPreferenceFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		View layout = super.onCreateView(inflater, container, savedInstanceState); 
		
		addCheckBoxItem(MapPreferences.IS_INFO_ENABLED, R.string.info_box_visible,
				R.string.info_box_visible_desc, true);
		
		addCheckBoxItem(MapPreferences.IS_CONTROL_ENABLED, 
				R.string.view_ctrls_visible,
				R.string.view_ctrls_visible_desc, true);
		
		addCheckBoxItem(MapPreferences.IS_ZOOM_ENABLED, R.string.zoom_ctrl_visible, 
				R.string.zoom_ctrl_visible_desc, true);
		
		addCheckBoxItem(MapPreferences.CENTER_TO_TRACK, R.string.center_to_track, 
				R.string.center_to_track_desc, true);
		
		addColorSelectorItem(MapPreferences.TRACK_COLOR, 
				R.string.hike_color_title, MapPreferences.DEFAULT_TRACK_COLOR);
		
		addColorSelectorItem(MapPreferences.USER_PATH_COLOR,
				R.string.my_hike_color_title, MapPreferences.DEFAULT_USER_HIKE_COLOR);
		
		addResetPreferencesItem();
		return layout; 
	}
}
