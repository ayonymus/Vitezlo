package org.szvsszke.vitezlo;

import org.szvsszke.vitezlo.adapter.NavDrawerAdapter;
import org.szvsszke.vitezlo.gpslogger.GpsLoggerConstants;
import org.szvsszke.vitezlo.gpslogger.GpsLoggerService;
import org.szvsszke.vitezlo.gpslogger.GpsLoggerServiceReplyHandler;
import org.szvsszke.vitezlo.gpslogger.GpsLoggerServiceReplyHandler.HandleReply;
import org.szvsszke.vitezlo.gpslogger.IGpsLoggerServiceMessages;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends ActionBarActivity implements 
		IGpsLoggerServiceMessages {

	/* constants*/
	private static final String TAG = MainActivity.class.getName();
	
	private static final String FIRST_RUN = "first_run";

	private ActionBar mActionBar;
	
	// drawer fields
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mFragmentTitles;
    
    // fragment
    private Fragment mCurrentFragment = null;
    private MapFragment mMapFragment = null;
    
    private boolean doubleBackToExitPressedOnce;
    
	// service 
    private Messenger mReqestMessengerReference = null;
    
    // service connection
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "onServiceDisconnected");
			 /* Called if the Service crashes and is no longer
	            * available.  The ServiceConnection will remain bound,
	            * but the Service will not respond to any requests.
	            */
			mReqestMessengerReference = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			Log.d(TAG, "onServiceConnected");
			mReqestMessengerReference = new Messenger(binder);
		}
	};
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        
        
        setContentView(R.layout.activity_vitezlo_main);
        // service
    	if (mReqestMessengerReference == null) {
			// get an intent that starts the service
			Intent loggerI = GpsLoggerService.makeIntent(this);
			this.bindService(loggerI,
					mServiceConnection, 
					Context.BIND_AUTO_CREATE);
		}
        
        // action bar stuff
        mActionBar = getSupportActionBar();        
        
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        
        // setup drawer
        mFragmentTitles = getResources().getStringArray(R.array.fragment_titles);
        int[] icons = new int[]{R.drawable.map_switch, R.drawable.map_switch, 
        		R.drawable.my_hike, R.drawable.my_hike, R.drawable.play,
        		R.drawable.ic_action_about};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView ) findViewById(R.id.navigation_drawer);
        /*
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
        		R.layout.item_drawer_list, mFragmentTitles));
        */
        mDrawerList.setAdapter(new NavDrawerAdapter(mFragmentTitles, icons, this));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());        
        
        // setup drawerToggle
        mDrawerToggle = new ActionBarDrawerToggle (
        		this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */	        		
        ) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
            	supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {
        	// start with map fragment
        	setCurrentFragment(0);
            
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);         
        // extend navigation drawer on first run
        if(prefs.getBoolean(FIRST_RUN, true)) {
        	mDrawerLayout.openDrawer(Gravity.LEFT);
        	prefs.edit().putBoolean(FIRST_RUN, false).commit();
        }
    }	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
		// inflate menu
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.hike_menu_items, menu);		
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onPause() {	
		super.onPause();	
	}
	
	// listener for action bar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
		// toggle drawer
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    // Sync the toggle state after onRestoreInstanceState has occurred.
	    mDrawerToggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public void setTitle(CharSequence title) {
    	mActionBar.setTitle(title); 
    }
    
    //press back twice to exit
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.back_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;                       
            }
        }, 2000); // wait 2 seconds
    }
    
    @Override
    protected void onDestroy() {
    	unbindService(mServiceConnection);
    	super.onDestroy();
    };
    
    /**
     * Checks weather google maps is installed
     * @return true if installed
     * */
    private boolean isGoogleMapsInstalled(){
        try{
            getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            return true;
        } catch(PackageManager.NameNotFoundException e){
            return false;
        }
    }

	// select content
	public void setCurrentFragment(int position) {
		Log.d(TAG, "selectFragment: " + position);
		
		switch (position) {
		// start logging fragment
		case 1:
			mCurrentFragment = new MapPreferncesFragment();
			break;
		case 2:			
			mCurrentFragment = new GpsLoggerFragment();
			break;
		case 3:
			mCurrentFragment = new GpsLoggerPrefernceFragment();
			break;
		case 4:
			mCurrentFragment = new ExportFragment();
			break;
		case 5: 
			mCurrentFragment = new GeneralInfoFragment();
			break;
		default:
			// launch map
			// check if it had already been instantiated
			if (mMapFragment == null) {
			
				// check if google services are available			
				boolean servicesAvailable = (GooglePlayServicesUtil
						.isGooglePlayServicesAvailable(this) == 
						ConnectionResult.SUCCESS);
				if(servicesAvailable && isGoogleMapsInstalled()) {					
					mMapFragment = new MapFragment();
					// keep reference so the parsed xml data doesn't have to
					// be loaded again
					mCurrentFragment = mMapFragment;
				}else {
					mCurrentFragment = new NoGoogleServicesFragment();
				}
			}
			else {
				mCurrentFragment = mMapFragment;
			}
		}
		
		// begin transaction
		FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();	
		fragTransaction.replace(R.id.fragment_container, mCurrentFragment);
		fragTransaction.commit();
		
		// udpate drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
		
		// set title
		mActionBar.setTitle(mFragmentTitles[position]);
	}
	
	// listener for the drawer
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
        		long id) {        	
            setCurrentFragment(position);
        }
    }
	
    /**
     * @return the current fragment in the activity for testing purposes.
     * */
	public Fragment getFragment() {
		return mCurrentFragment;
	}
	
	/// services messages
	@Override
	public Messenger getGpsLoggerServiceMessenger() {
		return mReqestMessengerReference;
	}
	
	/**
	 * Messages the logger service with a request.
	 * 
	 * @param request something from the service
	 * @param replyTo where to reply
	 * 
	 * */
	public void sendRequest (Bundle request, 
			Handler replyTo) {
		Log.d(TAG, "messageService");		
		Message requestMsg = Message.obtain();
		requestMsg.replyTo = new Messenger (replyTo);
		requestMsg.setData(request);
		sendMessage(requestMsg);
	}
	
	private void sendMessage(final Message message) {
		// try to send a couple of times while not blocking the ui thread		
		
		new Thread (new Runnable() {
			
			@Override
			public void run() {
				try {
					boolean sent = false;
					int tries = 0;
					while (tries < 10){
						if (mReqestMessengerReference != null) {
							mReqestMessengerReference.send(message);
							sent = true;
							break;
						} 
						tries++;
						Thread.sleep(100); //ms
					}
					if (sent) {
						Log.d(TAG, "message sent in " + tries + " tires" );
					} else {
						Log.e(TAG, "message not sent!");
					}
										
				} catch (InterruptedException e){
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}				
			}
		}).start();
	}
	
	/**
	 * Makes a request to the logger service.
	 * */
	public void requestStatus(Handler replyTo) {
		sendRequest(GpsLoggerService.makeServiceRequest(
				GpsLoggerService.REQ_LOGGER_STATE), replyTo);
	}
	
	/**
	 * Retrieves logger preferences from shared preferes creates a 
	 * start request message.
	 * 
	 * @param logName the name (date, time) of the hike
	 * @param base of the chronometer
	 * @param replyTo message handler
	 *  
	 * */
	public void requestStartService (String logName, long base, 
			Handler replyTo) {
		Log.d(TAG, "startLogger");
		// make request
		SharedPreferences prefs = 
				PreferenceManager.getDefaultSharedPreferences(this);
		Bundle request = GpsLoggerService.makeServiceRequest(
				GpsLoggerService.REQ_START_LOGGING_GPS);
		request.putString(GpsLoggerService.LOG_NAME, logName);
		request.putLong(GpsLoggerService.BASE_TIME, base);
		request.putInt(GpsLoggerConstants.PREF_MIN_TIME_BETWEEN_FIX,  
				prefs.getInt(GpsLoggerConstants.PREF_MIN_TIME_BETWEEN_FIX,
						GpsLoggerConstants.FIVE_SECONDS));
		request.putInt(GpsLoggerConstants.PREF_MIN_DISTANCE_BETWEEN_FIX, 
				prefs.getInt(GpsLoggerConstants.PREF_MIN_DISTANCE_BETWEEN_FIX, 
						GpsLoggerConstants.DEF_MIN_DISTANCE));
		request.putInt(GpsLoggerConstants.PREF_BATTERY_LIMIT, 
				prefs.getInt(GpsLoggerConstants.PREF_BATTERY_LIMIT,
						GpsLoggerConstants.DEF_BATTERY_LIMIT));
		//TODO calculate when to stop the logging service (long)
		request.putLong(GpsLoggerConstants.PREF_GPS_TIMEOUT, 
				prefs.getLong(GpsLoggerConstants.PREF_GPS_TIMEOUT, 0));
				
		// start the logger service		
		Intent starter = GpsLoggerService.makeIntent(this);
		this.startService(starter);
		sendRequest(request, replyTo);
	}
	
	/**
	 * Stops the service.
	 * */
	@Override
	public void requestStopService(Handler replyTo) {
		sendRequest(GpsLoggerService.makeServiceRequest(
				GpsLoggerService.REQ_STOP_LOGGING_GPS), replyTo);
		
	}
	
	/**
	 * Restarts the services.
	 * */
	@Override
	public void restartService() {
		Log.d(TAG, "restartService");
		// stop service first
		requestStopService(new GpsLoggerServiceReplyHandler( new HandleReply() {
			
			@Override
			public void handleReply(int stateCode, String logName, long chronoBaseTime) {
				//start again 
				requestStartService(logName, chronoBaseTime, new LoggerHandler() );
				
			}
		}));		
	}
	
	private class LoggerHandler extends Handler {
		
		@Override
		public void handleMessage(Message msg) {		
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int stateCode = data.getInt(GpsLoggerService.LOGGER_STATE);
			Log.d(TAG, "service status: " + stateCode);
		}
	}

}
