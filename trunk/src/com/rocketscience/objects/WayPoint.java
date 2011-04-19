package com.rocketscience.objects;

import java.io.DataInputStream;
import java.io.IOException;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;

import android.content.Context;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.rocketscience.helpers.CollisionFilter;
import com.rocketscience.helpers.ContactListenerManager;
import com.rocketscience.helpers.ObjectKeys;
import com.rocketscience.helpers.ObjectLoader;
import com.rocketscience.helpers.ObjectLoadingAdapter;
import com.rocketscience.level.Section;
import com.rocketscience.player.Player;

public class WayPoint extends BaseObject implements ContactListener
{
	private static final String TEX_FILE = "gfx/obj/waypoint.png";
	private static final int TEX_WIDTH = 64, TEX_HEIGHT = 32; // remember, the texture is 2 cells. deactive and active
	private static TextureRegion TEX_REGION = null;
	private static final FixtureDef FIX_DEF = getFixtureDef();
	
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
	
	public WayPoint(Body b, Sprite s) 
	{
		super(b,s, ObjectKeys.WAY_POINT);
		
		sprite = s;
		inactivetx = s.getTextureRegion().getTexturePositionX();
		active = false;
		
		ContactListenerManager.addListener(this);
	}
	
	public static void LoadResources(Context context, TextureManager texman)
	{
		final Texture tex = new Texture(TEX_WIDTH, TEX_HEIGHT);
		TEX_REGION = TextureRegionFactory.createFromAsset(tex, context, TEX_FILE, 0, 0);
		TEX_REGION.setWidth((int)(TEX_WIDTH * 0.5f));
		
		texman.loadTexture(tex);
	}

	public static WayPoint Load(final DataInputStream inp, final Section section, final PhysicsWorld world) throws IOException
	{
		final float x, y;
		final float angle, angularVelocity;
		
		x = inp.readFloat();
		y = inp.readFloat();
		angle = inp.readFloat();
		angularVelocity = inp.readFloat();
		
		if (TEX_REGION == null)
			WayPoint.LoadResources(ObjectLoader.context, ObjectLoader.engine.getTextureManager());
		
		return Create(x, y, angle, angularVelocity, world, section);
	}
	
	public static WayPoint Create(final float x, final float y, final float ang, final float angVel,
									 final PhysicsWorld pw, final Section section)
	{
		final Sprite sprite = new Sprite(x, y, TEX_REGION.clone());
		
		final Body body = PhysicsFactory.createBoxBody(pw, sprite, BodyType.StaticBody, FIX_DEF);
			
		section.getTopLayer().addEntity(sprite);
		
		return new WayPoint(body, sprite);
	}
	
	public static ObjectLoadingAdapter getLoadingAdapter()
	{
		return new ObjectLoadingAdapter(ObjectKeys.WAY_POINT)
		{
			@Override
			public BaseObject load(DataInputStream inp) throws IOException 
			{
				return WayPoint.Load(inp, ObjectLoader.currentSection, ObjectLoader.physicsWorld);
			}	
		};
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

	@Override
	public void beginContact(Contact contact) 
	{
		// assign some temp vars
		final Body bodyA, bodyB;
		final BaseObject objA, objB;
		bodyA = contact.getFixtureA().getBody();
		bodyB = contact.getFixtureB().getBody();
		// if any userData is null, return
		if ((objA = (BaseObject)bodyA.getUserData()) == null)
			return;
		if ((objB = (BaseObject)bodyB.getUserData()) == null)
			return;
		// check if we got a match
		if (contact.isTouching())
		{
			BaseObject me = null, it = null;
			if (bodyA == this.body)
			{
				me = objA;
				it = objB;
			}
			if (bodyB == this.body)
			{
				me = objB;
				it = objA;
			}
			
			if (me != null && it.type == ObjectKeys.PLAYER)
			{
				((Player)it).setWayPoint(this);
			}// player touched me (teehee)
		}// isTouching
	}// beginContact

	@Override
	public void endContact(Contact contact) 
	{
		
	}
}
