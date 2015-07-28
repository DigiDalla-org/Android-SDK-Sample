package com.oovoo.sdk.sample.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.oovoo.core.LoggerListener;
import com.oovoo.core.Utils.LogSdk;
import com.oovoo.core.sdk_error;
import com.oovoo.sdk.api.ooVooClient;
import com.oovoo.sdk.api.ui.VideoPanel;
import com.oovoo.sdk.interfaces.AVChatListener;
import com.oovoo.sdk.interfaces.AudioControllerListener;
import com.oovoo.sdk.interfaces.AudioRoute;
import com.oovoo.sdk.interfaces.AudioRouteController;
import com.oovoo.sdk.interfaces.Effect;
import com.oovoo.sdk.interfaces.Participant;
import com.oovoo.sdk.interfaces.VideoController.ResolutionLevel;
import com.oovoo.sdk.interfaces.VideoControllerListener;
import com.oovoo.sdk.interfaces.VideoDevice;
import com.oovoo.sdk.interfaces.ooVooSdkResult;
import com.oovoo.sdk.interfaces.ooVooSdkResultListener;
//import com.oovoo.sdk.plugin.ooVooPluginFactory ;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class ooVooSdkSampleShowApp extends Application implements VideoControllerListener, LoggerListener,
AVChatListener, AudioControllerListener
{
	public static final String	TAG	= "ooVooSdkSampleShowApp";

	public enum Operation
	{
		Authorized, LoggedIn, Processing, AVChatJoined, AVChatDisconnected, Error,NoToken ;
		private String		description		= "";
		private Operation	forOperation	= null;

		public String getDescription()
		{
			return description;
		}

		public void setDescription( String description)
		{
			this.description = description;
		}

		public Operation forOperation()
		{
			return forOperation;
		}

		public void setForOperation( Operation state)
		{
			forOperation = state;
		}
	}

	private ooVooClient							sdk							= null;
	public static final int						WaitingState				= 0;
	public static final int						LoginState					= 1;
	private Handler								operation_handler			= null;
	private ArrayList<OperationChangeListener>	listeners					= new ArrayList<OperationChangeListener>();
	private Operation							state						= null;
	private boolean								m_iscameraopened			= false;
	private boolean								m_previewopened				= false;
	private ArrayList<ParticipantsListener>		m_participantListeners		= new ArrayList<ParticipantsListener>();
	private Hashtable<String, String>			participants				= new Hashtable<String, String>();
	private ApplicationSettings					settings					= null;
	private CallControllerListener				controllerListener			= null;
	private NetworkReliabilityListener			networkReliabilityListener	= null;





	@Override
	public void onCreate()
	{
		super.onCreate();
		try
		{

			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
				public void uncaughtException(Thread t, Throwable e) {
					LogSdk.e(TAG,"UncaughtExceptionHandler threade = "+t+", error "+e);
				}
			});

			if (!ooVooClient.isDeviceSupported()) {
				return;
			}



			
			settings = new ApplicationSettings( this);

			ooVooClient.setLogger(this, LogLevel.fromString(getSettings().get(ApplicationSettings.LogLevelKey)));
			ooVooClient.setContext( this);
			sdk = ooVooClient.sharedInstance();
			sdk.enableMessaging(false) ;
			sdk.getAVChat().setListener( this);
			sdk.getAVChat().getVideoController().setListener( this);
			sdk.getAVChat().getAudioController().setListener(this);
			sdk.getAVChat().setSslVerifyPeer(true);
			// sdk.getAVChat().registerPlugin(new ooVooPluginFactory());

			AudioRouteController audioController = sdk.getAVChat().getAudioController().getAudioRouteController();
			LogSdk.d( TAG, "Audio controller " + audioController);



		} catch( Exception e)
		{
			e.printStackTrace();
			sdk = null;
		}
		operation_handler = new Handler();
	}



	public ApplicationSettings getSettings()
	{
		return settings;
	}

	public void addOperationChangeListener( OperationChangeListener listener)
	{
		synchronized( listeners)
		{
			listeners.add(listener);
		}
	}

	public void removeOperationChangeListener( OperationChangeListener listener)
	{
		synchronized( listeners)
		{
			listeners.remove(listener);
		}
	}

	public void setNetworkReliabilityListener( NetworkReliabilityListener listener)
	{
		networkReliabilityListener = listener;
	}

	protected synchronized void fireApplicationStateEvent( final Operation state)
	{
		fireApplicationStateEvent( state, 0);
	}

	protected synchronized void fireApplicationStateEvent( final Operation state, String description)
	{
		state.setDescription( description);
		fireApplicationStateEvent( state, 0);
	}

	protected synchronized void fireApplicationStateEvent( final Operation state, Operation forOperation,
			String description)
	{
		state.setForOperation( forOperation);
		state.setDescription( description);
		fireApplicationStateEvent( state, 0);
	}

	protected synchronized void fireApplicationStateEvent( final Operation state, String description, long delayMillis)
	{
		state.setDescription( description);
		fireApplicationStateEvent( state, delayMillis);
	}

	protected synchronized void fireApplicationStateEvent( final Operation state, final Runnable excecuteAfter)
	{
		fireApplicationStateEvent(state, excecuteAfter, 0);
	}

	protected synchronized void fireApplicationStateEvent( final Operation state, final Runnable excecuteAfter,
			long delayMillis)
	{
		operation_handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				synchronized (listeners) {
					for (OperationChangeListener listener : listeners) {
						listener.onOperationChange(state);
					}
				}
				if (excecuteAfter != null) {
					operation_handler.post(excecuteAfter);
				}
			}
		}, delayMillis);
	}

	protected synchronized void fireApplicationStateEvent( final Operation new_state, long delayMillis)
	{
		this.state = new_state;
		operation_handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				synchronized (listeners) {
					for (OperationChangeListener listener : listeners) {
						listener.onOperationChange(new_state);
					}
				}
			}
		}, delayMillis);

	}

	public Operation getState()
	{
		return state;
	}

	/***
	 * Called when main activity created
	 */
	public void onMainActivityCreated()
	{

		reautorize();
	}

	public void reautorize()
	{
		fireApplicationStateEvent( Operation.Processing, "Authorizing");
		autorize();
	}

	private void autorize()
	{
		try
		{
			String APP_TOKEN = settings.get(ApplicationSettings.Token) ;
			if(APP_TOKEN == null || APP_TOKEN.trim().isEmpty())
			{
				fireApplicationStateEvent( Operation.Error, Operation.Authorized, "App Token probably invalid or might be empty.\n\nGet your App Token at\nhttp://developer.oovoo.com.\nSet TOKEN constant in code.");
				return ;
			}

			sdk.authorizeClient( APP_TOKEN , new ooVooSdkResultListener() {
				@Override
				public void onResult( ooVooSdkResult autorize_result)
				{
					if( autorize_result.getResult() == sdk_error.OK)
					{
						fireApplicationStateEvent( Operation.Authorized);
						return;
					}
					fireApplicationStateEvent( Operation.Error, Operation.Authorized, autorize_result.getDescription());
				}
			});
		} catch( Exception e)
		{
			fireApplicationStateEvent( Operation.Error, Operation.Authorized,e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	public static interface OperationChangeListener
	{
		public void onOperationChange( Operation state);
	}

	public synchronized void login( final String username)
	{
		m_iscameraopened = false;
		m_previewopened = false;
		fireApplicationStateEvent( Operation.Processing, "Log in");

		sdk.getAccount().login(username, new ooVooSdkResultListener() {
			@Override
			public void onResult(ooVooSdkResult result) {

				if (result.getResult() == sdk_error.OK) {

					settings.put(ApplicationSettings.Username, username);
					settings.save();
					fireApplicationStateEvent(Operation.LoggedIn);
					return;
				}
				fireApplicationStateEvent(Operation.Error, Operation.LoggedIn, result.getDescription());
			}
		});
	}



	public synchronized void openPreview()
	{
		if( !m_iscameraopened)
		{
			sdk.getAVChat().getVideoController().openCamera();
			if( !m_previewopened)
				sdk.getAVChat().getVideoController().openPreview();
			return;
		}
		else
		{
			sdk.getAVChat().getVideoController().openPreview();
		}
	}

	@Override
	public void onCameraChanged( String arg0, sdk_error arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraStateChanged( boolean is_opened, String deviceId,int width,int height, int fps ,sdk_error error)
	{
		LogSdk.d(TAG, "ooVooCamera ->onCameraStateChanged [is_opened = " + is_opened + ". error = " + error + ", size = " + width + "x" + height + "]");
		if( is_opened)
		{
			sdk.getAVChat().getVideoController().openPreview();
			m_iscameraopened = true;
		}
		else{
			sdk.getAVChat().getVideoController().closePreview();
			m_iscameraopened = false;
		}


	}

	@Override
	public void onRemoteVideoStateChanged( String uid, RemoteVideoState state,int width,int height, sdk_error error)
	{

		LogSdk.d(TAG, "ooVooCamera ->onRemoteVideoStateChanged [uid = " + uid + ". RemoteVideoState = " + state + "]");
		if( m_participantListeners.size() > 0)
		{
			Iterator<ParticipantsListener> iter = m_participantListeners.iterator();
			while(iter.hasNext()){
				ParticipantsListener listener = iter.next();
				listener.onRemoteVideoStateChanged(uid, state, error);
			}
		}
	}

	@Override
	public void onTransmitStateChanged( boolean arg0, sdk_error arg1)
	{
		if( m_participantListeners.size() > 0)
		{
			Iterator<ParticipantsListener> iter = m_participantListeners.iterator();
			while(iter.hasNext()){
				ParticipantsListener listener = iter.next();
				listener.onTransmitStateChanged(arg0, arg1);
			}
		}
	}

	@Override
	public void onVideoPreviewStateChanged( boolean arg0, sdk_error arg1)
	{
		LogSdk.d( TAG, "ooVooCamera ->onVideoPreviewStateChanged [is_opened = " + arg0 + ". error = " + arg1 + "]");
		m_previewopened = arg0;

	}

	@Override
	public void OnLog( LogLevel level, String tag, String message)
	{
		switch( level)
		{
		case None:
			break;
		case Debug:
			Log.d( tag, "[" + level.toString() + "] " + message);
			break;
		case Fatal:
			Log.wtf( tag, "[" + level.toString() + "] " + message);
			break;
		case Info:
			Log.i( tag, "[" + level.toString() + "] " + message);
			break;
		case Trace:
			Log.v( tag, "[" + level.toString() + "] " + message);
			break;
		case Warning:
			Log.w( tag, "[" + level.toString() + "] " + message);
			break;
		case Error:
		default:
			Log.e( TAG, "[" + level.toString() + "] " + message);
			break;
		}
	}

	private VideoPanel	panel_preview	= null;

	public void bindPreviewPanel( VideoPanel panel)
	{
		unbindPreviewPanel();
		panel_preview = panel;
		try
		{
			sdk.getAVChat().getVideoController().bindRender(null, panel);
		} catch( InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void unbindPreviewPanel()
	{
		if( panel_preview != null)
		{
			try
			{
				sdk.getAVChat().getVideoController().unbindRender(null, panel_preview);
				panel_preview = null ;
			} catch( InstantiationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void bindVideoPanel( String id, VideoPanel video)
	{
		if( id == null)
		{
			bindPreviewPanel(video);

			return;
		}
		try
		{
			sdk.getAVChat().getVideoController().bindRender(id, video);
		} catch( InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sdk.getAVChat().getVideoController().registerRemote( id);
	}

	public void unbindVideoPanel( String id, VideoPanel video)
	{
		if( id == null)
		{
			unbindPreviewPanel();
			return;
		}
		sdk.getAVChat().getVideoController().unregisterRemote(id);
		try
		{
			sdk.getAVChat().getVideoController().unbindRender(id, video);
		} catch( InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void join( final String session_id, final String displayname)
	{

		participants.clear();

		settings.put( ApplicationSettings.AvsSessionId, session_id);
		settings.put( ApplicationSettings.AvsSessionDisplayName, displayname);

		fireApplicationStateEvent(Operation.Processing, Operation.AVChatJoined, "Joining");

	}

	public void onProcessingStarted()
	{
		if( Operation.Processing.forOperation() != null)
		{
			switch( Operation.Processing.forOperation())
			{
			case AVChatJoined:
			{
				final String session_id = settings.get( ApplicationSettings.AvsSessionId);
				final String session_dn = settings.get( ApplicationSettings.AvsSessionDisplayName);

				LogSdk.d(TAG, "Application - > onProcessingStarted start join conference id = " + session_id + ", display name = " + session_dn);

				sdk.updateConfig(new ooVooSdkResultListener() {
					@Override
					public void onResult(ooVooSdkResult result) {
						LogSdk.d(TAG, "Application - > updateConfig result = " +result);
						if(result.getResult() == sdk_error.OK) {
							sdk.getAVChat().getAudioController().initAudio(new ooVooSdkResultListener() {
								@Override
								public void onResult(ooVooSdkResult init_audio_result) {
									LogSdk.d(TAG, "Application - > init audio completion " + init_audio_result);
									sdk.getAVChat().join(session_id, session_dn);

								}
							});
						}
					}
				});




			}
				break;
			default:
				break;
			}
			Operation.Processing.setForOperation(null);
		}
	}

	@Override
	public void onConferenceError( sdk_error error)
	{
		LogSdk.d(TAG, "Application - > onConferenceError  error " + error);
	}

	@Override
	public void onConferenceStateChanged( ConferenceState avchat_state, sdk_error error)
	{
		LogSdk.d(TAG, "Application - > onConferenceStateChanged " + avchat_state + ", error " + error);
		if( avchat_state == ConferenceState.Joined && error == sdk_error.OK)
		{
			settings.save();
			fireApplicationStateEvent(Operation.AVChatJoined);

		} else if (avchat_state == ConferenceState.Joined && error != sdk_error.OK)
		{
			fireApplicationStateEvent( Operation.Error, Operation.AVChatJoined, "AVChat error");

		}
		else if( avchat_state == ConferenceState.Disconnected) {

			unbindPreviewPanel();
			m_iscameraopened = false;
			m_previewopened = false;
			fireApplicationStateEvent( Operation.AVChatDisconnected);
		}

	}

	@Override
	public void onParticipantJoined(Participant participant, String displayName) {
			if( m_participantListeners.size() > 0)
			{
				Iterator<ParticipantsListener> iter = m_participantListeners.iterator();
				while(iter.hasNext()){
					ParticipantsListener listener = iter.next();
					listener.onParticipantJoined(participant.getID(), displayName);
					LogSdk.d(TAG, "Application - > onParticipantJoined " + participant.getID() + ", "
							+ displayName + ", m_participantListener = " + listener);
				}
			}
			synchronized( participants)
			{
				participants.put(participant.getID(), displayName);
			}

	}

	@Override
	public void onParticipantLeft( Participant participant)
	{
		LogSdk.d(TAG, "Application - > onParticipantLeft " + participant.getID());
		if( m_participantListeners.size() > 0)
		{
			Iterator<ParticipantsListener> iter = m_participantListeners.iterator();
			while(iter.hasNext()){
				ParticipantsListener listener = iter.next();
				listener.onParticipantLeft(participant.getID());
			}
		}
		synchronized( participants)
		{
			participants.remove(participant.getID());
		}
	}

	@Override
	public void onReceiveData( String arg0, byte[] arg1)
	{
		LogSdk.d( TAG, "Application - > onReceiveData " + arg0 + ", " + arg1);

	}

	public void logout(){

		if (sdk != null) {
			sdk.getAccount().logout();
		}
		System.gc();
	}

	public void addParticipantListener( ParticipantsListener listener)
	{
		if(listener == null || m_participantListeners.contains(listener))
			return;
		
		synchronized( participants)
		{
			m_participantListeners.add(listener);
			Enumeration<String> en = participants.keys();
			while (en.hasMoreElements()) {
				String key = en.nextElement();
				String displayname = participants.get(key);
				listener.onParticipantJoined(key, displayname);
			}
		}
	}
	
	public void removeParticipantListener(ParticipantsListener listener)
	{
		m_participantListeners.remove(listener);
	}

	public Hashtable<String, String> getParticipants()
	{
		return participants;
	}

	public static interface ParticipantsListener
	{
		public void onParticipantJoined( String userId, String userData);

		public void onParticipantLeft( String userId);

		public void onRemoteVideoStateChanged( String userId, RemoteVideoState state, sdk_error error);

		public void onTransmitStateChanged(boolean state,sdk_error err) ;
	}

	@Override
	public void onAudioReceiveStateChanged( boolean arg0, sdk_error arg1)
	{
		try
		{
			LogSdk.d( TAG, "onAudioReceiveStateChanged " + (!arg0) + ", error " + arg1);
			if( controllerListener != null)
			{
				controllerListener.updateController();
			}
		} catch( Exception err)
		{
			LogSdk.d( TAG, "onAudioReceiveStateChanged err " + err);
		}
	}

	@Override
	public void onAudioTransmitStateChanged( boolean arg0, sdk_error arg1)
	{
		try
		{
			LogSdk.d( TAG, "onAudioTransmitStateChanged " + (!arg0) + ", error " + arg1);
			if( controllerListener != null)
			{
				controllerListener.updateController();
			}
		} catch( Exception err)
		{
			LogSdk.d( TAG, "onAudioTransmitStateChanged err " + err);
		}
	}

	public void onMicrophoneClick()
	{
		boolean state = sdk.getAVChat().getAudioController().isRecordMuted();
		LogSdk.d( TAG, "Change record state from " + state + ", to " + (!state));
		sdk.getAVChat().getAudioController().setRecordMuted(!state);
	}

	public void onSpeakerClick()
	{
		boolean state = sdk.getAVChat().getAudioController().isPlaybackMuted();
		LogSdk.d( TAG, "Change playback state from " + (state) + ", to " + (!state));
		sdk.getAVChat().getAudioController().setPlaybackMuted(!state);
	}

	public void switchCamera( VideoDevice camera)
	{
		sdk.getAVChat().getVideoController().setActiveDevice((VideoDevice) camera);
	}

	public VideoDevice getActiveCamera()
	{
		return sdk.getAVChat().getVideoController().getActiveDevice();
	}

	public ArrayList<Effect> getVideoFilters()
	{
		return sdk.getAVChat().getVideoController().getEffectList();
	}

	public Effect getActiveEffect()
	{
		return sdk.getAVChat().getVideoController().getActiveEffect();
	}

	public ArrayList<VideoDevice> getVideoCameras()
	{
		return sdk.getAVChat().getVideoController().getDeviceList();
	}

	public void onEndOPfCall()
	{
		unbindPreviewPanel();
		m_iscameraopened = false;
		m_previewopened = false;
		sdk.getAVChat().leave();
	}

	public static interface CallControllerListener
	{
		public void updateController();
	}

	public void setControllerListener( CallControllerListener controllerListener)
	{
		this.controllerListener = controllerListener;
	}

	public boolean isMicMuted()
	{
		return sdk.getAVChat().getAudioController().isRecordMuted();
	}

	public boolean isSpeakerMuted()
	{

		return sdk.getAVChat().getAudioController().isPlaybackMuted();
	}

	@Override
	public void onMicrophoneStateChange( boolean arg0, sdk_error arg1)
	{
		if( controllerListener != null)
		{
			controllerListener.updateController();
		}
	}

	@Override
	public void onSpeakerStateChange( boolean arg0, sdk_error arg1)
	{
		if( controllerListener != null)
		{
			controllerListener.updateController();
		}
	}

	public AudioRouteController getAudioRouteController()
	{
		return sdk.getAVChat().getAudioController().getAudioRouteController();
	}

	public void performOperation( Runnable operationOnResume)
	{
		if( operationOnResume != null)
		{
			operation_handler.postDelayed(operationOnResume, 100);
		}
	}

	public boolean isCameraMuted()
	{
		boolean tr = sdk.getAVChat().getVideoController().isTransmited() ;
		LogSdk.d(TAG,"isTransmited = "+tr+", will return as "+!tr);
		return !sdk.getAVChat().getVideoController().isTransmited();
	}

	public void muteCamera( boolean state)
	{
		if( state)
		{
			//sdk.getAVChat().getVideoController().closePreview();
			sdk.getAVChat().getVideoController().closeCamera();
			sdk.getAVChat().getVideoController().stopTransmit();
		}
		else{
			sdk.getAVChat().getVideoController().openCamera();
			sdk.getAVChat().getVideoController().startTransmit();
		}
		//	sdk.getAVChat().getVideoController().startTransmit();
	}

	public void changeResolution( ResolutionLevel resolution)
	{
		try
		{

			boolean state = sdk.getAVChat().getVideoController().getActiveDevice().isResolutionSupported(resolution) ;
			LogSdk.d(TAG,"VideoControler -> isResolutionSupported :: changeResolution "+resolution);
			sdk.getAVChat().getVideoController().setActiveResolution(resolution);
		} catch( Exception err)
		{
			err.printStackTrace();
		}
	}

	public void changeVideoEffect( Effect effect)
	{
		try
		{
			sdk.getAVChat().getVideoController().setActiveEffect( effect);
		} catch( Exception err)
		{
			err.printStackTrace();
		}
	}

	public void changeRoute( AudioRoute route)
	{
		try
		{
			sdk.getAVChat().getAudioController().getAudioRouteController().setRoute(route);
		} catch( Exception err)
		{
			err.printStackTrace();
		}
	}

	@Override
	public void onNetworkReliability( int level)
	{
		LogSdk.d(TAG, "onNetworkReliability level = " + level + "networkReliabilityListener = " + (networkReliabilityListener == null ? "null" : "OK"));
		if( networkReliabilityListener != null)
		{

			networkReliabilityListener.onNetworkSignalStrength( level);
		}
	}

	public void releaseAVChat()
	{
		try
		{
			unbindPreviewPanel();
			m_iscameraopened = false;
			m_previewopened = false;
			sdk.getAVChat().getVideoController().closeCamera();
		} catch( Exception err)
		{
			err.printStackTrace();
		}
	}

	public String getSdkVersion()
	{
		return ooVooClient.getSdkVersion();
	}

	public static interface NetworkReliabilityListener
	{
		public void onNetworkSignalStrength( int level);
	}

	public boolean isTablet()
	{
		return ooVooClient.isTablet();
	}



	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {
				return true;
			}
		} catch (Exception e) {
			LogSdk.e(TAG, e.toString());

			// probably connectivity problem so we will return false
		}
		return false;
	}




	public int getDeviceDefaultOrientation() {

		WindowManager windowManager =  (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		Configuration config = getResources().getConfiguration();

		int rotation = windowManager.getDefaultDisplay().getRotation();

		if ( ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) &&
				config.orientation == Configuration.ORIENTATION_LANDSCAPE)
				|| ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&
				config.orientation == Configuration.ORIENTATION_PORTRAIT)) {
			return Configuration.ORIENTATION_LANDSCAPE;
		} else {
			return Configuration.ORIENTATION_PORTRAIT;
		}
	}

	public Point getDisplaySize() {
		WindowManager windowManager =  (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}
	
	public void setLogLevel(String logLevel) {
		ooVooClient.setLogLevel(LogLevel.fromString(logLevel));
	}
}
