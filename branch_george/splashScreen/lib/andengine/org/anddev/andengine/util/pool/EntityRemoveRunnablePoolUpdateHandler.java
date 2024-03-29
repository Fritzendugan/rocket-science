package org.anddev.andengine.util.pool;

/**
 * @author Nicolas Gramlich
 * @since 23:16:25 - 31.08.2010
 */
public class EntityRemoveRunnablePoolUpdateHandler extends RunnablePoolUpdateHandler<EntityRemoveRunnablePoolItem> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	protected EntityRemoveRunnablePoolItem onAllocatePoolItem() {
		return new EntityRemoveRunnablePoolItem();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
