package com.rocketscience.player;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.rocketscience.actions.CheckDeathAction;
import com.rocketscience.actions.JumpAction;
import com.rocketscience.actions.TakeDamageAction;
import com.rocketscience.helpers.CollisionFilter;
import com.rocketscience.helpers.ContactListenerManager;
import com.rocketscience.helpers.ObjectKeys;
import com.rocketscience.level.LevelScreen;
import com.rocketscience.level.Section;
import com.rocketscience.objects.BaseObject;
import com.rocketscience.objects.WayPoint;
import com.rocketscience.objects.creatures.Creature;

public class Player extends Creature implements ContactListener
{
	public final static Vector2 VECTOR2_ZERO = new Vector2(0,0);
	
	private static final float MAX_SPEED = 15f;
	private static final float MAX_JUMP  = 20f;
	private static final float START_HP  = 10f;
	private final static String TEX_FILE = "gfx/mobs/weirdman.png";
	private final static int TEX_WIDTH = 256, TEX_HEIGHT = 128, TEX_COLS = 4, TEX_ROWS = 2, 
	                         TEX_ROWS_PER_ANIMATION = 1;
	private final static FixtureDef FIX_DEF = getFixtureDef(),
									FIX_BOTTOM = getBottomDef();
	private final static int STATE_WALKING = 1,
							 STATE_JUMPING = 2,
							 STATE_FALLING = 3,
							 STATE_FLYING = 4,
							 STATE_STOPPING = 5,
							 STATE_STILL = -1;
	private final Body bottom; // the bottom of the player. Used to test if the player is on the ground or hitting an enemy
	private Section activeSection = null; // the section the player is currently on
	private final Engine myEngine;
	private long  score = 0;
	private LevelScreen level;
	// player actions
	final JumpAction jumpAction;
	final TakeDamageAction takeDamageAction;
	// also has:
	// (from Creature) spawnAction
	// state information
	private final Vector2 velocity = new Vector2(0,0); // this gets mul'd * seconds in update and added
	private WayPoint lastWayPoint = null; // where he'll respawn
	private int state = STATE_FALLING;
	private boolean faceRight = true;
	final private Vector2 flySpeed = new Vector2();
	private Vector2 spawnPosition = null; // this gets set when the player is "sent" somewhere
	private Body standingOn = null;
	private final Vector2 standingOnLastPosition = new Vector2();
	// Controls Information
	private float stickX = 0, stickY = 0;
	private float desiredSpeed;
	
	public Player(final Body b, final AnimatedSprite s, final Body bt, final LevelScreen lvl)
	{
		super(b, s, ObjectKeys.PLAYER, (short)-2, (short)-1);
		bottom = bt;
		level = lvl;
		myEngine = level.getLoadingScreen().getEngine();
		
		// actions
		// jump
		jumpAction = new JumpAction(this, Player.MAX_JUMP);
		this.addAction(jumpAction);
		// takeDamage
		takeDamageAction = new TakeDamageAction(this);
		final CheckDeathAction cd = new CheckDeathAction(this);
		cd.addReaction(this.spawnAction);
		takeDamageAction.addReaction(cd);
		this.addAction(takeDamageAction);
		
		// contactlistener
		ContactListenerManager.addListener(this);
		
	}
	
	private static FixtureDef getFixtureDef()
	{
		final FixtureDef fixdef = PhysicsFactory.createFixtureDef(1, 0f, 0f);
		fixdef.filter.categoryBits = CollisionFilter.CATEGORY_PLAYER;
		fixdef.filter.maskBits = CollisionFilter.MASK_NONPLAYER;
		
		return fixdef;
	}
	
	private static FixtureDef getBottomDef()
	{
		final FixtureDef fixdef = new FixtureDef();
		fixdef.isSensor = true;
		fixdef.filter.categoryBits = CollisionFilter.CATEGORY_PLAYER;
		fixdef.filter.maskBits = CollisionFilter.MASK_NONPLAYER;

		
		return fixdef;
	}
	
	public static Player MakePlayer(final float x, final float y, final PhysicsWorld world, final Context context, final LevelScreen level)
	{
		final Texture tex = new Texture(TEX_WIDTH, TEX_HEIGHT);
		final TiledTextureRegion texregion = TextureRegionFactory.createTiledFromAsset(tex, context, TEX_FILE, 0, 0, 
				                             TEX_COLS, TEX_ROWS);
		// set up the graphics for the player
		level.getLoadingScreen().getEngine().getTextureManager().loadTexture(tex);
		final AnimatedSprite sprite = new AnimatedSprite(x,y,texregion);
		sprite.animate(new long[]{300,300,300,300}, 0, 3, true);
		
		// create the physics body
		final Body body = PhysicsFactory.createBoxBody(world, sprite, BodyType.DynamicBody, FIX_DEF);
		body.setFixedRotation(true);
		body.setBullet(true);
		
		// create the bottom sensor
		final Rectangle rect = new Rectangle(0, 0, sprite.getWidth(), 32);
		final Body bottom = PhysicsFactory.createBoxBody(world, rect, BodyType.DynamicBody, FIX_BOTTOM);
		bottom.setBullet(true);
        
        // create the player object
        final Player player = new Player(body, sprite, bottom, level);
		bottom.setUserData(player);
        
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
				Player.this.body.setLinearVelocity(Player.VECTOR2_ZERO);
				activeSection.getTopLayer().addEntity(Player.this.sprite);
				
				activeSection.setAsCurrentSection();
			}
		});
	}
	
	public void setWayPoint(WayPoint wp)
	{
		// deactivate the old one, if any
		if (this.lastWayPoint != null)
			this.lastWayPoint.deActivate();
		// activate the new one
		this.lastWayPoint = wp;
		wp.activate();
	}
	
	public LevelScreen getLevel()
	{
		return level;
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
		else if (i == STATE_STOPPING)
			this.sprite.animate(new long[]{Long.MAX_VALUE, 100}, 0, 1, false);
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
		else if (this.state == STATE_FLYING)
		{
			this.updateState(STATE_FALLING);
		}
	}
	
	public void onControlChange(final BaseOnScreenControl control, final float pValueX, final float pValueY) 
	{
		stickX = pValueX;
		stickY = pValueY;
		desiredSpeed = stickX * MAX_SPEED;
	}
	
	@Override
	public void onUpdate(final float pSecondsElapsed)
	{		
		// make sure we're within bounds
		if (activeSection.getBoundaries().isOutside(this.body.getPosition()))
		{
			this.spawnAction.perform();
		}
		
		// check if we should be slowing down
		if (this.state == Player.STATE_WALKING && this.stickX == 0)
		{
			this.updateState(Player.STATE_STOPPING);
		}
		// update velocity
		velocity.set(this.body.getLinearVelocity());
		// slow down?
		if (this.state == Player.STATE_STOPPING)
		{
			velocity.x *= 0.3f;
		}
		
		// if on a moving platform, update position
		if (this.standingOn != null)
		{
			if (((BaseObject)this.standingOn.getUserData()).moves)
			{
				this.standingOnLastPosition.sub(this.standingOn.getPosition());
				this.standingOnLastPosition.mul(-1f);
				this.body.setTransform(this.body.getPosition().tmp().add(this.standingOnLastPosition), 0);
				this.standingOnLastPosition.set(this.standingOn.getPosition());
			}
		}
		
		// check input states
		if (this.state == STATE_FLYING)
		{
			this.body.setLinearVelocity(flySpeed.tmp().mul(pSecondsElapsed));
		}
		else
		{// update move states accordingly
			velocity.x += desiredSpeed * pSecondsElapsed;
			if (Math.abs(velocity.x) > Math.abs(desiredSpeed)) velocity.x = desiredSpeed;
			this.body.setLinearVelocity(velocity);
			
			// update state
			if (velocity.len() > Section.ONE_PIXEL && this.state == Player.STATE_STILL)
			{
				this.updateState(STATE_WALKING);
			}
			if (velocity.len() < Section.ONE_PIXEL)
			{
				this.updateState(STATE_STILL);
			}
		}
		
		// move auxillary bodies into place
		bottom.setTransform(body.getWorldCenter().tmp().add(0, sprite.getWidth() * 0.5f / Section.PIXRATIO), 0);
	}

	@Override
	public void reset() 
	{
		final WayPoint lastWP = this.lastWayPoint;
		if (lastWP != null)
		{
			this.body.setTransform(lastWP.getBody().getPosition(), 0);
		}
		else
		{
			this.body.setTransform(spawnPosition, 0);
		}
		
		this.body.setLinearVelocity(VECTOR2_ZERO);
		
		this.setHP(Player.START_HP);
	}
	
	/*
	 * I still have no idea how the return values work for touch events in andengine
	 * I know that in android, you return true if you've handled the event and no further
	 * processing is necessary, or false to let other events go.
	 */
	public boolean onSceneTouchEvent(final TouchEvent e)
	{
		if (this.standingOn == null)
			return true;
		
		System.out.println("pressure on touchevent: " + e.getMotionEvent().getPressure());
		activeSection.postRunnable(new Runnable()
		{
			@Override
			public void run() 
			{
				//TODO: Test this on the actual device and possible remove the "1 - "
				Player.this.jumpAction.perform(0.5f + e.getMotionEvent().getPressure());
				Player.this.updateState(STATE_JUMPING);
			}
		});
		
		return false;
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

	@Override
	public void beginContact(Contact contact) 
	{	
		if (contact.isTouching() == false)
			return;
		
		final Body bodyA, bodyB;
		final BaseObject objA, objB;
		bodyA = contact.getFixtureA().getBody();
		bodyB = contact.getFixtureB().getBody();
		if ((objA = (BaseObject)bodyA.getUserData()) == null)
			return;
		if ((objB = (BaseObject)bodyB.getUserData()) == null)
			return;
		
		// see what kind of contact we have here
		final BaseObject objBottom, objOther;
		
		if (bodyA.equals(this.bottom))
		{
			objBottom = objA;
			objOther = objB;
		}
		else if (bodyB.equals(this.bottom))
		{
			objBottom = objB;
			objOther = objA;
		}
		else
		{
			objBottom = null;
			objOther = null;
		}
		
		if (objBottom != null)
		{
			standingOn = objOther.getBody();
			standingOnLastPosition.set(standingOn.getPosition());
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
		
		// see what kind of contact we have here
		final BaseObject objBottom, objOther;
		
		if (bodyA.equals(this.bottom))
		{
			objBottom = objA;
			objOther = objB;
		}
		else if (bodyB.equals(this.bottom))
		{
			objBottom = objB;
			objOther = objA;
		}
		else
		{
			objBottom = null;
			objOther = null;
		}
		
		if (objBottom != null && standingOn != null)
		{
			if (standingOn.equals(objOther.getBody()))
				standingOn = null;
		}
	}
}
