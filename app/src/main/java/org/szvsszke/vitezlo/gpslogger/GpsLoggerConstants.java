package org.szvsszke.vitezlo.gpslogger;

public class GpsLoggerConstants {
	
	// logger preferences
	public static final String PREF_MIN_DISTANCE_BETWEEN_FIX = "minDist";
	public static final String PREF_MIN_TIME_BETWEEN_FIX = "minTime";
	public static final String PREF_GPS_TIMEOUT = "gpsTimeout";
	public static final String PREF_TIMEOUT = "timeout";
	public static final String PREF_BATTERY_LIMIT = "batteryLimit";
	
	// logger default values
	/**The default minimum distance between fixes to  record.*/
	public static final int DEF_MIN_DISTANCE = 5;
	/**Five seconds in milliseconds.*/
	public static final int FIVE_SECONDS = 5000;
	/**The defauld battery limit where the service stops itself.*/
	public static final int DEF_BATTERY_LIMIT = 30;
	/**The default hour when to stop the service. (that is 22:00)*/
	public static final int DEF_GPS_TIMEOUT = 22;
	
}
