package org.szvsszke.vitezlo.preferences;

import org.szvsszke.vitezlo.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ColorSelectorItem extends AbstractPreferenceItem {
	
	private final String TAG = getClass().getName();
	
	private View mColorPreVeiw;
	
	private Context mContext;	
	
	private int mCurrentColor;
	private int defaultValue;
	
	/**
	 * Constructor for the class.
	 * @param inflater to inflate the color selector layout
	 * @param root where the layout is inflated to
	 * @param prefs here the preferences are stored
	 * @param preferenceKey where the changers should be saved.
	 * @param defaultARGB default value for this item
	 * @param titleStringID id for the string to display as title
	 * @param descriptionStringID id for the string to display as description
	 * */
	public ColorSelectorItem(LayoutInflater inflater,
			SharedPreferences prefs, String preferenceKey, int defaultARGB,
			int titleStringID, Context context) {
		
		super(inflater, R.layout.pref_item_color, 
				R.layout.pref_item_color_selector_dialog,
				preferenceKey, prefs, titleStringID);
		Log.d(TAG, "constructor");
		this.defaultValue = defaultARGB;
		mColorPreVeiw = mItemView.findViewById(R.id.viewColorPreview);
		mCurrentColor = mPref.getInt(preferenceKey, defaultARGB);
		mColorPreVeiw.setBackgroundColor(mCurrentColor);
		
		mContext = context;
	}
	
	@Override
	public void resetItem() {		
		mCurrentColor = defaultValue;	
		updateView();
		savePreference(defaultValue);
	}

	protected void updateView() {
		mColorPreVeiw.setBackgroundColor(mCurrentColor);
	}

	protected void showPreferenceDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
		dialogBuilder.setTitle(mPreferenceTitleID);
		
		// find dialog items
		View mDialogView = mInflater.inflate(R.layout.pref_item_color_selector_dialog, null);
		View colorPreview = mDialogView.findViewById(R.id.colorPreview);
		colorPreview.setBackgroundColor(mCurrentColor);
		final SeekBar r = (SeekBar) mDialogView.findViewById(R.id.seekBarR);
		final SeekBar g = (SeekBar) mDialogView.findViewById(R.id.SeekBarG);
		final SeekBar b = (SeekBar) mDialogView.findViewById(R.id.SeekBarB);
		
		r.setMax(255);
		g.setMax(255);
		b.setMax(255);
		
		r.setProgress(Color.red(mCurrentColor));
		g.setProgress(Color.green(mCurrentColor));
		b.setProgress(Color.blue(mCurrentColor));	
		
		SeekerChangedListener l = new SeekerChangedListener(colorPreview, r, g, b);
		r.setOnSeekBarChangeListener(l);
		g.setOnSeekBarChangeListener(l);
		b.setOnSeekBarChangeListener(l);
		
		dialogBuilder.setView(mDialogView);
		dialogBuilder.setCancelable(true)
			.setPositiveButton(R.string.ok, 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mCurrentColor = Color.argb(255, r.getProgress(),
									g.getProgress(), b.getProgress());
							savePreference(mCurrentColor);
							updateView();
						}
					})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		AlertDialog alertDialog = dialogBuilder.create();
	    alertDialog.show();
	}
	
	class SeekerChangedListener implements OnSeekBarChangeListener {
		
		View colorPreview;
		SeekBar r;
		SeekBar g;
		SeekBar b;
		
		public SeekerChangedListener(View colorPreview, SeekBar r,
				SeekBar g, SeekBar b) {
			this.colorPreview = colorPreview;
			this.r = r;
			this.g = g;
			this.b = b;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			int argb = Color.argb(
					255,
					r.getProgress(),
					g.getProgress(),
					b.getProgress());
			
			colorPreview.setBackgroundColor(argb);
			mColorPreVeiw.setBackgroundColor(argb);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// don't care
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// do nothing
		}
		
	}
}
