package org.anddev.andengine.engine;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.input.touch.TouchEvent;

/**
 * @author Nicolas Gramlich
 * @since 22:28:34 - 27.03.2010
 */
public class DoubleSceneSplitScreenEngine extends Engine {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private Scene mSecondScene;
	private Camera mSecondCamera;

	// ===========================================================
	// Constructors
	// ===========================================================

	public DoubleSceneSplitScreenEngine(final EngineOptions pEngineOptions, final Camera pSecondCamera) {
		super(pEngineOptions);
		this.mSecondCamera = pSecondCamera;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	@Deprecated
	
	public Camera getCamera() {
		return super.mCamera;
	}

	public Camera getFirstCamera() {
		return super.mCamera;
	}

	public Camera getSecondCamera() {
		return this.mSecondCamera;
	}

	@Deprecated
	
	public Scene getScene() {
		return super.getScene();
	}

	public Scene getFirstScene() {
		return super.getScene();
	}

	public Scene getSecondScene() {
		return this.mSecondScene;
	}

	@Deprecated
	
	public void setScene(final Scene pScene) {
		super.setScene(pScene);
	}

	public void setFirstScene(final Scene pScene) {
		super.setScene(pScene);
	}

	public void setSecondScene(final Scene pScene) {
		this.mSecondScene = pScene;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	protected void onDrawScene(final GL10 pGL) {
		final Camera firstCamera = this.getFirstCamera();
		final Camera secondCamera = this.getSecondCamera();

		final int surfaceWidth = this.mSurfaceWidth;
		final int surfaceWidthHalf = surfaceWidth >> 1;

		final int surfaceHeight = this.mSurfaceHeight;

		pGL.glEnable(GL10.GL_SCISSOR_TEST); // TODO --> GLHelper

		/* First Screen. With first camera, on the left half of the screens width. */
		{
			pGL.glScissor(0, 0, surfaceWidthHalf, surfaceHeight);
			pGL.glViewport(0, 0, surfaceWidthHalf, surfaceHeight);

			super.mScene.onDraw(pGL, firstCamera);
			firstCamera.onDrawHUD(pGL);
		}

		/* Second Screen. With second camera, on the right half of the screens width. */
		{
			pGL.glScissor(surfaceWidthHalf, 0, surfaceWidthHalf, surfaceHeight);
			pGL.glViewport(surfaceWidthHalf, 0, surfaceWidthHalf, surfaceHeight);

			this.mSecondScene.onDraw(pGL, secondCamera);
			secondCamera.onDrawHUD(pGL);
		}

		pGL.glDisable(GL10.GL_SCISSOR_TEST);
	}

	
	protected Camera getCameraFromSurfaceTouchEvent(final TouchEvent pTouchEvent) {
		if(pTouchEvent.getX() <= this.mSurfaceWidth >> 1) {
			return this.getFirstCamera();
		} else {
			return this.getSecondCamera();
		}
	}

	
	protected Scene getSceneFromSurfaceTouchEvent(final TouchEvent pTouchEvent) {
		if(pTouchEvent.getX() <= this.mSurfaceWidth >> 1) {
			return this.getFirstScene();
		} else {
			return this.getSecondScene();
		}
	}

	
	protected void onUpdateScene(final float pSecondsElapsed) {
		super.onUpdateScene(pSecondsElapsed);
		if(this.mSecondScene != null) {
			this.mSecondScene.onUpdate(pSecondsElapsed);
		}
	}

	
	protected void convertSurfaceToSceneTouchEvent(final Camera pCamera, final TouchEvent pSurfaceTouchEvent) {
		final int surfaceWidthHalf = this.mSurfaceWidth >> 1;

		if(pCamera == this.getFirstCamera()) {
			pCamera.convertSurfaceToSceneTouchEvent(pSurfaceTouchEvent, surfaceWidthHalf, this.mSurfaceHeight);
		} else {
			pSurfaceTouchEvent.offset(-surfaceWidthHalf, 0);
			pCamera.convertSurfaceToSceneTouchEvent(pSurfaceTouchEvent, surfaceWidthHalf, this.mSurfaceHeight);
		}
	}

	
	protected void updateUpdateHandlers(final float pSecondsElapsed) {
		super.updateUpdateHandlers(pSecondsElapsed);
		this.getSecondCamera().onUpdate(pSecondsElapsed);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
