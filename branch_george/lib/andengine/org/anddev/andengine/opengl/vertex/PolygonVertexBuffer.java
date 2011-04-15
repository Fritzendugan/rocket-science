package org.anddev.andengine.opengl.vertex;

import java.nio.FloatBuffer;

public class PolygonVertexBuffer extends VertexBuffer
{
	public PolygonVertexBuffer(int pVerticesCount, int pDrawType)
	{
		super(2 * pVerticesCount * BYTES_PER_FLOAT, pDrawType);
	}
	
	public void update(float[] pVertices)
	{
		final FloatBuffer buffer = this.getFloatBuffer();
		buffer.position(0);
		
		buffer.put(pVertices);
		
		buffer.position(0);
		
		super.update();
	}
}
