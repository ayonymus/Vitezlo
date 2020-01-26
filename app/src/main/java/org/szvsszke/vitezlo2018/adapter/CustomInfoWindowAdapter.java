package org.szvsszke.vitezlo2018.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.szvsszke.vitezlo2018.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import timber.log.Timber;

public class CustomInfoWindowAdapter implements InfoWindowAdapter {
	
	private Activity parent;
	private HashMap<String, Drawable> sights;
	
	public CustomInfoWindowAdapter(Activity parent) {
		this.parent = parent;
		sights = new HashMap<String, Drawable>();
	}
	
	@Override
	public View getInfoContents(Marker marker) {
		return render(marker);
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return render(marker);
	}
	
	private View render(Marker marker) {
		View view = parent.getLayoutInflater()
				.inflate(R.layout.marker_info_window, null);
		ImageView image = (ImageView)view.findViewById(R.id.imageViewSight);
		
		TextView title = (TextView) view.findViewById(R.id.TVtitle);
		title.setText(marker.getTitle());
		TextView snippet = (TextView) view.findViewById(R.id.TVsnippet);
		
		String fileName = null;
		if (marker.getSnippet() != null) {
			if (marker.getSnippet().endsWith(".jpg")) {			
				fileName = marker.getSnippet();
			} else {
				snippet.setText(marker.getSnippet());
			}
		} else {
			snippet.setVisibility(View.GONE);
		}
		
		// load from cache
		if (sights.containsKey(marker.getTitle())) {
			image.setImageDrawable(sights.get(marker.getTitle()));
		}		
		
		// main thread???
		else if (fileName != null){
			Drawable d = null;
			// load image
	        try {
	            // get input stream
	            InputStream in = parent.getAssets().open("pic/" + fileName);	            
	            // load image as Drawable
	            d = Drawable.createFromStream(in, null);
	            in.close();
	            
	        }
	        catch(IOException ex) {
	        	Timber.e("error lodaing image: " + marker.getTitle());
	            //return null;
	        }
	        if (d != null) {
	        	// cache drawable
	        	sights.put(marker.getTitle(), d);
	        	image.setImageDrawable(d);
	        }
		} else {
        	image.setVisibility(View.GONE);
        }				
		
		return view;		
	}

}
