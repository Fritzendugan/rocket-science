package com.rocketscience.player;

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

public class Player extends Creature implements IUpdateHandler
{
	private final static String TEX_FILE = "gfx/mobs/weirdman.png";
	private final static int TEX_WIDTH = 256, TEX_HEIGHT = 128, TEX_COLS = 4, TEX_ROWS = 2, 
	                         TEX_ROWS_PER_ANIMATION = 1;
	private final static FixtureDef FIX_DEF = getFixtureDef(),
									FIX_BOTTOM = getBottomDef();
	private final static int ANIMATION_WALKING = 1,
							 ANIMATION_JUMP = 2,
							 ANIMATION_STILL = -1;
	private final Body bottom; // the bottom of the player. Used to test if the player is on the ground or hitting an enemy
	private final AnimatedSprite sprite;
	private Section activeSection = null; // the section the player is currently on
	private long  score = 0;
	// player stats
	private float maxspeed = 5f;
	private float maxjump  = 100f;
	// state information
	private final Vector2 acceleration = new Vector2(0,0); // this gets mul'd * seconds in update and added
	private WayPointObj lastWayPoint = null; // where he'll respawn
	private int animation = ANIMATION_WALKING;
	private boolean faceRight = true;
	private boolean canJump = true;
	private boolean flying = false;
	final private Vector2 flySpeed = new Vector2();
	private Vector2 spawnPosition = null; // this gets set when the player is "sent" somewhere
	
	public Player(final Body b, final AnimatedSprite s, final Body bt)
	{
		super(b,s, ObjectKeys.PLAYER);
		sprite = s;
		bottom = bt;
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
	
	public static Player MakePlayer(final float x, final float y, final Scene scene, final PhysicsWorld world, final Context context, final TextureManager texman)
	{
		final Texture tex = new Texture(TEX_WIDTH, TEX_HEIGHT);
		final TiledTextureRegion texregion = TextureRegionFactory.createTiledFromAsset(tex, context, TEX_FILE, 0, 0, 
				                             TEX_COLS, TEX_ROWS);
		// set up the graphics for the player
		texman.loadTexture(tex);
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
        final Player player = new Player(body, sprite, bottom);
        
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
					player.canJump = true;
					
					Log.e("RocketScience->Player->BottomContactListener", "Bottom hit something.");
				}
			}
			@Override
			public void endContact(Contact contact) 
			{
				
			}
        });
        // register the grahics
		scene.getTopLayer().addEntity(sprite);
		world.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, true, false, false));
		
		return player;
	}
	
	public Vector2 getPosition()
	{
		return body.getPosition();
	}
	
	public Body getBody() { return this.body; }
	
	public AnimatedSprite getSprite() { return this.sprite; }
	
	public void centerCamera(final Camera cam)
	{
		cam.setChaseShape(sprite);
	}
	
	public void sendTo(final Section section, final Vector2 position)
	{
		if (activeSection != null)
		{
			// remove from old section
			activeSection.getTopLayer().removeEntity(this.sprite);
		}
		activeSection = section;
		// add to the new
		this.spawnPosition = position;
		this.body.setTransform(position, 0);
		activeSection.getTopLayer().addEntity(this.sprite);
	}
	
	public void jump()
	{
		//TODO: use a pool
		this.activeSection.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run() 
			{
				Player.this.body.setLinearVelocity(new Vector2(Player.this.body.getLinearVelocity().x, Player.this.maxjump));
				Player.this.setAnimation(ANIMATION_JUMP);
				Player.this.canJump = false;
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
	
	public void spawn()
	{
		if (lastWayPoint == null)
		{
			this.body.setTransform(this.spawnPosition, 0);
		}
		else
		{
			this.body.setTransform(lastWayPoint.getPosition(), 0);
		}
	}
	
	private void setAnimation(int i)
	{
		this.animation = i;
		
		if (i == ANIMATION_WALKING)
			sprite.animate(new long[]{400,300,300,300}, 0, 3, 0, StartWalking.instance);
		else if (i == ANIMATION_JUMP)
			this.sprite.animate(new long[]{100,100,200,300}, 4, 7, false);
		else if (i == ANIMATION_STILL)
			this.sprite.animate(new long[]{300,300,300,300}, 4, 7, true);
		else
			return ; //error	
	}
	
	public void onFlyControlChange(final BaseOnScreenControl control, final float pX, final float pY)
	{
		if (pX * pX * pY * pY > 0.0001)
		{
			flying = true;
			flySpeed.x = pX * 1000f;
			flySpeed.y = pY * 1000f;
		}
		else
		{
			flying = false;
		}
	}
	
	public void onControlChange(final BaseOnScreenControl control, final float pValueX, final float pValueY) 
	{
		acceleration.set(pValueX * 20f, 0);
		
	}
	
	@Override
	public void onUpdate(final float pSecondsElapsed)
	{
		bottom.setTransform(body.getWorldCenter().tmp().add(0, sprite.getWidth() * 0.5f / Section.PIXRATIO), 0);
		
		if (this.body.getLinearVelocity().x < -1 * Section.ONE_PIXEL && faceRight)
		{
			faceRight = false;
			this.setAnimation(ANIMATION_WALKING);
			this.sprite.getTextureRegion().setFlippedHorizontal(true);
		}
		if (this.body.getLinearVelocity().x > Section.ONE_PIXEL && !faceRight)
		{
			faceRight = true;
			this.setAnimation(ANIMATION_WALKING);
			this.sprite.getTextureRegion().setFlippedHorizontal(false);
		}
		
		// update animations based on movement
		if (this.animation != ANIMATION_STILL) 
		{
			if (this.body.getLinearVelocity().len() <= 0.000001f)
				this.setAnimation(ANIMATION_STILL);
		}
		else if (this.body.getLinearVelocity().len() > 0.000001f)
		{
			this.setAnimation(ANIMATION_WALKING);
		}
		// multiply acceleration by seconds elapsed, add to velocity
		if (this.body.getLinearVelocity().len() < maxspeed)
		{ // if it's bigger than maxspeed, normalize and scale it to maxspeed
			this.body.setLinearVelocity(this.body.getLinearVelocity().add(this.acceleration.tmp().mul(pSecondsElapsed)));
		}
		if (flying == true)
		{
			this.body.setLinearVelocity(flySpeed.tmp().mul(pSecondsElapsed));
		}
	}

	@Override
	public void reset() 
	{
		// TODO Auto-generated method stub
		
	}
	
	public boolean onSceneTouchEvent(TouchEvent e)
	{
		if (this.canJump)
		{
			this.jump();
			return false;
		}
		
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
