package org.anddev.andengine.entity.scene.background;

import org.anddev.andengine.util.modifier.IModifier;
import org.anddev.andengine.util.modifier.ModifierList;



/**
 * @author Nicolas Gramlich
 * @since 14:08:17 - 19.07.2010
 */
public abstract class BaseBackground implements IBackground {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final ModifierList<IBackground> mBackgroundModifiers = new ModifierList<IBackground>(this);

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public void addBackgroundModifier(final IModifier<IBackground> pBackgroundModifier) {
		this.mBackgroundModifiers.add(pBackgroundModifier);
	}

	
	public boolean removeBackgroundModifier(final IModifier<IBackground> pBackgroundModifier) {
		return this.mBackgroundModifiers.remove(pBackgroundModifier);
	}

	
	public void clearBackgroundModifiers() {
		this.mBackgroundModifiers.clear();
	}

	
	public void onUpdate(final float pSecondsElapsed) {
		this.mBackgroundModifiers.onUpdate(pSecondsElapsed);
	}

	
	public void reset() {
		this.mBackgroundModifiers.reset();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
