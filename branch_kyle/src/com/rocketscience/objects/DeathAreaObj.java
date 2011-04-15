package com.rocketscience.objects;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.anddev.andengine.entity.primitive.Polygon;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.rocketscience.helpers.CollisionFilter;
import com.rocketscience.helpers.ContactListenerManager;
import com.rocketscience.helpers.ObjectKeys;
import com.rocketscience.helpers.ObjectLoader;
import com.rocketscience.helpers.PolygonHelper;
import com.rocketscience.level.Section;
import com.rocketscience.mobs.BodyWithActions;
import com.rocketscience.player.Player;

/*
 * this class kills players (and if you set the flag, other stuff) on contact
 */

public class DeathAreaObj extends BaseObject
{
	// NOTE!!!!!
	// you must never try to draw a DeathArea. They have a null texture AND THIS WILL CAUSE ERRORS
	// if you want a graphic associated, then create a separate object with a graphic in the same place.
	private static FixtureDef FIX_DEF_KILL_PLAYER = getFixtureDefKillPlayer(),
	                          FIX_DEF_KILL_ALL = getFixtureDefKillAll();
	private static FixtureDef getFixtureDefKillPlayer()
	{
		final FixtureDef fixdef = new FixtureDef();
		fixdef.isSensor = true;
		fixdef.filter.categoryBits = CollisionFilter.CATEGORY_NORMAL;
		fixdef.filter.maskBits = CollisionFilter.MASK_PLAYER; // only collide with the player
		return fixdef;
	}
	private static FixtureDef getFixtureDefKillAll()
	{
		final FixtureDef fixdef = new FixtureDef();
		fixdef.isSensor = true;
		fixdef.filter.categoryBits = CollisionFilter.CATEGORY_NORMAL;
		fixdef.filter.maskBits = CollisionFilter.MASK_NORMAL; // Collide with normal things (no idea wtf I mean by that)
		return fixdef;
	}
	
	public DeathAreaObj(Body b) 
	{
		super(b, null, ObjectKeys.DEATH_AREA);
	}
	
	public static DeathAreaObj Load(final DataInputStream inp, final Context context, final PhysicsWorld world, final Player player) throws IOException
	{
		// SYNTAX:
		// float basex, basey
		// int numVerts
		// (for each vert): float x,y
		// boolean killAll
		// NOTE!!!!!
		// you must never try to draw a DeathArea. They have a null texture AND THIS WILL CAUSE ERRORS
		// if you want a graphic associated, then create a separate object with a graphic in the same place.
		final DeathAreaObj wpo;
		final float basex, basey; // the base x and y, vertices are relative
		final int numVerts; // number of vertices. the DeathArea is a polygon
		final ArrayList<Vector2> verts; // the vertices of the DeathArea
		final boolean killAll; // should this DeathArea kill anything that hits it, or only the player?
		final Body body;
		
		basex = inp.readFloat();
		basey = inp.readFloat();
		
		numVerts = inp.readInt();
		verts = new ArrayList<Vector2>();
		
		for (int i = 0; i < numVerts; i++)
		{
			verts.add(new Vector2(inp.readFloat() / Section.PIXRATIO, inp.readFloat() / Section.PIXRATIO));
		}
		
		killAll = inp.readBoolean();
		
		body = PolygonHelper.getPolygonBody(basex, basey, Section.PIXRATIO, world, BodyType.StaticBody, 
				                            (killAll) ? FIX_DEF_KILL_ALL : FIX_DEF_KILL_PLAYER, verts);
		body.setTransform(new Vector2(basex, basey).mul(1 / Section.PIXRATIO), 0);
		wpo = new DeathAreaObj(body);

		ContactListenerManager.addListener(new ContactListener()
		{
			@Override
			public void beginContact(Contact contact) 
			{
				Fixture a = contact.getFixtureA(),
				        b = contact.getFixtureB();
				if ((a.getBody().equals(body) || 
						b.getBody().equals(body)) &&
						contact.isTouching()) // actually touching
				{
					//TODO handle real death
					if (a.getBody().equals(player) || b.getBody().equals(player))
					{
						player.spawn();
						Toast.makeText(context, "You have died.", Toast.LENGTH_SHORT);
					}
				}
			}
			@Override
			public void endContact(Contact contact) 
			{
				// TODO Auto-generated method stub	
			}
		});
		
		return wpo;
	}
}
