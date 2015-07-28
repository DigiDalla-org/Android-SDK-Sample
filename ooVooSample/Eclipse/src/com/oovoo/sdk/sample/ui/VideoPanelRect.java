package com.oovoo.sdk.sample.ui;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import com.oovoo.sdk.api.ui.VideoPanel;

public class VideoPanelRect extends VideoPanel {
	
	public VideoPanelRect(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        int height = ((Point) getTag()).y;
        
        setMeasuredDimension(getMeasuredWidth(), height);
    }
}
