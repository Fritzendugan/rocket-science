package org.anddev.andengine.entity.shape.modifier;

import org.anddev.andengine.entity.shape.modifier.ease.IEaseFunction;


/**
 * @author Nicolas Gramlich
 * @since 19:03:12 - 08.06.2010
 */
public class FadeOutModifier extends AlphaModifier {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public FadeOutModifier(final float pDuration) {
		super(pDuration, 1.0f, 0.0f, IEaseFunction.DEFAULT);
	}

	public FadeOutModifier(final float pDuration, final IEaseFunction pEaseFunction) {
		super(pDuration, 1.0f, 0.0f, pEaseFunction);
	}

	public FadeOutModifier(final float pDuration, final IShapeModifierListener pShapeModiferListener) {
		super(pDuration, 1.0f, 0.0f, pShapeModiferListener, IEaseFunction.DEFAULT);
	}

	public FadeOutModifier(final float pDuration, final IShapeModifierListener pShapeModiferListener, final IEaseFunction pEaseFunction) {
		super(pDuration, 1.0f, 0.0f, pShapeModiferListener, pEaseFunction);
	}

	protected FadeOutModifier(final FadeOutModifier pFadeOutModifier) {
		super(pFadeOutModifier);
	}

	
	public FadeOutModifier clone() {
		return new FadeOutModifier(this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
