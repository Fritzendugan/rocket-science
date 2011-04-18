package com.rocketscience.objects;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
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
	private final static TreeMap<Short, Door> doors = new TreeMap<Short, Door>();
	
	private final short mKey, tKey; // my key and the teleport key
	private final Section section;
	
	private Door destination;
	private boolean ignoreContact = false;
	
	public Door(final Body b, final Shape s, final Section sec, final short mk, final short tk)
	{
		super(b, s, ObjectKeys.DOOR);
		
		mKey = mk;
		tKey = tk;
		section = sec;
		
		ContactListenerManager.addListener(this);
		doors.put(mKey, this);
	}
	
	public void teleportHere(Player p)
	{
		this.ignoreContact = true;
		p.sendTo(section, this.body.getWorldCenter());
	}

	public static Door Load(DataInputStream inp) throws IOException
	{
		final float cx, cy, angle, angularVelocity;
		final short myKey, teleportKey;
		// read values
		cx = inp.readFloat();
		cy = inp.readFloat();
		angle = inp.readFloat();
		angularVelocity = inp.readFloat();
		myKey = inp.readShort();
		teleportKey = inp.readShort();
		// make shape
		if (TEX_REGION == null)
			Door.LoadResources(ObjectLoader.engine.getTextureManager(), ObjectLoader.context);
		
		return Create(cx, cy, angle, angularVelocity, myKey, teleportKey, ObjectLoader.physicsWorld, ObjectLoader.currentSection);
	}
	
	public static Door Create(final float cx, final float cy, final float angle, final float angularVelocity,
			                  final short myKey, final short teleKey, final PhysicsWorld pw, final Section section)
	{
		final Sprite sprite = new Sprite(0, 0, TEX_REGION);
		final Body body = PhysicsFactory.createBoxBody(pw, sprite, BodyType.StaticBody, FIXDEF);
		pw.registerPhysicsConnector(new PhysicsConnector(sprite, body));
		section.getTopLayer().addEntity(sprite);
		
		return new Door(body, sprite, section, myKey, teleKey);
	}
	
	private static void LoadResources(final TextureManager texman, final Context context)
	{
		final Texture texture = new Texture(TEX_WIDTH, TEX_HEIGHT);
		TEX_REGION = TextureRegionFactory.createFromAsset(texture, context, texturePath, 0, 0);
		texman.loadTexture(texture);
	}
	
	public static void ResetDoorMap()
	{
		doors.clear();
	}
	
	public static void BuildDoorMap()
	{
		for (Entry<Short, Door> e : doors.entrySet())
		{
			Door curDoor = e.getValue();
			curDoor.destination = doors.get(curDoor.tKey);
		}
	}

	@Override
	public void beginContact(Contact contact) 
	{
		if (this.ignoreContact)
			return;
		
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
			this.destination.teleportHere((Player)objB);
		}
		if (objB.equals(this) && objA.type == ObjectKeys.PLAYER)
		{
			this.destination.teleportHere((Player)objA);
		}
	}

	@Override
	public void endContact(Contact contact) 
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
			this.ignoreContact = false;
		}
		if (objB.equals(this) && objA.type == ObjectKeys.PLAYER)
		{
			this.ignoreContact = false;
		}
	}
}
