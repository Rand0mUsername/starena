//Firepig
package firepig.starena;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class HowToPlay extends Activity implements OnTouchListener
{
	//basic
	Context context;
	View mainLayout;
	Display display;
	
	//core
	int br; //page counter
	
	//sharedpref
	SharedPreferences hsRead;
	SharedPreferences.Editor hsEdit;
	
	//resizing drawables
	Bitmap temp, resbit;
	Drawable resized;
	
	//language
	String lang;
	int language;
	
	//design
	int[] a; //textures
	ImageButton GoNext,GoBack;
	
	//buttons
	Drawable[] nextImg,backImg;
	int ButtonWidth,ButtonHeight;
	double ScrWidth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		//basic oncreate stuff
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_how_to_play);
		mainLayout = findViewById(R.id.main_layout1);
		context=getApplicationContext();
		
		//fetching screen size
		display = getWindowManager().getDefaultDisplay();
		ScrWidth=display.getWidth();
		ButtonWidth=(int)(ScrWidth/6.0);
		ButtonHeight=(int)(ScrWidth/6.0);

		ReadLanguage();
		br=0;

		//init
		nextImg=new Drawable[2];
		backImg=new Drawable[2];
		a=new int[4];
		if(language==0)
		{
			nextImg[0]=context.getResources().getDrawable(R.drawable.looknext1);
			backImg[0]=context.getResources().getDrawable(R.drawable.lookback1);
			nextImg[1]=context.getResources().getDrawable(R.drawable.looknext2);
			backImg[1]=context.getResources().getDrawable(R.drawable.lookback2);
			a[0]=R.drawable.bghtp1;
			a[1]=R.drawable.bghtp2;
			a[2]=R.drawable.bghtp3;
			a[3]=R.drawable.bghtp4;
		}
		else
		{
			a[0]=R.drawable.srbbghtp1;
			a[1]=R.drawable.srbbghtp2;
			a[2]=R.drawable.srbbghtp3;
			a[3]=R.drawable.srbbghtp4;
			nextImg[0]=context.getResources().getDrawable(R.drawable.srblooknext1);
			backImg[0]=context.getResources().getDrawable(R.drawable.srblookback1);
			nextImg[1]=context.getResources().getDrawable(R.drawable.srblooknext2);
			backImg[1]=context.getResources().getDrawable(R.drawable.srblookback2);
		}
		
		//set first background
		mainLayout.setBackgroundResource(a[0]);
		
		//take care of buttons
		nextImg[0]=ResizeDrawable(nextImg[0],(int)(2.5*ButtonWidth),ButtonHeight);
		backImg[0]=ResizeDrawable(backImg[0],(int)(2.5*ButtonWidth),ButtonHeight);
		nextImg[1]=ResizeDrawable(nextImg[1],(int)(2.5*ButtonWidth),ButtonHeight);
		backImg[1]=ResizeDrawable(backImg[1],(int)(2.5*ButtonWidth),ButtonHeight);
		 
		GoNext = (ImageButton) findViewById(R.id.HtpNext);
		GoNext.setOnTouchListener(this);
		
		GoBack = (ImageButton) findViewById(R.id.HtpBack);
		GoBack.setOnTouchListener(this);
		
		GoNext.setImageDrawable(nextImg[0]);
		GoBack.setImageDrawable(backImg[0]);
	}

	void ReadLanguage()
	{
		hsRead = getSharedPreferences("hs", 0);
		hsEdit= hsRead.edit();
		
		//key is 1337
		lang=hsRead.getString("1337", "e");
		if( lang.charAt(0)=='e')
			language=0;
		else if( lang.charAt(0)=='s')
			language=1;
	}
	
	Drawable ResizeDrawable(Drawable image,int width,int height) 
	{
		//more harm than good but nvm, too late to change now
		temp = ((BitmapDrawable)image).getBitmap();
		resbit = Bitmap.createScaledBitmap(temp, width, height, true);
	    resized = new BitmapDrawable(getResources(), resbit ); 
		return resized;
	}
	
	@Override
    public boolean onTouch(View buttonPressed, MotionEvent event)
     {
		 //on touch listener for page flipping
		 
         if(event.getAction() == MotionEvent.ACTION_DOWN)
         {
             if(buttonPressed.getId()==GoNext.getId())
                {
					GoNext.setImageDrawable(nextImg[1]);
            	    //next page
            	 	br++;
					br=br % 4;
					mainLayout.setBackgroundResource(a[br]);
                }

             if(buttonPressed.getId()==GoBack.getId())
             	{
					GoBack.setImageDrawable(backImg[1]);
            	    //last page
					br--;
					if(br==-1)
						br = 3;
					mainLayout.setBackgroundResource(a[br]);
				}
         }
         if(event.getAction() == MotionEvent.ACTION_UP)
         {
             if(buttonPressed.getId()==GoNext.getId())
            	 GoNext.setImageDrawable(nextImg[0]);

             if(buttonPressed.getId()==GoBack.getId())
            	 GoBack.setImageDrawable(backImg[0]);
         }
         return true;
     }
	 
	//fixing memory leaks
	@Override
	public void onDestroy() 
	{
		//resize drawable caused them
		super.onDestroy();
		nextImg[0]=null;
		nextImg[1]=null;
		backImg[0]=null;
		backImg[1]=null;
		GoBack.setImageDrawable(null);
		GoNext.setImageDrawable(null);
	}


}
