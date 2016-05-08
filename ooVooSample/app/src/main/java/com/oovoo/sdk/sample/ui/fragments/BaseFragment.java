package com.oovoo.sdk.sample.ui.fragments;

import com.oovoo.sdk.sample.app.ApplicationSettings;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp;

import android.app.Fragment;

public class BaseFragment extends Fragment{
	
	private BaseFragment back_fragment = null ;
	
	public void setKeepScreenOn(boolean state){
		if(getView() != null)
			getView().setKeepScreenOn(state);
	}

	ooVooSdkSampleShowApp app(){
		return ((ooVooSdkSampleShowApp) getActivity().getApplication()) ;
	}

	public ApplicationSettings settings() {
		return app().getSettings();
	}
	
	public BaseFragment getBackFragment() {
		return back_fragment;
	}

	public void setBackFragment(BaseFragment back_fragment) {
		this.back_fragment = back_fragment;
	}

	public boolean onBackPressed() {
	   return true ;
    }
}
