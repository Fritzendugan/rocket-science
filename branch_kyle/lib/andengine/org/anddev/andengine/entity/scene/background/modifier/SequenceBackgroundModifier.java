package org.anddev.andengine.entity.scene.background.modifier;

import org.anddev.andengine.entity.scene.background.IBackground;
import org.anddev.andengine.util.modifier.SequenceModifier;

/**
 * @author Nicolas Gramlich
 * @since 15:04:02 - 03.09.2010
 */
public class SequenceBackgroundModifier extends SequenceModifier<IBackground> implements IBackgroundModifier {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public SequenceBackgroundModifier(final IBackgroundModifier... pBackgroundModifiers) throws IllegalArgumentException {
		super(pBackgroundModifiers);
	}

	public SequenceBackgroundModifier(final IBackgroundModifierListener pBackgroundModiferListener, final IBackgroundModifier... pBackgroundModifiers) throws IllegalArgumentException {
		super(pBackgroundModiferListener, pBackgroundModifiers);
	}

	public SequenceBackgroundModifier(final IBackgroundModifierListener pBackgroundModiferListener, final ISubSequenceBackgroundModifierListener pSubSequenceBackgroundModifierListener, final IBackgroundModifier... pBackgroundModifiers) throws IllegalArgumentException {
		super(pBackgroundModiferListener, pSubSequenceBackgroundModifierListener, pBackgroundModifiers);
	}

	protected SequenceBackgroundModifier(final SequenceBackgroundModifier pSequenceBackgroundModifier) {
		super(pSequenceBackgroundModifier);
	}
	
	
	public SequenceBackgroundModifier clone() {
		return new SequenceBackgroundModifier(this);
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

	public interface ISubSequenceBackgroundModifierListener extends ISubSequenceModifierListener<IBackground> {
		// ===========================================================
		// Final Fields
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================
	}
}
