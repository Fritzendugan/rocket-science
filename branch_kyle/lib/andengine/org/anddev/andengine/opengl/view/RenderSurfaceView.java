package org.anddev.andengine.opengl.view;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.opengl.util.GLHelper;
import org.anddev.andengine.util.Debug;

import android.content.Context;

/**
 * @author Nicolas Gramlich
 * @since 11:57:29 - 08.03.2010
 */
public class RenderSurfaceView extends GLSurfaceView {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final Renderer mRenderer;

	// ===========================================================
	// Constructors
	// ===========================================================

	public RenderSurfaceView(final Context pContext, final Engine pEngine) {
		super(pContext);
		this.setOnTouchListener(pEngine);
		this.mRenderer = new Renderer(pEngine);
		//		setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
	}

	public void applyRenderer() {
		this.setRenderer(this.mRenderer);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	/**
	 * @author Nicolas Gramlich
	 * @since 11:45:59 - 08.03.2010
	 */
	public static class Renderer implements GLSurfaceView.Renderer {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		private final Engine mEngine;

		// ===========================================================
		// Constructors
		// ===========================================================

		public Renderer(final Engine pEngine) {
			this.mEngine = pEngine;
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		
		public void onSurfaceChanged(final GL10 pGL, final int pWidth, final int pHeight) {
			Debug.d("onSurfaceChanged");
			this.mEngine.setSurfaceSize(pWidth, pHeight);
			pGL.glViewport(0, 0, pWidth, pHeight);
			pGL.glLoadIdentity();
		}

		
		public void onSurfaceCreated(final GL10 pGL, final EGLConfig pConfig) {
			Debug.d("onSurfaceCreated");
			GLHelper.reset(pGL);

			GLHelper.setPerspectiveCorrectionHintFastest(pGL);
//			pGL.glEnable(GL10.GL_POLYGON_SMOOTH); 
//			GLHelper.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST);
//			GLHelper.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST); 

			GLHelper.setShadeModelFlat(pGL);

			GLHelper.disableLightning(pGL);
			GLHelper.disableDither(pGL);
			GLHelper.disableDepthTest(pGL);
			GLHelper.disableMultisample(pGL);

			GLHelper.enableBlend(pGL);
			GLHelper.enableTextures(pGL);
			GLHelper.enableTexCoordArray(pGL);
			GLHelper.enableVertexArray(pGL);

			GLHelper.enableCulling(pGL);
			pGL.glFrontFace(GL10.GL_CCW);
			pGL.glCullFace(GL10.GL_BACK);

			GLHelper.enableExtensions(pGL, this.mEngine.getEngineOptions().getRenderOptions());
		}

		
		public void onDrawFrame(final GL10 pGL) {
			try {
				this.mEngine.onDrawFrame(pGL);
			} catch (final InterruptedException e) {
				Debug.e("GLThread interrupted!", e);
			}
		}

		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}
