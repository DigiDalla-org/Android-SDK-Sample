package com.oovoo.sdk.sample.ui.controllers;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.oovoo.sdk.api.LogSdk;
import com.oovoo.sdk.api.ui.VideoPanel;
import com.oovoo.sdk.interfaces.VideoRender;
import com.oovoo.sdk.oovoosdksampleshow.R;
import com.oovoo.sdk.sample.app.ApplicationSettings;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp;
import com.oovoo.sdk.sample.ui.CustomVideoPanel;
import com.oovoo.sdk.sample.ui.views.ConferenceView;
import com.oovoo.sdk.sample.ui.views.CustomScrollView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by oovoo on 11/3/15.
 */
public class ConferenceViewController implements View.OnClickListener {

    public static final String TAG = "ConferenceViewController";

    private final static int COLUMNS_NUMBER = 2;
    private ooVooSdkSampleShowApp application = null;
    private ConferenceView conferenceView = null;
    private List<VideoViewItem> items = new LinkedList<VideoViewItem>();
    private LayoutInflater inflater = null;
    private boolean isFullScreenMode = false;
    private View clickedView = null;
    private CustomScrollView scrollView = null;
    private int scrollY = 0;
    private boolean isMeasured = false;
    private int contentViewBottom = 0;
    private DisplayMetrics displayMetrics = null;
    private Handler mainLooper = null;

    public ConferenceViewController(final View view, Context context, ooVooSdkSampleShowApp application) {
        conferenceView = (ConferenceView) view.findViewById(R.id.conference_view);
        scrollView = (CustomScrollView)view.findViewById(R.id.scroll_view);
        inflater = LayoutInflater.from(context);
        this.application = application;

        mainLooper = new Handler(Looper.getMainLooper());

        final Window window = ((Activity)application.getContext()).getWindow();
        final int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();

        displayMetrics = new DisplayMetrics();
        ((Activity)application.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        conferenceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (!isMeasured) {
                    final View bottomView = view.findViewById(R.id.call_controll_layout);
                    contentViewBottom = bottomView.getMeasuredHeight();

                    final int w = displayMetrics.widthPixels;
                    final int h = displayMetrics.heightPixels - contentViewTop - contentViewBottom;

                    conferenceView.setClientSize(w, h);

                    isMeasured = true;
                }
            }
        });
    }

    public View getConferenceView() {
        return conferenceView;
    }

    public void addParticipant(String userId, String userData) {

        VideoViewItem item = new VideoViewItem(userId, userData);

        items.add(item);

        item.view.setOnClickListener(this);
        if (userId.isEmpty()) {
            conferenceView.addView(item.view, item.videoRender, 0);
        } else {
            conferenceView.addView(item.view, item.videoRender);
        }

        application.bindVideoPanel(item.userId, item.videoRender);

        item.showVideo();

        onResize();
    }

    public void removeParticipant(String userId) {

        VideoViewItem item = findItem(userId);
        if (item != null) {

            application.unbindVideoPanel(item.userId, item.videoRender);
            conferenceView.removeView(item.view);
            items.remove(item);
            if (item.view == clickedView) {
                isFullScreenMode = false;
            }
        }

        onResize();
    }

    public void showNoVideoMessage(String userId) {
        VideoViewItem item = findItem(userId);
        if (item != null) {
            item.showNoVideoMessage();
        }
    }

    public void hideNoVideoMessage(String userId) {
        VideoViewItem item = findItem(userId);
        if (item != null) {
            item.hideNoVideoMessage();
        }
    }

    public void showAvatar(String userId) {
        VideoViewItem item = findItem(userId);
        if (item != null) {
            item.showAvatar();
        }
    }

    public void hideAvatar(String userId) {
        VideoViewItem item = findItem(userId);
        if (item != null) {
            item.hideAvatar();
        }
    }

    private VideoViewItem findItem(String userId) {
        for (VideoViewItem item : items) {
            if (item.userId.equals(userId)) {
                return  item;
            }
        }

        return null;
    }

    public void onResize() {
        int childCount = conferenceView.getChildCount();

        int childWidth = conferenceView.getChildWidth();
        int childHeight = conferenceView.getChildHeight();
        int column = 0, row = 0;
        int x, y;

        for (int i = 0; i < childCount; i++) {
            View v = conferenceView.getChildAt( i );
            if( v.getVisibility() != View.GONE ) {

                x = column * childWidth;
                y = row * childHeight;

                if( isFullScreenMode && v == clickedView ) {

                    conferenceView.layoutView( v, 0, 0, childWidth * 2, childHeight * 2 );
                    v.setVisibility( View.VISIBLE );

                }
                else if( isFullScreenMode ) {

                    conferenceView.layoutView( v, x, y, x + 1, y + 1 );
                    v.setVisibility( View.INVISIBLE );

                }
                else if( ! isFullScreenMode ) {

                    conferenceView.layoutView( v, x, y, x + childWidth, y + childHeight );
                    v.setVisibility( View.VISIBLE );
                }

                column = column + 1;

                if( column >= COLUMNS_NUMBER ) {
                    column = 0;
                    row = row + 1;
                }
            }
        }
    }

    public void onConfigurationChanged() {

        final Window window = ((Activity)application.getContext()).getWindow();
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();

        ((Activity)application.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels - contentViewTop - contentViewBottom;

        conferenceView.onConfigurationChanged(w, h);

        onResize();
    }

    @Override
    public void onClick(View view) {
        isFullScreenMode = !isFullScreenMode;

        conferenceView.setFullScreenMode(isFullScreenMode);

        if (isFullScreenMode) {
            scrollY = scrollView.getScrollY();
            scrollView.setScrollY(0);
            scrollView.setEnableScrolling(false);
        } else {
            scrollView.setScrollY(scrollY);
            scrollView.setEnableScrolling(true);
        }

        clickedView = view;

        mainLooper.post(new Runnable() {
            @Override
            public void run() {
                try {
                    onResize();
                } catch (Exception err) {
                    LogSdk.e(TAG, "onClick " + err);
                }
            }
        });
    }

    public class VideoViewItem {
        private View view = null;
        private TextView displayNameTextView = null;
        private TextView noVideoMessage = null;
        private ImageView avatarImageView = null;
        private VideoRender videoRender = null;
        private final String userId;
        private final String userData;
        private VideoPanel.VideoRenderStateChangeListener listener = null;

        public VideoViewItem(String userId, String userData) {
            this.userId = userId;
            this.userData = userData;

            view = inflater.inflate(R.layout.video_item, conferenceView, false);

            String useCustomRenderValue = application.getSettings().get(ApplicationSettings.UseCustomRender);
            if (useCustomRenderValue != null && Boolean.valueOf(useCustomRenderValue)) {
                videoRender = (VideoRender) view.findViewById(R.id.custom_panel_view);
            } else {
                videoRender = (VideoRender) view.findViewById(R.id.video_panel_view);

                VideoPanel videoPanel = (VideoPanel)videoRender;
                String videoModeValue = application.getSettings().get(ApplicationSettings.VideoModeKey);
                if (videoModeValue != null) {
                    videoPanel.setVideoFittingMode(VideoPanel.FittingMode.fromString(videoModeValue));
                }

                boolean videoOrientationLockValue = Boolean.valueOf(application.getSettings().
                        get(ApplicationSettings.VideoOrientationLockKey));

                videoPanel.setVideoOrientationLocked(videoOrientationLockValue);

                boolean videoOrientationAnimValue = Boolean.valueOf(application.getSettings().
                        get(ApplicationSettings.VideoOrientationAnimKey));

                videoPanel.setVideoOrientationChangesAnimated(videoOrientationAnimValue);
            }

            setVideoRenderStateChangeListener(videoRender);

            displayNameTextView = (TextView) view.findViewById(R.id.display_name_text_view);
            avatarImageView = (ImageView) view.findViewById(R.id.avatar_image_view);
            noVideoMessage = (TextView) view.findViewById(R.id.no_video_message);

            setDisplayName(this.userData);
        }

        public void setVideoRenderStateChangeListener(VideoRender videoRender) {
            if (videoRender != null) {
                this.listener = new VideoPanel.VideoRenderStateChangeListener() {
                    public String toString() {
                        return userId.isEmpty() ? "preview" : userId;
                    }

                    @Override
                    public void onVideoRenderStart() {
                        try {
                            hideAvatar();
                            LogSdk.d(TAG, "VideoControllerWrap -> VideoPanel -> Application  onVideoRenderStop hideAvatar " + toString());

                        } catch (Exception err) {
                            LogSdk.e(TAG, "onVideoRenderStart " + err);
                        }
                    }

                    @Override
                    public void onVideoRenderStop() {
                        try {
                            showAvatar();
                            LogSdk.d(TAG, "VideoControllerWrap -> VideoPanel -> Application  onVideoRenderStop showAvatar " + toString());

                        } catch (Exception err) {
                            LogSdk.e(TAG, "onVideoRenderStop " + err);
                        }

                    }
                };
                setVideoRenderStateChangeListener(videoRender, this.listener);
            }
        }

        public void setVideoRenderStateChangeListener(VideoRender video, VideoPanel.VideoRenderStateChangeListener listener) {
            if (video instanceof CustomVideoPanel) {
                ((CustomVideoPanel)video).setVideoRenderStateChangeListener(listener);
            } else if (video instanceof VideoPanel) {
                ((VideoPanel)video).setVideoRenderStateChangeListener(listener);
            }
        }

        public void showVideo() {
            ((View)videoRender).setVisibility(View.VISIBLE);
        }

        public void showAvatar() {
            avatarImageView.setVisibility(View.VISIBLE);
            ViewGroup vg = (ViewGroup)avatarImageView.getParent() ;
            vg.bringChildToFront(avatarImageView);
            vg.bringChildToFront(displayNameTextView);
            avatarImageView.invalidate();
        }

        public void hideAvatar() {
            avatarImageView.setVisibility(View.INVISIBLE);
        }

        public void setDisplayName(String displayName) {
            displayNameTextView.setText(displayName);
        }

        public void showNoVideoMessage() {
            noVideoMessage.setVisibility(View.VISIBLE);

            ViewGroup vg = (ViewGroup)noVideoMessage.getParent() ;
            vg.bringChildToFront(noVideoMessage);
        }

        public void hideNoVideoMessage() {
            noVideoMessage.setVisibility(View.INVISIBLE);
        }
    }
}
