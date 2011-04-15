package com.rocketscience.level;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.ZoomCamera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.util.Debug;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.rocketscience.RocketScience;
import com.rocketscience.helpers.ContactListenerManager;
import com.rocketscience.objects.BaseObject;
import com.rocketscience.player.Player;

public class LevelScreen extends Scene
{
	protected final static int MAJOR_REVISION = 4, MINOR_REVISION = 0;
	protected final static String CONTROL_STICK_BASE_TEX_FILE = "gfx/mobs/controlStickBase.png",
	                              CONTROL_STICK_KNOB_TEX_FILE = "gfx/mobs/controlStickKnob.png";
	protected final Engine myEngine;
	protected final ZoomCamera myCamera;
	protected final PhysicsWorld myWorld;
	protected final TreeMap<Short, Texture> textures = new TreeMap<Short, Texture>(); // textures used in the level
	protected final TreeMap<Short, Section> sections = new TreeMap<Short, Section>();
	protected short curSectionKey = -1;
	protected Section curSection = null;
	protected float progress;
	protected final RocketScience loadingScreen;
	protected final Player player;
	protected final AnalogOnScreenControl analogStick, analogStickFly;
	protected final HUD myHUD; // the heads up display of the game, has stuff like score, info, etc.
	private final Font mFont;
	private final ChangeableText tTop, tBottom;
	
	protected short spawnSectionKey = -1;
	protected Vector2 spawnPosition = null;
	
	public LevelScreen(final int pLayerCount, final Engine e, final ZoomCamera c, final RocketScience main) 
	{
		super(pLayerCount);
		
		myEngine = e;
		myCamera = c;
		loadingScreen = main;
		
		// Text stuff
		final Texture fontTexture = new Texture(256, 256, TextureOptions.BILINEAR);
		this.mFont = new Font(fontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 16, true, Color.WHITE);
		this.myEngine.getTextureManager().loadTexture(fontTexture);
        this.myEngine.getFontManager().loadFont(this.mFont);
        tTop = new ChangeableText(0, 0, this.mFont, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        tBottom = new ChangeableText(0, this.tTop.getHeight() + 2, this.mFont, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		
		// physics
		myWorld = new PhysicsWorld(new Vector2(0, 2 * SensorManager.GRAVITY_EARTH), false);
		myEngine.registerUpdateHandler(myWorld);
		ContactListenerManager.attachToWorld(myWorld);
		ContactListenerManager.clearAllListeners();
		// the following contact listener is to get information about colliding shapes
		ContactListenerManager.addListener(new ContactListener()
		{
			@Override
			public void beginContact(Contact contact) 
			{
				Body a = contact.getFixtureA().getBody(),
				     b = contact.getFixtureB().getBody();
				
				if (a.getUserData() == null || b.getUserData() == null)
				{
					Log.e("RocketScience->LevelScreen", "in a contact, a body without user data set was detected.");
					return;
				}
				
				BaseObject objA = (BaseObject)a.getUserData(), 
				           objB = (BaseObject)a.getUserData();
				
				LevelScreen.this.setTopText("Contact bodyA(" + String.valueOf(objA.getID()) + ") type: " +
						String.valueOf(objA.getType()) + " centerX: " + 
						Float.toString(a.getWorldCenter().x) + " centerY: " + 
						Float.toString(a.getWorldCenter().y));
				
				LevelScreen.this.setBottomText("Contact bodyB(" + String.valueOf(objB.getID()) + ") type: " +
						String.valueOf(objB.getType()) + " centerX: " + 
						Float.toString(b.getWorldCenter().x) + " centerY: " + 
						Float.toString(b.getWorldCenter().y));
			}
			@Override
			public void endContact(Contact contact) 
			{
				Body a = contact.getFixtureA().getBody(),
			     b = contact.getFixtureB().getBody();
			
				if (a.getUserData() == null || b.getUserData() == null)
				{
					Log.e("RocketScience->LevelScreen", "in a contact, a body without user data set was detected.");
					return;
				}
				
				BaseObject objA = (BaseObject)a.getUserData(), 
				           objB = (BaseObject)a.getUserData();
				
				LevelScreen.this.setTopText("End Contact bodyA(" + String.valueOf(objA.getID()) + ") type: " +
						String.valueOf(objA.getType()) + " centerX: " + 
						Float.toString(a.getWorldCenter().x) + " centerY: " + 
						Float.toString(a.getWorldCenter().y));
				
				LevelScreen.this.setBottomText("End Contact bodyB(" + String.valueOf(objB.getID()) + ") type: " +
						String.valueOf(objB.getType()) + " centerX: " + 
						Float.toString(b.getWorldCenter().x) + " centerY: " + 
						Float.toString(b.getWorldCenter().y));
			}
		});
		
		// player
		player = Player.MakePlayer(0, 0, this, myWorld, main, myEngine.getTextureManager());
		player.centerCamera(myCamera);
		myEngine.registerUpdateHandler(player);
		
		// HUD
		myHUD = new HUD(2);
		myHUD.getTopLayer().addEntity(tTop);
		myHUD.getTopLayer().addEntity(tBottom);
		myCamera.setHUD(myHUD);
		
		// controls
		final Texture anaStickTex = new Texture(256,128, TextureOptions.BILINEAR);
		final TextureRegion base = TextureRegionFactory.createFromAsset(anaStickTex, main, LevelScreen.CONTROL_STICK_BASE_TEX_FILE, 0, 0);
		final TextureRegion knob = TextureRegionFactory.createFromAsset(anaStickTex, main, LevelScreen.CONTROL_STICK_KNOB_TEX_FILE, 128, 0);
		myEngine.getTextureManager().loadTexture(anaStickTex);
		
		// move stick
		analogStick = new AnalogOnScreenControl(0, (int) (myCamera.getHeight() - base.getHeight()), myCamera, base, knob, 0.1f, 200, new IAnalogOnScreenControlListener()
		{
			@Override
			public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) 
			{
				LevelScreen.this.curSection.onControlClick(pAnalogOnScreenControl);
			}
			@Override
			public void onControlChange(BaseOnScreenControl pBaseOnScreenControl, float pValueX,float pValueY) 
			{
				LevelScreen.this.curSection.onControlChange(pBaseOnScreenControl, pValueX, pValueY);
			}
		});
		analogStick.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		analogStick.getControlBase().setAlpha(0.65f);
		analogStick.getControlKnob().setAlpha(0.65f);
		//analogStick.getControlKnob().setScale(scale);
		//analogStick.getControlBase().setScaleCenter(0, 0);
		//analogStick.getControlBase().setScale(scale);
		analogStick.refreshControlKnobPosition();
		myHUD.setChildScene(analogStick);
		
		// fly stick
		analogStickFly = new AnalogOnScreenControl((int) (myCamera.getWidth() - base.getWidth()), (int) (myCamera.getHeight() - base.getHeight()), myCamera, base, knob, 0.1f, 200, new IAnalogOnScreenControlListener()
		{
			@Override
			public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) 
			{
				
			}
			@Override
			public void onControlChange(BaseOnScreenControl pBaseOnScreenControl, float pValueX,float pValueY) 
			{
				LevelScreen.this.player.onFlyControlChange(pBaseOnScreenControl, pValueX, pValueY);
			}
		});
		analogStickFly.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		analogStickFly.getControlBase().setAlpha(0.65f);
		analogStickFly.getControlKnob().setAlpha(0.65f);
		//analogStick.getControlKnob().setScale(scale);
		//analogStick.getControlBase().setScaleCenter(0, 0);
		//analogStick.getControlBase().setScale(scale);
		analogStickFly.refreshControlKnobPosition();
		analogStick.setChildScene(analogStickFly);
	}
	
	/* 
	 * loads the textures part of the file 
	 * length is the length of the file
	 */
	protected void loadResources(final InputStream inp, final Context context, final long fileLength) throws IOException
	{
		int progress; // the number of bytes read
		Scanner in = new Scanner(inp);
		final TextureManager texman = myEngine.getTextureManager();
		Short k; // the key for the texture
		String s; // the path of the texture
		int w,h; // the width and height of the texture
		Texture t; // the texture itself
		final int revMajor, revMinor; // version stuff
		String buf; // current line
		String dir; // current directory
		
		// check version info
		// level loader should be backwards compatible for minor revisions
		// break on: loader revMinor < file revMinor OR loader revMajor != file revMajor
		revMajor = in.nextInt();
		if (revMajor != LevelScreen.MAJOR_REVISION)
		{
			Debug.e("Level Major Revision number(" + Integer.toString(revMajor) + ") does not match Loader Major Revision number (" + Integer.toString(LevelScreen.MAJOR_REVISION) + ").\n" +
					"Please update to the newest version of this program.");
			return;
		}
		revMinor = in.nextInt();
		in.nextLine();
		
		if (revMinor == LevelScreen.MINOR_REVISION)
		{
			Log.e("Rocket Science", "File versions up to date.");
		}
		else if (revMinor < LevelScreen.MINOR_REVISION)
		{// this ok, but warn them
			Log.e("Rocket Science","File minor revision less than loader minor revision. Not a big deal though, loading anyway.");
		}
		else
		{// can't continue loading level
			Debug.e("Level loader out of date! (LevelLoader v" + Integer.toString(LevelScreen.MAJOR_REVISION) + "." + Integer.toString(LevelScreen.MINOR_REVISION) +
					                           " < file v" + Integer.toString(revMajor) + "." + Integer.toString(revMinor) + ")" +
					                           "\nCan't load file, update to newest version to load file.");
			return;
		}
		
		dir = "";
		progress = 0;
		while (in.hasNext())
		{
			buf = in.nextLine();
			// progress stuff
			progress += buf.length();
			loadingScreen.setProgress(((float)progress / fileLength) * 0.25f);
			
			String[] line = buf.split(" ");
			if (line.length == 4) // load a texture
			{
				w = Integer.parseInt(line[0]);
				h = Integer.parseInt(line[1]);
				k = Short.parseShort(line[2]);
				s = line[3];
				t = new Texture(w, h, TextureOptions.REPEATING_BILINEAR);
				// load the image onto the texture, then load t into graphicsmem, then put it in the map
				TextureRegionFactory.addTextureSourceFromAsset(t, context, dir + s, 0, 0);
				texman.loadTexture(t);
				textures.put(k, t);
			}
			else if (line.length == 2) // load a song
			{
				//TODO: load song
				//add song here
				//line[0] key
				//line[1] path
				
			}
			else // change the asset path
			{
				dir = buf;
			}
		}// while reading file
	}// loadResources()
	

	
	/*
	 * loads a level from a correctly formatted binary file
	 */
	protected void loadFromBinaryFile(final InputStream inputStream, final Context context, final long fileLength) throws IOException
	{
		long progress; // how many bytes have been read
		final DataInputStream inp = new DataInputStream(inputStream);
		//TODO: create a visual loading bar
		final int revMajor, revMinor;
		final int numSections; // the number of sections in the level
		
		// check version info
		// level loader should be backwards compatible for minor revisions
		// break on: loader revMinor < file revMinor OR loader revMajor != file revMajor
		revMajor = inp.readInt();
		if (revMajor != LevelScreen.MAJOR_REVISION)
		{
			Debug.e("Level Major Revision number(" + Integer.toString(revMajor) + ") does not match Loader Major Revision number (" + Integer.toString(LevelScreen.MAJOR_REVISION) + ").\n" +
					"Please update to the newest version of this program.");
			return;
		}
		revMinor = inp.readInt();
		if (revMinor == LevelScreen.MINOR_REVISION)
		{
			Log.e("Rocket Science", "File versions up to date.");
		}
		else if (revMinor < LevelScreen.MINOR_REVISION)
		{// this ok, but warn them
			Log.e("Rocket Science", "File minor revision less than loader minor revision. Not a big deal though, loading anyway.");
		}
		else
		{// can't continue loading level
			Debug.e("Level loader out of date! (LevelLoader v" + Integer.toString(LevelScreen.MAJOR_REVISION) + "." + Integer.toString(LevelScreen.MINOR_REVISION) +
					                           " < file v" + Integer.toString(revMajor) + "." + Integer.toString(revMinor) + ")" +
					                           "\nCan't load file, update to newest version to load file.");
			return;
		}
		// load the spawn area
		spawnSectionKey = inp.readShort();
		spawnPosition = new Vector2(inp.readFloat(), inp.readFloat()).mul(1 / Section.PIXRATIO);
		// read the sections
		numSections = inp.readInt();
		for (int i = 0; i < numSections; i++)
		{
			final short key = inp.readShort();
			final Rectangle bb = new Rectangle(inp.readFloat(), inp.readFloat(), inp.readFloat(), inp.readFloat());
			bb.setWidth(bb.getWidth() - bb.getX());
			bb.setHeight(bb.getHeight() - bb.getY());
			this.sections.put(key, new Section(2, this, myWorld, myCamera, myEngine, textures, player, bb, key));
			this.sections.get(key).load(inp, context);
			//sections[i].setChildScene(analogStick); // add the analog stick to the screen
		}
	}
	
	public void loadLevel(final Context context, String levelname) throws IOException
	{
		InputStream res,bin;
		res = context.getAssets().open(levelname + ".levelres");
		bin = context.getAssets().open(levelname + ".levelbin");
		
		this.loadLevel(context, res, bin);
	}
	
	/*
	 * take in a context, a directory where the level files are, and a level name
	 */
	public void loadLevel(final Context context, final InputStream inputStream, final InputStream inputStream2) throws IOException, FileNotFoundException
	{	
		long length = 0, skip;
		/*// load resource data
		length = 0;
		do
		{
			skip = inputStream.skip(Long.MAX_VALUE);
			length += skip;
		} while (skip > 0);
		inputStream.reset();//*/
		this.loadResources(inputStream, context, length);
		
		/*// load binary data
		length = 0;
		do
		{
			skip = inputStream2.skip(Long.MAX_VALUE);
			length += skip;
		} while (skip > 0);
		inputStream2.reset();//*/
		this.loadFromBinaryFile(inputStream2, context, length);
		
		this.setCurrentSection(spawnSectionKey);	
		player.sendTo(this.curSection, this.spawnPosition);
		player.spawn();
		
		this.dumpToLog();
	}
	
	public Section getCurrentSection()
	{
		return curSection;
	}
	
	public Vector2 getSpawnPosition()
	{
		return this.spawnPosition;
	}
	
	public short getSpawnSectionKey()
	{
		return this.spawnSectionKey;
	}
	
	public void setCurrentSection(final short num)
	{
		// do anything we have to to the old section
		// now set the new section
		curSectionKey = num;
		curSection = sections.get(curSectionKey);
		myEngine.setScene(curSection);
	}

	public void dumpToLog()
	{
		Log.e("RocketScience->LevelScreenDUMP", "dumping LevelScreen to log...");
		Log.e("RocketScience->LevelScreenDUMP", "SpawnSectionKey: " + String.valueOf(this.spawnSectionKey));
		Log.e("RocketScience->LevelScreenDUMP", "SpawnPosition: " + String.valueOf(this.spawnPosition.x) + ", " + String.valueOf(this.spawnPosition.y));
		for (Entry<Short, Section> e : sections.entrySet())
		{
			Section s = e.getValue();
			Log.e("RocketScience->LevelScreenDUMP", "begin new section");
			s.dumpToLog();
		}
	}
	
	public void setTopText(final String t)
	{
		this.myEngine.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run() 
			{
				LevelScreen.this.tTop.setText(t);
			}
		});
	}
	
	public void setBottomText(final String t)
	{
		this.myEngine.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run() 
			{
				LevelScreen.this.tBottom.setText(t);
			}
		});
	}
}
