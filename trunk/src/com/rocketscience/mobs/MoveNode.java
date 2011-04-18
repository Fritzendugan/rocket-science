package com.rocketscience.mobs;

import com.badlogic.gdx.math.Vector2;

public class MoveNode
{
	protected final float pX, pY;
	public final long delay; // the time you have to wait before you stop moving
	public final long time; // time it takes to get to the next node
	public final long totalTime; // the total time spent on this node
	private long totalElapsed;
	private long excessTime; // gets set on setPosition, otherwise it's garbage
	
	public MoveNode(final float x, final float y, final long d, final long t)
	{
		pX = x;
		pY = y;
		delay = d;
		time = t;
		excessTime = 0;
		totalElapsed = 0;
		totalTime = delay + time;
	}
	
	public void update(final long e)
	{
		totalElapsed += e;
		// calculate excess time
		excessTime = totalElapsed - totalTime;
		excessTime = (excessTime > 0) ? excessTime : 0;
	}
	
	public long getExcessTime()
	{
		return excessTime;
	}
	
	public void reset()
	{
		totalElapsed = 0;
		excessTime = 0;
	}
	
	public void setPosition(final Vector2 result, final MoveNode nextNode)
	{
		float nx = nextNode.pX;
		float ny = nextNode.pY;
		// calculate position
		if (totalElapsed < delay)
		{
			result.x = pX;
			result.y = pY;
		}
		else if (totalElapsed > totalTime)
		{
			result.x = nx;
			result.y = ny;
		}
		else // lerp
		{
			final float scale = (float)(totalElapsed - delay) / time;
			result.x = pX + (nx - pX) * scale;
			result.y = pY + (ny - pY) * scale;
		}
	}// setPosition
	
	@Override
	public String toString()
	{
		return "MoveNode: " + new Vector2(pX, pY).toString() + " delay: " + Long.toString(delay) + " time: " + Long.toString(time) + ".";
	}
}