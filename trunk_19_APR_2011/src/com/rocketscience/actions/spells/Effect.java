package com.rocketscience.actions.spells;
import java.util.ArrayList;

import com.rocketscience.actions.Action;
import com.rocketscience.objects.creatures.Creature;

public abstract class Effect 
{
	Creature target;
	
	//parallel arrays
	ArrayList<Action> reactions;
	ArrayList<Action> targetActions;
	
	public static final int HEX = 0;
	public static final int ENCHANTMENT = 1;
	public static final int OTHER = 2;
	
	public int type;
	
	
	public Effect(Creature target)
	{
		this.target = target;
		target.addEffect(this);
		//type is set in constructor
	}
	
	//these should be added in the overridden constructor
	public final void addReaction(Action targetAction, Action reaction)
	{
		targetAction.addReaction(reaction);
		targetActions.add(targetAction);
		reactions.add(reaction);
	}	
	
	//determines the conditions under which the effect naturally expires
	public abstract void onUpdate(float pSeconds);
	
	//called when update() chooses to or when effect is manually removed from creature (remove hex for example)
	public final void remove()
	{
		ArrayList<Action> reactions = this.reactions;
		ArrayList<Action> targetActions = this.targetActions;
		int i, max = reactions.size();
		for (i=0;i<max;++i)
			targetActions.get(i).removeReaction(reactions.get(i));
		//clear lists? idk if this will be good or neutral for gc or bad for speed
		//reactions.clear();
		//targetActions.clear();
	}	
}
