package com.rocketscience.objects;

import java.io.DataInputStream;
import java.io.IOException;
import org.anddev.andengine.entity.shape.Shape;
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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.rocketscience.helpers.CollisionFilter;
import com.rocketscience.helpers.ContactListenerManager;
import com.rocketscience.helpers.ObjectKeys;
import com.rocketscience.helpers.ObjectLoader;
import com.rocketscience.helpers.ObjectLoadingAdapter;
import com.rocketscience.level.Section;
import com.rocketscience.player.Player;

public class Door extends BaseObject implements ContactListener
{
	private final static int TEX_WIDTH = 32, TEX_HEIGHT = 64;
	private static TextureRegion TEX_REGION = null;
	private final static String texturePath = "gfx/obj/door1.PNG";
	private final static FixtureDef FIXDEF = PhysicsFactory.createFixtureDef(1f, 0f, 0f, true, 
											        CollisionFilter.CATEGORY_NORMAL, 
											        CollisionFilter.MASK_PLAYER, (short)0);
		
	private Section destination = null;
	private final short destinationKey;
	private final Vector2 location;
	private Player touchingPlayer;
	
	public Door(final Body b, final Shape s, final short key, final Vector2 loc, final short mk, final short rk)
	{
		super(b, s, ObjectKeys.DOOR, mk, rk);
		
		destinationKey = key;
		location = loc;
		
		ContactListenerManager.addListener(this);
	}
	
	public void teleport(Player p)
	{
		if (destination == null)
			destination = p.getLevel().getSection(destinationKey);
		
		p.sendTo(destination, location);
	}

	public static Door Load(final DataInputStream inp, final short myKey, final short relKey) throws IOException
	{
		final float cx, cy, angle, angularVelocity;
		final short destKey;
		final Vector2 location;
		
		// read values
		cx = inp.readFloat() * Section.ONE_PIXEL;
		cy = inp.readFloat() * Section.ONE_PIXEL;
		angle = inp.readFloat();
		angularVelocity = inp.readFloat();
		destKey = inp.readShort();
		location = new Vector2(inp.readFloat(), inp.readFloat()).mul(Section.ONE_PIXEL);;
		
		// make sure resources are loaded
		if (TEX_REGION == null)
			Door.LoadResources(ObjectLoader.engine.getTextureManager(), ObjectLoader.context);
		
		return Create(cx, cy, angle, angularVelocity, ObjectLoader.physicsWorld, 
				      ObjectLoader.currentSection, destKey, location, myKey, relKey);
	}
	
	public static Door Create(final float cx, final float cy, final float angle, final float angularVelocity,
			                  final PhysicsWorld pw, final Section section, final short key, final Vector2 loc,
			                  final short mk, final short rk)
	{
		final Sprite sprite = new Sprite(0, 0, TEX_REGION)
		{
			@Override
			public boolean onAreaTouched(TouchEvent e, float pX, float pY)
			{

				return true;
			}
		};

		final Body body = PhysicsFactory.createBoxBody(pw, sprite, BodyType.StaticBody, FIXDEF);
		pw.registerPhysicsConnector(new PhysicsConnector(sprite, body));
		section.getTopLayer().addEntity(sprite);
		body.setTransform(new Vector2(cx,cy), angle);
		
		return new Door(body, sprite, key, loc, mk, rk);
	}
	
	private static void LoadResources(final TextureManager texman, final Context context)
	{
		final Texture texture = new Texture(TEX_WIDTH, TEX_HEIGHT);
		TEX_REGION = TextureRegionFactory.createFromAsset(texture, context, texturePath, 0, 0);
		texman.loadTexture(texture);
	}
	
	public static ObjectLoadingAdapter getLoadingAdapter()
	{
		return new ObjectLoadingAdapter(ObjectKeys.DOOR)
		{
			@Override
			public BaseObject load(final DataInputStream inp, final short mk, final short rk) throws IOException 
			{
				return Door.Load(inp, mk, rk);
			}
		};
	}

	@Override
	public void beginContact(Contact contact) 
	{	
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
			this.teleport((Player)objB);
		}
		if (objB.equals(this) && objA.type == ObjectKeys.PLAYER)
		{
			this.teleport((Player)objA);
		}
	}

	@Override
	public void endContact(Contact contact) 
	{
		//empty
	}
}
