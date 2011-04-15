package com.rocketscience.objects;

import org.anddev.andengine.entity.shape.Shape;

import com.badlogic.gdx.physics.box2d.Body;
import com.rocketscience.helpers.ObjectKeys;

public class BaseObject
{
	private static long _lastid = 0;
	
	public static BaseObject NULL_OBJECT = new BaseObject(null, null, ObjectKeys.NULL_OBJECT);

	protected final Body body;
	protected final Shape sprite;
	protected final long id;
	protected final short type;
	
	public BaseObject(final Body b, final Shape s, final short k)
	{
		id = _lastid++; // used to identify any object
		body = b;
		sprite = s;
		type = k; // this is used for safe down-casting
		b.setUserData(this); // this allows a link from any physics body back
		                     // to the Object it belongs to.
	}
	
	public long getID()
	{
		return id;
	}
	
	public short getType()
	{
		return type;
	}
	
	public String toString()
	{
		return "{BaseObject : id=" + String.valueOf(id) + " type=" + String.valueOf(type) + "}";
	}
	
}
