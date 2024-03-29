package org.anddev.andengine.entity.shape.modifier.ease;

import org.anddev.andengine.util.constants.MathConstants;

import android.util.FloatMath;

/**
 * @author Gil, Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
public class EaseSineOut implements IEaseFunction, MathConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static EaseSineOut INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private EaseSineOut() {
	}

	public static EaseSineOut getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new EaseSineOut();
		}
		return INSTANCE;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public float getPercentageDone(final float pSecondsElapsed, final float pDuration, final float pMinValue, final float pMaxValue) {
		return (pMaxValue * FloatMath.sin(pSecondsElapsed / pDuration * _HALF_PI) + pMinValue);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
