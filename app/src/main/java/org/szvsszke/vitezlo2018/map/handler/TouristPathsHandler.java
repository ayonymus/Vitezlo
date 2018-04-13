package org.szvsszke.vitezlo2018.map.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.szvsszke.vitezlo2018.map.data.TouristPathCache;
import org.szvsszke.vitezlo2018.map.data.AbstractDataCache.DataLoadedListener;
import org.szvsszke.vitezlo2018.map.model.Track;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**This class is responsible for displaying tourist paths.
 * 
 * @author Gabor Tatrai
 * */
public class TouristPathsHandler extends AbstractMapItemHandler implements 
		DataLoadedListener<ArrayList<Track>>{
	
	private static final String TAG = TouristPathsHandler.class.getName();
	
	public static final String TROUSIT_MARK = "touristmark";
	public static final int ARGB_BLUE  = Color.argb(255, 0, 0, 255);
	public static final int ARGB_RED = Color.argb(255, 255, 0, 0);
	public static final int ARGB_GREEN = Color.argb(255, 0, 150, 0);
	public static final int ARGB_YELLOW = Color.argb(255, 255, 255, 0);
	
	
	private static final int TOURIST_PATH_START_ZINDEX = 10;
	private static final int TOURIST_PATH_LINE_WIDTH = 3;	
	private static final String PATH_TO_TROUSIT_MARKS = "touristmarks/";
	private static final String PNG = ".png";
	
	private TouristPathCache mPaths;
	private ArrayList<Marker> mTouristSigns;
	private ArrayList<Polyline> mTourstPaths;
	
	public TouristPathsHandler(Activity activity) {
		super(activity);
		mPaths = new TouristPathCache(mParent);
	}
	
	public void draw() {
		if (mMap == null) {
			return;
		}
		if (mPaths.acquireData() != null) {
			drawTouristPaths(mPaths.acquireData());
		}
		else {
			mPaths.setDataLoadedListener(this);
		}
	}

	@Override
	public void remove() {
		removeLines();
		removeMarkers();
	}

	@Override
	public void prepare() {
		mPaths.acquireData();		
	}
	

	//Draw all toursit paths loaded from xml
	private void drawTouristPaths(ArrayList<Track> paths) {
		//do not redraw if they are already present
		if (mTourstPaths == null) {
			mTourstPaths = new ArrayList<Polyline>();
			int zIndex = TOURIST_PATH_START_ZINDEX;
			for(Track path : paths) {
					
				int color;
				if (path.getTrackDescription().startsWith("P")) {
					color =  ARGB_RED;
				} else if (path.getTrackDescription().startsWith("Z")) {
					color =  ARGB_GREEN;
				} else if (path.getTrackDescription().startsWith("K")) {
					color =  ARGB_BLUE;
				}else {
					color =  ARGB_YELLOW;
				}
					
				drawLine(path.getTrackPoints(), color, zIndex, 
						TOURIST_PATH_LINE_WIDTH);
					
				drawTouristPathSign(path);
				zIndex++;
			}				
		}
	}
	
	/**
	 * Draw a line on the map defined my a list of latlng and color
	 * @param lineCoordinates a list of LatLng's representing the line
	 * @param argb The int representation of ARGB color  
	 * */
	private void drawLine(List<LatLng> lineCoordinates, int argb,
			float zIndex, int width) {
		Log.v(TAG, "drawLine");
		if (lineCoordinates != null) {
			//draw track with poylines
			PolylineOptions lineOptions = new PolylineOptions();
			lineOptions.geodesic(true)			
			.addAll(lineCoordinates)
			.color(argb)
			.zIndex(zIndex)
			.width(width);
			mTourstPaths.add(mMap.addPolyline(lineOptions));
		}
		else {
			Log.e(TAG, "drawPath: linecoordinates is null!");
		}
	}

	private void removeLines() {
		if (mTourstPaths != null) {
			for (Polyline line : mTourstPaths){
				line.remove();
			}
			mTourstPaths.clear();
			mTourstPaths = null;
		}
	}
	
	private void removeMarkers() {
		if (mTouristSigns != null) {
			for (Marker mark : mTouristSigns) {
				mark.remove();
			}
			mTouristSigns = null;
		}
	}
	
	private void drawTouristPathSign (Track track) {
		Log.d(TAG, "drawTouristPathMarker: " + track.getTrackDescription());
		if (mTouristSigns == null) {
			mTouristSigns = new ArrayList<Marker>();
		}
		
		InputStream is = null;
		try {
			is = mParent.getResources().getAssets()
			.open(PATH_TO_TROUSIT_MARKS + track.getTrackDescription() + PNG);
			
			Bitmap image = BitmapFactory.decodeStream(is);
			Bitmap scaled = Bitmap.createScaledBitmap(image, 20, 20, true);
			
			LatLng middle = track.getTrackPoints()
					.get(track.getTrackPoints().size() / 2);
			
			MarkerOptions mark = new MarkerOptions();
			mark.icon(BitmapDescriptorFactory.fromBitmap(scaled))
				.title(TROUSIT_MARK)
				.position(middle);
				mTouristSigns.add(mMap.addMarker(mark));
		} catch (IOException e){
			Log.e(TAG, "getAssetStream: path to file invalid", e);			
		}					
	}

	@Override
	public void onDataLoaded(ArrayList<Track> loaded) {
		drawTouristPaths(loaded);
		
	}
}
