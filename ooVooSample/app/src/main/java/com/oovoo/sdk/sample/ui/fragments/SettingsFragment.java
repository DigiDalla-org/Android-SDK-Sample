package com.oovoo.sdk.sample.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.oovoo.sdk.api.LoggerListener.LogLevel;
import com.oovoo.sdk.api.ui.VideoPanel;
import com.oovoo.sdk.oovoosdksampleshow.R;
import com.oovoo.sdk.sample.app.ApplicationSettings;
import com.oovoo.sdk.sample.ui.CustomVideoPanel;

public class SettingsFragment extends BaseFragment {
	
	private Spinner logSpinner = null;
	private Spinner videoModeSpinner = null;
	private TextView tokenTextView = null;
	private CustomVideoPanel customPanel = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_fragment_layout, container, false);

		customPanel = (CustomVideoPanel) container.findViewById(R.id.custom_preview_view);

		tokenTextView = (TextView) view.findViewById(R.id.token_edit_text);
		tokenTextView.setText(settings().get(ApplicationSettings.Token));
		
		logSpinner = (Spinner) view.findViewById(R.id.log_level_spinner);

		String[] logLevelValues = {LogLevel.None.toString(), LogLevel.Fatal.toString(), LogLevel.Error.toString(), LogLevel.Warning.toString(),
				LogLevel.Info.toString(), LogLevel.Debug.toString(), LogLevel.Trace.toString()};
		ArrayAdapter<String> logAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, logLevelValues);
		logSpinner.setAdapter(logAdapter);
		int logSpinnerPosition = logAdapter.getPosition(settings().get(ApplicationSettings.LogLevelKey));
		logSpinner.setSelection(logSpinnerPosition);
		
		logSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				String logLevel = (String) logSpinner.getSelectedItem();
		        settings().put(ApplicationSettings.LogLevelKey, logLevel);
		        app().setLogLevel(logLevel);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
        });

		videoModeSpinner = (Spinner) view.findViewById(R.id.video_fitting_mode_spinner);

		String[] videoModeValues = {VideoPanel.FittingMode.FillUp.toString(), VideoPanel.FittingMode.AutoBoxing.toString()};
		ArrayAdapter<String> videoModeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, videoModeValues);
		videoModeSpinner.setAdapter(videoModeAdapter);
		int videoModeSpinnerPosition = videoModeAdapter.getPosition(settings().get(ApplicationSettings.VideoModeKey));
		videoModeSpinner.setSelection(videoModeSpinnerPosition);

		videoModeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				String videoMode = (String) videoModeSpinner.getSelectedItem();
				settings().put(ApplicationSettings.VideoModeKey, videoMode);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		CheckBox videoOrientationLock = (CheckBox) view.findViewById(R.id.video_orientation_lock);

		String videoOrientationLockValue = settings().get(ApplicationSettings.VideoOrientationLockKey);

		if (videoOrientationLockValue != null) {
			videoOrientationLock.setChecked(Boolean.valueOf(videoOrientationLockValue));
		} else {
			settings().put(ApplicationSettings.VideoOrientationLockKey, Boolean.toString(false));
		}

		videoOrientationLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				settings().put(ApplicationSettings.VideoOrientationLockKey, Boolean.toString(isChecked));
			}
		});

		CheckBox videoOrientationAnim = (CheckBox) view.findViewById(R.id.video_orientation_anim);

		String videoOrientationAnimValue = settings().get(ApplicationSettings.VideoOrientationAnimKey);

		if (videoOrientationAnimValue != null) {
			videoOrientationAnim.setChecked(Boolean.valueOf(videoOrientationAnimValue));
		} else {
			settings().put(ApplicationSettings.VideoOrientationAnimKey, Boolean.toString(true));
		}

		videoOrientationAnim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				settings().put(ApplicationSettings.VideoOrientationAnimKey, Boolean.toString(isChecked));
			}
		});

		TextView sdkVersion = (TextView) view.findViewById(R.id.sdk_version_text_view);
		sdkVersion.setText(app().getSdkVersion());
		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (customPanel != null) {
			customPanel.setVisibility(View.INVISIBLE);
		}
	}

	@Override
    public void onPause() {
        super.onPause();
	
        String logLevel = (String) logSpinner.getSelectedItem();
        settings().put(ApplicationSettings.LogLevelKey, logLevel);
        settings().put(ApplicationSettings.Token, tokenTextView.getText().toString());
        settings().save();

		if (customPanel != null) {
			customPanel.setVisibility(View.VISIBLE);
		}
	}
}
