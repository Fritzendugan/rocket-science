package com.rocketscience.actions;

import java.util.LinkedList;

import com.rocketscience.mobs.BodyWithActions;

public abstract class Action implements Cloneable
{	
	protected LinkedList<Action> reactions = new LinkedList<Action>();
	protected final void performReactions()
	{
		for (Action a : reactions)
			a.perform();
	}
	public final void addReaction(Action action)
	{
		reactions.addLast(action);
	}
	public final void removeReaction(Action action)
	{
		reactions.remove(action);
	}
	public abstract void perform();
	public abstract void setTarget(BodyWithActions body);
}
