package com.rocketscience.player;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.rocketscience.helpers.CollisionFilter;
import com.rocketscience.helpers.ContactListenerManager;
import com.rocketscience.helpers.ObjectKeys;
import com.rocketscience.level.Section;
import com.rocketscience.objects.WayPointObj;
import com.rocketscience.objects.creatures.Creature;

public class Player extends Creature
{
	private final static String TEX_FILE = "gfx/mobs/weirdman.png";
	private final static int TEX_WIDTH = 256, TEX_HEIGHT = 128, TEX_COLS = 4, TEX_ROWS = 2, 
	                         TEX_ROWS_PER_ANIMATION = 1;
	private final static FixtureDef FIX_DEF = getFixtureDef(),
									FIX_BOTTOM = getBottomDef();
	private final static int STATE_WALKING = 1,
							 STATE_JUMPING = 2,
							 STATE_FALLING = 3,
							 STATE_FLYING = 4,
							 STATE_STILL = -1;
	private final Body bottom; // the bottom of the player. Used to test if the player is on the ground or hitting an enemy
	private Section activeSection = null; // the section the player is currently on
	private final Engine myEngine;
	private long  score = 0;
	// player stats
	private float maxspeed = 15f;
	private float maxjump  = 15f;
	// state information
	private final Vector2 acceleration = new Vector2(0,0); // this gets mul'd * seconds in update and added
	private WayPointObj lastWayPoint = null; // where he'll respawn
	private int state = STATE_FALLING;
	private boolean faceRight = true;
	final private Vector2 flySpeed = new Vector2();
	private Vector2 spawnPosition = null; // this gets set when the player is "sent" somewhere
	// Controls Information
	private float stickX = 0, stickY = 0;
	
	public Player(final Body b, final AnimatedSprite s, final Body bt, final Engine e)
	{
		super(b,s, ObjectKeys.PLAYER);
		bottom = bt;
		myEngine = e;
	}
	
	private static FixtureDef getFixtureDef()
	{
		final FixtureDef fixdef = PhysicsFactory.createFixtureDef(1, 0.01f, 0.5f);
		fixdef.filter.categoryBits = CollisionFilter.CATEGORY_PLAYER;
		
		return fixdef;
	}
	
	private static FixtureDef getBottomDef()
	{
		final FixtureDef fixdef = new FixtureDef();
		fixdef.isSensor = true;
		fixdef.filter.categoryBits = CollisionFilter.CATEGORY_PLAYER;
		
		return fixdef;
	}
	
	public static Player MakePlayer(final float x, final float y, final PhysicsWorld world, final Context context, final Engine engine)
	{
		final Texture tex = new Texture(TEX_WIDTH, TEX_HEIGHT);
		final TiledTextureRegion texregion = TextureRegionFactory.createTiledFromAsset(tex, context, TEX_FILE, 0, 0, 
				                             TEX_COLS, TEX_ROWS);
		// set up the graphics for the player
		engine.getTextureManager().loadTexture(tex);
		final AnimatedSprite sprite = new AnimatedSprite(x,y,texregion);
		sprite.animate(new long[]{300,300,300,300}, 0, 3, true);
		
		// create the physics body
		final Body body = PhysicsFactory.createBoxBody(world, sprite, BodyType.DynamicBody, FIX_DEF);
		body.setFixedRotation(true);
		body.setBullet(true);
		
		// create the bottom sensor
		final Rectangle rect = new Rectangle(0, 0, sprite.getWidth(), 32);
		final Body bottom = PhysicsFactory.createBoxBody(world, rect, BodyType.DynamicBody, FIX_BOTTOM);
		final WeldJointDef joint = new WeldJointDef();
        joint.initialize(body, bottom, bottom.getPosition());
        joint.collideConnected = false;
        
        world.createJoint(joint);
        
        // create the player object
        final Player player = new Player(body, sprite, bottom, engine);
        
        // create the contactListener for the bottom sensor
        ContactListenerManager.addListener(new ContactListener()
        {
			@Override
			public void beginContact(Contact contact) 
			{	
				final Body a = contact.getFixtureA().getBody(),
				           b = contact.getFixtureB().getBody();
				//Log.e("RocketScience->Player->BottomContactListener", "beginContact bodyA: " + (String)a.getUserData() + " bodyB: " + (String)b.getUserData());
				if ((a.equals(bottom) || b.equals(bottom)) && contact.isTouching())
				{
					player.state = STATE_STILL;
					
					Log.e("RocketScience->Player->BottomContactListener", "Bottom hit something.");
				}
			}
			@Override
			public void endContact(Contact contact) 
			{
				
			}
        });
        // register the grahics
		//scene.getTopLayer().addEntity(sprite);
		world.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, true, false, false));
		
		return player;
	}
			
	public void centerCamera(final Camera cam)
	{
		cam.setChaseShape(sprite);
	}
	
	public void sendTo(final Section section, final Vector2 position)
	{
		// post to update thread to avoid synchronization errors
		myEngine.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run() 
			{
				if (activeSection != null)
				{
					// remove from old section
					activeSection.getTopLayer().removeEntity(Player.this.sprite);
				}
				activeSection = section;
				// add to the new
				Player.this.spawnPosition = position;
				Player.this.body.setTransform(position, 0);
				activeSection.getTopLayer().addEntity(Player.this.sprite);
				
				activeSection.setAsCurrentSection();
			}
		});
	}
	
	public void setWayPoint(WayPointObj wp)
	{
		// deactivate the old one, if any
		if (this.lastWayPoint != null)
			this.lastWayPoint.deActivate();
		// activate the new one
		this.lastWayPoint = wp;
		wp.activate();
	}
	
	private void updateState(int i)
	{
		this.state = i;
		
		if (i == STATE_WALKING || i == STATE_FLYING)
			sprite.animate(new long[]{400,300,300,300}, 0, 3, 0, StartWalking.instance);
		else if (i == STATE_JUMPING || i == STATE_FALLING)
			this.sprite.animate(new long[]{100,100,200,300}, 4, 7, false);
		else if (i == STATE_STILL)
			this.sprite.animate(new long[]{300,300,300,300}, 4, 7, true);
		else
			return ; //error	
	}
	
	public void onFlyControlChange(final BaseOnScreenControl control, final float pX, final float pY)
	{
		if (pX * pX * pY * pY > Section.ONE_PIXEL)
		{
			this.updateState(STATE_FLYING);
			flySpeed.x = pX * 1000f;
			flySpeed.y = pY * 1000f;
		}
	}
	
	public void onControlChange(final BaseOnScreenControl control, final float pValueX, final float pValueY) 
	{
		stickX = pValueX;
		stickY = pValueY;
	}
	
	@Override
	public void onUpdate(final float pSecondsElapsed)
	{		
		if (activeSection.getBoundaries().contains(this.body.getPosition().x, this.body.getPosition().y) == false)
		{
			this.body.setTransform(spawnPosition, 0);
			this.activeSection.getLevel().setTopText("OUT OF BOUNDS");
		}
		
		if (this.state == STATE_FLYING)
		{
			this.body.setLinearVelocity(flySpeed.tmp().mul(pSecondsElapsed));
		}
		
		bottom.setTransform(body.getWorldCenter().tmp().add(0, sprite.getWidth() * 0.5f / Section.PIXRATIO), 0);
	}

	@Override
	public void reset() 
	{
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * I still have no idea how the return values work for touch events in andengine
	 * I know that in android, you return true if you've handled the event and no further
	 * processing is necessary, or false to let other events go.
	 */
	public boolean onSceneTouchEvent(TouchEvent e)
	{
		
		return true;
	}
	
	private static class StartWalking implements AnimatedSprite.IAnimationListener
	{
		public static StartWalking instance = new StartWalking();
		
		@Override
		public void onAnimationEnd(AnimatedSprite pAnimatedSprite) 
		{
			pAnimatedSprite.animate(new long[]{300,300,300}, 1, 3, true);
		}	
	}
}
