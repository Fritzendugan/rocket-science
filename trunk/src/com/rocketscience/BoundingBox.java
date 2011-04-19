package com.rocketscience;

import com.badlogic.gdx.math.Vector2;

public class BoundingBox 
{
	private final float left, top, right, bottom;
	
	public BoundingBox(final float l, final float t, final float r, final float b)
	{
		left = l;
		top = t;
		right = r;
		bottom = b;
	}
	
	public final boolean isOutside(final Vector2 p)
	{
		return isOutside(p.x, p.y);
	}
	
	public final boolean isOutside(final float x, final float y)
	{
		if (x < left)
			return true;
		if (y < top)
			return true;
		if (x > right)
			return true;
		if (y > bottom)
			return true;
		
		return false;
	}
}
