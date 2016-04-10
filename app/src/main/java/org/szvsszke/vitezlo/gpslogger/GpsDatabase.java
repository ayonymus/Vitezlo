package org.szvsszke.vitezlo.gpslogger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.szvsszke.vitezlo.map.model.Track;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class for handling database queries. Singleton.
 * 
 * @author Gabor Tatrai
 * */
public class GpsDatabase extends SQLiteOpenHelper {

	private static final String TAG = GpsDatabase.class.getName();
	
	private static final String DB_NAME = "gps_locations.gdb";
	private static final String DB_TABLE = "coordinates";
	private static final int DB_VERSION = 2;

	private static final String KEY_ID = "_id";
	private static final String KEY_HIKE_NAME = "hikeName";
	private static final String KEY_LAT = "lat";
	private static final String KEY_LON = "lon";
	private static final String KEY_ALT = "alt";
	private static final String KEY_TIME = "time";
	private static final String KEY_ACCURACY = "accuracy";
	
	// callback
	private HashSet<OnDatasetChangedListener> mCallbacks;
	
	protected SQLiteDatabase mDB;
	
	private static GpsDatabase instance;
	
	private AtomicInteger mOpenCounter;
	
	/**
	 * Constructor for the database helper class
	 * */	
	private GpsDatabase (Context context) {		
		super(context, DB_NAME, null, DB_VERSION);
		mCallbacks = new HashSet<GpsDatabase.OnDatasetChangedListener>();
		mOpenCounter = new AtomicInteger();
	}
	
	/**
	 * @return the instance of the database.
	 * */
	public static synchronized GpsDatabase getInstance(Context context) {
		if (instance == null) {
			// app context
			instance = new GpsDatabase(context.getApplicationContext());
		}
		
		return instance;
	}
	
	/**
	 * Opens a database and returns it.
	 * @return a writable sqlite database
	 * */
	protected synchronized SQLiteDatabase openDatabase() {
		// when open has not been called before
		if (mOpenCounter.incrementAndGet() == 1) {
			mDB = getWritableDatabase();
		}		
		return mDB;
	}
	
	/**
	 * Close database when it is not accessed any more
	 * */
	protected synchronized void closeDatabase() {
		if (mOpenCounter.decrementAndGet() == 0) {
			// no thread is accessing database, it can be closed
			mDB.close();
		}
	}

	
	/**Inserts a single location object into the database. 
	 * @param loc location object to inert.*/
	public void insertSingleLocation(Location loc, String hikeName) {
		Log.d(TAG, "insertSingleLocation: to "+ hikeName);
		
		ContentValues vals = new ContentValues();
		vals.put(KEY_HIKE_NAME, hikeName);
		vals.put(KEY_LAT, loc.getLatitude());
		vals.put(KEY_LON, loc.getLongitude());
		vals.put(KEY_TIME, Calendar.getInstance().getTime().getTime());
		vals.put(KEY_ALT, loc.getAltitude());
		vals.put(KEY_ACCURACY, loc.getAccuracy());
		
		openDatabase().insert(DB_TABLE, null, vals);
		closeDatabase();
		callback(OnDatasetChangedListener.INSERT_SINGLE_LOCATION);
	}

	/**
	 * @param hikeName the name of the hike to query.
	 * @return The hike ready for use on the map.
	 * */
	public ArrayList<LatLng> getPathLatLng(String hikeName) {
		Log.d(TAG, "getHike: " + hikeName);
		ArrayList<LatLng> hike = new ArrayList<LatLng>();

		SQLiteDatabase db = openDatabase();
		Cursor cur = db.query(DB_TABLE, new String[]{KEY_LAT, KEY_LON, KEY_HIKE_NAME}, 
				KEY_HIKE_NAME + " =? ",new String[]{hikeName}, null, null, null);
		if (cur.moveToFirst()) {
			do {
				double lat = cur.getDouble(0);
				double lon = cur.getDouble(1);
				LatLng latLng = new LatLng(lat, lon);
				hike.add(latLng);
			}
			while (cur.moveToNext());
		}
		cur.close();
		closeDatabase();
			
		return hike;
	}
	
	/**
	 * Finds a track in the database based on the hike name.
	 * 
	 * @param hikeName name of track to find
	 * 
	 * @return corresponding Track object
	 * */
	public Track getPathTrack(String hikeName){
		Log.d(TAG, "getHike: " + hikeName);
		if (hikeName == null) {
			return null;
		}
		ArrayList<LatLng> hike = new ArrayList<LatLng>();		
		ArrayList<Integer> elevation = new ArrayList<Integer>();
		ArrayList<Double> accuracy = new ArrayList<Double>();
		ArrayList<Long> time = new ArrayList<Long>();

		SQLiteDatabase db = openDatabase();
		Cursor cur = db.query(DB_TABLE, new String[]{
				KEY_HIKE_NAME, KEY_LAT, KEY_LON, KEY_ALT, KEY_TIME, 
				KEY_ACCURACY}, 
				KEY_HIKE_NAME + " =? ",new String[]{hikeName}, null, null, null);
		if (cur.getCount() < 1) {
			return null;
		}
		if (cur.moveToFirst()) {
			do {
				double lat = cur.getDouble(1);
				double lon = cur.getDouble(2);
				int	ele = cur.getInt(3);
				long tim = cur.getLong(4);
				double acc = cur.getDouble(5);
				LatLng latLng = new LatLng(lat, lon);
				hike.add(latLng);
				elevation.add(ele);
				accuracy.add(acc);
				time.add(tim);
			}
			while (cur.moveToNext());
		}
		cur.close();
		closeDatabase();		
		return new Track(hikeName, "", hike, null, elevation, accuracy, time);
	}
		
	/**
	 * Queries the hike names in descending order of time
	 * they have been put into the database.
	 * 
	 * @return the hike names stored in the database;
	 * */
	public ArrayList<String> getPathNames() {
		Log.d(TAG, "getHikeNames");
		SQLiteDatabase db = openDatabase();
		Cursor cur = db.query(true, DB_TABLE, new String[]{KEY_HIKE_NAME, KEY_TIME},
				null, null, KEY_HIKE_NAME, null, KEY_TIME + " DESC", null);
		ArrayList<String>  hikeNames = new ArrayList<String> ();
		if (cur.moveToFirst()) {
			do {
				hikeNames.add(cur.getString(0));
			}
			while (cur.moveToNext());
		}
		cur.close();
		closeDatabase();
		return hikeNames;
	}
	
	/**
	 * @return the last hike made, or null if database is empty.
	 * */
	public Track getLastPath() {
		String lastHikeName = getLastPathName();
		if(lastHikeName == null) {
			return null;
		}
		return getPathTrack(getLastPathName());
	}
	
	/**
	 * @return the name of the last hike.
	 * */
	public String getLastPathName(){			
		SQLiteDatabase db = openDatabase();
		Cursor cur = db.query(DB_TABLE, new String[]{KEY_HIKE_NAME, 
				"MAX(" + KEY_TIME + ")"}, 
				null, null, null, null, null);
		String latestKey = null;
		if (cur.moveToFirst()) {
			latestKey = cur.getString(0);
		}
		cur.close();
		closeDatabase();
		return latestKey;
	}
	
	/**  
	 * @return the time of last entry
	 */
	public long getLastTimeStamp(){
		SQLiteDatabase db = openDatabase();
		Cursor cur = db.query(DB_TABLE, new String[]{"MAX(" + KEY_TIME + ")"}, 
				null, null, null, null, null);
		long lastDate = 0;
		if (cur.moveToFirst()) {
			lastDate = cur.getLong(0);
		}
		cur.close();
		closeDatabase();
		return lastDate;
	}
	
	/**
	 * @return the number of rows in the table.
	 * */
	public int getSize() {
		SQLiteDatabase db = openDatabase();
	    Cursor cur = db.query(DB_TABLE, new String[] {KEY_ID}, 
	    		null, null, null, null, null);
	    int size = cur.getCount(); 
	    cur.close();
		closeDatabase();
	    return size;
	}
	
	/**
	 * Checks weather there is any entries.
	 * 
	 * @return true if database contains no entries.
	 * */
	public boolean isEmpty() {
		return (getSize() == 0);
	}
	
	/**
	 * Deletes all data from database and recreates the structure.
	 * */	
	public void resetDatabase(){
		SQLiteDatabase db = openDatabase();
		db.execSQL("DROP TABLE " + DB_TABLE);
		createDatabase(db);
		callback(OnDatasetChangedListener.RESET_DATABASE);
	}

	/**
	 * Creates the database table and structure in the provided database.
	 * 
	 * @param db the database in which the table should be created.
	 * */
	public void createDatabase(SQLiteDatabase db){
		db.execSQL("CREATE TABLE " + DB_TABLE + " (" +
				KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				KEY_HIKE_NAME + " TEXT,"+
			    KEY_LAT + " REAL," +
	               KEY_LON + " REAL," +
	               KEY_TIME + " INT," +
	               KEY_ALT + " REAL," +
	               KEY_ACCURACY + " REAL)");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createDatabase(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
	       onCreate(db);
	}
	
	/**
	 * Add a listener to the database. It gets called every time an element
	 * is added or removed.
	 * 
	 * @param listener an OnDatasetChangedListener
	 * */
	public void setOnDatasetChangedListener(OnDatasetChangedListener listener){
		
		mCallbacks.add(listener);
	}

	/**
	 * Calls the onDatasetChanged on all registered OnDatasetChangedListeners.
	 * @param code stored in the interface
	 * */
	private void callback(int code){
		for(OnDatasetChangedListener listener : mCallbacks) {
			listener.onDatasetChanged(code);
		}
	}

	public interface OnDatasetChangedListener {
		public static final int INSERT_SINGLE_LOCATION = 0;
		public static final int RESET_DATABASE = 1;
		public void onDatasetChanged(int changeCode);
	};
}
