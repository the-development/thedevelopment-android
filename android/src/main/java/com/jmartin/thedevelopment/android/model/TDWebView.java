package com.jmartin.thedevelopment.android.model;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by jeff on 2014-03-26.
 */
public class TDWebView extends WebView {

    private OnScrollChangedCallback onScrollChangedCallback;

    public TDWebView(Context context) {
        super(context);
    }

    public TDWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TDWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
        if (onScrollChangedCallback != null) onScrollChangedCallback.onScroll(horiz, vert, oldHoriz, oldVert);
    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return onScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(OnScrollChangedCallback onScrollChangedCallback) {
        this.onScrollChangedCallback = onScrollChangedCallback;
    }

    public static interface OnScrollChangedCallback {
        public void onScroll(int horiz, int vert, int oldHoriz, int oldVert);
    }
}
