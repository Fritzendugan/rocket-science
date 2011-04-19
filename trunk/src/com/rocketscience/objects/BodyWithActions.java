package com.rocketscience.objects;

import java.util.TreeMap;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.shape.Shape;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.rocketscience.actions.Action;
import com.rocketscience.actions.ActionKeys;
import com.rocketscience.actions.TakeDamageAction;
import com.rocketscience.helpers.ObjectKeys;
import com.rocketscience.mobs.MoveNode;
import com.rocketscience.mobs.MovePathController;
import com.rocketscience.player.Player;

public class BodyWithActions extends BaseObject implements IUpdateHandler, ContactListener
{
	private static long _lastid = 0;
	protected final MovePathController mpc; // the path the object moves in
	protected final TreeMap<Integer,Action> actions = new TreeMap<Integer,Action>();
	protected final float angularvelocity;
	protected final Vector2 startingPosition;
	
	public final boolean moves;

	protected int curPath = 0; // the current path it's on
	protected boolean forward = true; // whether it is moving forward or backward on the current path

	// damage dealing stuff
	protected final boolean isSensor;
	protected final float damageDealt;
	protected final long damageInterval;
	protected long damageTimeLeft;
	protected boolean dealingDamage = false;
	protected TakeDamageAction playerTakeDamage;
	
	public BodyWithActions(final MoveNode[] nodes, final Body b, final Shape s, final float av,
			               final boolean iss, final float dd, final long di) 
	{
		super(b,s,ObjectKeys.BODY_WITH_ACTIONS);
		mpc = (nodes == null) ? null : new MovePathController(nodes);
		moves = (mpc != null);
		startingPosition = b.getPosition();
		angularvelocity = av;
		
		isSensor = iss;
		damageDealt = dd;
		damageTimeLeft = damageInterval = di;
	}
	
	public Vector2 getPosition()
	{
		return this.body.getWorldCenter();
	}

	public void startDamagingPlayer(final Player p)
	{
		dealingDamage = true;
		damageTimeLeft = damageInterval;
		playerTakeDamage = (TakeDamageAction)p.getAction(ActionKeys.TAKE_DAMAGE);
		if (playerTakeDamage == null)
			dealingDamage = false;
	}
	
	public void stopDamagingPlayer()
	{
		damageTimeLeft = 0;
		dealingDamage = false;
	}
	
	@Override
	public void onUpdate(float pSecondsElapsed) 
	{
		if (dealingDamage)
		{
			damageTimeLeft -= (int)(pSecondsElapsed * 1000);
			if (damageTimeLeft <= 0) // time to deal damage
			{
				playerTakeDamage.perform(this.damageDealt);
			}
		}
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

	@Override
	public void beginContact(Contact contact) 
	{
		final Body bodyA, bodyB;
		final BaseObject objA, objB;
		bodyA = contact.getFixtureA().getBody();
		bodyB = contact.getFixtureB().getBody();
		if ((objA = (BaseObject)bodyA.getUserData()) == null)
			return;
		if ((objB = (BaseObject)bodyB.getUserData()) == null)
			return;
		
		// see if anyone in the contact was us
		final Action takeDamage;
		if (objA == this)
		{
			if (objB.getType() == ObjectKeys.PLAYER)
			{
				this.startDamagingPlayer((Player)objB);
			}
		}
		else if (objB == this)
		{
			if (objA.getType() == ObjectKeys.PLAYER)
			{
				this.startDamagingPlayer((Player)objA);
			}
		}	
	}

	@Override
	public void endContact(Contact contact) 
	{
		final Body bodyA, bodyB;
		final BaseObject objA, objB;
		bodyA = contact.getFixtureA().getBody();
		bodyB = contact.getFixtureB().getBody();
		if ((objA = (BaseObject)bodyA.getUserData()) == null)
			return;
		if ((objB = (BaseObject)bodyB.getUserData()) == null)
			return;
		
		// see if anyone in the contact was us
		final Action takeDamage;
		if (objA == this)
		{
			if (objB.getType() == ObjectKeys.PLAYER)
			{
				this.stopDamagingPlayer();
			}
		}
		else if (objB == this)
		{
			if (objA.getType() == ObjectKeys.PLAYER)
			{
				this.stopDamagingPlayer();
			}
		}
	}
}
