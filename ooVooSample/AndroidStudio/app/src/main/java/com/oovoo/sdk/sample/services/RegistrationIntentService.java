package com.oovoo.sdk.sample.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.oovoo.core.Utils.LogSdk;
import com.oovoo.sdk.sample.app.ApplicationSettings;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp;

/**
 * Created by oovoo on 9/8/15.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegistrationIntentService";
    
    private static final String SENDER_ID = "522796524817";

    public RegistrationIntentService() {
        super(TAG);

        LogSdk.i(TAG, "RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            ooVooSdkSampleShowApp application = (ooVooSdkSampleShowApp) getApplication();
            ApplicationSettings settings = application.getSettings();
            String username = settings.get(ApplicationSettings.Username);
            String token = settings.get(username);

            if (token == null) {
                InstanceID instanceID = InstanceID.getInstance(this);
                token = instanceID.getToken(SENDER_ID/*getString(R.string.gcm_defaultSenderId)*/, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                settings.put(username, token);
                settings.save();
                sendRegistrationToServer(token);
                LogSdk.i(TAG, "GCM Registration Token: " + token);
            }

            sharedPreferences.edit().putBoolean(ApplicationSettings.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            LogSdk.e(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(ApplicationSettings.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed
        Intent registrationComplete = new Intent(ApplicationSettings.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

        ooVooSdkSampleShowApp application = (ooVooSdkSampleShowApp) getApplication();

        application.subscribe(token);
    }
}
