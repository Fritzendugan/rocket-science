package com.rocketscience.objects;

import java.io.DataInputStream;
import java.io.IOException;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;

import android.content.Context;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.rocketscience.helpers.CollisionFilter;
import com.rocketscience.helpers.ContactListenerManager;
import com.rocketscience.helpers.ObjectKeys;
import com.rocketscience.helpers.ObjectLoader;
import com.rocketscience.level.Section;
import com.rocketscience.player.Player;

public class LevelEnder extends BaseObject implements ContactListener, IUpdateHandler
{
	private static TextureRegion TEX_REGION = null;
	private static final int TEX_WIDTH = 32, TEX_HEIGHT = 64;
	private static final String TEX_PATH = "gfx/obj/door1.PNG";
	private final static FixtureDef FIXDEF = PhysicsFactory.createFixtureDef(1f, 0f, 0f, true, 
	        CollisionFilter.CATEGORY_NORMAL, 
	        CollisionFilter.MASK_PLAYER, (short)0);
	
	public static LevelEnder LevelEnderTouchingPlayer = null;
	
	private Player touchingPlayer = null;
	
	public void LoadResources(Context context, TextureManager texman)
	{
		final Texture tex = new Texture(TEX_WIDTH, TEX_HEIGHT);
		TEX_REGION = TextureRegionFactory.createFromAsset(tex, context, TEX_PATH, 0, 0);
		texman.loadTexture(tex);
	}
	
	public LevelEnder(final Body b, final Sprite s, final short mk, final short rk)
	{
		super(b,s,ObjectKeys.LEVEL_ENDER, mk, rk);
		
		ContactListenerManager.addListener(this);
	}
	
	public static LevelEnder Load(final DataInputStream inp, final short mk, final short rk) throws IOException
	{
		final float cx, cy, ang, angV;
		
		cx = inp.readFloat() * Section.ONE_PIXEL;
		cy = inp.readFloat() * Section.ONE_PIXEL;
		ang = inp.readFloat();
		angV = inp.readFloat();
		
		return Create(cx,cy,ang,angV,mk,rk, ObjectLoader.physicsWorld, ObjectLoader.currentSection);
	}
	
	public static LevelEnder Create(final float cx, final float cy, final float ang, final float angV,
			                 final short mk, final short rk, final PhysicsWorld pw, final Section section)
	{
		final Body body;
		final Sprite sprite;

		// create graphics
		sprite = new Sprite(0,0,TEX_REGION)
		{
			@Override
			public boolean onAreaTouched(TouchEvent e, float pX, float pY)
			{
				if (LevelEnderTouchingPlayer != null)
				{
					//TODO: run quiz and all that
				}
				return true;
			}
		};
		
		// create physics
		body = PhysicsFactory.createBoxBody(pw, sprite, BodyDef.BodyType.StaticBody, FIXDEF);
		pw.registerPhysicsConnector(new PhysicsConnector(sprite, body));
		section.getTopLayer().addEntity(sprite);
		body.setTransform(new Vector2(cx,cy), ang);
		
		return new LevelEnder(body, sprite, mk, rk);
	}

	@Override
	public void beginContact(Contact contact) 
	{
		if (touchingPlayer != null)
			return; // if we already know we're touching, who cares
		
		
		final Body bodyA, bodyB;
		final BaseObject objA, objB;
		bodyA = contact.getFixtureA().getBody();
		bodyB = contact.getFixtureB().getBody();
		if ((objA = (BaseObject)bodyA.getUserData()) == null)
			return;
		if ((objB = (BaseObject)bodyB.getUserData()) == null)
			return;
		
		// check if we got a match
		if (objA.equals(this) && objB.type == ObjectKeys.PLAYER)
		{
			touchingPlayer = (Player)objB;
			LevelEnderTouchingPlayer = this;
		}
		if (objB.equals(this) && objA.type == ObjectKeys.PLAYER)
		{
			touchingPlayer = (Player)objA;
			LevelEnderTouchingPlayer = this;
		}
		
	}

	@Override
	public void endContact(Contact contact) 
	{
		if (touchingPlayer == null)
			return; // if we don't need to set this to false, who cares
		
		
		final Body bodyA, bodyB;
		final BaseObject objA, objB;
		bodyA = contact.getFixtureA().getBody();
		bodyB = contact.getFixtureB().getBody();
		if ((objA = (BaseObject)bodyA.getUserData()) == null)
			return;
		if ((objB = (BaseObject)bodyB.getUserData()) == null)
			return;
		
		// check if we got a match
		if (objA.equals(this) && objB.type == ObjectKeys.PLAYER)
		{
			touchingPlayer = null;
			LevelEnderTouchingPlayer = null;
		}
		if (objB.equals(this) && objA.type == ObjectKeys.PLAYER)
		{
			touchingPlayer = null;
			LevelEnderTouchingPlayer = null;
		}
		
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
