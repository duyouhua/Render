package org.suirui.srpass.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;


public class GLFrameSurface extends GLSurfaceView {
    public GLFrameSurface(Context context) {
        this(context, null);
    }

    public GLFrameSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
