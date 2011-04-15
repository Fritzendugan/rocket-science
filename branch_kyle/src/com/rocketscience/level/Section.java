package com.rocketscience.level;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import org.anddev.andengine.util.Debug;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.rocketscience.helpers.CollisionFilter;
import com.rocketscience.helpers.ObjectLoader;
import com.rocketscience.helpers.PolygonHelper;
import com.rocketscience.mobs.BodyWithActions;
import com.rocketscience.mobs.MoveNode;
import com.rocketscience.objects.BaseObject;
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
	protected final Engine myEngine;
	protected final ZoomCamera myCamera;
	protected final PhysicsWorld myWorld;
	protected final ArrayList<BodyWithActions> worldShapes = new ArrayList<BodyWithActions>();
	protected final ArrayList<BaseObject> objects = new ArrayList<BaseObject>();
	protected final TreeMap<Short, Texture> textures; // textures used in the level
	protected final LevelScreen level; // the level to which this belongs
	protected final Player player;
	protected final Rectangle boundingBox; // the boundaries of the section
	protected final short key;
	

	public Section(final int pLayerCount, final LevelScreen ls, final PhysicsWorld pw, final ZoomCamera c, 
			final Engine e, final TreeMap<Short, Texture> t, final Player p, final Rectangle bb, final short k) 
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
		final Body left, top, right, bottom;
		Vector2 pos;
		
		//TODO: all this boundary stuff needs to be handled differently
		Rectangle s = new Rectangle(bb.getX() - 100, bb.getY(), 100, bb.getHeight());
		left = PhysicsFactory.createBoxBody(pw, s, BodyType.StaticBody, FIX_DEF);
		pos = new Vector2(s.getX() + s.getWidth() * 0.5f, s.getY() + s.getHeight() * 0.5f).mul(1 / Section.PIXRATIO);
		left.setTransform(pos, 0);
		
		s = new Rectangle(bb.getX(), bb.getY() - 100, bb.getWidth(), 100);
		top = PhysicsFactory.createBoxBody(pw, s, BodyType.StaticBody, FIX_DEF);
		pos = new Vector2(s.getX() + s.getWidth() * 0.5f, s.getY() + s.getHeight() * 0.5f).mul(1 / Section.PIXRATIO);
		top.setTransform(pos, 0);

		s = new Rectangle(bb.getX() + bb.getWidth(), bb.getY(), 100, bb.getHeight());
		right = PhysicsFactory.createBoxBody(pw, s, BodyType.StaticBody, FIX_DEF);
		pos = new Vector2(s.getX() + s.getWidth() * 0.5f, s.getY() + s.getHeight() * 0.5f).mul(1 / Section.PIXRATIO);
		right.setTransform(pos, 0);

		s = new Rectangle(bb.getX(), bb.getY() + bb.getHeight(), bb.getWidth(), 100);
		bottom = PhysicsFactory.createBoxBody(pw, s, BodyType.StaticBody, FIX_DEF);
		pos = new Vector2(s.getX() + s.getWidth() * 0.5f, s.getY() + s.getHeight() * 0.5f).mul(1 / Section.PIXRATIO);
		bottom.setTransform(pos, 0);

		left.setUserData(BaseObject.NULL_OBJECT);
		top.setUserData(BaseObject.NULL_OBJECT);
		right.setUserData(BaseObject.NULL_OBJECT);
		bottom.setUserData(BaseObject.NULL_OBJECT);
	}

	/*
	 * remove an object with the given id.
	 * if no object was found for the given id, this fact is logged.
	 */
	public void removeObject(final long id)
	{
		BaseObject remove = null;
		for (BaseObject b : objects)
			if (b.getID() == id)
			{
				remove = b;
				break;
			}
		
		if (remove == null)
		{
			Debug.e("Could not find object with id '" + Long.toString(id) + "' !!");
		}
		else
		{
			objects.remove(remove);
		}
	}
	
	/*
	 * loads UniversalShapeAttributes from the input stream
	 */
	protected UniversalShapeAttributes loadUniversalShapeAttributes(final DataInputStream inp) throws IOException
	{
		final float cx, cy; // centerx, centery
		final float elasticity, friction; // for fixture def
		final Vector2 position;	
		final float av; // angular velocity
		final float[] color = new float[4];// the border color
		
		// read in the position and angular velocity
		cx = inp.readFloat();
		cy = inp.readFloat();
		position = new Vector2(cx, cy);
		av = (float)Math.toRadians(inp.readFloat()); // angular velocity
		
		// read in the fixture def
		elasticity = inp.readFloat();
		friction = inp.readFloat();
		//elasticity = 0.01f;
		//friction = 0.5f;
		// read move path
		final int numVerts = inp.readInt();
		final MoveNode[] nodes;
		if (numVerts > 1)
		{
			nodes = new MoveNode[numVerts];
			for (int i = 0; i < numVerts; i++)
			{
				nodes[i] = new MoveNode(new Vector2((cx + inp.readFloat()) / Section.PIXRATIO, (cy + inp.readFloat()) / Section.PIXRATIO), inp.readLong(), inp.readLong());
			}
		}
		else if (numVerts == 1)
		{
			Debug.e("ERROR! Number of Vertices in the movePath must be either 0 or >1.");
			nodes = null;
		}
		else
		{
			nodes = null;
		}
		
		final Short texkey = inp.readShort();
		
		// load the border color
		for (int i = 0; i < 4; i++)
			color[i] = inp.readFloat();
		
		final int fallsdown = inp.readInt(),
				  hp = inp.readInt();
		
		return new UniversalShapeAttributes(position, av, nodes, texkey, fallsdown, hp, color, elasticity, friction);
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
	 * loads a circle body and adds it to the scene and all that jazz
	 */
	/*// old code
	protected BodyWithActions loadCircle(final DataInputStream inp, final Context context) throws IOException
	{
		final float radius;
		final UniversalShapeAttributes usa;
		// load attributes
		radius = inp.readFloat();
		usa = loadUniversalShapeAttributes(inp);
		
		Texture tex = textures.get(usa.texKey);
		TiledTextureRegion tr = new TiledTextureRegion(tex, 0, 0, tex.getWidth(), tex.getHeight(), 1, 1);	
		Sprite sprite = new Sprite(usa.position.x,usa.position.y,tr);
		sprite.setWidth(radius);
		sprite.setHeight(radius);
		sprite.setPosition(usa.position.x - radius * 0.5f, usa.position.y - radius * 0.5f);
		sprite.setUpdatePhysics(false);
		this.getTopLayer().addEntity(sprite);
		
		// create the box body
		//TODO: fixture defs!!!!!!
		final Body body = PhysicsFactory.createCircleBody(myWorld, sprite, BodyType.StaticBody, FIX_DEF);
		body.setAngularVelocity(usa.angularVelocity);
		myWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, true, false, false));
		
		// build the bwa
		BodyWithActions bwa = new BodyWithActions(usa.nodes, body, sprite);
		this.registerUpdateHandler(bwa);
		
		return bwa;
	} //*/
	
	/*
	 * same as loadcircle except (OMGOSH!!!) it's a rectangle
	 */
	/*// old code
	protected BodyWithActions loadRectangle(final DataInputStream inp, final Context context) throws IOException
	{
		final float w,h;
		final UniversalShapeAttributes usa;
		// load attributes
		w = inp.readFloat();
		h = inp.readFloat();
		usa = loadUniversalShapeAttributes(inp);
		
		Texture tex = textures.get(usa.texKey);
		TiledTextureRegion tr = new TiledTextureRegion(tex, 0, 0, tex.getWidth(), tex.getHeight(), 1, 1);
		Sprite sprite = new Sprite(usa.position.x, usa.position.y, tr);
		sprite.setWidth(w);
		sprite.setHeight(h);
		sprite.setPosition(usa.position.x - w * 0.5f, usa.position.y - h * 0.5f);
		sprite.setUpdatePhysics(false);
		this.getTopLayer().addEntity(sprite);
		
		// create the circle body
		//TODO: fixture defs!!!!!!
		final Body body = PhysicsFactory.createBoxBody(myWorld, sprite, BodyType.StaticBody, FIX_DEF);
		body.setAngularVelocity(usa.angularVelocity);
		myWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, true, false, false));
		
		// build the bwa
		BodyWithActions bwa = new BodyWithActions(usa.nodes, body, sprite);
		this.registerUpdateHandler(bwa);

		return bwa;
	} //*/
	
	/*
	 * same as loadCircle except it's a polygon
	 */
	protected BodyWithActions loadPolygon(final DataInputStream inp, final Context context) throws IOException
	{
		final int numVerts;
		final ArrayList<Vector2> verts = new ArrayList<Vector2>();
		final UniversalShapeAttributes usa;
		// load attributes
		numVerts = inp.readInt();
		for (int i = 0; i < numVerts; i++)
		{
			verts.add(new Vector2(inp.readFloat() / Section.PIXRATIO, inp.readFloat() / Section.PIXRATIO));
		}
		usa = loadUniversalShapeAttributes(inp);
		
		Texture tex = textures.get(usa.texKey);
		final float[] triverts = PolygonHelper.getTriangulatedVertices(verts, Section.PIXRATIO);
		final Polygon poly = PolygonHelper.getPolygon(usa.position.x, usa.position.y, triverts, tex, 0, 0, tex.getWidth(), tex.getHeight());
		poly.setUpdatePhysics(false);
		this.getTopLayer().addEntity(poly);

		// create the circle body
		final Body body = PolygonHelper.getPolygonBody(usa.position.x, usa.position.y, Section.PIXRATIO, myWorld, BodyType.StaticBody, usa.fixdef, verts);
		myWorld.registerPhysicsConnector(new PhysicsConnector(poly, body, true, true, false, false));
		body.setTransform(usa.position.tmp().mul(1 / Section.PIXRATIO), 0);
		// create motor if av is non-zero
		/*if (usa.angularVelocity != 0)
		{
			final Rectangle r = new Rectangle(body.getWorldCenter().x - 16, body.getWorldCenter().y - 16, 16, 16);
			final Body anchor = PhysicsFactory.createBoxBody(myWorld, r, BodyType.StaticBody, FIX_DEF_NOCOLLISION);
			body.setType(BodyType.DynamicBody);
			anchor.setTransform(body.getWorldCenter(), 0);
			final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
			revoluteJointDef.initialize(anchor, body, anchor.getWorldCenter());
			revoluteJointDef.enableMotor = true;
            revoluteJointDef.motorSpeed = usa.angularVelocity;
            revoluteJointDef.maxMotorTorque = 200;

            this.myWorld.createJoint(revoluteJointDef);
		}*/
		
		// build bwa
		BodyWithActions bwa = new BodyWithActions(usa.nodes, body, poly, usa.angularVelocity);
		this.registerUpdateHandler(bwa);

		return bwa;
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
		// r = (float)inp.readShort() / 255;
		// g = (float)inp.readShort() / 255;
		// b = (float)inp.readShort() / 255;
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
			tempTexture = textures.get(inp.readByte());
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
		
		// load shapes
		final int numPolygons = inp.readInt();
		for (int i = 0; i < numPolygons; i++)
		{
			final BodyWithActions bwa = loadPolygon(inp,context);
			worldShapes.add(bwa);
		}
		
		// load objects
		ObjectLoader.currentSection = this;
		ObjectLoader.physicsWorld = myWorld;
		ObjectLoader.player = player;
		ObjectLoader.context = context;
		final int numObjects = inp.readInt();
		for (int i = 0; i < numObjects; i++)
		{
			objects.add(loadObject(inp, context));
		}
	}
	
	public LevelScreen getLevel()
	{
		return this.level;
	}
	
	public Rectangle getBoundaries()
	{
		return boundingBox;
	}
	
	public void dumpToLog()
	{
		Log.e("RocketScience->SectionDUMP", "dumping World Shapes to log");
		for (BodyWithActions bwa : worldShapes)
		{
			Log.e("RocketScience->SectionDUMP", bwa.toString());
		}
		
		Log.e("RocketScience->SectionDUMP", "dumping Objects to log");
		for (BaseObject bwa : objects)
		{
			Log.e("RocketScience->SectionDUMP", bwa.toString());
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
		myCamera.setZoomFactor(myCamera.getZoomFactor() * (1 +pValueY));
	}
	
	@Override
	public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) 
	{
		if (player.onSceneTouchEvent(pSceneTouchEvent) == false)
			return false; // player says we're done processing events
		
		return super.onSceneTouchEvent(pSceneTouchEvent);
	}
}
