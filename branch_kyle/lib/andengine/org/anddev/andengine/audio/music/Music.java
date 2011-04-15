package org.anddev.andengine.audio.music;

import org.anddev.andengine.audio.BaseAudioEntity;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * @author Nicolas Gramlich
 * @since 14:53:12 - 13.06.2010
 */
public class Music extends BaseAudioEntity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final MediaPlayer mMediaPlayer;

	// ===========================================================
	// Constructors
	// ===========================================================

	Music(final MusicManager pMusicManager, final MediaPlayer pMediaPlayer) {
		super(pMusicManager);
		this.mMediaPlayer = pMediaPlayer;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean isPlaying() {
		return this.mMediaPlayer.isPlaying();
	}
	
	public MediaPlayer getMediaPlayer() {
		return this.mMediaPlayer;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	protected MusicManager getAudioManager() {
		return (MusicManager)super.getAudioManager();
	}

	
	public void play() {
		this.mMediaPlayer.start();
	}

	
	public void stop() {
		this.mMediaPlayer.stop();
	}

	
	public void resume() {
		this.mMediaPlayer.start();
	}

	
	public void pause() {
		this.mMediaPlayer.pause();
	}

	
	public void release() {
		this.mMediaPlayer.release();
	}

	
	public void setLooping(final boolean pLooping) {
		this.mMediaPlayer.setLooping(pLooping);
	}

	
	public void setVolume(final float pLeftVolume, final float pRightVolume) {
		super.setVolume(pLeftVolume, pRightVolume);

		final float masterVolume = this.getAudioManager().getMasterVolume();
		final float actualLeftVolume = pLeftVolume * masterVolume;
		final float actualRightVolume = pRightVolume * masterVolume;

		this.mMediaPlayer.setVolume(actualLeftVolume, actualRightVolume);
	}

	
	public void onMasterVolumeChanged(final float pMasterVolume) {
		this.setVolume(this.mLeftVolume, this.mRightVolume);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	public void seekTo(final int pMilliseconds) {
		this.mMediaPlayer.seekTo(pMilliseconds);
	}
	
	public void setOnCompletionListener(final OnCompletionListener pOnCompletionListener) {
		this.mMediaPlayer.setOnCompletionListener(pOnCompletionListener);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
