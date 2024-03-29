package org.anddev.andengine.entity.shape.modifier;

import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.shape.modifier.ease.IEaseFunction;


/**
 * @author Nicolas Gramlich
 * @since 21:59:38 - 06.07.2010
 */
public class RotationAtModifier extends RotationModifier {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final float mRotationCenterX;
	private final float mRotationCenterY;

	// ===========================================================
	// Constructors
	// ===========================================================

	public RotationAtModifier(final float pDuration, final float pFromRotation, final float pToRotation, final float pRotationCenterX, final float pRotationCenterY) {
		super(pDuration, pFromRotation, pToRotation, IEaseFunction.DEFAULT);
		this.mRotationCenterX = pRotationCenterX;
		this.mRotationCenterY = pRotationCenterY;
	}

	public RotationAtModifier(final float pDuration, final float pFromRotation, final float pToRotation, final float pRotationCenterX, final float pRotationCenterY, final IEaseFunction pEaseFunction) {
		super(pDuration, pFromRotation, pToRotation, pEaseFunction);
		this.mRotationCenterX = pRotationCenterX;
		this.mRotationCenterY = pRotationCenterY;
	}

	public RotationAtModifier(final float pDuration, final float pFromRotation, final float pToRotation, final float pRotationCenterX, final float pRotationCenterY, final IShapeModifierListener pShapeModiferListener) {
		super(pDuration, pFromRotation, pToRotation, pShapeModiferListener, IEaseFunction.DEFAULT);
		this.mRotationCenterX = pRotationCenterX;
		this.mRotationCenterY = pRotationCenterY;
	}

	public RotationAtModifier(final float pDuration, final float pFromRotation, final float pToRotation, final float pRotationCenterX, final float pRotationCenterY, final IShapeModifierListener pShapeModiferListener, final IEaseFunction pEaseFunction) {
		super(pDuration, pFromRotation, pToRotation, pShapeModiferListener, pEaseFunction);
		this.mRotationCenterX = pRotationCenterX;
		this.mRotationCenterY = pRotationCenterY;
	}

	protected RotationAtModifier(final RotationAtModifier pRotationAtModifier) {
		super(pRotationAtModifier);
		this.mRotationCenterX = pRotationAtModifier.mRotationCenterX;
		this.mRotationCenterY = pRotationAtModifier.mRotationCenterY;
	}

	
	public RotationAtModifier clone(){
		return new RotationAtModifier(this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	protected void onManagedInitialize(final IShape pShape) {
		super.onManagedInitialize(pShape);
		pShape.setRotationCenter(this.mRotationCenterX, this.mRotationCenterY);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
