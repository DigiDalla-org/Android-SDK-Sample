//
// OoVooSampleApplication.java
//
// Created by ooVoo on July 22, 2013
//
// © 2013 ooVoo, LLC.  Used under license.
//
package com.ooVoo.oovoosample.Common;

import android.app.Application;
import android.content.res.Resources;

import com.ooVoo.oovoosample.ConferenceManager;

public class OoVooSampleApplication extends Application
{
	protected static Resources getOoVooSampleResources()
	{
		return mInstance.getResources();
	}

	private static Application mInstance;

	private ConferenceManager	mConferenceManager	= null;

	@Override
	public void onCreate()
	{
		super.onCreate();
		mInstance = this;
		if( mConferenceManager  == null) {
			mConferenceManager = ConferenceManager.getInstance(getApplicationContext());
			mConferenceManager.initConferenceCore();
		}
	}
}
