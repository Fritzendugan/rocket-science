package org.anddev.andengine.opengl.texture.region.buffer;


import java.nio.FloatBuffer;

import org.anddev.andengine.opengl.buffer.BufferObject;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.PolygonTextureRegion;

public class PolygonTextureRegionBuffer extends BufferObject
{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final PolygonTextureRegion mTextureRegion;
//	private boolean mFlippedVertical;
//	private boolean mFlippedHorizontal;

	// ===========================================================
	// Constructors
	// ===========================================================

	public PolygonTextureRegionBuffer(final PolygonTextureRegion pTextureRegion, final int pVerticesCount, final int pDrawType)
	{
		super(2 * pVerticesCount * BYTES_PER_FLOAT, pDrawType);
		this.mTextureRegion = pTextureRegion;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public PolygonTextureRegion getTextureRegion()
	{
		return this.mTextureRegion;
	}

//	public boolean isFlippedHorizontal() {
//		return this.mFlippedHorizontal;
//	}
//
//	public void setFlippedHorizontal(final boolean pFlippedHorizontal) {
//		if(this.mFlippedHorizontal != pFlippedHorizontal){
//			this.mFlippedHorizontal = pFlippedHorizontal;
//			this.update();
//		}
//	}
//
//	public boolean isFlippedVertical() {
//		return this.mFlippedVertical;
//	}
//
//	public void setFlippedVertical(final boolean pFlippedVertical) {
//		if(this.mFlippedVertical != pFlippedVertical){
//			this.mFlippedVertical = pFlippedVertical;
//			this.update();
//		}
//	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	
	public synchronized void update(float[] pVertices)
	{
		final PolygonTextureRegion textureRegion = this.mTextureRegion;
		final Texture texture = textureRegion.getTexture();

		if(texture == null) {
			return;
		}

//		final float x1 = 0;
//		final float y1 = 0;
//		final float x2 = 0;
//		final float y2 = 0;
//
//		final FloatBuffer buffer = this.getFloatBuffer();
//		buffer.position(0);
//
//		buffer.put(x1); buffer.put(y1);
//		buffer.put(x1); buffer.put(y2);
//		buffer.put(x2); buffer.put(y1);
//		buffer.put(x2); buffer.put(y2);
//
//		buffer.position(0);
		
		final FloatBuffer buffer = this.getFloatBuffer();
		
		buffer.position(0);
		buffer.put(pVertices);
		buffer.position(0);

		super.update();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

