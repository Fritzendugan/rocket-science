package org.anddev.andengine.entity.scene.background.modifier;

import org.anddev.andengine.entity.scene.background.IBackground;
import org.anddev.andengine.entity.shape.modifier.ease.IEaseFunction;
import org.anddev.andengine.util.modifier.BaseTripleValueSpanModifier;

/**
 * @author Nicolas Gramlich
 * @since 14:51:03 - 03.09.2010
 */
public class ColorModifier extends BaseTripleValueSpanModifier<IBackground> implements IBackgroundModifier {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public ColorModifier(final float pDuration, final float pFromRed, final float pToRed, final float pFromGreen, final float pToGreen, final float pFromBlue, final float pToBlue) {
		this(pDuration, pFromRed, pToRed, pFromGreen, pToGreen, pFromBlue, pToBlue, null, IEaseFunction.DEFAULT);
	}

	public ColorModifier(final float pDuration, final float pFromRed, final float pToRed, final float pFromGreen, final float pToGreen, final float pFromBlue, final float pToBlue, final IEaseFunction pEaseFunction) {
		this(pDuration, pFromRed, pToRed, pFromGreen, pToGreen, pFromBlue, pToBlue, null, pEaseFunction);
	}

	public ColorModifier(final float pDuration, final float pFromRed, final float pToRed, final float pFromGreen, final float pToGreen, final float pFromBlue, final float pToBlue, final IBackgroundModifierListener pBackgroundModiferListener) {
		super(pDuration, pFromRed, pToRed, pFromGreen, pToGreen, pFromBlue, pToBlue, pBackgroundModiferListener, IEaseFunction.DEFAULT);
	}

	public ColorModifier(final float pDuration, final float pFromRed, final float pToRed, final float pFromGreen, final float pToGreen, final float pFromBlue, final float pToBlue, final IBackgroundModifierListener pBackgroundModiferListener, final IEaseFunction pEaseFunction) {
		super(pDuration, pFromRed, pToRed, pFromGreen, pToGreen, pFromBlue, pToBlue, pBackgroundModiferListener, pEaseFunction);
	}

	protected ColorModifier(final ColorModifier pColorModifier) {
		super(pColorModifier);
	}

	
	public ColorModifier clone(){
		return new ColorModifier(this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	protected void onSetInitialValues(final IBackground pBackground, final float pRed, final float pGreen, final float pBlue) {
		pBackground.setColor(pRed, pGreen, pBlue);
	}

	
	protected void onSetValues(final IBackground pBackground, final float pPerctentageDone, final float pRed, final float pGreen, final float pBlue) {
		pBackground.setColor(pRed, pGreen, pBlue);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
