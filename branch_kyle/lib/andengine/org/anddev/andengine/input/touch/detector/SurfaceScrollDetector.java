package org.anddev.andengine.input.touch.detector;

import org.anddev.andengine.input.touch.TouchEvent;

/**
 * @author Nicolas Gramlich
 * @since 16:12:29 - 16.08.2010
 */
public class SurfaceScrollDetector extends ScrollDetector {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public SurfaceScrollDetector(final float pTriggerScrollMinimumDistance, final IScrollDetectorListener pScrollDetectorListener) {
		super(pTriggerScrollMinimumDistance, pScrollDetectorListener);
	}

	public SurfaceScrollDetector(final IScrollDetectorListener pScrollDetectorListener) {
		super(pScrollDetectorListener);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	protected float getX(final TouchEvent pTouchEvent) {
		return pTouchEvent.getMotionEvent().getX();
	}

	
	protected float getY(final TouchEvent pTouchEvent) {
		return pTouchEvent.getMotionEvent().getY();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
