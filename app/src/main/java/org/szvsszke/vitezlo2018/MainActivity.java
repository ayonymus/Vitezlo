package org.szvsszke.vitezlo2018;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.legacy.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

import org.szvsszke.vitezlo2018.adapter.NavDrawerAdapter;
import org.szvsszke.vitezlo2018.presentation.preferences.PreferencesFragment;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
	
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


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
		// inflate menu
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.hike_menu_items, menu);		
		
		return super.onCreateOptionsMenu(menu);
	}
	
	// listener for action bar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
        // check permissions
        if (!PermissionHelper.hasCoarse(this) || !PermissionHelper.hasFineLocation(this)) {
            Timber.v("No perissions, start askpermission fragment");
            // start with map fragment
            mCurrentFragment = new AskPermissionFragment();
        } else {
		
		switch (position) {
            // start logging fragment
            case 1:
                mCurrentFragment = new PreferencesFragment();
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
