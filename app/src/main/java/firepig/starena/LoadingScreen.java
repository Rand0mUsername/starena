//Firepig
package firepig.starena;

import java.util.Timer;
import java.util.TimerTask;


import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class LoadingScreen extends Activity
{
	//vars
	
	//basic
	Context context;
	Display display;
	int ScrWidth,ScrHeight;
	
	//timer
	Timer tm;
	
	//basic stuff
	Drawable Lo;
	ImageView Loader;
	LayoutParams Lod;
	
	//some more vars
	double m; //random goes here
	double Buf;//random buffering for realistic fake loading screen
	boolean Over;//loading is over, stop the timer
	
	//drawable resizing
	Bitmap temp;
	Bitmap resbit;
	Drawable resized;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//basic oncreate stuff
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_loading_screen);
		context=getApplicationContext();
		
		//fetching screen size
		display = getWindowManager().getDefaultDisplay();
		ScrWidth = display.getWidth();
		ScrHeight=display.getHeight();
		
		//init
		Over=false;
		m=1;
		Buf=(double) 1;
		
		//setting it up
		Lo=context.getResources().getDrawable(R.drawable.loder);
		Lo=ResizeDrawable(Lo,1,(int)(((ScrHeight)*6)/80.0));
		
		Loader = (ImageView) findViewById(R.id.imageView1);
		Loader.setImageDrawable(Lo);
		
		Lod = new LayoutParams
			       (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		Lod.topMargin=(int)(((ScrHeight)*37)/80.0);
		Lod.bottomMargin=ScrHeight;
		
		Lod.leftMargin=(int)(((ScrWidth)*2)/40.0);
		Lod.rightMargin=ScrWidth;
		
		Loader.setLayoutParams(Lod);
		
		//timer
		tm = new Timer();
	    tm.schedule(new TimerTask()
	    {
	    	public void run()
		    {
			   TickLoop();//40 ms loop
		    }
	    } ,  0, 40);
		
		
	}
	
	Drawable ResizeDrawable(Drawable image,int width,int height) 
	{
		temp = ((BitmapDrawable)image).getBitmap();
		resbit = Bitmap.createScaledBitmap(temp, width, height, true);
	    resized = new BitmapDrawable(getResources(), resbit ); 
		return resized;
	}
	
	public void TickLoop() 
	{
		this.runOnUiThread(MovingTimerTick);
	}
	
	private Runnable MovingTimerTick = new Runnable() 
	{
		public void run() 
		{
			//main loop
			
			//if over do nothing
			if(Over)
				return;
			
			m=Math.random()*30;//random increment
			Buf+=m;//inc it
			
			if(Buf>((ScrWidth)*36)/40.0)//clamp
			   Buf=((ScrWidth)*36)/40.0;
			
			//apply the loading
			Lo=ResizeDrawable(Lo,(int)(Buf) ,(int)(((ScrHeight)*6)/80.0));
			Loader.setImageDrawable(Lo);
			
			//loading ended
			if(Buf==((ScrWidth)*36)/40.0)
			{
				finish();
			}
		}
	};
	
	@Override
	public void onDestroy() 
	 {
		//fixing leakes
	    super.onDestroy();
	    Over=true;
	    Loader.setImageDrawable(null);
	    temp=null;
	    resbit=null;
	    resized=null;
	    Lo=null;
	 }
	
}
