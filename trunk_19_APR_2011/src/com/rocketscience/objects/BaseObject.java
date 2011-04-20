package com.rocketscience.objects;

import java.util.LinkedList;
import java.util.TreeMap;

import org.anddev.andengine.entity.shape.Shape;

import com.badlogic.gdx.physics.box2d.Body;
import com.rocketscience.actions.Action;
import com.rocketscience.actions.spells.Effect;

public class BaseObject
{
	//private static long _lastid = 0;

	protected final Body body;
	protected final Shape sprite;
	//protected final long id;
	protected final short type;
	protected final short myKey;
	protected final short relativeKey;
	
	public final boolean moves;
	
	protected final TreeMap<Integer,Action> actions = new TreeMap<Integer,Action>();
	protected final LinkedList<Effect> effects = new LinkedList<Effect>();
	
	// state and stats
	protected float hp;
	
	public BaseObject(final Body b, final Shape s, final short k, final short mk, final short rk)
	{
		//id = _lastid++; // used to identify any object
		body = b;
		sprite = s;
		type = k; // this is used for safe down-casting
		b.setUserData(this); // this allows a link from any physics body back
		                     // to the Object it belongs to.
		myKey = mk;
		relativeKey = rk;
		moves = true;
	}
	
	public float getHP()
	{
		return hp;
	}
	
	public void setHP(final float h)
	{
		hp = h;
	}
	
	public long getMyKey()
	{
		return myKey;
	}
	
	public long getRelativeKey()
	{
		return relativeKey;
	}
	
	public Body getBody()
	{
		return body;
	}
	
	public short getType()
	{
		return type;
	}
	
	public String toString()
	{
		return "{BaseObject : myKey=" + String.valueOf(myKey) + " type=" + String.valueOf(type) + "}";
	}
	
	//must get a reference to an action to perform the action
	public final Action getAction(Integer key)
	{
		return actions.get(key);
	}

	public final void addAction(Action action)
	{
		actions.put(action.getKey(), action);
	}

	public final void removeAction(Integer key)
	{
		actions.remove(key);
	}
	
	public void addEffect(Effect effect) 
	{
		effects.add(effect);	
	}
}
