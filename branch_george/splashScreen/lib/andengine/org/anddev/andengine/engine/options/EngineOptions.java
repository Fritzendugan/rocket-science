package org.anddev.andengine.engine.options;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.resolutionpolicy.IResolutionPolicy;
import org.anddev.andengine.opengl.texture.source.ITextureSource;

/**
 * @author  Nicolas Gramlich
 * @since  15:59:52 - 09.03.2010
 */
public class EngineOptions {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final boolean mFullscreen;
	private final ScreenOrientation mScreenOrientation;
	private final IResolutionPolicy mResolutionPolicy;
	private final Camera mCamera;

	private final TouchOptions mTouchOptions = new TouchOptions();
	private final RenderOptions mRenderOptions = new RenderOptions();

	private ITextureSource mLoadingScreenTextureSource;
	private boolean mNeedsSound;
	private boolean mNeedsMusic;
	private WakeLockOptions mWakeLockOptions = WakeLockOptions.SCREEN_BRIGHT;

	// ===========================================================
	// Constructors
	// ===========================================================

	public EngineOptions(final boolean pFullscreen, final ScreenOrientation pScreenOrientation, final IResolutionPolicy pResolutionPolicy, final Camera pCamera) {
		this.mFullscreen = pFullscreen;
		this.mScreenOrientation = pScreenOrientation;
		this.mResolutionPolicy = pResolutionPolicy;
		this.mCamera = pCamera;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public TouchOptions getTouchOptions() {
		return this.mTouchOptions;
	}

	public RenderOptions getRenderOptions() {
		return this.mRenderOptions;
	}

	public boolean isFullscreen() {
		return this.mFullscreen;
	}

	public ScreenOrientation getScreenOrientation() {
		return this.mScreenOrientation;
	}

	public IResolutionPolicy getResolutionPolicy() {
		return this.mResolutionPolicy;
	}

	public Camera getCamera() {
		return this.mCamera;
	}

	public boolean hasLoadingScreen() {
		return this.mLoadingScreenTextureSource != null;
	}

	public ITextureSource getLoadingScreenTextureSource() {
		return this.mLoadingScreenTextureSource;
	}

	public EngineOptions setLoadingScreenTextureSource(final ITextureSource pLoadingScreenTextureSource) {
		this.mLoadingScreenTextureSource = pLoadingScreenTextureSource;
		return this;
	}

	public boolean needsSound() {
		return this.mNeedsSound;
	}

	public EngineOptions setNeedsSound(final boolean pNeedsSound) {
		this.mNeedsSound = pNeedsSound;
		return this;
	}

	public boolean needsMusic() {
		return this.mNeedsMusic;
	}

	public EngineOptions setNeedsMusic(final boolean pNeedsMusic) {
		this.mNeedsMusic = pNeedsMusic;
		return this;
	}

	public WakeLockOptions getWakeLockOptions() {
		return this.mWakeLockOptions;
	}

	public EngineOptions setWakeLockOptions(final WakeLockOptions pWakeLockOptions) {
		this.mWakeLockOptions = pWakeLockOptions;
		return this;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static enum ScreenOrientation {
		// ===========================================================
		// Elements
		// ===========================================================

		LANDSCAPE, PORTRAIT
	}
}