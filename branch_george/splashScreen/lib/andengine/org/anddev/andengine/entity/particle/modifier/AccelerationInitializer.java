package org.anddev.andengine.entity.particle.modifier;

import org.anddev.andengine.entity.particle.Particle;

/**
 * @author Nicolas Gramlich
 * @since 21:21:10 - 14.03.2010
 */
public class AccelerationInitializer extends BaseDoubleValueInitializer {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public AccelerationInitializer(final float pAcceleration) {
		this(pAcceleration, pAcceleration);
	}

	public AccelerationInitializer(final float pAccelerationX, final float pAccelerationY) {
		this(pAccelerationX, pAccelerationX, pAccelerationY, pAccelerationY);
	}

	public AccelerationInitializer(final float pMinAccelerationX, final float pMaxAccelerationX, final float pMinAccelerationY, final float pMaxAccelerationY) {
		super(pMinAccelerationX, pMaxAccelerationX, pMinAccelerationY, pMaxAccelerationY);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public float getMinAccelerationX() {
		return this.mMinValue;
	}

	public float getMaxAccelerationX() {
		return this.mMaxValue;
	}

	public float getMinAccelerationY() {
		return this.mMinValueB;
	}

	public float getMaxAccelerationY() {
		return this.mMaxValueB;
	}

	public void setAccelerationX(final float pAccelerationX) {
		this.mMinValue = pAccelerationX;
		this.mMaxValue = pAccelerationX;
	}

	public void setAccelerationY(final float pAccelerationY) {
		this.mMinValueB = pAccelerationY;
		this.mMaxValueB = pAccelerationY;
	}

	public void setAccelerationX(final float pMinAccelerationX, final float pMaxAccelerationX) {
		this.mMinValue = pMinAccelerationX;
		this.mMaxValue = pMaxAccelerationX;
	}

	public void setAccelerationY(final float pMinAccelerationY, final float pMaxAccelerationY) {
		this.mMinValueB = pMinAccelerationY;
		this.mMaxValueB = pMaxAccelerationY;
	}

	public void setAcceleration(final float pMinAccelerationX, final float pMaxAccelerationX, final float pMinAccelerationY, final float pMaxAccelerationY) {
		this.mMinValue = pMinAccelerationX;
		this.mMaxValue = pMaxAccelerationX;
		this.mMinValueB = pMinAccelerationY;
		this.mMaxValueB = pMaxAccelerationY;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public void onInitializeParticle(final Particle pParticle, final float pValueA, final float pValueB) {
		pParticle.accelerate(pValueA, pValueB);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
