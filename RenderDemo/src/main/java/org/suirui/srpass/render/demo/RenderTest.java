package org.suirui.srpass.render.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import org.suirui.srpass.render.GLES20Support;
import org.suirui.srpass.render.GLFrameRenderer;
import org.suirui.srpass.render.VideoView;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class RenderTest extends Activity {
    private VideoView glSurfaceView;
    private GLFrameRenderer glRenderer;
    private Handler mHandler = new Handler();
    private int width = 640;
    private int height = 360;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (GLES20Support.detectOpenGLES20(this) == false) {
            GLES20Support.getNoSupportGLES20Dialog(this);
        }
        setContentView(R.layout.render_test);
        glSurfaceView = (VideoView) this
                .findViewById(R.id.glrender_surface);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setListener(new VideoView.Listener() {

            @Override
            public boolean onVideoViewTouchEvent(MotionEvent var1) {
                Log.e("", "RenderTest........onVideoViewTouchEvent..........");
                return false;
            }

            @Override
            public void onVideoViewSingleTapConfirmed(MotionEvent var1) {
                Log.e("", "RenderTest........onVideoViewSingleTapConfirmed..........");
            }

            @Override
            public void onVideoViewScroll(MotionEvent var1, MotionEvent var2,
                                          float var3, float var4) {
                Log.e("", "RenderTest........onVideoViewScroll..........");
            }

            @Override
            public boolean onVideoViewHoverEvent(MotionEvent var1) {
                Log.e("", "RenderTest........onVideoViewHoverEvent..........");
                return false;
            }

            @Override
            public void onVideoViewFling(MotionEvent var1, MotionEvent var2,
                                         float var3, float var4) {
                Log.e("", "RenderTest........onVideoViewFling..........");

            }

            @Override
            public void onVideoViewDown(MotionEvent var1) {
                Log.e("", "RenderTest........onVideoViewDown..........");
            }

            @Override
            public void onVideoViewDoubleTap(MotionEvent var1) {
                Log.e("", "RenderTest........onVideoViewDoubleTap..........");
            }

            @Override
            public void beforeSurfaceDestroyed() {
                Log.e("", "RenderTest........beforeSurfaceDestroyed..........");
            }

            @Override
            public void beforeGLContextDestroyed() {
                Log.e("", "RenderTest........beforeGLContextDestroyed..........");
            }
        });
        glRenderer = new GLFrameRenderer(glSurfaceView, this, getDM(this));
        glSurfaceView.setRenderer(glRenderer);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = getResources().openRawResource(
                            R.raw.yuv_360p);
                    int length = in.available();
                    int len = (width * height * 3 / 2);
                    int count = 0;
                    while (true) {
                        if (length > 0) {
                            byte[] buffer = null;
                            if (length >= len) {
                                buffer = new byte[len];
                                length -= (len);
                            } else {
                                buffer = new byte[length];
                                length = 0;
                            }

                            in.read(buffer);

                            copyFrom(buffer, width, height);
                            render();

                        } else {
                            in.close();
                            break;
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }

    private boolean isRender = true;

    public void render() {
        byte[] y = new byte[yuvPlanes[0].remaining()];
        yuvPlanes[0].get(y, 0, y.length);

        byte[] u = new byte[yuvPlanes[1].remaining()];
        yuvPlanes[1].get(u, 0, u.length);

        byte[] v = new byte[yuvPlanes[2].remaining()];
        yuvPlanes[2].get(v, 0, v.length);
        glRenderer.update(width, height, false);
        glRenderer.update(y, u, v);
    }

    public DisplayMetrics getDM(Context context) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics;
    }

    public byte[] getFromRaw() {
        try {
            InputStream in = getResources().openRawResource(R.raw.yuv_360p); // 从Resources中raw中的文件获取输入

            int length = in.available();
            byte[] buffer = new byte[length];

            in.read(buffer);
            in.close();

            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ByteBuffer[] yuvPlanes;

    public void copyFrom(byte[] yuvData, int width, int height) {

        int[] yuvStrides = {width, width / 2, width / 2};

        if (yuvPlanes == null) {
            yuvPlanes = new ByteBuffer[3];
            yuvPlanes[0] = ByteBuffer.allocateDirect(yuvStrides[0] * height);
            yuvPlanes[1] = ByteBuffer
                    .allocateDirect(yuvStrides[1] * height / 2);
            yuvPlanes[2] = ByteBuffer
                    .allocateDirect(yuvStrides[2] * height / 2);
        }
        int planeSize = 0;
        if (yuvData.length < width * height * 3 / 2) {
            planeSize = yuvData.length * 2 / 3;
        } else {
            planeSize = width * height;
        }
        ByteBuffer[] planes = new ByteBuffer[3];
        planes[0] = ByteBuffer.wrap(yuvData, 0, planeSize);
        planes[1] = ByteBuffer.wrap(yuvData, planeSize, planeSize / 4);
        planes[2] = ByteBuffer.wrap(yuvData, planeSize + planeSize / 4,
                planeSize / 4);
        for (int i = 0; i < 3; i++) {
            yuvPlanes[i].position(0);
            yuvPlanes[i].put(planes[i]);
            yuvPlanes[i].position(0);
            yuvPlanes[i].limit(yuvPlanes[i].capacity());
        }
    }

    /**
     * Remember to resume the glSurface
     */
    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    /**
     * Also pause the glSurface
     */
    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isRender = false;
    }
}
