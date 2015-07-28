package com.oovoo.sdk.sample.ui.fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.oovoo.core.Utils.LogSdk;
import com.oovoo.core.sdk_error;
import com.oovoo.sdk.api.ui.VideoPanel;
import com.oovoo.sdk.interfaces.AudioRoute;
import com.oovoo.sdk.interfaces.AudioRouteController;
import com.oovoo.sdk.interfaces.Device;
import com.oovoo.sdk.interfaces.Effect;
import com.oovoo.sdk.interfaces.VideoController;
import com.oovoo.sdk.interfaces.VideoControllerListener.RemoteVideoState;
import com.oovoo.sdk.interfaces.VideoDevice;
import com.oovoo.sdk.oovoosdksampleshow.R;
import com.oovoo.sdk.sample.app.ApplicationSettings;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp.CallControllerListener;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp.NetworkReliabilityListener;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp.ParticipantsListener;
import com.oovoo.sdk.sample.ui.SampleActivity.MenuList;
import com.oovoo.sdk.sample.ui.SignalBar;
import com.oovoo.sdk.sample.ui.fragments.AVChatSessionFragment.VideoAdapter.VideoItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AVChatSessionFragment extends BaseFragment implements ParticipantsListener, CallControllerListener, View.OnClickListener, OnItemClickListener, NetworkReliabilityListener {

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

    protected static final String TAG = "AVChatSessionFragment";

    private View self = null;
    private Button microphoneBttn = null;
    private Button speakerBttn = null;
    private Button cameraBttn = null;
    private Button endOfCall = null;
    private View callbar = null;
    private GridView videoGridView = null;
    private VideoAdapter videoAdapter = null;
    private VideoPanel fullScreenPreview = null;
    private VideoPanel fullScreenRemoteview = null;
    private VideoPanel currentFullScreenView = null;
    private ImageView fullScreenAvatar = null;
    private TextView fullScreenLabel = null;
    private MenuItem signalStrengthMenuItem = null;
    private MenuItem informationMenuItem = null;
    private CameraState cameraState = CameraState.FRONT_CAMERA;
    private ArrayList<Effect> filters = null;
    private ArrayList<String> mutedUserIds = new ArrayList<String>();

    public AVChatSessionFragment() {
    }

    public static final AVChatSessionFragment newInstance(MenuItem signalStrengthMenuItem, MenuItem informationMenuItem) {
        AVChatSessionFragment instance = new AVChatSessionFragment();
        instance.setSignalStrengthMenuItem(signalStrengthMenuItem);
        instance.setInformationMenuItem(informationMenuItem);
        return instance;
    }

    public void setSignalStrengthMenuItem(MenuItem signalStrengthMenuItem) {
        this.signalStrengthMenuItem = signalStrengthMenuItem;
    }

    public void setInformationMenuItem(MenuItem informationMenuItem) {
        this.informationMenuItem = informationMenuItem;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        self = inflater.inflate(R.layout.avchat_fragment, container, false);
        filters = app().getVideoFilters();


        ArrayList<Effect> filters = app().getVideoFilters();

        for (Effect effect : filters) {
            if (effect.getName().equalsIgnoreCase("original")) {
                app().changeVideoEffect(effect);
                break;
            }
        }

        initControlBar(self);


        videoGridView = (GridView) self.findViewById(R.id.video_grid_view);
        videoAdapter = new VideoAdapter(getActivity());
        videoGridView.setAdapter(videoAdapter);
        videoGridView.setOnItemClickListener(this);

        fullScreenPreview = (VideoPanel) self.findViewById(R.id.full_screen_video_panel_preview);
        fullScreenRemoteview = (VideoPanel) self.findViewById(R.id.full_screen_video_panel_remoteview);
        fullScreenAvatar = (ImageView) self.findViewById(R.id.full_screen_avatar_image_view);
        fullScreenLabel = (TextView) self.findViewById(R.id.full_screen_label);
        setupFullScreenViewClickListener();

        addParticipantVideoPanel(null, "Me");
        videoAdapter.hideAvatar(null);
        videoAdapter.hideNoVideoMessage(null);

        app().addParticipantListener(this);
        app().setControllerListener(this);

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
                        }

                        item.setOnMenuItemClickListener(new DeviceMenuClickListener(camera) {
                            @Override
                            public boolean onMenuItemClick(Device camera, MenuItem item) {
                                app().switchCamera((VideoDevice) camera);
                                app().muteCamera(false);
                                videoAdapter.hideAvatar(null);
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
                            app().muteCamera(state);
                            videoAdapter.showAvatar(null);
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
                app().onEndOPfCall();

                int count = getFragmentManager().getBackStackEntryCount();
                String name = getFragmentManager().getBackStackEntryAt(count - 2).getName();
                getFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });


        prepareButtonMenu((Button) callbar.findViewById(R.id.effectButton), new MenuList() {
            @Override
            public void fill(View view, ContextMenu menu) {
                try {
                    menu.setHeaderTitle(R.string.filters);


                    for (Effect effect : filters) {
                        MenuItem item = menu.add(effect.toString());
                        item.setChecked(false);
                        LogSdk.d(TAG, "Effect " + effect);
                        item.setOnMenuItemClickListener(new EffectMenuClickListener(effect) {

                            @Override
                            public boolean onMenuItemClick(Effect effect, MenuItem item) {
                                app().changeVideoEffect(effect);
                                return false;
                            }

                        });
                        item.setCheckable(true);
                        Effect active_effect = app().getActiveEffect();
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
        final ArrayList<String> resolutions = new ArrayList<String>();

        resolutions.add(toResolutionString(VideoController.ResolutionLevel.ResolutionLevelLow));
        resolutions.add(toResolutionString(VideoController.ResolutionLevel.ResolutionLevelMed));
        resolutions.add(toResolutionString(VideoController.ResolutionLevel.ResolutionLevelHigh));
        resolutions.add(toResolutionString(VideoController.ResolutionLevel.ResolutionLevelHD));

        app().changeResolution(VideoController.ResolutionLevel.ResolutionLevelMed);
        settings().put(ApplicationSettings.ResolutionLevel, toResolutionString(VideoController.ResolutionLevel.ResolutionLevelMed));

        prepareButtonMenu((Button) callbar.findViewById(R.id.videoResolution), new MenuList() {
            @Override
            public void fill(View view, ContextMenu menu) {
                try {
                    menu.setHeaderTitle(R.string.resolution);
                    String storedResolution = settings().get(ApplicationSettings.ResolutionLevel);
                    menu.setGroupCheckable(view.getId(), true, true);

                    for (String resolution : resolutions) {
                        MenuItem item = menu.add(resolution);
                        item.setOnMenuItemClickListener(new ResolutionMenuClickListener(stringToResolution(resolution)) {

                            @Override
                            public boolean onMenuItemClick(VideoController.ResolutionLevel resolution, MenuItem item) {
                                app().changeResolution(resolution);
                                settings().put(ApplicationSettings.ResolutionLevel, item.getTitle().toString());
                                item.setChecked(true);
                                return false;
                            }
                        });

                        if (item.getTitle().toString().equals(storedResolution)) {
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

    private VideoController.ResolutionLevel stringToResolution(String level) {
        VideoController.ResolutionLevel resolution = VideoController.ResolutionLevel.ResolutionLevelMed;
        if (level.equalsIgnoreCase("Low")) {
            return VideoController.ResolutionLevel.ResolutionLevelLow;
        }

        if (level.equalsIgnoreCase("Medium")) {
            return VideoController.ResolutionLevel.ResolutionLevelMed;
        }

        if (level.equalsIgnoreCase("HD")) {
            return VideoController.ResolutionLevel.ResolutionLevelHD;
        }

        if (level.equalsIgnoreCase("High")) {
            return VideoController.ResolutionLevel.ResolutionLevelHigh;
        }

        return resolution;
    }

    @Override
    public void onResume() {

        try {
            app().setNetworkReliabilityListener(this);
            signalStrengthMenuItem.setVisible(true);
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
            app().setNetworkReliabilityListener(null);
            signalStrengthMenuItem.setVisible(false);
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

    public Point getDisplaySize() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }


    public void onTransmitStateChanged(boolean state, sdk_error error) {
//		try {
//			if (state) {
//				videoAdapter.hideAvatar(null);
//			} else {
//				videoAdapter.showAvatar(null);
//			}
//		}
//		catch(Exception err){
//			LogSdk.e(TAG,"onTransmitStateChanged error "+err);
//		}
    }

    @Override
    public void onRemoteVideoStateChanged(final String userId, final RemoteVideoState state, final sdk_error error) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				switch (state) {
					case RVS_Started:
					case RVS_Resumed:

						videoAdapter.hideAvatar(userId);
						videoAdapter.hideNoVideoMessage(userId);
						break;
					case RVS_Stopped:
						//videoAdapter.showAvatar(userId);
						break;
					case RVS_Paused:

						//videoAdapter.showAvatar(userId);
						videoAdapter.showNoVideoMessage(userId);
						break;
				}
				
				if (error == sdk_error.ResolutionNotSupported) {
					videoAdapter.showAvatar(userId);
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
            videoAdapter.addItem(videoAdapter.new VideoItem(userId, userData));

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public void onParticipantLeft(final String userId) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeParticipantVideoPanel(userId);
            }
        });
        mutedUserIds.remove(userId);
    }

    protected void removeParticipantVideoPanel(String userId) {
        try {
            videoAdapter.removeItem(userId);
            disableFullScreenView();

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        VideoItem item = videoAdapter.getItem(position);

        if (!item.isAvatarVisible()) {
            currentFullScreenView = item.getUserId() == null ? fullScreenPreview : fullScreenRemoteview;

            currentFullScreenView.setTag(R.id.video_panel_view, item);
            currentFullScreenView.setVisibility(View.VISIBLE);
            currentFullScreenView.setZOrderMediaOverlay(true);
            fullScreenLabel.setText(item.getUserData());
            fullScreenLabel.setVisibility(View.VISIBLE);

            app().unbindVideoPanel(item.getUserId(), item.getVideo());
            app().bindVideoPanel(item.getUserId(), currentFullScreenView);

            for (int i = 0; i < videoAdapter.getCount(); i++) {
                item = videoAdapter.getItem(i);
                try {
                    if (item.getUserId() != null) {
                        item.getVideo().setVisibility(View.INVISIBLE);
                    }

                } catch (Exception ex) {
                    LogSdk.e("TAG", ex.toString());
                }
            }
        }
    }

    private void disableFullScreenView() {
        try {
            if (currentFullScreenView != null) {
                for (int i = 0; i < videoAdapter.getCount(); i++) {
                    VideoItem item = videoAdapter.getItem(i);
                    if (item.getVideo() != null) {
                        item.getVideo().setVisibility(View.VISIBLE);
                    }
                }

                VideoItem item = (VideoItem) currentFullScreenView.getTag(R.id.video_panel_view);

                app().unbindVideoPanel(item.getUserId(), currentFullScreenView);
                app().bindVideoPanel(item.getUserId(), item.getVideo());

                currentFullScreenView.setVisibility(View.GONE);
                currentFullScreenView = null;
                fullScreenAvatar.setVisibility(View.GONE);
                fullScreenLabel.setVisibility(View.GONE);
            }
        } catch (Exception err) {

        }
    }

    public void setupFullScreenViewClickListener() {
        fullScreenPreview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                disableFullScreenView();
            }
        });

        fullScreenRemoteview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                disableFullScreenView();
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
        videoAdapter.removeAllItems();

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

    public class VideoAdapter extends BaseAdapter {
        private final List<VideoItem> mItems = new ArrayList<VideoItem>();
        private final LayoutInflater mInflater;

        public VideoAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return mItems.size();
        }

        @Override
        public VideoItem getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return mItems.get(i).getVideo().getId();
        }

        public View getView(final int position, View view, ViewGroup viewGroup) {
            View v = view;

            VideoItem item = getItem(position);

            if (v == null) {
                v = mInflater.inflate(R.layout.video_grid_item, viewGroup, false);

                VideoPanel video = (VideoPanel) v.findViewById(R.id.video_panel_view);

                v.setTag(R.id.video_panel_view, video);

                TextView displayNameTextView = (TextView) v.findViewById(R.id.display_name_text_view);
                v.setTag(R.id.display_name_text_view, displayNameTextView);

                ImageView avatarImageView = (ImageView) v.findViewById(R.id.avatar_image_view);
                v.setTag(R.id.avatar_image_view, avatarImageView);

                TextView noVideoMessage = (TextView) v.findViewById(R.id.no_video_message);
                v.setTag(R.id.no_video_message, noVideoMessage);

                if (item.getVideo() == null) {
                    if (mutedUserIds.contains(item.getUserId())) {
                        item.showAvatar();
                    } else {
                        item.setVideo(video);
                        app().bindVideoPanel(item.getUserId(), video);
                        video.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (item.getVideo() == null) {
                    VideoPanel video = (VideoPanel) v.getTag(R.id.video_panel_view);
                    item.setVideo(video);
                    app().bindVideoPanel(item.getUserId(), video);
                    video.setVisibility(View.VISIBLE);
                }
            }
            VideoPanel video = (VideoPanel) v.getTag(R.id.video_panel_view);

            final View bottomView = AVChatSessionFragment.this.callbar.findViewById(R.id.call_controll_layout);
            final Window window = AVChatSessionFragment.this.getActivity().getWindow();
            int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
            int contentViewBottom = bottomView.getMeasuredHeight();
            contentViewBottom = contentViewBottom == 0 ? contentViewTop : contentViewBottom;

            int width = getDisplaySize().x / 2;
            int height = (getDisplaySize().y - (contentViewTop + contentViewBottom)) / 2 - (v.getPaddingTop() * 3);
            video.setTag(new Point(width, height));

            TextView displayNameTextView = (TextView) v.getTag(R.id.display_name_text_view);
            displayNameTextView.setText(item.getUserData());

            ImageView avatarImageView = (ImageView) v.getTag(R.id.avatar_image_view);
            if (item.isAvatarVisible()) {
                avatarImageView.setVisibility(View.VISIBLE);
            } else {
                avatarImageView.setVisibility(View.INVISIBLE);
            }

            TextView errorMessage = (TextView) v.getTag(R.id.no_video_message);
            if (item.isErrorMessageVisible()) {
                errorMessage.setText(getString(R.string.video_cannot_be_viewed));
                errorMessage.setVisibility(View.VISIBLE);
                avatarImageView.setVisibility(View.VISIBLE);
            } else {
                errorMessage.setVisibility(View.GONE);
                if (!item.isAvatarVisible()) {
                    avatarImageView.setVisibility(View.INVISIBLE);
                }
            }

            return v;
        }

        public void addItem(VideoItem item) {
            item.setAdapter(this);
            mItems.add(item);
            notifyDataSetChanged();
        }

        public void removeItem(String userId) {

            Iterator<VideoItem> iter = mItems.iterator();
            while (iter.hasNext()) {
                VideoItem item = iter.next();
                if ((item.getUserId() == null && userId == null) || (item.getUserId() != null && item.getUserId().equals(userId))) {
                    VideoPanel video = item.getVideo();
                    if (video != null) {
                        video.setVisibility(View.INVISIBLE);
                        item.setAdapter(null);
                        item.setVideo(null);
                    }
                    iter.remove();
                    app().unbindVideoPanel(item.getUserId(), video);
                } else if (item.getUserId() != null) {
                    VideoPanel video = item.getVideo();
                    item.setAdapter(null);
                    item.setVideo(null);
                    app().unbindVideoPanel(item.getUserId(), video);
                }
            }

            notifyDataSetChanged();
        }

        public void removeAllItems() {
            Iterator<VideoItem> iter = mItems.iterator();
            while (iter.hasNext()) {
                VideoItem item = iter.next();
                VideoPanel video = item.getVideo();
                if (video != null) {
                    video.setVisibility(View.INVISIBLE);
                    item.setVideo(null);
                }
                iter.remove();
                app().unbindVideoPanel(item.getUserId(), video);
            }
        }

        public void showAvatar(String userId) {
            try {
				if (currentFullScreenView != null)
				{
					VideoItem videoItem = (VideoItem) currentFullScreenView.getTag(R.id.video_panel_view);
					if (videoItem != null && (videoItem.getUserId() == userId || (videoItem.getUserId() != null && videoItem.getUserId().equals(userId))) &&
							currentFullScreenView.getVisibility() == View.VISIBLE) {
						fullScreenAvatar.setVisibility(View.VISIBLE);
					}
				}
				for (VideoItem item : mItems) {
					if ((item.getUserId() == null && userId == null) || (item.getUserId() != null && userId != null && item.getUserId().equals(userId))) {
						item.showAvatar();
						break;
					}
				}

				notifyDataSetChanged();
            } catch (Exception err) {
                LogSdk.e(TAG, "showAvatar " + err);
            }
        }

        public void showNoVideoMessage(String userId) {
            if (currentFullScreenView != null) {
                VideoItem videoItem = (VideoItem) currentFullScreenView.getTag(R.id.video_panel_view);
                if (videoItem != null && videoItem.getUserId() == userId && currentFullScreenView.getVisibility() == View.VISIBLE) {
                    //TODO
                }
            }
            for (VideoItem item : mItems) {
                if ((item.getUserId() == null && userId == null) || (item.getUserId() != null && userId != null && item.getUserId().equals(userId))) {
                    item.showErrorMessage();
                    break;
                }
            }

            notifyDataSetChanged();
        }

        public void hideAvatar(String userId) {
            try {
				if (currentFullScreenView != null) {
					VideoItem videoItem = (VideoItem) currentFullScreenView.getTag(R.id.video_panel_view);
					if (videoItem != null && (videoItem.getUserId() == userId || (videoItem.getUserId() != null && userId != null && videoItem.getUserId().equals(userId))) &&
							currentFullScreenView.getVisibility() == View.VISIBLE) {
						fullScreenAvatar.setVisibility(View.GONE);
					}
				}
				for (VideoItem item : mItems) {
					if ((item.getUserId() == null && userId == null) || (item.getUserId() != null && userId != null && item.getUserId().equals(userId))) {
						item.hideAvatar();
						break;
					}
				}

				notifyDataSetChanged();
            } catch (Exception err) {
                LogSdk.e(TAG, "hideAvatar = " + err);
            }
        }

        public void hideNoVideoMessage(String userId) {
            try {
                if (currentFullScreenView != null) {
                    VideoItem videoItem = (VideoItem) currentFullScreenView.getTag(R.id.video_panel_view);
                    if (videoItem != null && videoItem.getUserId() == userId && currentFullScreenView.getVisibility() == View.VISIBLE) {
                        //fullScreenAvatar.setVisibility(View.GONE);
                    }
                }
                for (VideoItem item : mItems) {
                    if ((item.getUserId() == null && userId == null) || (item.getUserId() != null && userId == null && item.getUserId().equals(userId))) {
                        item.hideErrorMessage();
                        break;
                    }
                }

                notifyDataSetChanged();
            } catch (Exception err) {
                LogSdk.e(TAG, "hideNoVideoMessage = " + err);
            }
        }

        public class VideoItem {
            private VideoPanel video = null;
            private boolean isAvatarVisible = true;
            private boolean isErrorMessageVisible = false;
            private final String userId;
            private final String userData;
            private BaseAdapter adapter = null;

            public VideoItem(String userId, String userData) {
                this.userId = userId;
                this.userData = userData;
                showAvatar();
            }

            public void setVideo(VideoPanel video) {
                if (video != null) {
                    video.setVideoRenderStateChangeListener(new VideoPanel.VideoRenderStateChangeListener() {

                        public String toString() {
                            return userId != null ? userId : "preview";
                        }

                        @Override
                        public void onVideoRenderStart() {
                            try {
                                hideAvatar();
                                LogSdk.d(TAG, "VideoControllerWrap -> VideoPanel -> Application  onVideoRenderStop hideAvatar " + userId != null ? userId : "preview" + ", adapter " + (adapter != null ? "set" : "null"));
                                if (adapter != null)
                                    adapter.notifyDataSetChanged();
                            } catch (Exception err) {
                                LogSdk.e(TAG, "onVideoRenderStart " + err);
                            }
                        }

                        @Override
                        public void onVideoRenderStop() {
                            try {
                                showAvatar();
                                LogSdk.d(TAG, "VideoControllerWrap -> VideoPanel -> Application  onVideoRenderStop showAvatar " + userId != null ? userId : "preview" + ", adapter " + (adapter != null ? "set" : "null"));
                                if (adapter != null)
                                    adapter.notifyDataSetChanged();

                            } catch (Exception err) {
                                LogSdk.e(TAG, "onVideoRenderStop " + err);
                            }

                        }
                    });
                } else {
                    if (this.video != null) {
                        this.video.setVideoRenderStateChangeListener(null);
                    }
                }
                this.video = video;
            }

            public void setAdapter(BaseAdapter adapter) {
                this.adapter = adapter;
            }

            public VideoPanel getVideo() {
                return this.video;
            }

            public boolean isAvatarVisible() {
                return this.isAvatarVisible;
            }

            public boolean isErrorMessageVisible() {
                return isErrorMessageVisible;
            }

            public void showAvatar() {
                this.isAvatarVisible = true;
            }

            public void hideAvatar() {
                this.isAvatarVisible = false;
                this.isErrorMessageVisible = false;
            }

            public void showErrorMessage() {
                this.isErrorMessageVisible = true;
            }

            public void hideErrorMessage() {
                this.isErrorMessageVisible = false;
            }

            public String getUserId() {
                return userId;
            }

            public String getUserData() {
                return userData;
            }
        }
    }

    public boolean onBackPressed() {
        app().onEndOPfCall();

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

    public void muteVideo(String id) {
        if (!mutedUserIds.contains(id)) {
            mutedUserIds.add(id);
        }
        //videoAdapter.showAvatar(id);
    }

    public void unmuteVideo(String id) {
        mutedUserIds.remove(id);
        //videoAdapter.hideAvatar(id);
    }

    public boolean isMuted(String id) {
        return mutedUserIds.contains(id);
    }
}
