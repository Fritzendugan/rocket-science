package com.rocketscience.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.rocketscience.objects.BaseObject;

public class JumpAction extends Action
{
	private static final Vector2 tmpVec = new Vector2();
	
	final BaseObject target;
	final Body targetBody;
	final float forceScalar; 
	
	/*
	 * force should be the maximum positive amount to assign
	 * to the y dimension of the velocity vector of the target's body
	 */
	public JumpAction(BaseObject t, float force) 
	{
		super(ActionKeys.JUMP);
		
		target = t;
		targetBody = t.getBody();
		forceScalar = force * -1;
	}

	@Override
	public void perform() 
	{
		perform(1f);
	}
	
	/*
	 * scale is imagined to be between 0 and 1.
	 * This gets multiplied by the forceScalar and assigned to velocity.
	 */
	public void perform(float scale)
	{
		tmpVec.set(targetBody.getLinearVelocity().x, scale * forceScalar);
		target.getBody().setLinearVelocity(tmpVec);
		
		this.performReactions();
	}
}
