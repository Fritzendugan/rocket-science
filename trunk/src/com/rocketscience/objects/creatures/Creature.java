package com.rocketscience.objects.creatures;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.sprite.AnimatedSprite;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.rocketscience.actions.SpawnAction;
import com.rocketscience.objects.BaseObject;

public class Creature extends BaseObject implements IUpdateHandler
{
	private static final Vector2 VECTOR2_ZERO = new Vector2(0,0);
	
	protected final Vector2 startingPosition;
	protected final AnimatedSprite sprite;
	
	protected int curPath = 0; // the current path it's on
	protected boolean forward = true; // whether it is moving forward or backward on the current path
	// actions
	protected final SpawnAction spawnAction;
	
	public Creature(final Body b, final AnimatedSprite s, final short k) 
	{
		super(b,s,k);
		sprite = s;
		startingPosition = b.getPosition();
		
		// load actions
		spawnAction = new SpawnAction(this);
		this.addAction(spawnAction);
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
		this.body.setTransform(startingPosition, 0);
		this.body.setLinearVelocity(VECTOR2_ZERO);
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

	public boolean canAffordCost(float energyCost, float healthCost,
			float sacrificePercent) 
	{
		// TODO Auto-generated method stub
		return true;
	}

	public long getModifiedRechargeTime(long rechargeTime) 
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
