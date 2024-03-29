package org.anddev.andengine.util.modifier;

import java.util.ArrayList;

import org.anddev.andengine.engine.handler.IUpdateHandler;

/**
 * @author Nicolas Gramlich
 * @since 14:34:57 - 03.09.2010
 */
public class ModifierList<T> extends ArrayList<IModifier<T>> implements IUpdateHandler {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final long serialVersionUID = 1610345592534873475L;

	// ===========================================================
	// Fields
	// ===========================================================

	private final T mTarget;

	// ===========================================================
	// Constructors
	// ===========================================================

	public ModifierList(final T pTarget){
		this.mTarget = pTarget;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public T getTarget() {
		return this.mTarget;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public void onUpdate(final float pSecondsElapsed) {
		final ArrayList<IModifier<T>> modifiers = this;
		final int modifierCount = this.size();
		if(modifierCount > 0) {
			for(int i = modifierCount - 1; i >= 0; i--) {
				final IModifier<T> modifier = modifiers.get(i);
				modifier.onUpdate(pSecondsElapsed, this.mTarget);
				if(modifier.isFinished() && modifier.isRemoveWhenFinished()) {
					modifiers.remove(i);
				}
			}
		}
	}

	
	public void reset() {
		final ArrayList<IModifier<T>> modifiers = this;
		for(int i = modifiers.size() - 1; i >= 0; i--) {
			modifiers.get(i).reset();
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
