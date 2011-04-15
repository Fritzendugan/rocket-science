package com.rocketscience.objects;

import java.io.DataInputStream;
import java.io.IOException;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.rocketscience.helpers.CollisionFilter;
import com.rocketscience.helpers.ContactListenerManager;
import com.rocketscience.level.Section;
import com.rocketscience.mobs.BodyWithActions;
import com.rocketscience.player.Player;

public class WayPointObj extends BodyWithActions 
{
	private static FixtureDef FIX_DEF = getFixtureDef();
	private static FixtureDef getFixtureDef()
	{
		final FixtureDef fixdef = new FixtureDef();
		fixdef.isSensor = true;
		fixdef.filter.maskBits = CollisionFilter.MASK_PLAYER; // only collide with the player
		return fixdef;
	}
	
	private boolean active;
	private final int inactivetx;
	private final Sprite sprite;
	private final Player player; // a reference to the player
	
	public WayPointObj(Body b, Sprite s, Player p) 
	{
		super(null, b, s, 0);
		sprite = s;
		inactivetx = s.getTextureRegion().getTexturePositionX();
		player = p;
		active = false;
	}

	public static WayPointObj Load(final DataInputStream inp, final Section section, final Texture texture, final PhysicsWorld world, final Player player) throws IOException
	{
		// SYNTAX:
		// float x, y, w, h, tx, ty, tw, th
		// boolean isActive
		final WayPointObj wpo;
		final float x, y, w, h; // dimensions of the way point
		final int tx, ty, tw, th; // the texture coordinates of the object
		final boolean isActive; // should this wayPoint start as active?
		final TextureRegion texregion; // the texture region for the sprite
		final Sprite sprite;
		final Body body;
		
		x = inp.readFloat();
		y = inp.readFloat();
		//w = inp.readFloat();
		//h = inp.readFloat();
		
		tx = inp.readInt();
		ty = inp.readInt();
		tw = inp.readInt();
		th = inp.readInt();
		
		//isActive = inp.readBoolean();
		
		texregion = new TextureRegion(texture, tx, ty, tw, th);
		sprite = new Sprite(x,y,texregion);
		sprite.setWidth(tw);
		sprite.setHeight(th);
		
		body = PhysicsFactory.createBoxBody(world, sprite, BodyType.StaticBody, FIX_DEF);
		
		wpo = new WayPointObj(body, sprite, player);
		
		ContactListenerManager.addListener(new ContactListener()
		{
			@Override
			public void beginContact(Contact contact) 
			{
				//Log.e("RocketScience->WayPointObj", "->beginContact()");
				if ((contact.getFixtureA().getBody().equals(body) || 
						contact.getFixtureB().getBody().equals(body)) &&
						contact.isTouching()) // actually touching
				{
					wpo.player.setWayPoint(wpo);
				}
			}
			@Override
			public void endContact(Contact contact) 
			{
				// TODO Auto-generated method stub	
			}
		});
		
		//if (isActive)
		//	player.setWayPoint(wpo);
		
		section.getTopLayer().addEntity(sprite);
		
		return wpo;
	}
	
	public boolean isActive(){ return active; }
	
	public void activate() 
	{ 
		active = true; 
		// update the texture
		this.sprite.getTextureRegion().setTexturePosition(inactivetx + this.sprite.getTextureRegion().getWidth(), this.sprite.getTextureRegion().getTexturePositionY());
	}
	
	public void deActivate() 
	{ 
		active = false; 
		// update the texture
		this.sprite.getTextureRegion().setTexturePosition(inactivetx, this.sprite.getTextureRegion().getTexturePositionY());
	}
	
	public void toggle()
	{ 
		if (active == true) 
			deActivate();
		else
			activate();
	}
}
