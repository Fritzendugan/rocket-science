package com.rocketscience.actions;

import com.rocketscience.helpers.ObjectKeys;
import com.rocketscience.objects.BaseObject;

public class CheckDeathAction extends Action
{
	private final BaseObject target;
	public CheckDeathAction(final BaseObject t)
	{
		super(ActionKeys.CHECK_DEATH);
		
		target = t;
	}
	
	@Override
	public void perform() 
	{
		if (target.getHP() < 0)
		{ // death occurred
			this.performReactions();
		}
	}
}
