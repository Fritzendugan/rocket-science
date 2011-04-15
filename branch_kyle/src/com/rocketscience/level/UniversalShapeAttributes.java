package com.rocketscience.level;

import java.util.TreeMap;

import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.util.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.rocketscience.mobs.MoveNode;

/*
 * a class that acts as temporary storage for some attributes needed to generate an objectWithActions
 */
public class UniversalShapeAttributes
{
	private final static TreeMap<FixDefKey, FixtureDef> fixdefs = new TreeMap<FixDefKey, FixtureDef>();
	private final static FixDefKey lookup = new FixDefKey(0f,0f);
	public final float angularVelocity;
	public final MoveNode[] nodes;
	public final Short texKey;
	public final int fallsDown;
	public final int hp;
	public final Vector2 position;
	public final float[] color;
	public final FixtureDef fixdef;
	
	public UniversalShapeAttributes(final Vector2 p, final float av, final MoveNode[] mp, 
			final Short tk, final int fd, final int h, final float[] c, final float e, final float f)
	{
		position = p;
		angularVelocity = av;
		nodes = mp;
		texKey = tk;
		fallsDown = fd;
		hp = h;
		color = c;
		
		// fixture def
		lookup.elasticity = e;
		lookup.friction = f;
		FixtureDef tempdef = fixdefs.get(lookup);
		if (tempdef == null)
		{
			fixdef = PhysicsFactory.createFixtureDef(0, e, f);
			fixdefs.put(lookup.clone(), fixdef);
		}
		else
		{
			fixdef = tempdef;
		}
		
		if (color.length != 4)
			Debug.e("Invalid length of color array. Must be 4 rows: r, g, b, a");
	}
}

class FixDefKey implements Comparable<FixDefKey>
{
	public float elasticity, friction;
	
	public FixDefKey(final float e, final float f)
	{
		elasticity = e;
		friction = f;
	}
	
	public FixDefKey clone()
	{
		return new FixDefKey(this.elasticity, this.friction);
	}

	@Override
	public int compareTo(FixDefKey a) 
	{
		// first compare elasticity
		if (a.elasticity < this.elasticity)
			return -1;
		if (a.elasticity > this.elasticity)
			return 1;
		
		// elasticity is the same
		if (a.friction < this.friction)
			return -1;
		if (a.friction > this.friction)
			return 1;
		
		// friction is the same too
		return 0;
	}
}
