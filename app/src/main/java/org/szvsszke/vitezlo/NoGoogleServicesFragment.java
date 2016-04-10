package org.szvsszke.vitezlo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class NoGoogleServicesFragment extends Fragment{
	
	private final String TAG = getClass().getName();
	
	private TextView mTVService, mTVServiceSol, mTVMap, mTVMapSol;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		View root = inflater.inflate(R.layout.fragment_no_services, null);
		mTVService = (TextView) root.findViewById(R.id.textViewService1);
		mTVServiceSol = (TextView) root.findViewById(R.id.textViewService2);
		mTVMap = (TextView) root.findViewById(R.id.textViewMap1);
		mTVMapSol = (TextView) root.findViewById(R.id.textViewMap2);
				
		
		switch (GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(getActivity())){
			case ConnectionResult.SUCCESS:
				Log.d(TAG, "success");
				//no way
				break;
            case ConnectionResult.SERVICE_MISSING:
            	Log.d(TAG, "Services missing");
            	mTVService.setText(R.string.prob_no_service);
    			mTVServiceSol.setText(R.string.prob_no_service_sol);
    			mTVMap.setVisibility(View.GONE);
    			mTVMapSol.setVisibility(View.GONE);
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            	Log.d(TAG, "Services require update");
            	mTVService.setText(R.string.prob_service_update_);
    			mTVServiceSol.setText(R.string.prob_service_update_sol);
            	
                break;
            default:
            	Log.d(TAG, "NO success, code: " + GooglePlayServicesUtil
    					.isGooglePlayServicesAvailable(getActivity()));
            	mTVService.setText(R.string.prob_service_update_);
    			mTVServiceSol.setText(R.string.prob_service_update_sol);              
		}
		
		if(!isGoogleMapsInstalled()) {
			Log.d(TAG, "Google maps not installed");
			mTVMap.setText(R.string.prob_maps_missing);
			mTVMapSol.setText(R.string.prob_maps_missing_sol);
			mTVMapSol.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// find maps on market
					Intent intent = new Intent(Intent.ACTION_VIEW, 
							Uri.parse("market://details?id=com.google.android.apps.maps"));
		            startActivity(intent);
		 
		            //Finish the activity so they can't circumvent the check
		            getActivity().finish();
				}
			});
		}
		
		
		return root;
	}
	
	/**
	 * Determines weather Maps is installed to the system.
	 * @return true if installed
	 * */
    private boolean isGoogleMapsInstalled(){    	
        try{
            getActivity().getPackageManager()
            		.getApplicationInfo("com.google.android.apps.maps", 0 );            
            return true;
        } catch(PackageManager.NameNotFoundException e){
            return false;
        }
    }

}
