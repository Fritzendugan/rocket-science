package org.anddev.andengine.opengl.texture.buffer;

import java.nio.FloatBuffer;

import org.anddev.andengine.opengl.buffer.BufferObject;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.Letter;

/**
 * @author Nicolas Gramlich
 * @since 11:05:56 - 03.04.2010
 */
public class TextTextureBuffer extends BufferObject {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public TextTextureBuffer(final int pByteCount, final int pDrawType) {
		super(pByteCount, pDrawType);
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

	public synchronized void update(final Font pFont, final String[] pLines) {
		final FloatBuffer textureFloatBuffer = this.getFloatBuffer();
		textureFloatBuffer.position(0);

		final Font font = pFont;
		final String[] lines = pLines;

		final int lineCount = lines.length;
		for (int i = 0; i < lineCount; i++) {
			final String line = lines[i];

			final int lineLength = line.length();
			for (int j = 0; j < lineLength; j++) {
				final Letter letter = font.getLetter(line.charAt(j));

				final float letterTextureX = letter.mTextureX;
				final float letterTextureY = letter.mTextureY;
				final float letterTextureX2 = letterTextureX + letter.mTextureWidth;
				final float letterTextureY2 = letterTextureY + letter.mTextureHeight;

				textureFloatBuffer.put(letterTextureX);
				textureFloatBuffer.put(letterTextureY);

				textureFloatBuffer.put(letterTextureX);
				textureFloatBuffer.put(letterTextureY2);

				textureFloatBuffer.put(letterTextureX2);
				textureFloatBuffer.put(letterTextureY2);

				textureFloatBuffer.put(letterTextureX2);
				textureFloatBuffer.put(letterTextureY2);

				textureFloatBuffer.put(letterTextureX2);
				textureFloatBuffer.put(letterTextureY);

				textureFloatBuffer.put(letterTextureX);
				textureFloatBuffer.put(letterTextureY);
			}
		}
		textureFloatBuffer.position(0);

		this.update();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
