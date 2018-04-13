package org.szvsszke.vitezlo2018.adapter;

import org.szvsszke.vitezlo2018.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerAdapter extends ArrayAdapter<String> {
	
	private String[] mItemText;
	private int[] mItemIconIDs;
	private Context mContext;
	
	public NavDrawerAdapter(String[] titles, int[] icons, Context context) {
		super(context, R.layout.item_drawer_list, titles);
		mItemText = titles;
		mItemIconIDs = icons;
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// inflate convert view if null
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater)
                    mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_drawer_list, parent, false);
		}
		
		TextView title = (TextView) convertView.findViewById(R.id.drawerItemText);
		title.setText(mItemText[position]);
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
		icon.setImageResource(mItemIconIDs[position]);
		
		return convertView;
	}

}
