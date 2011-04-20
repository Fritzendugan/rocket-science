package rocketScience.johnnydayDreamer;

import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.opengl.texture.source.AssetTextureSource;
import org.anddev.andengine.opengl.texture.source.ITextureSource;
import org.anddev.andengine.ui.activity.BaseSplashActivity;

import android.app.Activity;

public class rocketSplash extends BaseSplashActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int SPLASH_DURATION = 3;
	private static final float SPLASH_SCALE_FROM = 0.8f;
	
	@Override
	protected ScreenOrientation getScreenOrientation() {
		// TODO check real orientation ?
		return ScreenOrientation.LANDSCAPE;
	}

	@Override
	protected ITextureSource onGetSplashTextureSource() {
		return new AssetTextureSource(this, "gfx/itsNotRocketScience.png");
	}

	@Override
	protected float getSplashDuration() {
		return SPLASH_DURATION;
	}

	@Override
    protected float getSplashScaleFrom() {
            return SPLASH_SCALE_FROM;
    }
	
	@Override
	protected Class<? extends Activity> getFollowUpActivity() {
		return JohnnyDayDreamer.class;
	}
}
