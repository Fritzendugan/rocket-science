package org.anddev.andengine.entity.shape.modifier.ease;

/**
 * @author Gil, Nicolas Gramlich
 * @since 16:50:40 - 26.07.2010
 */
public class EaseLinear implements IEaseFunction {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static EaseLinear INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private EaseLinear() {
	}

	public static EaseLinear getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new EaseLinear();
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
		return pMaxValue * pSecondsElapsed / pDuration + pMinValue;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
