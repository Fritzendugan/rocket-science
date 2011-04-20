package com.rocketscience.level;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.ZoomCamera;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.entity.primitive.Polygon;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.AutoParallaxBackground;
import org.anddev.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.rocketscience.BoundingBox;
import com.rocketscience.helpers.CollisionFilter;
import com.rocketscience.helpers.ObjectKeys;
import com.rocketscience.helpers.ObjectLoader;
import com.rocketscience.helpers.ObjectLoadingAdapter;
import com.rocketscience.helpers.PolygonHelper;
import com.rocketscience.mobs.MoveNode;
import com.rocketscience.objects.BaseObject;
import com.rocketscience.objects.BodyWithActions;
import com.rocketscience.player.Player;

/* 
 * this class represents a section of a level 
 * 
 * the section can then be added or removed from the scene as needed.
 */

public class Section extends Scene implements AnalogOnScreenControl.IAnalogOnScreenControlListener
{
	protected final static int AUTOPARALLAXBACKGROUND_UPDATES_PER_SECOND = 5;
	
	public final static float PIXRATIO = org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
	public final static float ONE_PIXEL = 1 / PIXRATIO;
	protected final static FixtureDef FIX_DEF = PhysicsFactory.createFixtureDef(0f,0.1f,0.5f, false, CollisionFilter.CATEGORY_NONPLAYER, CollisionFilter.MASK_NORMAL, (short) 0);
	protected final static FixtureDef FIX_DEF_NOCOLLISION = PhysicsFactory.createFixtureDef(0f, 0f, 0f, true, CollisionFilter.CATEGORY_NOCOLLISION, CollisionFilter.MASK_NOCOLLISION, (short) 0);
	private final static TreeMap<FixDefKey, FixtureDef> fixDefs = new TreeMap<FixDefKey, FixtureDef>(); // used for loading polygons
	final static FixDefKey fixDefLookup = new FixDefKey(0,0);
	
	protected final Engine myEngine;
	protected final ZoomCamera myCamera;
	protected final PhysicsWorld myWorld;
	protected final TreeMap<Long, BaseObject> objects = new TreeMap<Long, BaseObject>();
	protected final TreeMap<Short, Texture> textures; // textures used in the level
	protected final LevelScreen level; // the level to which this belongs
	protected final Player player;
	protected final BoundingBox boundingBox; // the boundaries of the section
	protected final short key;
	

	public Section(final int pLayerCount, final LevelScreen ls, final PhysicsWorld pw, final ZoomCamera c, 
			final Engine e, final TreeMap<Short, Texture> t, final Player p, final BoundingBox bb, final short k) 
	{
		super(pLayerCount);
		myEngine = e;
		myCamera = c;
		myWorld = pw;
		level = ls;
		textures = t;
		key = k;
		
		boundingBox = bb;
		player = p;
		
		// add the bounding box to the world
		Vector2 pos;
	}

	/*
	 * remove an object with the given id.
	 * if no object was found for the given id, this fact is logged.
	 */
	public void removeObject(final long id)
	{
		objects.remove(id);
	}
	
	/*
	 * loads an object:
	 * reads the key, and passes the key to ObjectLoader and loads it.
	 */
	protected BaseObject loadObject(final DataInputStream inp, final Context context) throws IOException
	{		
		final short key = inp.readShort();
		
		return ObjectLoader.loadObject(inp, key);
	}
	
	/*
	 * same as loadCircle except it's a polygon
	 */
	public BodyWithActions loadPolygon(final DataInputStream inp, final Context context, final short myKey, final short relKey) throws IOException
	{
		final int numVerts;
		final ArrayList<Vector2> verts = new ArrayList<Vector2>();
		final float cx, cy, angle, angularVelocity;
		final float elasticity, friction;
		final float[] borderColor = new float[4];
		final int fallsDown, hp;
		final boolean isSensor; // whether the body is a sensor
		final float damageDealt; // how much damage it deals
		final long damageInterval; // interval when the damage is dealt, -1 for none
		MoveNode[] moveNodes;
		FixtureDef polyDef;
		// load attributes
		cx = inp.readFloat() * Section.ONE_PIXEL;
		cy = inp.readFloat() * Section.ONE_PIXEL;
		angle = inp.readFloat();
		angularVelocity = inp.readFloat();
		// load shape data
		numVerts = inp.readInt();
		for (int i = 0; i < numVerts; i++)
		{
			verts.add(new Vector2(cx + inp.readFloat() * Section.ONE_PIXEL, cy + inp.readFloat() * Section.ONE_PIXEL));
		}
		// get fixture def
		this.fixDefLookup.elasticity = inp.readFloat();
		this.fixDefLookup.friction = inp.readFloat();
		polyDef = fixDefs.get(fixDefLookup);
		if (polyDef == null)
		{
			polyDef = PhysicsFactory.createFixtureDef(0, fixDefLookup.elasticity, fixDefLookup.friction);
			polyDef.filter.categoryBits = CollisionFilter.CATEGORY_NORMAL;
			polyDef.filter.maskBits = CollisionFilter.MASK_NORMAL;
			fixDefs.put(fixDefLookup.clone(), polyDef);
		}
		// get movePath
		moveNodes = new MoveNode[inp.readInt()];
		for (int i = 0; i < moveNodes.length; i++)
		{
			moveNodes[i] = new MoveNode(inp.readFloat() * Section.ONE_PIXEL, inp.readFloat() * Section.ONE_PIXEL, inp.readLong(), inp.readLong());
		}
		if (moveNodes.length < 2)
			moveNodes = null;
		// get texture
		Texture tex = textures.get(inp.readShort());
		// read border Color
		for (int i = 0; i < 4; i++)
		{
			borderColor[i] = inp.readFloat();
		}
		// read flags
		fallsDown = inp.readInt();
		hp = inp.readInt();
		isSensor = inp.readBoolean();
		if ((damageDealt = inp.readFloat()) > 0)
			damageInterval = inp.readLong();
		else
			damageInterval = -1;
		
		// do work
		final float[] triverts = PolygonHelper.getTriangulatedVertices(verts, Section.PIXRATIO);
		final Polygon poly = PolygonHelper.getPolygon(0, 0, triverts, tex, 0, 0, tex.getWidth(), tex.getHeight());
		poly.setUpdatePhysics(false);
		this.getTopLayer().addEntity(poly);

		// create the circle body
		if (isSensor)
		{
			final FixtureDef temp = PhysicsFactory.createFixtureDef(polyDef.density, 
					                 polyDef.restitution, polyDef.friction, true, 
					                 polyDef.filter.categoryBits, polyDef.filter.maskBits, 
					                 polyDef.filter.groupIndex);

			polyDef = temp;
		}
		final Body body = PolygonHelper.getPolygonBody(cx, cy, Section.PIXRATIO, myWorld, BodyType.StaticBody, polyDef, verts);
		myWorld.registerPhysicsConnector(new PhysicsConnector(poly, body, true, true, false, false));
		body.setTransform(new Vector2(cx * Section.ONE_PIXEL, cy * Section.ONE_PIXEL), 0);

		// build bwa
		BodyWithActions bwa = new BodyWithActions(moveNodes, body, poly, angularVelocity, isSensor, damageDealt, damageInterval, myKey, relKey);
		this.registerUpdateHandler(bwa);

		return bwa;
	}
	
	public ObjectLoadingAdapter getWorldShapeLoadingAdapter()
	{
		return new ObjectLoadingAdapter(ObjectKeys.BODY_WITH_ACTIONS)
		{
			@Override
			public BaseObject load(final DataInputStream inp, final short mk, final short rk) throws IOException 
			{
				return loadPolygon(inp, Section.this.level.getLoadingScreen(), mk, rk);
			}
		};
	}
	
	/*
	 * reads the background and foreground information
	 * as well as the background music
	 */
	protected void loadEnvironment(final DataInputStream inp) throws IOException
	{
		//readBackground
		final float r,g,b; // the background colors
		final int numP; // the number of parallax backgrounds
		TextureRegion tempRegion; // the current region we're loading
		Texture       tempTexture; // the current texture we're using
		float		  x,y;         // the x and y location of the entity
		float	      pF;		   // the parallax factor of the entity
		final AutoParallaxBackground autoPB; // the parallax background we're building

		// instantiate apb
		// background color
		r = inp.readFloat();
		g = inp.readFloat();
		b = inp.readFloat();
		autoPB = new AutoParallaxBackground(r,g,b, Section.AUTOPARALLAXBACKGROUND_UPDATES_PER_SECOND);
		// attach entities
		numP = inp.readInt();
		for (int i = 0; i < numP; i++)
		{
			pF = inp.readFloat();
			x = inp.readFloat();
			y = inp.readFloat();
			tempTexture = textures.get(inp.readShort());
			tempRegion = new TextureRegion(tempTexture, 0, 0, tempTexture.getWidth(), tempTexture.getHeight());
			autoPB.addParallaxEntity(new ParallaxEntity(pF, new Sprite(x,y,tempRegion)));
		}
		// finish 'er up
		this.setBackground(autoPB);
		
		// readForeground
		short weatherFlag = inp.readShort();
		
		// read music key
		short musicKey = inp.readShort();
		
		//TODO: handle background music
		//TODO: handle the weather flag
	}
	
	/*
	 * loads a section of the level
	 */
	public void load(final DataInputStream inp, final Context context) throws IOException
	{	
		// load background and foreground
		this.loadEnvironment(inp);
		
		// load objects
		ObjectLoader.currentSection = this;
		ObjectLoader.physicsWorld = myWorld;
		ObjectLoader.player = player;
		ObjectLoader.context = context;
		ObjectLoader.engine = this.myEngine;
		ObjectLoader.currentLevel = this.level;
		ObjectLoader.generateLoadingTree();
		final int numObjects = inp.readInt();
		for (int i = 0; i < numObjects; i++)
		{
			final BaseObject obj = loadObject(inp, context);
			objects.put(obj.getMyKey(), obj);
		}
	}
	
	public void setAsCurrentSection()
	{
		level.setCurrentSection(key);
	}
	
	public void makeActive()
	{

		for (Entry<Long, BaseObject> e : objects.entrySet())
		{
			e.getValue().getBody().setActive(true);
		}			
	}
	
	public void makeInactive()
	{
		for (Entry<Long, BaseObject> e : objects.entrySet())
		{
			e.getValue().getBody().setActive(false);
		}
	}
	
	public LevelScreen getLevel()
	{
		return this.level;
	}
	
	public BoundingBox getBoundaries()
	{
		return boundingBox;
	}
	
	public short getKey()
	{
		return this.key;
	}
	
	public void dumpToLog()
	{
		Log.e("RocketScience->SectionDUMP", "dumping Objects to log");
		for (Entry<Long, BaseObject> e : objects.entrySet())
		{
			Log.e("RocketScience->SectionDUMP", e.getValue().toString());
		}
		
		Log.e("RocketScience->SectionDUMP", "Section Dump done.");
	}
	
	public void runOnUpdateThread(final Runnable r)
	{
		myEngine.runOnUpdateThread(r);
	}

	@Override
	public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onControlChange(BaseOnScreenControl control, float pValueX, float pValueY) 
	{
		player.onControlChange(control, pValueX, pValueY);
	}
	
	/*
	 * I still have no idea how the return values work for touch events in andengine
	 * I know that in android, you return true if you've handled the event and no further
	 * processing is necessary, or false to let other events go.
	 * 
	 * I think the general philosophy with andengine is always return true
	 */
	@Override
	public boolean onSceneTouchEvent(TouchEvent e) 
	{
		//System.out.println("Number of fingers down: " + String.valueOf(e.getMotionEvent().getPointerCount()));
		// process gestures
		//if (this.level.getScaleGestureDetector().onTouchEvent(e.getMotionEvent()) == false)
		//	return true;
		
		// process player
		if (player.onSceneTouchEvent(e) == false)
			return true; // player says we're done processing events
		
		// run the super
		super.onSceneTouchEvent(e);
		
		return true;
	}
}

class FixDefKey implements Comparable<FixDefKey>
{
	public float elasticity, friction;
	
	public FixDefKey(final float e, final float f)
	{
		elasticity = e;
		friction = f;
	}
	
	public FixDefKey clone()
	{
		return new FixDefKey(this.elasticity, this.friction);
	}

	@Override
	public int compareTo(FixDefKey a) 
	{
		// first compare elasticity
		if (a.elasticity < this.elasticity)
			return -1;
		if (a.elasticity > this.elasticity)
			return 1;
		
		// elasticity is the same
		if (a.friction < this.friction)
			return -1;
		if (a.friction > this.friction)
			return 1;
		
		// friction is the same too
		return 0;
	}
}
