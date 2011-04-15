package com.rocketscience.mobs;

import com.badlogic.gdx.math.Vector2;

public class MoveNode
{
	public final Vector2 p;
	public final long delay; // the time you have to wait before you stop moving
	public final long time; // time it takes to get to the next node
	public final long totalTime; // the total time spent on this node
	private long totalElapsed;
	private long excessTime; // gets set on setPosition, otherwise it's garbage
	
	public MoveNode(final Vector2 position, final long d, final long t)
	{
		p = position;
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
	
	public void setPosition(final Vector2 result, final Vector2 next)
	{
		// calculate position
		if (totalElapsed < delay)
		{
			result.x = p.x;
			result.y = p.y;
		}
		else if (totalElapsed > totalTime)
		{
			result.x = next.x;
			result.y = next.y;
		}
		else // lerp
		{
			final float scale = (float)(totalElapsed - delay) / time;
			result.x = p.x + (next.x - p.x) * scale;
			result.y = p.y + (next.y - p.y) * scale;
		}
	}// setPosition
	
	@Override
	public String toString()
	{
		return "MoveNode: " + p.toString() + " delay: " + Long.toString(delay) + " time: " + Long.toString(time) + ".";
	}
}