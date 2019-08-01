package org.szvsszke.vitezlo2018.map.data;

import com.google.android.gms.maps.model.LatLng;

import org.szvsszke.vitezlo2018.map.model.Track;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class encapsulates the xml parsing functions.
 * 
 * **/
@Deprecated
public class XMLTools {
	
	private static final String TAG = XMLTools.class.getName();
	// no namespace
	private static final String nameSpace = null;
	
	// gpx constants
	private static final String TRKPT = "trkpt";
	private static final String LAT = "lat";
	private static final String LON = "lon";
	private static final String NAME = "name";
	private static final String DESCRIPTION = "desc";
	
	/**
	 * Creates and XmlPullParser object for parsing an object.
	 * 
	 * 
	 * */
	private static XmlPullParser getPullParser(InputStream in) {
		
		XmlPullParser parser = null;
		try {
			XmlPullParserFactory parserFactory = 
					XmlPullParserFactory.newInstance();
			parser = parserFactory.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);			
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return parser;
		
	}
	
	/**
	 * Method for parsing a prepared Gpx file into a Track object.
	 * @param is InputStream that points to the appropriate resource.
	 * 
	 * @return null if input stream is null, or the parsed track 
	 * */
	public static Track parseGPX (InputStream is) throws 
			XmlPullParserException, IOException {
		if( is == null) {
			return null;
		}
		
		XmlPullParser parser = getPullParser(is);
		
		ArrayList<LatLng> trkpts = new ArrayList<LatLng>();
		HashMap<Integer, String> trkptNames = new HashMap<Integer, String>();
		String name = "";
		String description = "";
		// loop through tags
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			
			switch(eventType) {
			case XmlPullParser.START_TAG:
				//determine current xml tag
				String tag = parser.getName();
				if (tag.equalsIgnoreCase(TRKPT)) {
					trkpts.add(new LatLng(
						Double.parseDouble(parser.getAttributeValue(nameSpace, LAT)),
						Double.parseDouble(parser.getAttributeValue(nameSpace, LON))));
					
				}
				else if (tag.equalsIgnoreCase(NAME)) {
					parser.next();
					if (parser.getEventType() == XmlPullParser.TEXT) {
						// set the track name first
						String n = parser.getText();
						if (name == "") {
							name = n;
						} else {
							trkptNames.put(trkpts.size() - 1, n);
						}
						
						
					} else {
						Log.d(TAG, "parseGpx: empty name");
					}					
				}
				
				else if (tag.equalsIgnoreCase(DESCRIPTION)) {
					parser.next();
					if (parser.getEventType() == XmlPullParser.TEXT) {
						description = parser.getText();
					}else {
						Log.d(TAG, "parseGpx: empty description");
					}
				}
				
				break;
			case XmlPullParser.END_TAG:
				// do nothing
				break;
			}
			
			eventType = parser.next();
		}
		// close input stream
		is.close();
		return new Track(name, description, trkpts, trkptNames, 
				new ArrayList<Integer>(), new ArrayList<Double>(), 
				new ArrayList<Long>());
	}
}
