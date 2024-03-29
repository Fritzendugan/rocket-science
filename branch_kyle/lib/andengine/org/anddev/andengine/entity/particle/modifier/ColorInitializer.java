package org.anddev.andengine.entity.particle.modifier;

import org.anddev.andengine.entity.particle.Particle;


/**
 * @author Nicolas Gramlich
 * @since 10:17:42 - 29.06.2010
 */
public class ColorInitializer extends BaseTripleValueInitializer {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public ColorInitializer(final float pRed, final float pGreen, final float pBlue) {
		super(pRed, pRed, pGreen, pGreen, pBlue, pBlue);
	}

	public ColorInitializer(final float pMinRed, final float pMaxRed, final float pMinGreen, final float pMaxGreen, final float pMinBlue, final float pMaxBlue) {
		super(pMinRed, pMaxRed, pMinGreen, pMaxGreen, pMinBlue, pMaxBlue);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	protected void onInitializeParticle(final Particle pParticle, final float pValueA, final float pValueB, final float pValueC) {
		pParticle.setColor(pValueA, pValueB, pValueC);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
