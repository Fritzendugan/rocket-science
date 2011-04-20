package com.rocketscience.actions;

import com.rocketscience.objects.creatures.Creature;

public class SpawnAction extends Action
{

	private final Creature target;
	
	public SpawnAction(Creature t) 
	{
		super(ActionKeys.SPAWN_SELF);
		
		target = t;
	}

	@Override
	public void perform() 
	{
		target.reset();
		
		this.performReactions();
	}
}
