package com.rocketscience.actions;

import java.util.LinkedList;

import com.rocketscience.objects.BaseObject;

public abstract class Action implements Cloneable
{	
	protected LinkedList<Action> reactions = new LinkedList<Action>();
	private final int key;
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
	
	public Action(final int actionKey)
	{
		key = actionKey;
	}
	
	public int getKey()
	{
		return this.key;
	}
}
