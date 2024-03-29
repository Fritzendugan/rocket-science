package org.anddev.andengine.util.pool;

/**
 * @author Valentin Milea
 * @author Nicolas Gramlich
 * 
 * @since 23:03:58 - 21.08.2010
 * @param <T>
 */
public abstract class RunnablePoolUpdateHandler<T extends RunnablePoolItem> extends PoolUpdateHandler<T> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public RunnablePoolUpdateHandler() {

	}

	public RunnablePoolUpdateHandler(final int pInitialPoolSize) {
		super(pInitialPoolSize);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	protected abstract T onAllocatePoolItem();

	
	protected void onHandlePoolItem(final T pRunnablePoolItem) {
		pRunnablePoolItem.run();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
