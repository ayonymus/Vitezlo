package org.szvsszke.vitezlo2018;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class GeneralInfoFragment extends Fragment {
	
	private static final String TAG = GeneralInfoFragment.class.getName() ;
	
	private Button contactBtn, emailBtn, homepageBtn;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
	
		View rootView = (View) inflater.inflate(R.layout.proto_fragment_general_info,
				container, false);
		
		contactBtn = (Button) rootView.findViewById(R.id.button1);
		emailBtn = (Button) rootView.findViewById(R.id.button2);
		homepageBtn = (Button) rootView.findViewById(R.id.button3);
		
		ClipboardListener listener = new ClipboardListener();
		contactBtn.setOnClickListener(listener);
		emailBtn.setOnClickListener(listener);
		homepageBtn.setOnClickListener(listener);
		
		return rootView;
	}
	
	private class ClipboardListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
			String str = "";
			ClipData clip;
			switch(v.getId()) {
			case R.id.button1:
				str = getString(R.string.phone1);				
				break;
			case R.id.button2:
				str = getString(R.string.email_address);
				break;
			default:
				str = getString(R.string.homepage);
			}
			clip = ClipData.newPlainText(str, str);			
			clipboard.setPrimaryClip(clip);
			Toast.makeText(getActivity(), str + " " + getString(R.string.copied), Toast.LENGTH_SHORT).show();;
		}
	}
}
