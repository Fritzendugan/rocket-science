package com.rocketscience.actions;

import com.rocketscience.objects.BaseObject;

public class TakeDamageAction extends Action
{
	final protected BaseObject target;
	
	public TakeDamageAction(BaseObject t) 
	{
		super(ActionKeys.TAKE_DAMAGE);
		
		target = t;
	}

	@Override
	public void perform() 
	{
		// this shouldn't be used
		perform(0f);
		
	}
	
	public void perform(final float damage)
	{
		target.setHP(target.getHP() - damage);
		this.performReactions();
	}

}
