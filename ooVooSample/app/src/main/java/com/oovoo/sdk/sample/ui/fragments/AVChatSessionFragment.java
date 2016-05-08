package com.oovoo.sdk.sample.ui.fragments;

import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.oovoo.sdk.api.LogSdk;
import com.oovoo.sdk.api.sdk_error;
import com.oovoo.sdk.interfaces.AudioRoute;
import com.oovoo.sdk.interfaces.AudioRouteController;
import com.oovoo.sdk.interfaces.Device;
import com.oovoo.sdk.interfaces.Effect;
import com.oovoo.sdk.interfaces.VideoController;
import com.oovoo.sdk.interfaces.VideoControllerListener.RemoteVideoState;
import com.oovoo.sdk.interfaces.VideoDevice;
import com.oovoo.sdk.interfaces.ooVooSdkResult;
import com.oovoo.sdk.interfaces.ooVooSdkResultListener;
import com.oovoo.sdk.oovoosdksampleshow.R;
import com.oovoo.sdk.sample.app.ApplicationSettings;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp.CallControllerListener;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp.NetworkListener;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp.ParticipantsListener;
import com.oovoo.sdk.sample.ui.SampleActivity.MenuList;
import com.oovoo.sdk.sample.ui.SignalBar;
import com.oovoo.sdk.sample.ui.controllers.ConferenceViewController;

import java.util.ArrayList;

public class AVChatSessionFragment extends BaseFragment implements ParticipantsListener, CallControllerListener, View.OnClickListener, NetworkListener {

    public enum CameraState {
        BACK_CAMERA(0), FRONT_CAMERA(1), MUTE_CAMERA(2);

        private final int value;

        private CameraState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    protected static final String TAG = AVChatSessionFragment.class.getSimpleName();

    private View self = null;
    private Button microphoneBttn = null;
    private Button speakerBttn = null;
    private Button cameraBttn = null;
    private Button endOfCall = null;
    private View callbar = null;
    private MenuItem signalStrengthMenuItem = null;
    private MenuItem secureNetworkMenuItem = null;
    private MenuItem informationMenuItem = null;
    private CameraState cameraState = CameraState.FRONT_CAMERA;
    private ConferenceViewController conferenceViewController = null;
    private boolean isLayoutFinished = false;
    private ArrayList<Effect> videoFilters = null;


    public AVChatSessionFragment() {
    }

    public static final AVChatSessionFragment newInstance(MenuItem signalStrengthMenuItem,
                                                          MenuItem secureNetworkMenuItem,
                                                          MenuItem informationMenuItem) {
        AVChatSessionFragment instance = new AVChatSessionFragment();
        instance.setSignalStrengthMenuItem(signalStrengthMenuItem);
        instance.setSecureNetworkMenuItem(secureNetworkMenuItem);
        instance.setInformationMenuItem(informationMenuItem);

        return instance;
    }

    public void setSignalStrengthMenuItem(MenuItem signalStrengthMenuItem) {
        this.signalStrengthMenuItem = signalStrengthMenuItem;
    }

    public void setSecureNetworkMenuItem(MenuItem secureNetworkMenuItem) {
        this.secureNetworkMenuItem = secureNetworkMenuItem;
    }

    public void setInformationMenuItem(MenuItem informationMenuItem) {
        this.informationMenuItem = informationMenuItem;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        self = inflater.inflate(R.layout.avchat_fragment, container, false);
        //filters = app().getVideoFilters();
        videoFilters = app().getVideoFilters();



        initControlBar(self);

        conferenceViewController = new ConferenceViewController(self, getActivity(), app());
        conferenceViewController.getConferenceView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!isLayoutFinished) {
                    addParticipantVideoPanel(ApplicationSettings.PREVIEW_ID, "Me");
                    isLayoutFinished = true;
                }
            }
        });

        app().addParticipantListener(this);
        app().setControllerListener(this);

        try {
            String securityState = settings().get(ApplicationSettings.SecurityState);
            if (securityState != null && Boolean.valueOf(securityState)) {
                secureNetworkMenuItem.setIcon(getResources().getDrawable(R.drawable.menu_ic_lock));
            } else {
                secureNetworkMenuItem.setIcon(getResources().getDrawable(R.drawable.menu_ic_lock_unlock));
            }
        }
        catch(Exception err){
            LogSdk.e(TAG,"onCreateView "+err);
        }

        return self;
    }

    private void initControlBar(View callbar) {
        this.callbar = callbar;

        microphoneBttn = (Button) callbar.findViewById(R.id.microphoneButton);
        microphoneBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                microphoneBttn.setEnabled(false);
                app().onMicrophoneClick();
            }
        });

        speakerBttn = (Button) callbar.findViewById(R.id.speakersButton);
        speakerBttn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                speakerBttn.setEnabled(false);
                app().onSpeakerClick();
            }
        });

        cameraBttn = (Button) callbar.findViewById(R.id.cameraButton);
        prepareButtonMenu(cameraBttn, new MenuList() {
            @Override
            public void fill(View view, ContextMenu menu) {
                try {
                    menu.setHeaderTitle(R.string.change_camera);
                    ArrayList<VideoDevice> cameras = app().getVideoCameras();
                    for (VideoDevice camera : cameras) {
                        MenuItem item = null;

                        if (camera.toString().equals("FRONT")) {
                            item = menu.add(view.getId(), CameraState.FRONT_CAMERA.getValue(), 0, R.string.front_camera);
                        } else if (camera.toString().equals("BACK")) {
                            item = menu.add(view.getId(), CameraState.BACK_CAMERA.getValue(), 0, R.string.back_camera);
                        } else {
                            item = menu.add(view.getId(), -1, 0, R.string.unknown);
                        }

                        item.setOnMenuItemClickListener(new DeviceMenuClickListener(camera) {
                            @Override
                            public boolean onMenuItemClick(Device camera, MenuItem item) {
                                if (item.getItemId() == cameraState.getValue()) {
                                    return true;
                                }
                                app().switchCamera((VideoDevice) camera);
                                if (item.getItemId() == CameraState.FRONT_CAMERA.getValue()) {
                                    cameraState = CameraState.FRONT_CAMERA;
                                } else {
                                    cameraState = CameraState.BACK_CAMERA;
                                }
                                cameraBttn.setSelected(false);
                                return true;
                            }

                        });
                    }

                    MenuItem item = menu.add(view.getId(), CameraState.MUTE_CAMERA.getValue(), 0, R.string.mute_camera);
                    item.setOnMenuItemClickListener(new MuteCameraMenuClickListener(app()) {

                        @Override
                        public boolean onMenuItemClick(boolean state, MenuItem item) {
                            if (item.getItemId() == cameraState.getValue()) {
                                return true;
                            }
                            app().muteCamera(state);
                            cameraState = state ? CameraState.MUTE_CAMERA : CameraState.MUTE_CAMERA;
                            cameraBttn.setSelected(true);
                            return true;
                        }
                    });

                    for (int i = 0; i < menu.size(); ++i) {
                        MenuItem mi = menu.getItem(i);
                        if (cameraState.getValue() == mi.getItemId()) {
                            mi.setChecked(true);
                            break;
                        }
                    }

                    menu.setGroupCheckable(view.getId(), true, true);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });

        endOfCall = (Button) callbar.findViewById(R.id.endOfCallButton);
        endOfCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                endOfCall.setEnabled(false);
                app().endCall(new ooVooSdkResultListener() {
                    @Override
                    public void onResult(ooVooSdkResult ooVooSdkResult) {
                        app().sendEndCall();
                        endOfCall.setEnabled(true);
                        int count = getFragmentManager().getBackStackEntryCount();
                        String name = getFragmentManager().getBackStackEntryAt(count - 2).getName();
                        getFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                });
            }
        });

        prepareButtonMenu((Button) callbar.findViewById(R.id.effectButton), new MenuList() {
            @Override
            public void fill(View view, ContextMenu menu) {
                try {
                    for (Effect effect : videoFilters) {
                        MenuItem item = menu.add(effect.toString());
                        item.setChecked(false);
                        LogSdk.d(TAG, "Effect " + effect);
                        item.setOnMenuItemClickListener(new EffectMenuClickListener(effect) {

                            @Override
                            public boolean onMenuItemClick(Effect effect, MenuItem item) {
                                app().changeVideoEffect(effect);
                            	//app().changeAudioEffect(effect);
                                return false;
                            }

                        });
                        item.setCheckable(true);
                        Effect active_effect = app().getActiveVideoEffect();
                        if (active_effect != null) {
                            item.setChecked(active_effect.getID().equalsIgnoreCase(effect.getID()));
                        } else {
                            if (effect.getName().equalsIgnoreCase("original")) {
                                item.setChecked(true);
                            }
                        }
                    }

                    menu.setGroupCheckable(view.getId(), true, true);
                } catch (Exception err) {
                    err.printStackTrace();
                    LogSdk.e(TAG, "Effect err" + err);
                }
            }
        });

       // app().selectCamera("FRONT");
        app().changeResolution(VideoController.ResolutionLevel.ResolutionLevelMed);
        app().openPreview();
//        app().startTransmit();
        settings().put(ApplicationSettings.ResolutionLevel, toResolutionString(VideoController.ResolutionLevel.ResolutionLevelMed));

        prepareButtonMenu((Button) callbar.findViewById(R.id.videoResolution), new MenuList() {
            @Override
            public void fill(View view, ContextMenu menu) {
                try {
                    menu.setHeaderTitle(R.string.resolution);
                    menu.setGroupCheckable(view.getId(), true, true);
                    String activeResolution = toResolutionString(app().getActiveResolution());

                    for (VideoController.ResolutionLevel resolution : app().getAvailableResolutions()) {
                        MenuItem item = menu.add(toResolutionString(resolution));
                        item.setOnMenuItemClickListener(new ResolutionMenuClickListener(resolution) {

                            @Override
                            public boolean onMenuItemClick(VideoController.ResolutionLevel resolution, MenuItem item) {
                                app().changeResolution(resolution);
                                settings().put(ApplicationSettings.ResolutionLevel, item.getTitle().toString());
                                item.setChecked(true);
                                return false;
                            }
                        });

                        if (item.getTitle().toString().equals(activeResolution)) {
                            item.setChecked(true);
                        }

                        item.setCheckable(true);
                    }

                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });

        app().getAudioRouteController().setListener(new AudioRouteController.AudioRouteControllerListener() {
            @Override
            public void onAudioRouteChanged(AudioRoute audioRoute, AudioRoute audioRoute1) {
                onAudioRouteChangedEvent(audioRoute, audioRoute1);
            }
        });

        prepareButtonMenu((Button) callbar.findViewById(R.id.audioRoutes), new MenuList() {
            @Override
            public void fill(View view, ContextMenu menu) {
                try {
                    menu.setHeaderTitle(R.string.audio_routes);
                    ArrayList<AudioRoute> routes = app().getAudioRouteController().getRoutes();
                    for (AudioRoute route : routes) {
                        MenuItem item = menu.add(route.toString());
                        item.setOnMenuItemClickListener(new AudioRouteMenuClickListener(route) {

                            @Override
                            public boolean onMenuItemClick(AudioRoute route, MenuItem item) {
                                app().changeRoute(route);
                                return false;
                            }
                        });
                        item.setCheckable(true);
                        item.setChecked(route.isActive());
                    }
                    menu.setGroupCheckable(view.getId(), true, true);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });

        app().setMicMuted(false);
        app().setSpeakerMuted(false);

        ArrayList<AudioRoute> routes = app().getAudioRouteController().getRoutes();
        for (AudioRoute route : routes) {
            if (route.isActive())
                updateRouteButtonImage(route);
        }

        updateController();
    }

    private String toResolutionString(VideoController.ResolutionLevel level) {
        String friendlyName = "";
        switch (level) {
            case ResolutionLevelLow:
                friendlyName = "Low";
                break;
            case ResolutionLevelMed:
                friendlyName = "Medium";
                break;
            case ResolutionLevelHigh:
                friendlyName = "High";
                break;
            case ResolutionLevelHD:
                friendlyName = "HD";
                break;
            default:
                break;
        }
        return friendlyName;
    }

    @Override
    public void onResume() {

        try {
            app().setNetworkListener(this);
            signalStrengthMenuItem.setVisible(true);
            secureNetworkMenuItem.setVisible(true);
            informationMenuItem.setVisible(true);

        } catch (Exception err) {
            LogSdk.e(TAG, "onResume" + err);
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            app().setNetworkListener(null);
            signalStrengthMenuItem.setVisible(false);
            secureNetworkMenuItem.setVisible(false);
            informationMenuItem.setVisible(false);
        } catch (Exception err) {

        }
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroy() {
        try {
            app().removeParticipantListener(this);
            app().setControllerListener(null);
            super.onDestroy();
        } catch (Exception err) {

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (app().isTablet()) {
            conferenceViewController.onConfigurationChanged();
        }
    }

    public void onTransmitStateChanged(boolean state, sdk_error error) {

    }

    @Override
    public void onRemoteVideoStateChanged(final String userId, final RemoteVideoState state, final sdk_error error) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				switch (state) {
					case RVS_Started:
					case RVS_Resumed:
                        conferenceViewController.hideNoVideoMessage(userId);
						break;
					case RVS_Stopped:
						break;
					case RVS_Paused:
                        conferenceViewController.showNoVideoMessage(userId);
						break;
				}
				
				if (error == sdk_error.ResolutionNotSupported) {
                    conferenceViewController.showAvatar(userId);
				}
			}
		});
    }

    @Override
    public void onParticipantJoined(final String userId, final String userData) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addParticipantVideoPanel(userId, userData);
                }
            });
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    protected void addParticipantVideoPanel(String userId, String userData) {
        try {
            conferenceViewController.addParticipant(userId, userData);

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public void onParticipantLeft(final String userId) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                conferenceViewController.removeParticipant(userId);
            }
        });
    }

    @Override
    public void updateController() {
        try {
            microphoneBttn.setEnabled(true);
            speakerBttn.setEnabled(true);
            microphoneBttn.setSelected(app().isMicMuted());
            speakerBttn.setSelected(app().isSpeakerMuted());
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Button && v.getTag() instanceof MenuList) {
            v.showContextMenu();
        }
    }

    private void prepareButtonMenu(final Button button, MenuList list) {
        button.setOnClickListener(this);
        button.setTag(list);
        getActivity().registerForContextMenu(button);
    }

    protected void onAudioRouteChangedEvent(AudioRoute old_route, AudioRoute new_route) {
        updateRouteButtonImage(new_route);
    }

    /**
     * When audio route changes we change button image too.
     *
     * @param new_route
     */
    private void updateRouteButtonImage(AudioRoute new_route) {
        try {
            Button button = (Button) callbar.findViewById(R.id.audioRoutes);
            switch (new_route.getRouteId()) {
                case AudioRoute.Earpiece:
                    button.setBackgroundResource(R.drawable.earpiece_selector);
                    break;
                case AudioRoute.Speaker:
                    button.setBackgroundResource(R.drawable.speakers_selector);
                    break;
                case AudioRoute.Headphone:
                    button.setBackgroundResource(R.drawable.headphone_selector);
                    break;
                case AudioRoute.Bluetooth:
                    button.setBackgroundResource(R.drawable.bluetooth_selector);
                    break;
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    abstract class DeviceMenuClickListener implements MenuItem.OnMenuItemClickListener {
        private Device device = null;

        DeviceMenuClickListener(Device device) {
            this.device = device;
        }

        @Override
        public final boolean onMenuItemClick(MenuItem item) {
            return onMenuItemClick(device, item);
        }

        public abstract boolean onMenuItemClick(Device device, MenuItem item);
    }

    abstract class AudioRouteMenuClickListener implements MenuItem.OnMenuItemClickListener {
        private AudioRoute route = null;

        AudioRouteMenuClickListener(AudioRoute route) {
            this.route = route;
        }

        @Override
        public final boolean onMenuItemClick(MenuItem item) {
            return onMenuItemClick(route, item);
        }

        public abstract boolean onMenuItemClick(AudioRoute route, MenuItem item);
    }

    abstract class EffectMenuClickListener implements MenuItem.OnMenuItemClickListener {
        private Effect effect = null;

        EffectMenuClickListener(Effect effect) {
            this.effect = effect;
        }

        @Override
        public final boolean onMenuItemClick(MenuItem item) {
            return onMenuItemClick(effect, item);
        }

        public abstract boolean onMenuItemClick(Effect device, MenuItem item);
    }

    abstract class ResolutionMenuClickListener implements MenuItem.OnMenuItemClickListener {
        private VideoController.ResolutionLevel resolution = null;

        ResolutionMenuClickListener(VideoController.ResolutionLevel resolution) {
            this.resolution = resolution;
        }

        @Override
        public final boolean onMenuItemClick(MenuItem item) {
            return onMenuItemClick(resolution, item);
        }

        public abstract boolean onMenuItemClick(VideoController.ResolutionLevel resolution, MenuItem item);
    }

    abstract class MuteCameraMenuClickListener implements MenuItem.OnMenuItemClickListener {
        ooVooSdkSampleShowApp app = null;

        MuteCameraMenuClickListener(ooVooSdkSampleShowApp app) {
            this.app = app;
        }

        @Override
        public final boolean onMenuItemClick(MenuItem item) {
            return onMenuItemClick(!app.isCameraMuted(), item);
        }

        public abstract boolean onMenuItemClick(boolean state, MenuItem item);
    }

    public boolean onBackPressed() {
        app().endCall();
        app().sendEndCall();

        int count = getFragmentManager().getBackStackEntryCount();
        String name = getFragmentManager().getBackStackEntryAt(count - 2).getName();
        getFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        return false;
    }

    @Override
    public void onNetworkSignalStrength(int level) {
        SignalBar signalBar = (SignalBar) signalStrengthMenuItem.getActionView();
        signalBar.setLevel(level);
    }

    @Override
    public void onNetworkSecurityState(boolean isSecure)
    {
        if (isSecure) {
            secureNetworkMenuItem.setIcon(getResources().getDrawable(R.drawable.menu_ic_lock));
        } else {
            secureNetworkMenuItem.setIcon(getResources().getDrawable(R.drawable.menu_ic_lock_unlock));
        }
    }

    public void muteVideo(String userId) {
    }

    public void unmuteVideo(String userId) {
    }
}
