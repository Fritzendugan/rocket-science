package rs.android.sound;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.*;
import android.view.View.OnTouchListener.*;
import android.widget.*;




public class Launch extends Activity {
	private Context mContext;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnTouchListener(new OnTouchListener()
        {
        	@Override
        	public boolean onTouch(View v, MotionEvent me)
        	{
        		if(me.getAction() == MotionEvent.ACTION_DOWN)
        		{
        		/**	MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.bark3);
        			mp.start();
        			
        			mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        			{	
        				@Override
        				public void onCompletion(MediaPlayer mp) 
        				{
        					mp.release();
        				}
        			});*/
        			initAudio();
        		}
        		return true;
        	}
        });     
    }
    
    
    public void initAudio()
    {
    	Audio bark = getAudio(R.raw.bark3);
   // 	bark.loop();	
    	bark.play();
    }
    
    protected Audio getAudio(int id)
    {
    	return new Audio(getBaseContext(), id);
    }
}