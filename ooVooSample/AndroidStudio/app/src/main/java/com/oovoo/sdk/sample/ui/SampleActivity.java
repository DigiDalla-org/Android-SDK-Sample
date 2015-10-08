package com.oovoo.sdk.sample.ui;

 
 

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.oovoo.core.Utils.LogSdk;
import com.oovoo.sdk.api.ooVooClient;
import com.oovoo.sdk.oovoosdksampleshow.R;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp.Operation;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp.OperationChangeListener;
import com.oovoo.sdk.sample.ui.fragments.AVChatLoginFragment;
import com.oovoo.sdk.sample.ui.fragments.AVChatSessionFragment;
import com.oovoo.sdk.sample.ui.fragments.BaseFragment;
import com.oovoo.sdk.sample.ui.fragments.FlashScreen;
import com.oovoo.sdk.sample.ui.fragments.InformationFragment;
import com.oovoo.sdk.sample.ui.fragments.LoginFragment;
import com.oovoo.sdk.sample.ui.fragments.ReautorizeFragment;
import com.oovoo.sdk.sample.ui.fragments.SettingsFragment;
import com.oovoo.sdk.sample.ui.fragments.WaitingFragment;

public class SampleActivity extends Activity implements OperationChangeListener {

 
	private static final String	  	TAG	           	= SampleActivity.class.getSimpleName();
	private static final String 	STATE_FRAGMENT 	= "current_fragment";
	private BaseFragment	      	current_fragment	= null;
	private ooVooSdkSampleShowApp	application	   = null;
	private MenuItem 				mSettingsMenuItem = null;
	private MenuItem 				mInformationMenuItem = null;
	private MenuItem 				mSignalStrengthMenuItem = null;
	private boolean					mIsAlive = false;
	private boolean					mNeedShowFragment = false;



 
 
 
 
 

 
 
 
 
 
 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (ooVooSdkSampleShowApp) getApplication();

		setRequestedOrientation(application.getDeviceDefaultOrientation());

		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.host_activity);

		application.addOperationChangeListener(this);

		if (savedInstanceState != null) {
			current_fragment = (BaseFragment)getFragmentManager().getFragment(savedInstanceState, STATE_FRAGMENT);
			showFragment(current_fragment);
		} else {
			Fragment newFragment = new FlashScreen();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(R.id.host_activity, newFragment).commit();

			if (!ooVooClient.isDeviceSupported()) {
				return;
			}

			try {
 
				application.onMainActivityCreated();
			} catch( Exception e) {
				Log.e( TAG, "onCreate exception: ", e);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		try {
			getFragmentManager().putFragment(savedInstanceState, STATE_FRAGMENT, current_fragment);
		} catch( Exception e) {
			Log.e( TAG, "onSaveInstanceState exception: ", e);
		}
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		application.removeOperationChangeListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		mIsAlive = true;
 

		if(mNeedShowFragment){
			showFragment(current_fragment);
			mNeedShowFragment = false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		mIsAlive = false;
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		Object tag = v.getTag();
		if (tag != null && tag instanceof MenuList) {
			MenuList list = (MenuList) tag;
			list.fill(v, menu);
		}
	}

	public void finish(){
		if(current_fragment != null) {
			this.removeFragment(current_fragment);
			current_fragment = null ;
		}
		application.logout();
		super.finish();
	}


	@Override
	public boolean onCreateOptionsMenu( Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.main_menu, menu);

		mSettingsMenuItem = menu.findItem(R.id.menu_settings);

		mInformationMenuItem = menu.findItem(R.id.menu_information);
		mInformationMenuItem.setVisible(false);

		mSignalStrengthMenuItem = menu.findItem(R.id.menu_signal_strenth);

		SignalBar signalBar = new SignalBar(this);

		mSignalStrengthMenuItem.setActionView(signalBar);
		mSignalStrengthMenuItem.setVisible(false);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item)
	{
		if( item == null)
			return false;

		switch( item.getItemId())
		{
			case R.id.menu_settings:
				SettingsFragment settings  = new SettingsFragment();
				settings.setBackFragment(current_fragment);

				mSettingsMenuItem.setVisible(false);

				addFragment(settings);

				current_fragment = settings;
			return true;

			case R.id.menu_information:
				InformationFragment information  = new InformationFragment();
				information.setBackFragment(current_fragment);

				mSignalStrengthMenuItem.setVisible(false);
				mInformationMenuItem.setVisible(false);

				addFragment(information);

				current_fragment = information;
			return true;
		}

		return super.onOptionsItemSelected( item);
	}

	@Override
	public void onOperationChange(Operation state) {
		try {
			Fragment prev_fragment = current_fragment ;
			switch (state) {
			case Error:
			{
				switch (state.forOperation()) {
				case Authorized:
					current_fragment = ReautorizeFragment.newInstance(mSettingsMenuItem, state.getDescription());
					break;
				case LoggedIn:
					current_fragment = LoginFragment.newInstance(state.getDescription());
					break;
				case AVChatJoined:
				case AVChatDirectJoined:
					application.showErrorMessageBox(this, getString(R.string.join_session), state.getDescription());
					current_fragment = AVChatLoginFragment.newInstance(mSettingsMenuItem);
					break;
				default:
					return;
				}
			}
				break;
			case Processing:
				current_fragment = WaitingFragment.newInstance(state.getDescription());
				break;
			case AVChatJoined:
			case AVChatDirectJoined:
				current_fragment = AVChatSessionFragment.newInstance(mSignalStrengthMenuItem, mInformationMenuItem);
				break;
			case Authorized:
				current_fragment = LoginFragment.newInstance();
				break;
			case AVChatDisconnected:
			case LoggedIn:
				current_fragment = AVChatLoginFragment.newInstance(mSettingsMenuItem);
				break;
			default:
				return;
			}

			showFragment(current_fragment);
			//removeOldFragment(prev_fragment);
			prev_fragment = null ;
			System.gc();
			Runtime.getRuntime().gc();

		} catch (Exception err) {
			err.printStackTrace();
		}
	}



	private void showFragment(Fragment newFragment) {
		if(!mIsAlive){
			mNeedShowFragment = true;
			return;
		}

		try {
			if (newFragment != null) {
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.host_activity, newFragment);
				transaction.addToBackStack(newFragment.getClass().getSimpleName());
				transaction.commit();
			}
		}
		catch(Exception err){
			LogSdk.e(TAG,"showFragment " + err);
		}
	}


	public void removeOldFragment(Fragment fragment) {
		try {
			if(fragment != null) {
				FragmentTransaction trans = getFragmentManager().beginTransaction();
				trans.remove(fragment);
				trans.commit();
				System.gc();
				Runtime.getRuntime().gc();
				LogSdk.d(TAG, "ooVooCamera -> VideoPanel -> finalize removeOldFragment " + fragment);
			}
		}
		catch(Exception err){
			LogSdk.e(TAG,"removeOldFragment " + err);
		}
	}

	private void addFragment(Fragment newFragment) {

		try {
			if (newFragment != null) {
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.add(R.id.host_activity, newFragment);
				transaction.show(newFragment);
				transaction.hide(current_fragment);
				transaction.commit();
			}
		}
		catch(Exception err){
			LogSdk.e(TAG,"addFragment " + err);
		}
	}

	private void removeFragment(Fragment fragment) {

		try {
			if (fragment != null) {
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.remove(current_fragment);
				transaction.show(fragment);
				transaction.commit();
			}
		}
		catch(Exception err){
			LogSdk.e(TAG,"removeFragment " + err);
		}
	}

	public static interface MenuList {
		public void fill(View view, ContextMenu menu);
	}

	@Override
	public void onBackPressed() {
		try {
			if (current_fragment != null) {

				if(/*(current_fragment instanceof WaitingFragment) ||*/ !current_fragment.onBackPressed()){
					return ;
				}

				BaseFragment fragment = current_fragment.getBackFragment();
				if (fragment != null) {

					if (current_fragment instanceof InformationFragment) {
						mSignalStrengthMenuItem.setVisible(true);
						mInformationMenuItem.setVisible(true);
						removeFragment(fragment);
					} else if (current_fragment instanceof SettingsFragment) {
						mSettingsMenuItem.setVisible(true);
						removeFragment(fragment);
					} else {

						showFragment(fragment);
						//removeOldFragment(current_fragment);
						System.gc();
						Runtime.getRuntime().gc();
					}
					current_fragment = fragment;

					return ;
				}

			}
		} catch (Exception err) {
			Log.e(TAG, "");
		}
		super.onBackPressed();
	}
}
