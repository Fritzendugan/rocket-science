package org.anddev.andengine.audio;

import java.util.ArrayList;

/**
 * @author Nicolas Gramlich
 * @since 18:07:02 - 13.06.2010
 */
public abstract class BaseAudioManager<T extends IAudioEntity> implements IAudioManager<T> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final ArrayList<T> mAudioEntities = new ArrayList<T>();

	protected float mMasterVolume = 1.0f;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	public float getMasterVolume() {
		return this.mMasterVolume;
	}

	public void setMasterVolume(final float pMasterVolume) {
		this.mMasterVolume = pMasterVolume;

		final ArrayList<T> audioEntities = this.mAudioEntities;
		for(int i = audioEntities.size() - 1; i >= 0; i--) {
			final T audioEntity = audioEntities.get(i);

			audioEntity.onMasterVolumeChanged(pMasterVolume);
		}
	}

	public void add(final T pAudioEntity) {
		this.mAudioEntities.add(pAudioEntity);
	}

	
	public void releaseAll() {
		final ArrayList<T> audioEntities = this.mAudioEntities;
		for(int i = audioEntities.size() - 1; i >= 0; i--) {
			final T audioEntity = audioEntities.get(i);

			audioEntity.stop();
			audioEntity.release();
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
