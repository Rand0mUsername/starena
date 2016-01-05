//Firepig
package firepig.starena;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity implements OnTouchListener
{
	//vars

	//basic
	Context context;
	Display display;
	double ScrWidth,ScrHeight;

	//drawables
	Drawable[] sp1,sp2,mp1,mp2,hs1,hs2,htp1,htp2,exit1,exit2;
	Drawable srb,eng;

	//buttons
	ImageButton GoSinglePlayer,GoMultiPlayer,GoHighScore,GoHowToPlay,GoExit;
	ImageButton LanguageChange;
	LayoutParams psp,pmp,phs,phtp,pexit;

	//lang
	int language; //0 eng, 1 srb

	//dimensions
	int LWidth,LHeight,BHeight,BWidth;

	//read lang
	SharedPreferences hsRead;
	SharedPreferences.Editor hsEdit;
	String lang;

	//resize draw
	Bitmap temp;
	Bitmap resbit;
	Drawable resized;

	//on touchy
	Intent Intent1;

	//about
	AlertDialog.Builder about;
	boolean aboutOpen;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//basic oncreate stuff
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		context=getApplicationContext();

		//fetching screen size
		display = getWindowManager().getDefaultDisplay();
		ScrWidth=display.getWidth();
		ScrHeight=display.getHeight();

		//loading screen goes first
		Intent1 = new Intent(this,LoadingScreen.class);
	     startActivity(Intent1);

	    //set sizes, careful calc!
		LWidth=(int)(0.05*ScrHeight);
		LHeight=(int)(0.04*ScrHeight);
		BHeight=(int)(0.1*ScrHeight);
		BWidth=4*BHeight;

		ReadLanguage();

		//take care of buttons

		LanguageChange =  (ImageButton) findViewById(R.id.lang);
		LanguageChange.setOnTouchListener(this);

		GoSinglePlayer = (ImageButton) findViewById(R.id.ButtonSP);
		GoSinglePlayer.setOnTouchListener(this);

		GoMultiPlayer = (ImageButton) findViewById(R.id.ButtonMP);
		GoMultiPlayer.setOnTouchListener(this);

		GoHighScore = (ImageButton) findViewById(R.id.ButtonHS);
		GoHighScore.setOnTouchListener(this);

		GoHowToPlay = (ImageButton) findViewById(R.id.ButtonHTP);
		GoHowToPlay.setOnTouchListener(this);

		GoExit = (ImageButton) findViewById(R.id.ButtonEX);
		GoExit.setOnTouchListener(this);

		//set params
		psp  = new LayoutParams
			       (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		pmp   = new LayoutParams
			       (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		phs = new LayoutParams
				   (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		phtp   = new LayoutParams
			       (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		pexit = new LayoutParams
				   (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

		psp.leftMargin=(int)((ScrWidth-BWidth)/2.0);
		pmp.leftMargin=(int)((ScrWidth-BWidth)/2.0);
		phs.leftMargin=(int)((ScrWidth-BWidth)/2.0);
		phtp.leftMargin=(int)((ScrWidth-BWidth)/2.0);
		pexit.leftMargin=(int)((ScrWidth-BWidth)/2.0);

		psp.topMargin=(int)(ScrHeight*0.25);
		pmp.topMargin=psp.topMargin+(int)(ScrHeight*0.14);
		phs.topMargin=pmp.topMargin+(int)(ScrHeight*0.14);
		phtp.topMargin=phs.topMargin+(int)(ScrHeight*0.14);
		pexit.topMargin=phtp.topMargin+(int)(ScrHeight*0.14);

		//apply them
		GoSinglePlayer.setLayoutParams(psp);
		GoMultiPlayer.setLayoutParams(pmp);
		GoHighScore.setLayoutParams(phs);
		GoHowToPlay.setLayoutParams(phtp);
		GoExit.setLayoutParams(pexit);

		//LOAD DRAWABLES

		sp1=new Drawable[2];
		sp2=new Drawable[2];
		mp1=new Drawable[2];
		mp2=new Drawable[2];
		hs1=new Drawable[2];
		hs2=new Drawable[2];
		htp1=new Drawable[2];
		htp2=new Drawable[2];
		exit1=new Drawable[2];
		exit2=new Drawable[2];

		sp1[0] = context.getResources().getDrawable(R.drawable.looksp1);
		sp1[0] = ResizeDrawable(sp1[0],BWidth,BHeight);
		sp2[0] = context.getResources().getDrawable(R.drawable.looksp2);
		sp2[0] = ResizeDrawable(sp2[0],BWidth,BHeight);
		sp1[1] = context.getResources().getDrawable(R.drawable.srblooksp1);
		sp1[1] = ResizeDrawable(sp1[1],BWidth,BHeight);
		sp2[1] = context.getResources().getDrawable(R.drawable.srblooksp2);
		sp2[1] = ResizeDrawable(sp2[1],BWidth,BHeight);

		mp1[0] = context.getResources().getDrawable(R.drawable.lookmp1);
		mp1[0] = ResizeDrawable(mp1[0],BWidth,BHeight);
		mp2[0] = context.getResources().getDrawable(R.drawable.lookmp2);
		mp2[0] = ResizeDrawable(mp2[0],BWidth,BHeight);
		mp1[1] = context.getResources().getDrawable(R.drawable.srblookmp1);
		mp1[1] = ResizeDrawable(mp1[1],BWidth,BHeight);
		mp2[1] = context.getResources().getDrawable(R.drawable.srblookmp2);
		mp2[1] = ResizeDrawable(mp2[1],BWidth,BHeight);

		hs1[0] = context.getResources().getDrawable(R.drawable.lookhs1);
		hs1[0] = ResizeDrawable(hs1[0],BWidth,BHeight);
		hs2[0] = context.getResources().getDrawable(R.drawable.lookhs2);
		hs2[0] = ResizeDrawable(hs2[0],BWidth,BHeight);
		hs1[1] = context.getResources().getDrawable(R.drawable.srblookhs1);
		hs1[1] = ResizeDrawable(hs1[1],BWidth,BHeight);
		hs2[1] = context.getResources().getDrawable(R.drawable.srblookhs2);
		hs2[1] = ResizeDrawable(hs2[1],BWidth,BHeight);

		htp1[0] = context.getResources().getDrawable(R.drawable.lookhtp1);
		htp1[0] = ResizeDrawable(htp1[0],BWidth,BHeight);
		htp2[0] = context.getResources().getDrawable(R.drawable.lookhtp2);
		htp2[0] = ResizeDrawable(htp2[0],BWidth,BHeight);
		htp1[1] = context.getResources().getDrawable(R.drawable.srblookhtp1);
		htp1[1] = ResizeDrawable(htp1[1],BWidth,BHeight);
		htp2[1] = context.getResources().getDrawable(R.drawable.srblookhtp2);
		htp2[1] = ResizeDrawable(htp2[1],BWidth,BHeight);

		exit1[0] = context.getResources().getDrawable(R.drawable.lookexit1);
		exit1[0] = ResizeDrawable(exit1[0],BWidth,BHeight);
		exit2[0] = context.getResources().getDrawable(R.drawable.lookexit2);
		exit2[0] = ResizeDrawable(exit2[0],BWidth,BHeight);
		exit1[1] = context.getResources().getDrawable(R.drawable.srblookexit1);
		exit1[1] = ResizeDrawable(exit1[1],BWidth,BHeight);
		exit2[1] = context.getResources().getDrawable(R.drawable.srblookexit2);
		exit2[1] = ResizeDrawable(exit2[1],BWidth,BHeight);

		srb = context.getResources().getDrawable(R.drawable.srblang);
		srb = ResizeDrawable(srb,LWidth,LHeight);


		eng = context.getResources().getDrawable(R.drawable.englang);
		eng = ResizeDrawable(eng,LWidth,LHeight);

		//set initial drawables

		//language dependent language o.o
		if(language==0)
		{
			LanguageChange.setImageDrawable(eng);
		}
		else
		{
			LanguageChange.setImageDrawable(srb);
		}

		GoSinglePlayer.setImageDrawable(sp1[language]);
		GoMultiPlayer.setImageDrawable(mp1[language]);
		GoHighScore.setImageDrawable(hs1[language]);
		GoHowToPlay.setImageDrawable(htp1[language]);
		GoExit.setImageDrawable(exit1[language]);

		//about
		about  = new AlertDialog.Builder(this);
		aboutOpen=false; //prevent it from opening twice

	}

	void ReadLanguage()

	{
		hsRead = getSharedPreferences("hs", 0);
		hsEdit= hsRead.edit();

		//lang at 1337
		lang=hsRead.getString("1337", "e");
		if( lang.charAt(0)=='e')
			language=0;
		else if( lang.charAt(0)=='s')
			language=1;
	}

	void WriteLanguage()
	{
		hsRead = getSharedPreferences("hs", 0);
		hsEdit= hsRead.edit();

		//lang at 1337
		if( language==0)
			lang="e";
		else if( language==1)
			lang="s";
		hsEdit.putString("1337",lang);
		hsEdit.commit();
	}

	Drawable ResizeDrawable(Drawable image,int width,int height)
	{
		temp = ((BitmapDrawable)image).getBitmap();
		resbit = Bitmap.createScaledBitmap(temp, width, height, true);
	    resized = new BitmapDrawable(getResources(), resbit );
		return resized;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1)
	{
		//change the lang
		if(arg0.getId()==LanguageChange.getId())
		{
			if(arg1.getAction()==MotionEvent.ACTION_UP)
			{
				if(language==1)//was srb
				{
					language=0;
					LanguageChange.setImageDrawable(eng);
				}
				else if(language==0)//was rng
				{
					language=1;
					LanguageChange.setImageDrawable(srb);
				}
				GoSinglePlayer.setImageDrawable(sp1[language]);
				GoMultiPlayer.setImageDrawable(mp1[language]);
				GoHighScore.setImageDrawable(hs1[language]);
				GoHowToPlay.setImageDrawable(htp1[language]);
				GoExit.setImageDrawable(exit1[language]);

				WriteLanguage();
			}

		}

		//basic buttons
		if(arg0.getId()==GoSinglePlayer.getId())
		{
			if(arg1.getAction()==MotionEvent.ACTION_DOWN)
			{
				GoSinglePlayer.setImageDrawable(sp2[language]);
			}
			if(arg1.getAction()==MotionEvent.ACTION_UP)
			{
				GoSinglePlayer.setImageDrawable(sp1[language]);
				Intent1 = new Intent(this,SinglePlayer.class);
			    startActivity(Intent1);
			}
		}
		if(arg0.getId()==GoMultiPlayer.getId())
		{
			if(arg1.getAction()==MotionEvent.ACTION_DOWN)
			{
				GoMultiPlayer.setImageDrawable(mp2[language]);
			}
			if(arg1.getAction()==MotionEvent.ACTION_UP)
			{
				GoMultiPlayer.setImageDrawable(mp1[language]);
				Intent1 = new Intent(this,MultiPlayer.class);
			    startActivity(Intent1);
			}
		}
		if(arg0.getId()==GoHighScore.getId())
		{
			if(arg1.getAction()==MotionEvent.ACTION_DOWN)
			{
				GoHighScore.setImageDrawable(hs2[language]);
			}
			if(arg1.getAction()==MotionEvent.ACTION_UP)
			{
				GoHighScore.setImageDrawable(hs1[language]);
				Intent1 = new Intent(this,HighScore.class);
			    startActivity(Intent1);
			}
		}
		if(arg0.getId()==GoHowToPlay.getId())
		{
			if(arg1.getAction()==MotionEvent.ACTION_DOWN)
			{
				GoHowToPlay.setImageDrawable(htp2[language]);
			}
			if(arg1.getAction()==MotionEvent.ACTION_UP)
			{
				GoHowToPlay.setImageDrawable(htp1[language]);
				Intent1 = new Intent(this,HowToPlay.class);
			    startActivity(Intent1);
			}
		}
		if(arg0.getId()==GoExit.getId())
		{
			if(arg1.getAction()==MotionEvent.ACTION_DOWN)
			{
				GoExit.setImageDrawable(exit2[language]);
			}
			if(arg1.getAction()==MotionEvent.ACTION_UP)
			{
				GoExit.setImageDrawable(exit1[language]);
				System.exit(0);
			}
		}
		return true;
	}

	public boolean onTouchEvent(MotionEvent arg1)
	{
		if(aboutOpen)
			return true;
		if(arg1.getX()>=0.7*ScrWidth && arg1.getY()>=0.96*ScrHeight)
		{
			aboutOpen=true;
            if(language==0)
            {
               about.setMessage("Team Firepig:\n\tNikola Jovanovic\n\tOgnjen Djuricic\n\tSvetozar Ikovic\nContact:\n\tfirepighq@gmail.com\n\nSpecial thanks to Kosta Stojiljkovic.");
    		   about.setTitle("About");
            }
            else
            {
                   about.setMessage("Tim Firepig:\n\tNikola Jovanovic\n\tOgnjen Djuricic\n\tSvetozar Ikovic\nKontakt:\n\tfirepighq@gmail.com\n\nPosebna zahvalnost Kosti Stojiljkovicu.");
        		   about.setTitle("O nama");
            }
            about.setPositiveButton("OK",
    			    new DialogInterface.OnClickListener() {
    			        public void onClick(DialogInterface dialog, int which)
    			        {
    			           aboutOpen=false;	
    			        }
    			        });
            about.setCancelable(false);
            about.create().show();
		}
		return true;
	}

	@Override
	public void onDestroy()
	{
		//fix leaks
	    super.onDestroy();
    	temp=null;
		resbit=null;
		resized=null;
	    for(int i=0;i<=1;i++)
	    {
			sp1[i]=null;
			sp2[i]=null;
			mp1[i]=null;
			mp2[i]=null;
			hs1[i]=null;
			hs2[i]=null;
			htp1[i]=null;
			htp2[i]=null;
			exit1[i]=null;
			exit2[i]=null;
	    }

		srb=null;
		eng=null;
		GoSinglePlayer.setImageDrawable(null);
		GoMultiPlayer.setImageDrawable(null);
		GoHighScore.setImageDrawable(null);
		GoHowToPlay.setImageDrawable(null);
		GoExit.setImageDrawable(null);
		LanguageChange.setImageDrawable(null);
	}

}
