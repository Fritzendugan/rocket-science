package org.anddev.andengine.entity.sprite;

import java.util.Arrays;

import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.opengl.vertex.RectangleVertexBuffer;
import org.anddev.andengine.util.MathUtils;
import org.anddev.andengine.util.constants.TimeConstants;

/**
 * @author Nicolas Gramlich
 * @since 15:25:46 - 10.03.2010
 */
public class AnimatedSprite extends TiledSprite implements TimeConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int LOOP_CONTINUOUS = -1;

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean mAnimationRunning;

	private long mAnimationProgress;
	private long mAnimationDuration;
	private long[] mFrameEndsInNanoseconds;

	private int mFirstTileIndex;
	private int mInitialLoopCount;
	private int mLoopCount;
	private IAnimationListener mAnimationListener;

	private int mFrameCount;

	// ===========================================================
	// Constructors
	// ===========================================================

	public AnimatedSprite(final float pX, final float pY, final TiledTextureRegion pTiledTextureRegion) {
		super(pX, pY, pTiledTextureRegion);
	}

	public AnimatedSprite(final float pX, final float pY, final float pTileWidth, final float pTileHeight, final TiledTextureRegion pTiledTextureRegion) {
		super(pX, pY, pTileWidth, pTileHeight, pTiledTextureRegion);
	}

	public AnimatedSprite(final float pX, final float pY, final TiledTextureRegion pTiledTextureRegion, final RectangleVertexBuffer pRectangleVertexBuffer) {
		super(pX, pY, pTiledTextureRegion, pRectangleVertexBuffer);
	}

	public AnimatedSprite(final float pX, final float pY, final float pTileWidth, final float pTileHeight, final TiledTextureRegion pTiledTextureRegion, final RectangleVertexBuffer pRectangleVertexBuffer) {
		super(pX, pY, pTileWidth, pTileHeight, pTiledTextureRegion, pRectangleVertexBuffer);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean isAnimationRunning() {
		return this.mAnimationRunning;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	protected void onManagedUpdate(final float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		if(this.mAnimationRunning) {
			final long nanoSecondsElapsed = (long)(pSecondsElapsed * NANOSECONDSPERSECOND);
			this.mAnimationProgress += nanoSecondsElapsed;

			if(this.mAnimationProgress > this.mAnimationDuration) {
				this.mAnimationProgress %= this.mAnimationDuration;
				if(this.mInitialLoopCount != LOOP_CONTINUOUS) {
					this.mLoopCount--;
				}
			}

			if(this.mInitialLoopCount == LOOP_CONTINUOUS || this.mLoopCount >= 0) {
				final int currentFrameIndex = this.calculateCurrentFrameIndex();
				this.setCurrentTileIndex(this.mFirstTileIndex + currentFrameIndex);
			} else {
				this.mAnimationRunning = false;
				if(this.mAnimationListener != null) {
					this.mAnimationListener.onAnimationEnd(this);
				}
			}
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void stopAnimation() {
		this.mAnimationRunning = false;
	}

	public void stopAnimation(final int pTileIndex) {
		this.mAnimationRunning = false;
		this.setCurrentTileIndex(pTileIndex);
	}

	private int calculateCurrentFrameIndex() {
		final long animationProgress = this.mAnimationProgress;
		final long[] frameEnds = this.mFrameEndsInNanoseconds;
		final int frameCount = this.mFrameCount;
		for(int i = 0; i < frameCount; i++) {
			if(frameEnds[i] > animationProgress) {
				return i;
			}
		}

		return frameCount - 1;
	}

	public AnimatedSprite animate(final long pFrameDurationEach) {
		return this.animate(pFrameDurationEach, true);
	}

	public AnimatedSprite animate(final long pFrameDurationEach, final boolean pLoop) {
		return this.animate(pFrameDurationEach, (pLoop) ? LOOP_CONTINUOUS : 0, null);
	}

	public AnimatedSprite animate(final long pFrameDurationEach, final int pLoopCount) {
		return this.animate(pFrameDurationEach, pLoopCount, null);
	}

	public AnimatedSprite animate(final long pFrameDurationEach, final boolean pLoop, final IAnimationListener pAnimationListener) {
		return this.animate(pFrameDurationEach, (pLoop) ? LOOP_CONTINUOUS : 0, pAnimationListener);
	}

	public AnimatedSprite animate(final long pFrameDurationEach, final int pLoopCount, final IAnimationListener pAnimationListener) {
		final long[] frameDurations = new long[this.getTextureRegion().getTileCount()];
		Arrays.fill(frameDurations, pFrameDurationEach);
		return this.animate(frameDurations, pLoopCount, pAnimationListener);
	}

	public AnimatedSprite animate(final long[] pFrameDurations) {
		return this.animate(pFrameDurations, true);
	}

	public AnimatedSprite animate(final long[] pFrameDurations, final boolean pLoop) {
		return this.animate(pFrameDurations, (pLoop) ? LOOP_CONTINUOUS : 0, null);
	}

	public AnimatedSprite animate(final long[] pFrameDurations, final int pLoopCount) {
		return this.animate(pFrameDurations, pLoopCount, null);
	}

	public AnimatedSprite animate(final long[] pFrameDurations, final boolean pLoop, final IAnimationListener pAnimationListener) {
		return this.animate(pFrameDurations, (pLoop) ? LOOP_CONTINUOUS : 0, pAnimationListener);
	}

	public AnimatedSprite animate(final long[] pFrameDurations, final int pLoopCount, final IAnimationListener pAnimationListener) {
		return this.animate(pFrameDurations, 0, this.getTextureRegion().getTileCount() - 1, pLoopCount, pAnimationListener);
	}

	public AnimatedSprite animate(final long[] pFrameDurations, final int pFirstTileIndex, final int pLastTileIndex, final boolean pLoop) {
		return this.animate(pFrameDurations, pFirstTileIndex, pLastTileIndex, (pLoop) ? LOOP_CONTINUOUS : 0, null);
	}

	public AnimatedSprite animate(final long[] pFrameDurations, final int pFirstTileIndex, final int pLastTileIndex, final int pLoopCount) {
		return this.animate(pFrameDurations, pFirstTileIndex, pLastTileIndex, pLoopCount, null);
	}

	/**
	 * @param pFrameDurations must have the same length as pFirstTileIndex to pLastTileIndex.
	 * @param pFirstTileIndex
	 * @param pLastTileIndex
	 * @param pLoopCount
	 * @param pAnimationListener
	 */
	public AnimatedSprite animate(final long[] pFrameDurations, final int pFirstTileIndex, final int pLastTileIndex, final int pLoopCount, final IAnimationListener pAnimationListener) {
		if(pLastTileIndex - pFirstTileIndex < 1) {
			throw new IllegalArgumentException("An animation needs at least two tiles to animate between.");
		}

		final int frameCount = (pLastTileIndex - pFirstTileIndex) + 1;
		if(pFrameDurations.length != frameCount) {
			throw new IllegalArgumentException("pFrameDurations must have the same length as pFirstTileIndex to pLastTileIndex.");
		}
		this.mFrameCount = frameCount;

		this.mAnimationListener = pAnimationListener;

		this.mInitialLoopCount = pLoopCount;
		this.mLoopCount = pLoopCount;
		this.mFirstTileIndex = pFirstTileIndex;


		if(this.mFrameEndsInNanoseconds == null || this.mFrameCount > this.mFrameEndsInNanoseconds.length) {
			this.mFrameEndsInNanoseconds = new long[this.mFrameCount];
		}

		final long[] frameEndsInNanoseconds = this.mFrameEndsInNanoseconds;
		MathUtils.arraySumInto(pFrameDurations, frameEndsInNanoseconds, NANOSECONDSPERMILLISECOND);

		final long lastFrameEnd = frameEndsInNanoseconds[this.mFrameCount - 1];
		this.mAnimationDuration = lastFrameEnd;

		this.mAnimationProgress = 0;
		this.mAnimationRunning = true;

		return this;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface IAnimationListener {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		public void onAnimationEnd(final AnimatedSprite pAnimatedSprite);
	}
}
