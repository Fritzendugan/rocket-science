package org.anddev.andengine.opengl.texture.region.buffer;

import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class TextureRegionBuffer extends BaseTextureRegionBuffer {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public TextureRegionBuffer(final TextureRegion pTextureRegion, final int pDrawType) {
		super(pTextureRegion, pDrawType);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	protected float getX1() {
		final TextureRegion textureRegion = this.getTextureRegion();
		return (float)textureRegion.getTexturePositionX() / textureRegion.getTexture().getWidth();
	}

	
	protected float getX2() {
		final TextureRegion textureRegion = this.getTextureRegion();
		return (float)(textureRegion.getTexturePositionX() + textureRegion.getWidth()) / textureRegion.getTexture().getWidth();
	}

	
	protected float getY1() {
		final TextureRegion textureRegion = this.getTextureRegion();
		return (float)textureRegion.getTexturePositionY() / textureRegion.getTexture().getHeight();
	}

	
	protected float getY2() {
		final TextureRegion textureRegion = this.getTextureRegion();
		return (float)(textureRegion.getTexturePositionY() + textureRegion.getHeight()) / textureRegion.getTexture().getHeight();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
