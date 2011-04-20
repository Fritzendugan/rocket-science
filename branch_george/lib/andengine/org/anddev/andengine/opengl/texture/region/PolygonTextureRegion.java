package org.anddev.andengine.opengl.texture.region;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import org.anddev.andengine.opengl.buffer.BufferObjectManager;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.buffer.PolygonTextureRegionBuffer;
import org.anddev.andengine.opengl.util.GLHelper;

/**
 * @author Nicolas Gramlich
 * @since 14:29:59 - 08.03.2010
 */
public class PolygonTextureRegion
{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final Texture mTexture;
	protected final PolygonTextureRegionBuffer mTextureRegionBuffer;
	protected final float[] mVertices;

	private int mWidth;
	private int mHeight;

	private int mTexturePositionX;
	private int mTexturePositionY;

	// ===========================================================
	// Constructors
	// ===========================================================

	public PolygonTextureRegion(final Texture pTexture, final int pTexturePositionX, final int pTexturePositionY, final int pWidth, final int pHeight, final float[] pVertices)
	{
		this.mVertices = new float[pVertices.length];
		
		for(int i=0; i<pVertices.length; i++)
		{
			mVertices[i] = pVertices[i] * 1.0f/pWidth;
		}
		
		this.mTexture = pTexture;
		this.mTexturePositionX = pTexturePositionX;
		this.mTexturePositionY = pTexturePositionY;
		this.mWidth = pWidth;
		this.mHeight = pHeight;

		this.mTextureRegionBuffer = this.onCreateTextureRegionBuffer();

		BufferObjectManager.getActiveInstance().loadBufferObject(this.mTextureRegionBuffer);

		this.initTextureBuffer();
	}

	protected void initTextureBuffer()
	{
		this.updateTextureRegionBuffer();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getWidth()
	{
		return this.mWidth;
	}

	public int getHeight()
	{
		return this.mHeight;
	}

	public void setWidth(final int pWidth)
	{
		this.mWidth = pWidth;
		this.updateTextureRegionBuffer();
	}

	public void setHeight(final int pHeight)
	{
		this.mHeight = pHeight;
		this.updateTextureRegionBuffer();
	}

	public void setTexturePosition(final int pX, final int pY)
	{
		this.mTexturePositionX = pX;
		this.mTexturePositionY = pY;
		this.updateTextureRegionBuffer();
	}

	public int getTexturePositionX()
	{
		return this.mTexturePositionX;
	}

	public int getTexturePositionY()
	{
		return this.mTexturePositionY;
	}

	public Texture getTexture()
	{
		return this.mTexture;
	}

	public PolygonTextureRegionBuffer getTextureBuffer()
	{
		return this.mTextureRegionBuffer;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public PolygonTextureRegion clone()
	{
		return new PolygonTextureRegion(this.mTexture, this.mTexturePositionX, this.mTexturePositionY, this.mWidth, this.mHeight, this.mVertices);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected void updateTextureRegionBuffer()
	{
		this.mTextureRegionBuffer.update(this.mVertices);
	}

	protected PolygonTextureRegionBuffer onCreateTextureRegionBuffer()
	{
		return new PolygonTextureRegionBuffer(this, this.mVertices.length, GL11.GL_STATIC_DRAW);
	}

	public void onApply(final GL10 pGL)
	{
		if(GLHelper.EXTENSIONS_VERTEXBUFFEROBJECTS)
		{
			final GL11 gl11 = (GL11)pGL;

			this.mTextureRegionBuffer.selectOnHardware(gl11);

			GLHelper.bindTexture(pGL, this.mTexture.getHardwareTextureID());
			GLHelper.texCoordZeroPointer(gl11);
		}
		else
		{
			GLHelper.bindTexture(pGL, this.mTexture.getHardwareTextureID());
			GLHelper.texCoordPointer(pGL, this.mTextureRegionBuffer.getFloatBuffer());
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
