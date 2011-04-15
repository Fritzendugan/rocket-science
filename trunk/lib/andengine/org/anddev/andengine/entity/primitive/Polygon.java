package org.anddev.andengine.entity.primitive;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.shape.GLShape;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.opengl.buffer.BufferObjectManager;
import org.anddev.andengine.opengl.texture.region.PolygonTextureRegion;
import org.anddev.andengine.opengl.util.GLHelper;
import org.anddev.andengine.opengl.vertex.PolygonVertexBuffer;
import org.anddev.andengine.opengl.vertex.VertexBuffer;

public class Polygon extends GLShape
{
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	protected float[] mVertices;
	
	private final PolygonVertexBuffer mPolygonVertexBuffer;
	private final PolygonTextureRegion mPolygonTextureRegion;
	private static final int BLENDFUNCTION_SOURCE_DEFAULT = GL10.GL_SRC_ALPHA;
	private static final int BLENDFUNCTION_DESTINATION_DEFAULT = GL10.GL_ONE_MINUS_SRC_ALPHA;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public Polygon(float pX, float pY, float[] pVertices, final PolygonTextureRegion pPolygonTextureRegion)
	{
		this(pX, pY, pVertices, new PolygonVertexBuffer(pVertices.length, GL11.GL_STATIC_DRAW), pPolygonTextureRegion);
	}
	
	public Polygon(float pX, float pY, float[] pVertices, final PolygonVertexBuffer pPolygonVertexBuffer, final PolygonTextureRegion pPolygonTextureRegion)
	{
		super(pX, pY);

		this.mVertices = pVertices;
		this.mPolygonTextureRegion = pPolygonTextureRegion;
		this.mPolygonVertexBuffer = pPolygonVertexBuffer;
		BufferObjectManager.getActiveInstance().loadBufferObject(this.mPolygonVertexBuffer);
		
		this.updateVertexBuffer();
		
		this.setBlendFunction(BLENDFUNCTION_SOURCE_DEFAULT, BLENDFUNCTION_DESTINATION_DEFAULT);
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public PolygonTextureRegion getPolygonTextureRegion() {
		return this.mPolygonTextureRegion;
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	//@Override
	protected void onInitDraw(GL10 pGL)
	{
		super.onInitDraw(pGL);
		GLHelper.enableTextures(pGL);
		GLHelper.enableTexCoordArray(pGL);
	}
	
	protected void onApplyTransformations(final GL10 pGL)
	{
		super.onApplyTransformations(pGL);
		this.mPolygonTextureRegion.onApply(pGL);
	}
	
	public void reset()
	{
		super.reset();
		this.setBlendFunction(BLENDFUNCTION_SOURCE_DEFAULT, BLENDFUNCTION_DESTINATION_DEFAULT);
	}
	
	//@Override
	protected VertexBuffer getVertexBuffer()
	{
		return this.mPolygonVertexBuffer;
	}

	//@Override
	protected void onUpdateVertexBuffer()
	{
		this.mPolygonVertexBuffer.update(mVertices);
	}

	//@Override
	protected void drawVertices(GL10 pGL, Camera pCamera)
	{
		pGL.glDrawArrays(GL10.GL_TRIANGLES, 0, mVertices.length/2);
	}

	//@Override
	protected boolean isCulled(Camera pCamera)
	{
		// TODO Auto-generated method stub
		return false;
	}

	//@Override
	@Deprecated
	public boolean collidesWith(IShape pOtherShape)
	{
		// TODO Auto-generated method stub
		return false;
	}

	//@Override
	@Deprecated
	public float getBaseHeight()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	@Deprecated
	public float getBaseWidth()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public float getHeight()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public float[] getSceneCenterCoordinates()
	{
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	public float getWidth()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	@Deprecated
	public boolean contains(float pX, float pY)
	{
		return false;
	}

	//@Override
	@Deprecated
	public float[] convertLocalToSceneCoordinates(float pX, float pY)
	{
		return null;
	}

	//@Override
	@Deprecated
	public float[] convertSceneToLocalCoordinates(float pX, float pY)
	{
		return null;
	}

}
