package com.rocketscience.helpers;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.TreeMap;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.util.Debug;

import android.content.Context;
import com.rocketscience.level.LevelScreen;
import com.rocketscience.level.Section;
import com.rocketscience.objects.BaseObject;
import com.rocketscience.objects.Door;
import com.rocketscience.objects.WayPoint;
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
	public static LevelScreen currentLevel;
	
	private static TreeMap<Short, ObjectLoadingAdapter> loaders;
	
	/*
	 * load whatever resources necessary to spawn objects
	 */
	public static void LoadResources(Context context, TextureManager texman)
	{
		TextureRegionFactory.addTextureSourceFromAsset(TEXTURE, context, TEX_FILE, 0, 0);
		texman.loadTexture(TEXTURE);
	}
	
	public static void generateLoadingTree() 
	{
		loaders = new TreeMap<Short, ObjectLoadingAdapter>();
		
		// add the loading methods for every object
		loaders.put(ObjectKeys.DOOR, Door.getLoadingAdapter());
		loaders.put(ObjectKeys.WAY_POINT, WayPoint.getLoadingAdapter());
		loaders.put(ObjectKeys.WORLD_SHAPE, currentSection.getWorldShapeLoadingAdapter());
	}

	public static Texture getObjectTexture()
	{
		return TEXTURE;
	}
	
	public static BaseObject loadObject(final DataInputStream inp, final short key) throws IOException
	{
		final BaseObject obj;
		final ObjectLoadingAdapter loader;
		final short myKey, relKey;
		
		myKey = inp.readShort();
		relKey = inp.readShort();
		
		loader = loaders.get(key);
		if (loader == null)
		{
			Debug.e("Invalid key given for object (" + String.valueOf(key) + ").");
			obj = null;
		}
		else
		{
			obj = loader.load(inp, myKey, relKey);
		}
		
		return obj;
	}
}
