package org.anddev.andengine.entity.util;

import org.anddev.andengine.engine.handler.IUpdateHandler;

/**
 * @author Nicolas Gramlich
 * @since 19:52:31 - 09.03.2010
 */
public class FPSCounter implements IUpdateHandler {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected float mSecondsElapsed;
	protected int mFrames;
	protected int mSmoothFramesCount;
	protected float mFPS;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public FPSCounter(int pSmoothFramesCount)
	{
		this.mSmoothFramesCount = pSmoothFramesCount;
		this.mFPS = 0;
	}
	
	public FPSCounter()
	{
		this.mSmoothFramesCount = 0;
		this.mFPS = 0;
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public float getFPS() {
		return this.mFPS;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public void onUpdate(final float pSecondsElapsed) {
		this.mFrames++;
		this.mSecondsElapsed += pSecondsElapsed;
		
		//Average over mSmoothFPSLength frames
		if( this.mSmoothFramesCount > 0 && this.mFrames > this.mSmoothFramesCount )
		{
			this.mFPS = this.mFrames / this.mSecondsElapsed;
			this.reset();
		}
	}

	
	public void reset() {
		this.mFrames = 0;
		this.mSecondsElapsed = 0;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
