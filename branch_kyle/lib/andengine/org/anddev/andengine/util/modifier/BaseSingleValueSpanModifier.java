package org.anddev.andengine.util.modifier;

import org.anddev.andengine.entity.shape.modifier.ease.IEaseFunction;

/**
 * @author Nicolas Gramlich
 * @since 23:29:22 - 19.03.2010
 */
public abstract class BaseSingleValueSpanModifier<T> extends BaseDurationModifier<T> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final float mFromValue;
	private final float mValueSpan;

	protected final IEaseFunction mEaseFunction;

	// ===========================================================
	// Constructors
	// ===========================================================

	public BaseSingleValueSpanModifier(final float pDuration, final float pFromValue, final float pToValue) {
		this(pDuration, pFromValue, pToValue, null, IEaseFunction.DEFAULT);
	}

	public BaseSingleValueSpanModifier(final float pDuration, final float pFromValue, final float pToValue, final IEaseFunction pEaseFunction) {
		this(pDuration, pFromValue, pToValue, null, pEaseFunction);
	}

	public BaseSingleValueSpanModifier(final float pDuration, final float pFromValue, final float pToValue, final IModifierListener<T> pModiferListener) {
		this(pDuration, pFromValue, pToValue, IEaseFunction.DEFAULT);
	}

	public BaseSingleValueSpanModifier(final float pDuration, final float pFromValue, final float pToValue, final IModifierListener<T> pModiferListener, final IEaseFunction pEaseFunction) {
		super(pDuration, pModiferListener);
		this.mFromValue = pFromValue;
		this.mValueSpan = pToValue - pFromValue;
		this.mEaseFunction = pEaseFunction;
	}

	protected BaseSingleValueSpanModifier(final BaseSingleValueSpanModifier<T> pBaseSingleValueSpanModifier) {
		super(pBaseSingleValueSpanModifier);
		this.mFromValue = pBaseSingleValueSpanModifier.mFromValue;
		this.mValueSpan = pBaseSingleValueSpanModifier.mValueSpan;
		this.mEaseFunction = pBaseSingleValueSpanModifier.mEaseFunction;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	protected abstract void onSetInitialValue(final T pItem, final float pValue);
	protected abstract void onSetValue(final T pItem, final float pPercentageDone, final float pValue);

	
	protected void onManagedInitialize(final T pItem) {
		this.onSetInitialValue(pItem, this.mFromValue);
	}

	
	protected void onManagedUpdate(final float pSecondsElapsed, final T pItem) {
		final float percentageDone = this.mEaseFunction.getPercentageDone(this.getTotalSecondsElapsed(), this.mDuration, 0, 1);

		this.onSetValue(pItem, percentageDone, this.mFromValue + percentageDone * this.mValueSpan);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
