package org.szvsszke.vitezlo2018.gpslogger;

import org.szvsszke.vitezlo2018.MainActivity;
import org.szvsszke.vitezlo2018.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class GpsLoggerService extends Service {
	
	// for logging
	private final String TAG = getClass().getName();
	
	// GpsLoggerConstants for messages
	public static final String REQUEST = "request";
	public static final int REQ_START_LOGGING_GPS = 1;
	public static final int REQ_STOP_LOGGING_GPS = 2;
	public static final int REQ_LOGGER_STATE = 3;	
		
	public static final int STOPPED = 0;
	public static final int RUNNING = 1;
	public static final int NO_GPS = 2;
	public static final int LOW_BATTERY = 3;
	public static final String LOGGER_STATE = "loggerState";
	public static final String LAST_LAT = "lastLat";
	public static final String LAST_LON = "lastLon";
	public static final String LOG_NAME = "logName";
	public static final String BASE_TIME = "base";
	
	public static final String REPLY = "reply";
	
	public static final int MIN_DISTANCE_BETWEEN_FIX = 5; 
	private static int NOTIFICATION_ID = 900000; //? 8675309
	
	// members
	// notification
	private Builder mNotificationBuilder;
	private NotificationManager mNotificationManager;
	
	//GPS
	private LocationManager mGpsLocManager;
    private GpsLocationListener mLocListener;    
	
    // Battery monitoring
    private BroadcastReceiver mBatteryRec;
	// IPC
    private Messenger mRequestMessenger;
    
    // state variables
	private int mUpdateFrequency;
	private int mUpdateDistance;
	private int mBatteryLimit;
	private long mTimeOut;
	private int mCurrentState = STOPPED;
	private long mChronoBase = 0;
	
	private String mLogName;
	
	
    
    @Override
    public void onCreate() {
    	Log.d(TAG, "onCreate");
    	mRequestMessenger = new Messenger(new RequestHandler());
		
		// setup the battery receiver
		mBatteryRec = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
				if (level <= mBatteryLimit) {					
					stopSession();
					// TODO NOTIFY USER
				}
			}
		};
		registerReceiver(mBatteryRec, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.d(TAG, "onStartCommand");
    	startForeground(NOTIFICATION_ID, new Notification());
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
    	Log.d(TAG, "onBind");
        return mRequestMessenger.getBinder();
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
    	Log.d(TAG, "onUnBind");
        // All clients have unbound with unbindService()
    	super.onUnbind(intent);
        return false;
    }
    
    @Override
    public void onDestroy() {
    	Log.d(TAG, "onDestroy");
    	//unregister things
    	unregisterReceiver(mBatteryRec);
    	super.onDestroy();
    }
    
    /**
     * Factory method to make the desired Intent..
     * */
    public static Intent makeIntent(Context context) {
    	return new Intent(context, GpsLoggerService.class);
    }
	
	/**
	 * Factory method for making a request.
	 * @param requestID the request code stored in the LoggerService
	 * */
	public static Bundle makeServiceRequest(int requestID){
		Bundle request = new Bundle();
		request.putInt(GpsLoggerService.REQUEST, requestID);	
		return request;
	}
	
	
	/**
	 * Binder for this service
	 * */
	public class LoggerServiceBinder extends Binder {
		
		public GpsLoggerService getLoggerService() {
			Log.d(TAG, "LoggerServiceBinder getLoggerService");
			return GpsLoggerService.this;
		}
	}
	
	/**
	 * Starts a logging session
	 * @return state
	 * */
	public int startSession() {
		Log.d(TAG, "startLogging");

		if (mGpsLocManager == null) {
			mGpsLocManager = (LocationManager) getSystemService(
					Context.LOCATION_SERVICE);
		}
		// chech if gps is enabled
		if (!mGpsLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return NO_GPS;
		}
		
		// register the battery listener
		
		mLocListener = new GpsLocationListener(this, mLogName);
		mGpsLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				mUpdateFrequency, mUpdateDistance, mLocListener);
		mCurrentState = RUNNING;
		showNotification();
		return RUNNING;
	}
	
	public void stopSession() {
		Log.d(TAG, "stopSession");
		if (mCurrentState == RUNNING) {
			stopLogging();
			stopSelf();
			mNotificationManager.cancel(NOTIFICATION_ID);
		}
	}
	
	public void stopLogging() {
		Log.d(TAG, "stopLogging");		
		mGpsLocManager.removeUpdates(mLocListener);				
		mNotificationManager.cancel(NOTIFICATION_ID);
		mCurrentState = STOPPED;
	}
	
	/**
     * Shows a notification icon in the status bar for GPS Logger.
     */
    private void showNotification() {
        // What happens when the notification item is clicked
        Intent contentIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(contentIntent);

        PendingIntent pending = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        long notificationTime = System.currentTimeMillis();

        if (mNotificationBuilder == null) {
            mNotificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(getString(R.string.logger_running_short))
                    .setOngoing(true)
                    .setContentIntent(pending);
        }

        mNotificationBuilder.setContentText(
        		getString(R.string.logger_running_in_background));
        mNotificationBuilder.setWhen(notificationTime);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }
    
	
	/**
	 * This class handles messages sent by the Logger fragment.
	 * */	
	class RequestHandler extends Handler {
		
        // Hook method called back when a request Message arrives from
        // the LoggerFragmenet.
	
		@Override
		public void handleMessage(Message request) {
			Log.d(TAG, "Request handler takes care of the message");
			Messenger replMessenger = request.replyTo;
			Bundle command = request.getData();
			Message reply = Message.obtain();
			int start = -1;
			
			switch (command.getInt(REQUEST)) {
			case REQ_START_LOGGING_GPS:
				mLogName = command.getString(LOG_NAME);
				mChronoBase = command.getLong(BASE_TIME);
		    	mUpdateFrequency = command.getInt(
		    			GpsLoggerConstants.PREF_MIN_TIME_BETWEEN_FIX); 
		    	mUpdateDistance = command.getInt(
		    			GpsLoggerConstants.PREF_MIN_DISTANCE_BETWEEN_FIX);
		    	mBatteryLimit = command.getInt(
		    			GpsLoggerConstants.PREF_BATTERY_LIMIT);
		    	mTimeOut = command.getLong(
		    			GpsLoggerConstants.PREF_GPS_TIMEOUT);				
				start = startSession();
				break;
			case REQ_STOP_LOGGING_GPS:
				stopSession();
				break;
			default:
				// do nothing just send back the loggerStatus
				break;
			}			
			
			Bundle data = new Bundle();
			data.putInt(LOGGER_STATE, start == -1 ? mCurrentState : start);
			data.putString(LOG_NAME, mLogName);
			data.putLong(BASE_TIME, mChronoBase);
			reply.setData(data);
			
			try {
				replMessenger.send(reply);
			} catch (RemoteException e) {
				Log.e(TAG, "reply message could not be delivered!");
				e.printStackTrace();
			}
		}
	}
}

