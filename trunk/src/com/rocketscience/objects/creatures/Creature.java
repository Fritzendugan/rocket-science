package com.rocketscience.objects.creatures;

import java.util.TreeMap;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.rocketscience.actions.Action;
import com.rocketscience.objects.BaseObject;

public class Creature extends BaseObject implements IUpdateHandler
{
	protected final TreeMap<Integer,Action> actions = new TreeMap<Integer,Action>();

	protected final Vector2 startingPosition;
	protected final AnimatedSprite sprite;
	
	protected int curPath = 0; // the current path it's on
	protected boolean forward = true; // whether it is moving forward or backward on the current path

	public Creature(final Body b, final AnimatedSprite s, final short k) 
	{
		super(b,s,k);
		sprite = s;
		startingPosition = b.getPosition();
	}
	
	//must get a reference to an action to perform the action
	public final Action getAction(Integer type)
	{
		return actions.get(type);
	}

	public final void addAction(Integer type, Action action)
	{
		actions.put(type, action);
	}

	public final void removeAction(Integer type)
	{
		actions.remove(type);
	}
	
	public Vector2 getPosition()
	{
		return this.body.getWorldCenter();
	}
	
	public Body getBody()
	{
		return this.body;
	}
	
	public AnimatedSprite getSprite() 
	{ 
		return this.sprite; 
	}


	@Override
	public void onUpdate(float pSecondsElapsed) 
	{
		// if there is no movePath controller, get out of here
	}

	@Override
	public void reset() 
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		buf.append(" --- Creature (id: ").append(id).append(") ---");
		buf.append("\nPosition: ").append(body.getPosition().toString());
		buf.append("\nstartingPosition: ").append(startingPosition.toString());
		buf.append("\n --- end BodyWithActions (id: ").append(id).append(") ---");
		return buf.toString();
	}
}
