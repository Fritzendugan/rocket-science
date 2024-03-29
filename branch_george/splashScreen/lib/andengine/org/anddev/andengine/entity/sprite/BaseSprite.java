package org.anddev.andengine.entity.sprite;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.entity.primitive.BaseRectangle;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.util.GLHelper;
import org.anddev.andengine.opengl.vertex.RectangleVertexBuffer;

/**
 * @author Nicolas Gramlich
 * @since 11:38:53 - 08.03.2010
 */
public abstract class BaseSprite extends BaseRectangle {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int BLENDFUNCTION_SOURCE_DEFAULT = GL10.GL_SRC_ALPHA;
	private static final int BLENDFUNCTION_DESTINATION_DEFAULT = GL10.GL_ONE_MINUS_SRC_ALPHA;

	// ===========================================================
	// Fields
	// ===========================================================

	private final TextureRegion mTextureRegion;

	// ===========================================================
	// Constructors
	// ===========================================================

	public BaseSprite(final float pX, final float pY, final float pWidth, final float pHeight, final TextureRegion pTextureRegion) {
		super(pX, pY, pWidth, pHeight);

		this.mTextureRegion = pTextureRegion;
		this.setBlendFunction(BLENDFUNCTION_SOURCE_DEFAULT, BLENDFUNCTION_DESTINATION_DEFAULT);
	}

	public BaseSprite(final float pX, final float pY, final float pWidth, final float pHeight, final TextureRegion pTextureRegion, final RectangleVertexBuffer pRectangleVertexBuffer) {
		super(pX, pY, pWidth, pHeight, pRectangleVertexBuffer);

		this.mTextureRegion = pTextureRegion;
		this.setBlendFunction(BLENDFUNCTION_SOURCE_DEFAULT, BLENDFUNCTION_DESTINATION_DEFAULT);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public TextureRegion getTextureRegion() {
		return this.mTextureRegion;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public void reset() {
		super.reset();

		this.setBlendFunction(BLENDFUNCTION_SOURCE_DEFAULT, BLENDFUNCTION_DESTINATION_DEFAULT);
	}

	
	protected void onInitDraw(final GL10 pGL) {
		super.onInitDraw(pGL);
		GLHelper.enableTextures(pGL);
		GLHelper.enableTexCoordArray(pGL);
	}

	
	protected void onApplyTransformations(final GL10 pGL) {
		super.onApplyTransformations(pGL);

		this.mTextureRegion.onApply(pGL);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
