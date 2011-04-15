package com.rocketscience;

import java.io.IOException;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.ZoomCamera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.widget.Toast;

import com.rocketscience.helpers.ObjectLoader;
import com.rocketscience.level.LevelScreen;

public class RocketScience extends BaseGameActivity 
{
        private static final int CAMERA_WIDTH = 720;
        private static final int CAMERA_HEIGHT = 480;
 
        private ZoomCamera mCamera;
        private float progress; // from 0 to 1
 
        public void setProgress(final float p)
        {
        	synchronized (this)
        	{
        		this.progress = p;
        	}
        }
        
        public float getProgress()
        {
        	synchronized (this)
        	{
        		return this.progress;
        	}
        }
 
        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================
 
        @Override
        public Engine onLoadEngine() 
        {
        	this.mCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
            Engine engine = new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
            try 
            {
				engine.setTouchController(new MultiTouchController());
				Toast.makeText(this, "MultiTouch enabled successfully!", Toast.LENGTH_SHORT);
			} 
            catch (MultiTouchException e) 
            {
				Toast.makeText(this, "Sorry, your device does NOT support MultiTouch. Playing this game will be difficult as you can only have one finger on the screen at a time!", Toast.LENGTH_LONG);
				e.printStackTrace();
			}
            
            return engine;
        }
 
        @Override
        public void onLoadResources() 
        {
        	ObjectLoader.LoadResources(this, mEngine.getTextureManager());
        }
 
        @Override
        public Scene onLoadScene() 
        {           
        	this.mEngine.registerUpdateHandler(new FPSLogger());

            final LevelScreen firstLevel = new LevelScreen(1, mEngine, mCamera, this);
            try 
            {
            	firstLevel.loadLevel(this, "levels/kyle2");
			} 
            catch (NotFoundException e) 
            {
				e.printStackTrace();
			} 
            catch (IOException e) 
            {
				e.printStackTrace();
			}

            return firstLevel.getCurrentSection();
        }
 
        @Override
        public void onLoadComplete() 
        {
 
        }
}