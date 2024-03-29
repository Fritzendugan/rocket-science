package org.anddev.andengine.audio.music;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

/**
 * @author Nicolas Gramlich
 * @since 15:05:49 - 13.06.2010
 */
public class MusicFactory {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static String sAssetBasePath = "";

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public static void setAssetBasePath(final String pAssetBasePath) {
		MusicFactory.sAssetBasePath = pAssetBasePath;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static Music createMusicFromFile(final MusicManager pMusicManager, final Context pContext, final File pFile) throws IOException {
		final MediaPlayer mediaPlayer = new MediaPlayer();

		mediaPlayer.setDataSource(new FileInputStream(pFile).getFD());
		mediaPlayer.prepare();

		final Music music = new Music(pMusicManager, mediaPlayer);
		pMusicManager.add(music);

		return music;
	}

	public static Music createMusicFromAsset(final MusicManager pMusicManager, final Context pContext, final String pAssetPath) throws IOException {
		final MediaPlayer mediaPlayer = new MediaPlayer();

		final AssetFileDescriptor assetFileDescritor = pContext.getAssets().openFd(MusicFactory.sAssetBasePath + pAssetPath);
		mediaPlayer.setDataSource(assetFileDescritor.getFileDescriptor(), assetFileDescritor.getStartOffset(), assetFileDescritor.getLength());
		mediaPlayer.prepare();

		final Music music = new Music(pMusicManager, mediaPlayer);
		pMusicManager.add(music);

		return music;
	}

	public static Music createMusicFromResource(final MusicManager pMusicManager, final Context pContext, final int pMusicResID) throws IOException {
		final MediaPlayer mediaPlayer = MediaPlayer.create(pContext, pMusicResID);
		mediaPlayer.prepare();

		final Music music = new Music(pMusicManager, mediaPlayer);
		pMusicManager.add(music);

		return music;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
