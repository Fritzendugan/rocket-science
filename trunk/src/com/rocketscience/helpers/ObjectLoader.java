package com.rocketscience.helpers;

import java.io.DataInputStream;
import java.io.IOException;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;

import android.content.Context;
import android.util.Log;

import com.rocketscience.level.Section;
import com.rocketscience.mobs.BodyWithActions;
import com.rocketscience.objects.BaseObject;
import com.rocketscience.objects.DeathAreaObj;
import com.rocketscience.objects.Door;
import com.rocketscience.objects.WayPointObj;
import com.rocketscience.player.Player;

public class ObjectLoader 
{
	private static final int TEX_WIDTH = 1024, TEX_HEIGHT = 1024;
	private static final String TEX_FILE = "gfx/misc/objects.png";
	private static final Texture TEXTURE = new Texture(TEX_WIDTH, TEX_HEIGHT);
	
	public static Engine engine;
	public static PhysicsWorld physicsWorld;
	public static Player player;
	public static Section currentSection;
	public static Context context;
	
	/*
	 * load whatever resources necessary to spawn objects
	 */
	public static void LoadResources(Context context, TextureManager texman)
	{
		TextureRegionFactory.addTextureSourceFromAsset(TEXTURE, context, TEX_FILE, 0, 0);
		texman.loadTexture(TEXTURE);
	}
	
	public static Texture getObjectTexture()
	{
		return TEXTURE;
	}
	
	public static BaseObject loadObject(final DataInputStream inp, final short key) throws IOException
	{
		final BaseObject obj;
		
		if (key == ObjectKeys.WORLD_SHAPE)
		{
			//TODO: this could be done more nicely
			obj = ObjectLoader.currentSection.loadPolygon(inp, ObjectLoader.context);
		}
		else if (key == ObjectKeys.DOOR)
		{
			obj = Door.Load(inp);
		}
		else if (key == ObjectKeys.WAY_POINT)
		{
			obj = WayPointObj.Load(inp, currentSection, TEXTURE, physicsWorld, player);
		}
		else if (key == ObjectKeys.DEATH_AREA)
		{
			obj = DeathAreaObj.Load(inp, context, physicsWorld, player);
		}
		else
		{
			Log.e("RocketScience->ObjectLoader", "Invalid ObjectKey: " + key);
			obj = null;
		}
		
		return obj;
	}
}
