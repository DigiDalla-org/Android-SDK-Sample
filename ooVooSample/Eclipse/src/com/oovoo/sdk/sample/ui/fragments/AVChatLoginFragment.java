package com.oovoo.sdk.sample.ui.fragments;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.oovoo.core.Utils.LogSdk;
import com.oovoo.sdk.api.ui.VideoPanel;
import com.oovoo.sdk.interfaces.Effect;
import com.oovoo.sdk.interfaces.VideoController;
import com.oovoo.sdk.interfaces.VideoDevice;
import com.oovoo.sdk.oovoosdksampleshow.R;
import com.oovoo.sdk.sample.ui.VideoPanelPreviewRect;

public class AVChatLoginFragment extends BaseFragment {
	private static final int CONFERENCE_ID_LIMIT = 200;
	private static final int DISPLAY_NAME_LIMIT = 100; 
	private static final String TAG = "AVChatLoginFragment" ;
	private EditText	sessionIdEditText	= null;
	private EditText	displayNameEditText	= null;
	private MenuItem 	settingsMenuItem = null;
	private VideoPanelPreviewRect previewRect = null;

	public AVChatLoginFragment(){}
	
	public static final AVChatLoginFragment newInstance(MenuItem settingsMenuItem) {
		AVChatLoginFragment instance = new AVChatLoginFragment();
	    instance.setSettingsMenuItem(settingsMenuItem);
	    return instance;
	}
	
	public void setSettingsMenuItem(MenuItem settingsMenuItem) {
		this.settingsMenuItem = settingsMenuItem;
	}
	
	@Override
    public void onResume() {
	    super.onResume();
		if(settingsMenuItem  != null)
	    	settingsMenuItem.setVisible(true);
    }
	
	@Override
    public void onPause() {
        super.onPause();
		if(settingsMenuItem  != null)
        	settingsMenuItem.setVisible(false);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    if (app().isTablet()) {			
	    	updatePreviewLayout(newConfig);
        }	    
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.avchat_login_fragment, container, false);
		VideoPanel panel = (VideoPanel) view.findViewById(R.id.preview_view);

		previewRect = (VideoPanelPreviewRect) view.findViewById(R.id.preview_rect);
		
		if (app().isTablet()) {
			Configuration config = getResources().getConfiguration();
			updatePreviewLayout(config);
		}

		ArrayList<VideoDevice> cameras = app().getVideoCameras();
	    for (VideoDevice camera : cameras) {
	    	if (camera.toString().equals("FRONT") && !app().getActiveCamera().getID().equalsIgnoreCase(camera.getID())) {
	    		app().switchCamera(camera);
	    		break;
	    	}
		}




	    ArrayList<Effect> filters = app().getVideoFilters();

		for(Effect effect : filters){
			if(effect.getName().equalsIgnoreCase("original")){
				app().changeVideoEffect(effect);
				break ;
			}
		}


		app().changeResolution(VideoController.ResolutionLevel.ResolutionLevelMed);

	    app().openPreview();

		String lastSessionId = settings().get("avs_session_id");
		String lastDisplayName = settings().get("avs_session_display_name");

		sessionIdEditText = (EditText) view.findViewById(R.id.session_field);

		if (lastSessionId != null) {
			sessionIdEditText.setText(lastSessionId);
		}

		displayNameEditText = (EditText) view.findViewById(R.id.displayname_field);

		if (lastDisplayName != null) {
			displayNameEditText.setText(lastDisplayName);
		}

		/****
		 * Let's bind the view for camera preview output
		 */
		app().bindVideoPanel(null, panel);

		Button join = (Button) view.findViewById(R.id.join_button);
		join.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				if (app().isOnline()) {
					join();
//				} else {
//					Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
//				}
			}
		});

		return view;
	}
	
	private void updatePreviewLayout(Configuration config) {
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			int width = app().getDisplaySize().x;
	    	int padding = (width - ((int)(app().getDisplaySize().y * 0.75f * (4.0/3.0))))/2;
        	previewRect.setPadding(padding, 0, padding, 0);
		} else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
	    	previewRect.setPadding(0, 0, 0, 0);
	    }
	}

	private void join() 
	{
		String sessionId = sessionIdEditText.getText().toString();
		String displayName = displayNameEditText.getText().toString();
		if (sessionId.isEmpty()) {
			Toast.makeText(getActivity(), R.string.enter_conference_id, Toast.LENGTH_LONG).show();
			
			return;
		}
		
		if (!sessionId.matches("^([a-zA-Z0-9-%])+$") || sessionId.length() > CONFERENCE_ID_LIMIT) {
			showErrorMessageBox(getString(R.string.join_session), getString(R.string.wrong_conference_id));
			
			return;
		}
		
		if (displayName.isEmpty()) {
			Toast.makeText(getActivity(), R.string.enter_conference_display_name, Toast.LENGTH_LONG).show();
			
			return;
		}
		
		if (displayName.length() > DISPLAY_NAME_LIMIT) {
			showErrorMessageBox(getString(R.string.join_session), getString(R.string.display_name_too_long));
			
			return;
		}

		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(displayNameEditText.getWindowToken(), 0);
			
		if(!app().isOnline()){
			showErrorMessageBox("Network Error", getString(R.string.no_internet));
			return;
		}
			
		app().join(sessionId, displayName);
	}


	protected void finalize() throws Throwable {
		LogSdk.d(TAG, "ooVooCamera -> VideoPanel -> finalize AVChatLoginFragment ->");
		super.finalize();
	}
	
	public void showErrorMessageBox(String title,String msg)
	{
		try {		
				AlertDialog.Builder popupBuilder = new AlertDialog.Builder(getActivity());
				TextView myMsg = new TextView(getActivity());
				myMsg.setText(msg);
				myMsg.setGravity(Gravity.CENTER);
				popupBuilder.setTitle(title);
				popupBuilder.setPositiveButton("OK", null);
				popupBuilder.setView(myMsg);

				popupBuilder.show();
		} catch( Exception e) {
		}
	}
	
	public BaseFragment getBackFragment() {
		return new LoginFragment();
	}
	
	public boolean onBackPressed() {

		app().releaseAVChat();
		return true ;
    }
}
