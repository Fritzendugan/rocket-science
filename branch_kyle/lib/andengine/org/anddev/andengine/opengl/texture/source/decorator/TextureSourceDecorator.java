package org.anddev.andengine.opengl.texture.source.decorator;

import org.anddev.andengine.opengl.texture.source.ITextureSource;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * @author Nicolas Gramlich
 * @since 16:43:29 - 06.08.2010
 */
public abstract class TextureSourceDecorator implements ITextureSource {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final ITextureSource mTextureSource;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TextureSourceDecorator(final ITextureSource pTextureSource) {
		this.mTextureSource = pTextureSource;
	}

	
	public abstract TextureSourceDecorator clone();

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	protected abstract void onDecorateBitmap(final Canvas pCanvas);

	
	public int getWidth() {
		return this.mTextureSource.getWidth();
	}

	
	public int getHeight() {
		return this.mTextureSource.getHeight();
	}

	
	public Bitmap onLoadBitmap() {
		final Bitmap bitmap = this.ensureLoadedBitmapIsMutable(this.mTextureSource.onLoadBitmap());

		final Canvas canvas = new Canvas(bitmap);
		this.onDecorateBitmap(canvas);
		return bitmap;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private Bitmap ensureLoadedBitmapIsMutable(final Bitmap pBitmap) {
		if(pBitmap.isMutable()) {
			return pBitmap;
		} else {
			final Bitmap mutableBitmap = pBitmap.copy(pBitmap.getConfig(), true);
			pBitmap.recycle();
			return mutableBitmap;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
