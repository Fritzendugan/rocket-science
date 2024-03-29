package org.anddev.andengine.engine;

import org.anddev.andengine.engine.options.EngineOptions;

/**
 * A subclass of {@link Engine} that tries to achieve a specific amount of
 * updates per second. When the time since the last update is bigger long the
 * steplength, additional updates are executed.
 * 
 * @author Nicolas Gramlich
 * @since 10:17:47 - 02.08.2010
 */
public class LimitedFPSEngine extends Engine {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final long mPreferredFrameLengthNanoseconds;

	// ===========================================================
	// Constructors
	// ===========================================================

	public LimitedFPSEngine(final EngineOptions pEngineOptions, final int pFramesPerSecond) {
		super(pEngineOptions);
		this.mPreferredFrameLengthNanoseconds = NANOSECONDSPERSECOND / pFramesPerSecond;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public void onUpdate(final long pNanosecondsElapsed) throws InterruptedException {
		final long preferredFrameLengthNanoseconds = this.mPreferredFrameLengthNanoseconds;
		final long deltaFrameLengthNanoseconds = preferredFrameLengthNanoseconds - pNanosecondsElapsed;

		if(deltaFrameLengthNanoseconds <= 0) {
			super.onUpdate(pNanosecondsElapsed);
		} else {
			final int sleepTimeMilliseconds = (int) (deltaFrameLengthNanoseconds / NANOSECONDSPERMILLISECOND);

			Thread.sleep(sleepTimeMilliseconds);
			super.onUpdate(pNanosecondsElapsed + deltaFrameLengthNanoseconds);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
