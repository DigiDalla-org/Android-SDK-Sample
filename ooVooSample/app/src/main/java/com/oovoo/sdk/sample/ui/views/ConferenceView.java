package com.oovoo.sdk.sample.ui.views;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.oovoo.sdk.interfaces.VideoRender;
import com.oovoo.sdk.oovoosdksampleshow.R;

/**
 * Created by oovoo on 11/3/15.
 */
public class ConferenceView extends FrameLayout {

    private final static String TAG = "ConferenceView";
    private final static int PAD_H = 0, PAD_V = 0;

    private int width = 0;
    private int height = 0;
    private int childWidth = 0;
    private int childHeight = 0;
    private boolean isFullScreenMode = false;

    public ConferenceView(Context context) {
        super(context);
    }

    public ConferenceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ConferenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFullScreenMode(boolean isFullScreenMode) {
        this.isFullScreenMode = isFullScreenMode;
    }

    public void setClientSize(int w, int h) {
        width = w;
        height = h;
    }

    public void addView(View v, VideoRender videoRender) {
        super.addView(v);

        childWidth = width / 2;
        childHeight = height / 2;

        ((View) videoRender).setTag(new Point(childWidth, childHeight));

        v.setTag(videoRender);
    }

    public void addView(View v, VideoRender videoRender, int index) {
        super.addView(v, index);

        childWidth = width / 2;
        childHeight = height / 2;

        ((View) videoRender).setTag(new Point(childWidth, childHeight));

        v.setTag(videoRender);
    }

    public int getChildWidth() {
        return childWidth;
    }

    public int getChildHeight() {
        return childHeight;
    }

    public void removeView(View v) {
        super.removeView(v);
    }

    public void layoutView(View v, int x, int y, int width, int height) {
        LayoutParams p = (LayoutParams) v.getLayoutParams();
        if( p == null ) {
            p = new FrameLayout.LayoutParams( width - x, height - y );
        }

        p.leftMargin = x;
        p.topMargin = y;
        p.width = width - x;
        p.height = height - y;
        v.setLayoutParams( p );

        View videoRender = (View) v.getTag();
        videoRender.setTag(new Point(p.width, p.height));

        FrameLayout.LayoutParams rp = (FrameLayout.LayoutParams) videoRender.getLayoutParams();
        rp.width = p.width;
        rp.height = p.height;
        videoRender.setLayoutParams( rp );

        TextView displayNameTextView = (TextView) v.findViewById( R.id.display_name_text_view );
        FrameLayout.LayoutParams dp = (FrameLayout.LayoutParams) displayNameTextView.getLayoutParams();
        dp.width = rp.width;
        displayNameTextView.setLayoutParams( dp );

        ImageView avatarImageView = (ImageView) v.findViewById( R.id.avatar_image_view );
        FrameLayout.LayoutParams p1 = (FrameLayout.LayoutParams) avatarImageView.getLayoutParams();
        p1.width = rp.width;
        p1.height = rp.height;
        avatarImageView.setLayoutParams( p1 );
    }

    public void onConfigurationChanged(int w, int h) {

        childWidth = w / 2;
        childHeight = h / 2;
    }

}
