package com.rocketscience.actions.spells;
import org.anddev.andengine.audio.sound.Sound;

import android.view.animation.Animation;

import com.rocketscience.helpers.ObjectKeys;
import com.rocketscience.objects.BaseObject;
import com.rocketscience.objects.creatures.Creature;



//not sure what this extends and whether it will get position info from what it extends
public abstract class SpellBody extends BaseObject
{
	public Creature sourceCreature;
	//TODO: animations
	public Sound sound;
	
	public SpellBody(Creature sourceCreature)
	{
		//TODO: graphics for the spell body, and the physics body
		super(null,null, ObjectKeys.SPELL_BODY);
		this.sourceCreature = sourceCreature;
	}
	
	public abstract void update();
	public abstract void onTouch(BaseObject object);
	
}
