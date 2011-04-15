package rs.android.sound;

import android.content.Context;
import android.media.MediaPlayer;

public class Audio 
{
	private MediaPlayer mPlayer;
	private String name;
	
	private boolean mediaPlaying = false;
	private boolean mLoop = false;
	
	public Audio(Context ctx, int resID)
	{
		name = ctx.getResources().getResourceName(resID);
		
		
		//Create a media player
		mPlayer = MediaPlayer.create(ctx, resID);
		
		//Listen for completion events
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
		{
			
			/**
			 * On completion of the audio clip, it decides whether to repeat or to stop.
			 */
			@Override
			public void onCompletion(MediaPlayer mp) 
			{
				mediaPlaying = false;
				if(mLoop)
					mp.start();
				else
					mp.release();
			}
		});
	}
	
	/**
	 * start the media player.
	 */
	public synchronized void play()
	{
		if(mediaPlaying)
			return;
		
		if(mPlayer != null)
		{
			mediaPlaying = true;
			mPlayer.start();
		}
	}
	
	/**
	 * Stop playing the audio clip
	 */
	public synchronized void stop()
	{
		try
		{
			mLoop = false;
			if(mediaPlaying)
			{
				mediaPlaying = false;
				mPlayer.pause();
			}
		}
		catch(Exception e)
		{
			System.err.println("Audio_Stop" + name + " " + e.toString());
		}
	}
	
	public synchronized void loop()
	{
		mLoop = true;
		mediaPlaying = true;
		mPlayer.start();
	}
	
	public void release()
	{
		if(mPlayer != null)
		{
			mPlayer.release();
			mPlayer = null;
		}
	}

}
