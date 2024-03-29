package org.anddev.andengine.entity.scene.menu.item;

import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.opengl.font.Font;

/**
 * @author Nicolas Gramlich
 * @since 20:15:20 - 01.04.2010
 */
public class TextMenuItem extends Text implements IMenuItem{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final int mID;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TextMenuItem(final int pID, final Font pFont, final String pText) {
		super(0, 0, pFont, pText);
		this.mID = pID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getID() {
		return this.mID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public void onSelected() {
		/* Nothing. */
	}

	
	public void onUnselected() {
		/* Nothing. */
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
