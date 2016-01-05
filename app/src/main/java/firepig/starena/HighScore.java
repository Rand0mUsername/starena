//Firepig
package firepig.starena;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class HighScore extends Activity
{
	//vars

	//basic
	double ScrWidth, ScrHeight;
	Display display;
	Context context;
	RelativeLayout bg;
	
	//sharedpref
	SharedPreferences hsRead;
	SharedPreferences.Editor hsEdit;
	String[] hsFullString;
	String[] hsNames;
	int[] hsScores;
	
	//design
	int hsTextSize;
	TextView[] nums,names,scores;
	LayoutParams[] paramsLeft,paramsRight,paramsMid;
	Typeface fontStarena;
	int L; //max len of someoneZ name
	
	//time
	int mins,secs,mss;

	//language
	String lang;
	int language;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		//standard oncreate stuff
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_high_score);
		context = getApplicationContext();
		bg=(RelativeLayout) findViewById(R.id.BackgroundHS);
		
		//fetching screen size
		display = getWindowManager().getDefaultDisplay();
		ScrWidth=display.getWidth();
		ScrHeight=display.getHeight();
		
		//init eveything
		nums = new TextView[11];
		names = new TextView[11];
		scores = new TextView[11];
		hsFullString = new String[11];
		hsNames = new String[11];
		hsScores = new int[11];
		L=7;//7 for example
		
		//set backgroud according to language
		ReadLanguage();
		if(language==0)
			bg.setBackgroundResource(R.drawable.hsback);
		else
			bg.setBackgroundResource(R.drawable.xbackhs);
		
		//set font
		hsTextSize=20;
		fontStarena = Typeface.createFromAsset(getAssets(),"starenafont.ttf");

		//fill the table
		getHighScores();
		
		//set basic params
		paramsLeft = new LayoutParams[11];
		paramsMid = new LayoutParams[11];
		paramsRight = new LayoutParams[11];
		
		//top 10 players
		for(int i=1;i<=10;i++)
		{
			//params
			paramsLeft[i]  = new LayoutParams
				       (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			paramsMid[i]   = new LayoutParams
				       (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			paramsRight[i] = new LayoutParams
					   (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			
			//careful calculations!
			paramsLeft[i].leftMargin=(int)(ScrWidth/10.0);
			paramsMid[i].leftMargin=(int)(3.0*ScrWidth/10.0);
			paramsRight[i].leftMargin=(int)(6.0*ScrWidth/10.0);
			//careful calculations!
			paramsLeft[i].topMargin=(int)(ScrHeight*0.2+((i-1)*ScrHeight*0.08));
			paramsMid[i].topMargin=(int)(ScrHeight*0.2+((i-1)*ScrHeight*0.08));
			paramsRight[i].topMargin=(int)(ScrHeight*0.2+((i-1)*ScrHeight*0.08));
			
			//some more formatting
			nums[i]=new TextView(context);
			names[i]=new TextView(context);
			scores[i]=new TextView(context);

			nums[i].setTypeface(fontStarena);
			names[i].setTypeface(fontStarena);
			scores[i].setTypeface(fontStarena);
			
			nums[i].setVisibility(View.VISIBLE);
			names[i].setVisibility(View.VISIBLE);
			names[i].setVisibility(View.VISIBLE);
			
			nums[i].setTextColor(Color.rgb(48,233,135));
			names[i].setTextColor(Color.rgb(48,233,135));
			scores[i].setTextColor(Color.rgb(48,233,135));
			
			nums[i].setTextSize(hsTextSize);
			names[i].setTextSize(hsTextSize);
			scores[i].setTextSize(hsTextSize);
			
			nums[i].setLayoutParams(paramsLeft[i]);
			names[i].setLayoutParams(paramsMid[i]);
			scores[i].setLayoutParams(paramsRight[i]);
			
			if(hsNames[i]!="/")//no player there (end of list?)
			{
				nums[i].setText(" "+Integer.toString(i));
				names[i].setText(hsNames[i]);
				scores[i].setText(makeScoreString(hsScores[i])+" ");
				bg.addView(nums[i]);
				bg.addView(names[i]);
				bg.addView(scores[i]);
			}
		}
	}
	
	void ReadLanguage()
	{
		hsRead = getSharedPreferences("hs", 0);
		hsEdit= hsRead.edit();

		//language key is 1337
		lang=hsRead.getString("1337", "e");
		if( lang.charAt(0)=='e')
			language=0;
		else if( lang.charAt(0)=='s')
			language=1;
	}
	
	String makeScoreString(int time)
	{
		//Ms -> mm:ss:MsMsMs
		String str=new String();
		mins=time/60000;
		secs=(time%60000)/1000;
		mss=time-mins*60000-secs*1000;

		if(mins<10) 
			str+="0";
		str+=mins;
		str+=":";
		if(secs<10) 
			str+="0";
		str+=secs;
		str+=".";
		if(mss<100)
			str+="0";
		if(mss<10)
			str+="0";
		str+=mss;
		return str;
	}
	
	public void getHighScores()
	{
		//init
		hsRead = getSharedPreferences("hs", 0);
		
		//read the list
		String key;
		for(int i=1;i<=10;i++)
		{
			key = Integer.toString(i);
			hsFullString[i] = hsRead.getString(key, "/");
			if(hsFullString[i].length()>1)
			{
			   
			  hsNames[i] = hsFullString[i].substring(0,L);
			  hsScores[i] = Integer.parseInt(hsFullString[i].substring(L+1,hsFullString[i].length()));
			}
			else
			{
				  hsNames[i] = "/";
				  hsScores[i] = -1;
			}
		}
		return;
	}

}
