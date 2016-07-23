package org.szvsszke.vitezlo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.szvsszke.vitezlo.gpslogger.GpsDatabase;
import org.szvsszke.vitezlo.map.data.XMLTools;
import org.szvsszke.vitezlo.map.model.Track;
import org.szvsszke.vitezlo.utilities.Utilities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ExportFragment extends Fragment {
	
	private final String TAG = getClass().getName();
	
	public static final String GPX_SUFFIX = ".gpx";
	public static final String DEFAULT_DIR = "gpx";
	
	private Spinner mSpinNames;
	
	private TextView mTVPathLabel;
	private EditText mETFileName, mETDescription, mETPath;
	//private CheckBox mCBToExternalSD;
	
	private Button mBtnEport;	
	
	private ArrayList<String> mNames;
	private int mCurrentSelected = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		
		View inflated = inflater.inflate(R.layout.fragment_export, null);
		
		mTVPathLabel = (TextView) inflated.findViewById(R.id.textViewPath);
		mETFileName = (EditText) inflated.findViewById(R.id.editTextFileName);
		mETDescription = (EditText) inflated.findViewById(R.id.editTextExpDesc);
		mETPath = (EditText) inflated.findViewById(R.id.editTextPath);

		mBtnEport = (Button) inflated.findViewById(R.id.buttonExport);
		mBtnEport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// check if there is anyhing to be exported
				if (!GpsDatabase.getInstance(getActivity()).isEmpty()) {
					// disable button while export is taking place
					mBtnEport.setEnabled(false);
					export(GPX_SUFFIX);
				}
				else {
					mBtnEport.setEnabled(false);
					Toast.makeText(getActivity(), R.string.export_no_data, 
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		if(!PermissionHelper.hasWriteExternal(getContext())) {
			mBtnEport.setEnabled(false);
            PermissionHelper.requestWritePermission(getActivity());
		}
		
		mSpinNames = (Spinner) inflated.findViewById(R.id.spinnerUserHikeNames);
		setupSpinner();
	
		return inflated;
	}
	
	private void setupSpinner() {
		Log.d(TAG, "setupSpinner");
		// set listener
		mSpinNames.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent,
					View view, int position, long id) {							
				if (mNames != null) {
					mETFileName.setText(mNames.get(position) + GPX_SUFFIX);
					mCurrentSelected = position;
				}				 
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// do nothing				
			}			
		});
		
		// load user hike names from database
		loadUserHikeNamesFromDatabase();
	}
	
	private void loadUserHikeNamesFromDatabase() {
		new AsyncTask<String, Integer, ArrayList<String> >() {

			@Override
			protected ArrayList<String>  doInBackground(String... params) {
				// query database
				mNames = GpsDatabase.getInstance(getActivity()).getPathNames();
				return mNames;
			}
			
			@Override
			protected void onPostExecute(ArrayList<String> result) {
				mSpinNames.setAdapter(new ArrayAdapter<String> (getActivity(), 
						R.layout.info_box_spinner_item, R.id.textViewTrackName, result));
				super.onPostExecute(result);
				
			}
		}.execute("");
	}
	
	private void export(final String suffix) {
		Log.d(TAG, "export");
		// do it async
		final int SUCCESS = 0;
		final int FILE_EXISTS = 1;
		final int FAIL = 2;
		final int DIR_NOT_CREATED = 3;
		
		new AsyncTask<String, Integer, Integer>() {
						
			@Override
			protected Integer doInBackground(String... params) {
				// disable button				
				// read from database first
				Track track = GpsDatabase.getInstance(getActivity()).getPathTrack(
						mNames.get(mCurrentSelected));
				// export to a temporary location
				try {
					
					String filename = params[0];
					if (!filename.endsWith(suffix)) {
						filename.concat(suffix);
					}
					
					File tmp = new File(getActivity().getCacheDir(), filename);	
					
					FileOutputStream fos = new FileOutputStream (tmp);
					String version = Utilities.getAppVersion(getActivity());
					XMLTools.writeToGPX(track, fos, version, params[0]);
					
					// verify file is created					
					if(tmp.exists()) {
						Log.d(TAG, "temp file created successfully");
						// email file
						/*if (mCBEmail.isChecked()) {
							sendEmail(tmp);
						}
						if(mCBToExternalSD.isChecked()) {
						*/
							// copy to specfied folder
							File dir = new File(Environment.getExternalStorageDirectory()
									+ File.separator + DEFAULT_DIR);
							dir.mkdir();
							// check if directory created
							if (!dir.isDirectory()) {
								return DIR_NOT_CREATED;
							}
							
							File export = new File(dir, filename);
							try {
								Utilities.copyFile(tmp, export);
								export.createNewFile();
							} catch (IOException e) {								
								e.printStackTrace();
								return FAIL;
							}

						// delete filefrom sd card
						
						tmp.delete();
						if(tmp.exists()) {
							Log.e(TAG, "file could not be deleted!");
						}
						
					} else {
						Log.d(TAG, "file does not exist");
					}
					return SUCCESS;
					
				} catch (FileNotFoundException e) {
					Log.e(TAG, "export(): file not found exception");					
					e.printStackTrace();
				}				
				
				return FAIL;
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				mBtnEport.setEnabled(true);
				int resultID;
				switch (result) {
				case SUCCESS:
					resultID = R.string.export_success;
					break;
				case FILE_EXISTS:
					resultID = R.string.file_exists;
					break;
				case DIR_NOT_CREATED:
					resultID = R.string.dir_not_created;
					break;
				default:					
					resultID = R.string.export_failed;
					break;
				}
				
				Toast.makeText(getActivity(), resultID, 
						Toast.LENGTH_SHORT).show();
				Log.d(TAG, getString(resultID));
			};
			
		}.execute(mETFileName.getText().toString());
				
	}
	
	private void sendEmail(File file) {
		Log.d(TAG, "sendEmail");
		Uri fileUri = Uri.fromFile(file);
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("vnd.android.cursor.dir/email");		
		emailIntent .putExtra(Intent.EXTRA_STREAM, fileUri);
		emailIntent .putExtra(Intent.EXTRA_SUBJECT, 
				getString(R.string.export_subject));
		startActivity(Intent.createChooser(emailIntent , "Send email..."));
	}
}
