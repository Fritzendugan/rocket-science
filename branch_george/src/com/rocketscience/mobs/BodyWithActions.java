package com.rocketscience.mobs;

import java.util.TreeMap;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.shape.Shape;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.rocketscience.actions.Action;
import com.rocketscience.helpers.ObjectKeys;
import com.rocketscience.objects.BaseObject;

public class BodyWithActions extends BaseObject implements IUpdateHandler
{
	private static long _lastid = 0;
	protected final MovePathController mpc; // the path the object moves in
	protected final TreeMap<Integer,Action> actions = new TreeMap<Integer,Action>();
	protected final float angularvelocity;
	protected final Vector2 startingPosition;

	protected int curPath = 0; // the current path it's on
	protected boolean forward = true; // whether it is moving forward or backward on the current path

	public BodyWithActions(MoveNode[] nodes, final Body b, final Shape s, final float av) 
	{
		super(b,s,ObjectKeys.BODY_WITH_ACTIONS);
		mpc = (nodes == null) ? null : new MovePathController(nodes);
		startingPosition = b.getPosition();
		angularvelocity = av;
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

	@Override
	public void onUpdate(float pSecondsElapsed) 
	{
		// if there is no movePath controller, get out of here
		if (mpc == null && this.angularvelocity == 0)
		{
			return;
		}
		if (mpc == null)
		{
			this.body.setTransform(startingPosition, 
					this.body.getAngle() + this.angularvelocity * pSecondsElapsed);
			return;
		}
		// otherwise, update
		mpc.onUpdate(pSecondsElapsed);
		this.body.setTransform(mpc.position, this.body.getAngle() + this.angularvelocity * pSecondsElapsed);
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
		buf.append(" --- BodyWithActions (id: ").append(id).append(") ---");
		buf.append("\nPosition: ").append(body.getPosition().toString());
		if (mpc == null)
			buf.append("\n MPC IS NULL");
		else
			buf.append("\nMPC.toString(): ").append(mpc.toString());
		buf.append("\nstartingPosition: ").append(startingPosition.toString());
		buf.append("\n --- end BodyWithActions (id: ").append(id).append(") ---");
		return buf.toString();
	}
}
