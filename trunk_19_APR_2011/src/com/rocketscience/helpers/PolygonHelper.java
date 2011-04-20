package com.rocketscience.helpers;

import java.util.ArrayList;
import java.util.List;

import org.anddev.andengine.entity.primitive.Polygon;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.triangulation.EarClippingTriangulator;
import org.anddev.andengine.extension.physics.box2d.util.triangulation.ITriangulationAlgoritm;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.PolygonTextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public final class PolygonHelper 
{
	
	public static Rectangle getBoundingBox(final float x, final float y, final ArrayList<Vector2> verts, final float spacefactor)
	{
		float left = Float.MAX_VALUE,
			  right = Float.MIN_VALUE,
			  top = Float.MAX_VALUE,
			  bottom = Float.MIN_VALUE;
		for (Vector2 v : verts)
		{
			if (v.x < left)
				left = v.x;
			if (v.x > right)
				right = v.x;
			if (v.y < top)
				top = v.y;
			if (v.y > bottom)
				bottom = v.y;
		}
		
		return new Rectangle(x + left*spacefactor, y + top*spacefactor, (bottom - top)*spacefactor, (right - left)*spacefactor);
	}
	
	public static Body getPolygonBody(final float pX, final float pY, final float box2dspacefactor, final PhysicsWorld world, final BodyType type, final FixtureDef fixdef, final ArrayList<Vector2> vertices)
	{
		//Create boundingbox (createTrianglulatedBody needs a shape with initial position)
		Rectangle polygonBoundingBox = getBoundingBox(pX, pY, vertices, box2dspacefactor);
		
		//Triangulate the points, making several smaller triangles of the complex shape
		final ITriangulationAlgoritm triangulationAlgoritm = new EarClippingTriangulator();
		final List<Vector2> triangles = triangulationAlgoritm.computeTriangles(vertices);
		final Body body = PhysicsFactory.createTrianglulatedBody(world, polygonBoundingBox, triangles, type, fixdef);

		return body;
	}
	
	public static float[] getTriangulatedVertices(final ArrayList<Vector2> vertices, final float box2dspacefactor)
	{
		//Triangulate the points, making several smaller triangles of the complex shape
		final ITriangulationAlgoritm triangulationAlgoritm = new EarClippingTriangulator();
		final List<Vector2> triangles = triangulationAlgoritm.computeTriangles(vertices);
		float[] triangulatedVertices = new float[triangles.size()*2];
		int j=0;
		for(int i=0; i<triangles.size(); i++)
		{
			Vector2 v = triangles.get(i);
			//Also convert back into screenspace
			triangulatedVertices[j] = v.x * box2dspacefactor;
			triangulatedVertices[j+1] = v.y * box2dspacefactor;
			j+=2;
		}
		
		return triangulatedVertices;
	}
	
	public static Polygon getPolygon(final float pX, final float pY, final float[] triangulatedVertices, final Texture tex, 
			final int tx, final int ty, final int tw, final int th)
	{
		//Create PolygonTextureRegion using the triangulated vertex list (needs to have exactly the same list as the polygon)
		//The size of the tiles is set by the width and height (32 atm which is 1 to 1 with the actual image).
		PolygonTextureRegion textureRegion = TextureRegionFactory.extractPolygonFromTexture(tex,
				tx, ty, tw, th, triangulatedVertices);

		//Create Polygon using the triangulated vertex list
		final Polygon polygon = new Polygon(pX, pY, triangulatedVertices, textureRegion);
		return polygon;
	}
}
