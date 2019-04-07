package org.szvsszke.vitezlo2018.map.data;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.szvsszke.vitezlo2018.map.model.Track;
import org.szvsszke.vitezlo2018.map.model.TrackDescription;
import org.szvsszke.vitezlo2018.map.model.Waypoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * This class encapsulates the xml parsing functions.
 * 
 * **/
public class XMLTools {
	
	private static final String TAG = XMLTools.class.getName();
	// no namespace
	private static final String nameSpace = null;
	
	// gpx constants
	public static final String TRKPT = "trkpt";
	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "desc";
	
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
		int trkptCount = 0;
		// loop through tags
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			
			switch(eventType) {
			case XmlPullParser.START_TAG:
				//determine current xml tag
				String tag = parser.getName();
				//Log.d(TAG, "parseGpx, " + tag);
				if (tag.equalsIgnoreCase(TRKPT)) {
					//Log.d(TAG, "parseGpx, tag: " + TRKPT);
					trkptCount++;
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
		Log.d(TAG, "parseGpx: trkpts parsed: " + trkptCount);
		Log.d(TAG, "parseGpx: trkpts in list: " + trkpts.size());
		return new Track(name, description, trkpts, trkptNames, 
				new ArrayList<Integer>(), new ArrayList<Double>(), 
				new ArrayList<Long>());
	}
	
	/**
	 *	Parses the wayoints (wpt tag) from a gpx file.	
	 * 
	 * @param inputStream input to a file
	 * @return The waypoints stored in the gpx in an arraylist.
	 * */
	
	public static ArrayList<Waypoint> parseWaypoints (InputStream inputStream) {
		// parse checkpoints xml

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		
		 ArrayList<Waypoint> waypointList = new  ArrayList<Waypoint>();
		
		// access file
		try { 
			DocumentBuilder documentBuilder = docBuilderFactory.newDocumentBuilder();
			//InputStream inputStream = context.getResources().getAssets().open(params[0]);
			//Log.d(TAG, "args[0] " + params[0]);
			Document doc = documentBuilder.parse(inputStream);
			Element rootElement = doc.getDocumentElement();
			
			NodeList nodeList = rootElement.getElementsByTagName("wpt");
			
			for(int i = 0; i < nodeList.getLength(); i++) {
				
				Node node = nodeList.item(i);
			    NamedNodeMap attributes = node.getAttributes();

			    String newLatitude = attributes.getNamedItem("lat").getTextContent();
			    Double newLatitude_double = Double.parseDouble(newLatitude);
			     
			    String newLongitude = attributes.getNamedItem("lon").getTextContent();
			    Double newLongitude_double = Double.parseDouble(newLongitude);
			    			    
			    // aquire name
			    Element fstElmnt = (Element) node;
			    NodeList nameList = fstElmnt.getElementsByTagName("name");
			    Element nameElement = (Element) nameList.item(0);
			    nameList = nameElement.getChildNodes();
			    String name = ((Node) nameList.item(0)).getNodeValue();
			    
			    // aquire comment
			    String comment = "";
			    NodeList cmtList = fstElmnt.getElementsByTagName("cmt");
			    if (cmtList.getLength() !=0) {
			    	Element cmtElement = (Element) cmtList.item(0);
				    cmtList = cmtElement.getChildNodes();
				    comment = ((Node) cmtList.item(0)).getNodeValue();
			    } else {
			    	comment = "n/a";
			    }
			    
			    // description
			    NodeList idList = fstElmnt.getElementsByTagName("desc");
			    String idString= "";
			    if (idList.getLength() != 0) {
			    	Element idElement = (Element) idList.item(0);
				    idList = idElement.getChildNodes();
				    idString= ((Node) idList.item(0)).getNodeValue();
			    }
			    
			    // link
			    NodeList linkList = fstElmnt.getElementsByTagName("link");
			    String linkString= "";
			    if (linkList.getLength() != 0) {
			    	Element linkElement = (Element) linkList.item(0);
			    	linkList = linkElement.getChildNodes();
			    	linkString= ((Node) linkList.item(0)).getNodeValue();
			    }
			    
			    Waypoint wp = new Waypoint(name, newLatitude_double, 
			    		newLongitude_double, comment, idString, linkString);
		    	
			    waypointList.add(wp);
			}
			
			inputStream.close();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (SAXException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		
		return waypointList;		
	}
	
	
	/**
	 * Run in a separate thread!
	 * */
	public static boolean writeToGPX(Track track, FileOutputStream fos, 
			String appNameVersion, String description) {
		 
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>"
		 		+ "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" "
		 		+ "creator=\"" + appNameVersion + "\" version=\"1.1\" "
		 		+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
		 		+ "xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 "
		 		+ "http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
		String name = "<name>" + track.getTrackName() + "</name><trkseg>\n";
		String desc = "<desc>"+ description + "</desc>";
		StringBuilder builder = new StringBuilder();
		builder.append(header);
		builder.append(name);
		builder.append(desc);
		
		for (int i = 0; i < track.getTrackPoints().size(); i++) {
			LatLng position = track.getTrackPoints().get(i);
			builder.append("\t<trkpt lat=\"" + position.latitude + "\" lon=\"" 
				 + position.longitude + "\">");
			if (track.getTime() != null && track.getTime().size() > 0) {
				builder.append("<time>"+ df.format(new Date(track.getTime().get(i))) + "</time>"); 
			}
			builder.append("</trkpt>\n");
		}
		builder.append("</trkseg></trk></gpx>");	//footer
		 
		try {

			fos.write(builder.toString().getBytes());
			fos.close();
			return true;
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		return false;
	}
	
	public static ArrayList<TrackDescription> parseHikeDescriptions(InputStream stream) {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		ArrayList<TrackDescription> tracks = new ArrayList<TrackDescription>();
		
		// access file
		try { 
			DocumentBuilder documentBuilder = docBuilderFactory.newDocumentBuilder();
			//Log.d(TAG, "args[0] " + params[0]);
			Document doc = documentBuilder.parse(stream);
			Element rootElement = doc.getDocumentElement();
			
			NodeList nodeList = rootElement.getElementsByTagName("track");
			
			for(int i = 0; i < nodeList.getLength(); i++) {
				
				Node node = nodeList.item(i);
			    NamedNodeMap attributes = node.getAttributes();

			    String name = attributes.getNamedItem("name").getTextContent();			   				     
			    String length = attributes.getNamedItem("length").getTextContent();			    
			    String id = attributes.getNamedItem("id").getTextContent();
			    int type = Integer.parseInt(attributes.getNamedItem("type").getTextContent());
			    String routeFile = attributes.getNamedItem("route").getTextContent();
			    String checkpoints = attributes.getNamedItem("checkpoints").getTextContent();
			    String starting = attributes.getNamedItem("starting").getTextContent();
			    String entryFee = attributes.getNamedItem("entryFee").getTextContent();				    
			    String other = attributes.getNamedItem("other").getTextContent();
			    String badge = attributes.getNamedItem("badge").getTextContent();
			    String date = attributes.getNamedItem("date").getTextContent();
			    String levelTime = attributes.getNamedItem("leveltime").getTextContent();

			    String[] cps = checkpoints.split(",");
			    TrackDescription track = new TrackDescription(name, id, type, routeFile, 
			    		starting, entryFee, other, cps, badge, date, length, levelTime);
			    tracks.add(track);
			}
			
			stream.close();
			
		} catch (ParserConfigurationException e) {
				  e.printStackTrace();
		} catch (FileNotFoundException e) {
			  	 e.printStackTrace();
		} catch (SAXException e) {
				  e.printStackTrace();
		} catch (IOException e) {
				  e.printStackTrace();
		}
		
		return tracks;
	}
}
