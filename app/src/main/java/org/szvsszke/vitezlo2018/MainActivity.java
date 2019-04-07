package org.szvsszke.vitezlo2018;

import org.szvsszke.vitezlo2018.adapter.NavDrawerAdapter;

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
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

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
                doubleBackToExitPressedOnce = false;
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

        // check permissions
        if (!PermissionHelper.hasCoarse(this) || !PermissionHelper.hasFineLocation(this)) {
            Log.d(TAG, "No perissions, start askpermission fragment");
            // start with map fragment
            mCurrentFragment = new AskPermissionFragment();
        } else {
		
		switch (position) {
            // start logging fragment
            case 1:
                mCurrentFragment = new MapPreferncesFragment();
                break;
            case 2:
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
                    if (servicesAvailable && isGoogleMapsInstalled()) {
                        mMapFragment = new MapFragment();
                        // keep reference so the parsed xml data doesn't have to
                        // be loaded again
                        mCurrentFragment = mMapFragment;
                    } else {
                        mCurrentFragment = new NoGoogleServicesFragment();
                    }
                } else {
                    mCurrentFragment = mMapFragment;
                }
            }
		}
		
		// begin transaction
		FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();	
		fragTransaction.replace(R.id.fragment_container, mCurrentFragment);
        fragTransaction.commitAllowingStateLoss();
		//fragTransaction.commit();
		
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PermissionHelper.LOCATION_REQUEST) {
            if (grantResults.length > 1){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setCurrentFragment(0);
                }
            }
        }
		// write permission
		if(requestCode == PermissionHelper.WRITE_REQUEST) {
			if (grantResults.length > 0){
				if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					setCurrentFragment(4);
				}
			}
		}
    }

}
