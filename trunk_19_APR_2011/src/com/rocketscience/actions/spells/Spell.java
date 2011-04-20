package com.rocketscience.actions.spells;
import org.anddev.andengine.audio.sound.Sound;

import com.rocketscience.actions.Action;
import com.rocketscience.objects.creatures.Creature;


public abstract class Spell 
{
	public Creature owner;
	//public Image icon;
	//TODO: spell icons
	public Sound castSound;
	public String name;
	public String description;
	public float energyCost;
	public float healthCost;
	public float sacrificePercent;
	public long rechargeTime;
	public long timeWhenWillBeAvailableAgain;	
	
	public Spell(Creature owner)
	{
		this.owner = owner;
	}
	
	//this is what gets overridden
	public abstract void action();
	
	public final void cast()
	{
		if (System.currentTimeMillis()>=timeWhenWillBeAvailableAgain)
		{
			//creature.canAffordCost determines if creature has enough energy, and if so,
			//subtracts that energy and applies the health costs then returns true.
			//We may add spell cost modifiers to the creature, for instance, if I pass in an
			//energy cost of 10, the creature might have an effect which doubles the cost to 20.
			//also applies on cast effects to creature.
			if (owner.canAffordCost(this.energyCost,this.healthCost,this.sacrificePercent))
			{
				castSound.play();
				this.action();
				timeWhenWillBeAvailableAgain = System.currentTimeMillis() + owner.getModifiedRechargeTime(rechargeTime);
			}				
		}
	}
			
	
}
