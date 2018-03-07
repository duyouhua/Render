//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.suirui.srpass.render;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class VideoView extends SRGLSurfaceView implements OnGestureListener,
		OnDoubleTapListener {
	private static String TAG = VideoView.class.getSimpleName();
	private static final boolean DEBUG = false;
	private Listener mListener;
	private GestureDetector mGestureDetector;

	public VideoView(Context context) {
		super(context);
		this.init(context, false, 0, 0);
	}

	public VideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context, false, 0, 0);
	}
	public VideoView(Context context, boolean translucent, int depth,
			int stencil) {
		super(context);
		this.init(context, translucent, depth, stencil);
	}

	private void init(Context context, boolean translucent, int depth,
			int stencil) {
		if (translucent) {
			this.getHolder().setFormat(-3);
		}

		this.setEGLContextFactory(new ContextFactory(this));
		this.setEGLConfigChooser(translucent ? new ConfigChooser(8,
				8, 8, 8, depth, stencil) : new ConfigChooser(5, 6, 5,
				0, depth, stencil));
		if (!this.isInEditMode()) {
			this.mGestureDetector = new GestureDetector(context, this);
			this.mGestureDetector.setOnDoubleTapListener(this);
			this.mGestureDetector.setIsLongpressEnabled(false);
		}

	}

	public void setListener(Listener l) {
		this.mListener = l;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		if (this.mListener != null) {
			this.mListener.beforeSurfaceDestroyed();
		}

		super.surfaceDestroyed(holder);
	}

	public boolean dispatchHoverEvent(MotionEvent event) {
		return this.mListener != null
				&& this.mListener.onVideoViewHoverEvent(event) ? true : super
				.dispatchHoverEvent(event);
	}

	public boolean onTouchEvent(MotionEvent event) {
		return this.mListener != null
				&& this.mListener.onVideoViewTouchEvent(event) ? true
				: this.mGestureDetector.onTouchEvent(event);
	}

	private void beforeGLContextDestroyed() {
		if (this.mListener != null) {
			this.mListener.beforeGLContextDestroyed();
		}

	}

	public boolean onDown(MotionEvent e) {
		if (this.mListener != null) {
			this.mListener.onVideoViewDown(e);
		}

		return true;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (this.mListener != null) {
			this.mListener.onVideoViewFling(e1, e2, velocityX, velocityY);
		}

		return true;
	}

	public void onLongPress(MotionEvent e) {
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (this.mListener != null) {
			this.mListener.onVideoViewScroll(e1, e2, distanceX, distanceY);
		}

		return true;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return true;
	}

	public boolean onDoubleTap(MotionEvent e) {
		if (this.mListener != null) {
			this.mListener.onVideoViewDoubleTap(e);
		}

		return true;
	}

	public boolean onDoubleTapEvent(MotionEvent e) {
		return true;
	}

	public boolean onSingleTapConfirmed(MotionEvent e) {
		if (this.mListener != null) {
			this.mListener.onVideoViewSingleTapConfirmed(e);
		}

		return true;
	}

	private static void checkEglError(String prompt, EGL10 egl) {
		while (egl.eglGetError() != 12288) {
			;
		}

	}

	private static class ConfigChooser implements EGLConfigChooser {
		private static int EGL_OPENGL_ES2_BIT = 4;
		private static int[] s_configAttribs2;
		protected int mRedSize;
		protected int mGreenSize;
		protected int mBlueSize;
		protected int mAlphaSize;
		protected int mDepthSize;
		protected int mStencilSize;
		private int[] mValue = new int[1];

		public ConfigChooser(int r, int g, int b, int a, int depth, int stencil) {
			this.mRedSize = r;
			this.mGreenSize = g;
			this.mBlueSize = b;
			this.mAlphaSize = a;
			this.mDepthSize = depth;
			this.mStencilSize = stencil;
		}

		public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
			int[] num_config = new int[1];
			egl.eglChooseConfig(display, s_configAttribs2, (EGLConfig[]) null,
					0, num_config);
			int numConfigs = num_config[0];
			if (numConfigs <= 0) {
				throw new IllegalArgumentException(
						"No configs match configSpec");
			} else {
				EGLConfig[] configs = new EGLConfig[numConfigs];
				egl.eglChooseConfig(display, s_configAttribs2, configs,
						numConfigs, num_config);
				return this.chooseConfig(egl, display, configs);
			}
		}

		public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
				EGLConfig[] configs) {
			EGLConfig[] var4 = configs;
			int var5 = configs.length;

			for (int var6 = 0; var6 < var5; ++var6) {
				EGLConfig config = var4[var6];
				int d = this.findConfigAttrib(egl, display, config, 12325, 0);
				int s = this.findConfigAttrib(egl, display, config, 12326, 0);
				if (d >= this.mDepthSize && s >= this.mStencilSize) {
					int r = this.findConfigAttrib(egl, display, config, 12324,
							0);
					int g = this.findConfigAttrib(egl, display, config, 12323,
							0);
					int b = this.findConfigAttrib(egl, display, config, 12322,
							0);
					int a = this.findConfigAttrib(egl, display, config, 12321,
							0);
					if (r == this.mRedSize && g == this.mGreenSize
							&& b == this.mBlueSize && a == this.mAlphaSize) {
						return config;
					}
				}
			}

			return null;
		}

		private int findConfigAttrib(EGL10 egl, EGLDisplay display,
				EGLConfig config, int attribute, int defaultValue) {
			return egl.eglGetConfigAttrib(display, config, attribute,
					this.mValue) ? this.mValue[0] : defaultValue;
		}

		private void printConfigs(EGL10 egl, EGLDisplay display,
				EGLConfig[] configs) {
			int numConfigs = configs.length;

			for (int i = 0; i < numConfigs; ++i) {
				this.printConfig(egl, display, configs[i]);
			}

		}

		private void printConfig(EGL10 egl, EGLDisplay display, EGLConfig config) {
			int[] attributes = new int[] { 12320, 12321, 12322, 12323, 12324,
					12325, 12326, 12327, 12328, 12329, 12330, 12331, 12332,
					12333, 12334, 12335, 12336, 12337, 12338, 12339, 12340,
					12343, 12342, 12341, 12345, 12346, 12347, 12348, 12349,
					12350, 12351, 12352, 12354 };
			String[] names = new String[] { "EGL_BUFFER_SIZE",
					"EGL_ALPHA_SIZE", "EGL_BLUE_SIZE", "EGL_GREEN_SIZE",
					"EGL_RED_SIZE", "EGL_DEPTH_SIZE", "EGL_STENCIL_SIZE",
					"EGL_CONFIG_CAVEAT", "EGL_CONFIG_ID", "EGL_LEVEL",
					"EGL_MAX_PBUFFER_HEIGHT", "EGL_MAX_PBUFFER_PIXELS",
					"EGL_MAX_PBUFFER_WIDTH", "EGL_NATIVE_RENDERABLE",
					"EGL_NATIVE_VISUAL_ID", "EGL_NATIVE_VISUAL_TYPE",
					"EGL_PRESERVED_RESOURCES", "EGL_SAMPLES",
					"EGL_SAMPLE_BUFFERS", "EGL_SURFACE_TYPE",
					"EGL_TRANSPARENT_TYPE", "EGL_TRANSPARENT_RED_VALUE",
					"EGL_TRANSPARENT_GREEN_VALUE",
					"EGL_TRANSPARENT_BLUE_VALUE", "EGL_BIND_TO_TEXTURE_RGB",
					"EGL_BIND_TO_TEXTURE_RGBA", "EGL_MIN_SWAP_INTERVAL",
					"EGL_MAX_SWAP_INTERVAL", "EGL_LUMINANCE_SIZE",
					"EGL_ALPHA_MASK_SIZE", "EGL_COLOR_BUFFER_TYPE",
					"EGL_RENDERABLE_TYPE", "EGL_CONFORMANT" };
			int[] value = new int[1];

			for (int i = 0; i < attributes.length; ++i) {
				int attribute = attributes[i];
				String var10000 = names[i];
				if (!egl.eglGetConfigAttrib(display, config, attribute, value)) {
					while (egl.eglGetError() != 12288) {
						;
					}
				}
			}

		}

		static {
			s_configAttribs2 = new int[] { 12324, 4, 12323, 4, 12322, 4, 12352,
					EGL_OPENGL_ES2_BIT, 12344 };
		}
	}

	private static class ContextFactory implements EGLContextFactory {
		private static int EGL_CONTEXT_CLIENT_VERSION = 12440;
		private VideoView mVideoView;

		public ContextFactory(VideoView videoView) {
			this.mVideoView = videoView;
		}

		public EGLContext createContext(EGL10 egl, EGLDisplay display,
				EGLConfig eglConfig) {
			VideoView.checkEglError("Before eglCreateContext", egl);
			int[] attrib_list = new int[] { EGL_CONTEXT_CLIENT_VERSION, 2,
					12344 };
			EGLContext context = egl.eglCreateContext(display, eglConfig,
					EGL10.EGL_NO_CONTEXT, attrib_list);
			VideoView.checkEglError("After eglCreateContext", egl);
			return context;
		}

		public void destroyContext(EGL10 egl, EGLDisplay display,
				EGLContext context) {
			if (this.mVideoView != null) {
				this.mVideoView.beforeGLContextDestroyed();
			}

			egl.eglDestroyContext(display, context);
		}
	}

	public interface Listener {
		void beforeSurfaceDestroyed();

		void beforeGLContextDestroyed();

		void onVideoViewDoubleTap(MotionEvent var1);

		void onVideoViewScroll(MotionEvent var1, MotionEvent var2, float var3,
							   float var4);

		void onVideoViewFling(MotionEvent var1, MotionEvent var2, float var3,
							  float var4);

		void onVideoViewSingleTapConfirmed(MotionEvent var1);

		void onVideoViewDown(MotionEvent var1);

		boolean onVideoViewTouchEvent(MotionEvent var1);

		boolean onVideoViewHoverEvent(MotionEvent var1);
	}
}
