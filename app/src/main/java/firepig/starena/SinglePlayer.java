//Firepig
package firepig.starena;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class SinglePlayer extends Activity implements OnTouchListener 
{
    //vars
	
	//basic
	RelativeLayout bg;
	Display display;
	Context context;
	double ScrWidth, ScrHeight;
	
	//enviro
	public Point ArenaTL;
    public Point ArenaTR;
    public Point ArenaBL;
    public Point ArenaBR;
	Point BlackHole;

	//time
	Timer tm;
	int timerPeriod;
	int timeElapsed,mins,secs,mss;
	//assets
	String highScoreString;
	int highscorems;
	Boolean newHighScore;
	Typeface fontScore,fontStarena;
	//textviews
	TextView tapStart;
	TextView scoreLabel,score,hscoreLabel,hscore;
	TextView yourName;//just a label
	LayoutParams nameParams;
	EditText nameField;
	Drawable playAgainImg1,playAgainImg2;
	ImageButton playAgain;
	Drawable mainMenuImg1,mainMenuImg2;
	ImageButton mainMenu;
	
	//highscore handling
	SharedPreferences hsRead;
	SharedPreferences.Editor hsEdit;
	String userName;
	static int L; //name max len
	int showButtonsDelay; //useful stuff
	String[] hsFullString;
	String[] hsNames;
	int[] hsScores;

	//player
	Drawable[] PlayerTextures;
	Player player; 
	Drawable ShieldedTexture;
    
	//asteroid
	ArrayList<Asteroid> asters;
	double[] asterVels;
	int[] asterSizes;
	int[] asterDmgs;
	int asterCnt;
	//ASteroid spawning
	double lastAster;
	double newAsterTime;		
	double minNewAsterTime;
	double maxNewAsterTime;
	int typeRand;
	
	//HealthPack
	HealthPack hPack;//always the same
	double hpVel;
	int hpSize;
	int hpValue;
	double hpLifetime;
	//spawning
	double lastHpDied;
	double newHpTime;		
	double minNewHpTime;
	double maxNewHpTime;
	
	//abilities
	
	//buttons
	ImageButton ChangeRotation;
	ImageButton Pause; 
	ImageButton Leap, Invert, Shield;
	int ButtonWidth,ButtonHeight;
	//drawables
	Drawable PauseOk,PauseUsed, Play;
	Drawable LeapOk, LeapCd, InvertOk, InvertCd, ShieldOk, ShieldCd;
	Drawable ChangeRotationOk, ChangeRotationPressed;
	//cds
	int LeapCdTime, ShieldCdTime, InvertCdTime;
	int LeapUsedTime, ShieldUsedTime, InvertUsedTime;
	boolean LeapOnCd, ShieldOnCd, InvertOnCd;
	int LeapRange, ShieldDuration;
	boolean pauseUsed;
	
	//language
	String lang;
	int language;
	
	//init
	boolean pause;
	boolean gameOver;
	
	//pointer coords
	Point clickPos;
	
	//resize drawables
	Bitmap temp;
	Bitmap resbit;
	Drawable resized;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	    //standard oncreate stuff
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.activity_single_player);
		context = getApplicationContext();
		bg=(RelativeLayout) findViewById(R.id.Background2);
		//we want fullscreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//fetching screen size
		display = getWindowManager().getDefaultDisplay();
		ScrWidth=display.getWidth();
		ScrHeight=display.getHeight();
		
		//set bg
		bg.setBackgroundResource(R.drawable.backnew);

		L=7;
		ReadLanguage();
		
		//textviews
		tapStart = (TextView) findViewById(R.id.tapstartsp);
		scoreLabel = (TextView) findViewById(R.id.textView1);
		hscoreLabel = (TextView) findViewById(R.id.textView2);
		score = (TextView) findViewById(R.id.textView3);
		hscore = (TextView) findViewById(R.id.textView4);
		//font
		fontScore = Typeface.createFromAsset(getAssets(),"scorefont.ttf");
		fontStarena = Typeface.createFromAsset(getAssets(),"starenafont.ttf");
		scoreLabel.setTypeface(fontScore);
		hscoreLabel.setTypeface(fontScore);
		//lang?
		if(language==0)
		{
			tapStart.setText("Tap Here To Start");
			scoreLabel.setText(" Score");
			hscoreLabel.setText("High Score ");
		}
		else
		{
			tapStart.setText("Dodirnite Ovde Za Start");
			scoreLabel.setText(" Skor");
			hscoreLabel.setText("Najbolji Skor ");
		}
		score.setTypeface(fontScore);
		hscore.setTypeface(fontScore);
		tapStart.setTypeface(fontStarena);

		//reading
		hsFullString=new String[11];
		hsNames=new String[11];
		hsScores=new int[11];
		
		//init scores
		timeElapsed=0;//score
		highscorems=getHighScore(); //highscore
		hscore.setText( makeScoreString(highscorems) );
		hscore.setTextColor(Color.GREEN);
		newHighScore=false;
		
		//Buttons
		ButtonWidth=(int)(ScrWidth/6.0);
		ButtonHeight=(int)(ScrWidth/6.0);
		TakeCareOfButtons();
		//set initial textures:
		Pause.setImageDrawable(PauseOk);
		Leap.setImageDrawable(LeapOk);
		Shield.setImageDrawable(ShieldOk);
		Invert.setImageDrawable(InvertOk);
		ChangeRotation.setImageDrawable(ChangeRotationOk);
		//set cds
		LeapOnCd=false;
		ShieldOnCd=false; 
		InvertOnCd=false;
		
		//setup endgame
		showButtonsDelay=800;
		pauseUsed=false;
		//namefield
		nameField=(EditText) findViewById(R.id.editText1);
		nameField.setTypeface(fontStarena);
		nameField.setText(getLastUserName());
		//yourname
		yourName=(TextView) findViewById(R.id.nameField);
		yourName.setTypeface(fontStarena);
		if(language==0)
			yourName.setText("Name:");
		else
			yourName.setText("Ime:"); //q je sh
		yourName.setTypeface(fontStarena);
		nameParams=new LayoutParams
			       (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

		nameParams.leftMargin =(int)(0.35*ScrWidth);
		nameParams.topMargin =(int)(0.4*ScrHeight);
		yourName.setLayoutParams(nameParams);
		//buttons
		nameField = (EditText) findViewById(R.id.editText1);
		//play again & main menu
		playAgain = (ImageButton) findViewById(R.id.playAgainSP);
		mainMenu = (ImageButton) findViewById(R.id.mainMenuSP);
		if(language==0)
		{
		  playAgainImg1 = context.getResources().getDrawable(R.drawable.lookplayagain1);
		  playAgainImg2 = context.getResources().getDrawable(R.drawable.lookplayagain2);
		  mainMenuImg1 = context.getResources().getDrawable(R.drawable.lookmainmenu1);
		  mainMenuImg2 = context.getResources().getDrawable(R.drawable.lookmainmenu2);
		}
		else
		{
				playAgainImg1 = context.getResources().getDrawable(R.drawable.srblookplayagain1);
				playAgainImg2 = context.getResources().getDrawable(R.drawable.srblookplayagain2);
				mainMenuImg1 = context.getResources().getDrawable(R.drawable.srblookmainmenu1);
				mainMenuImg2 = context.getResources().getDrawable(R.drawable.srblookmainmenu2);
				
		}
		//resize drawables
		playAgainImg1 = ResizeDrawable(playAgainImg1,(int)(2.5*ButtonWidth),ButtonHeight);
		playAgainImg2 = ResizeDrawable(playAgainImg2,(int)(2.5*ButtonWidth),ButtonHeight);
		mainMenuImg1 = ResizeDrawable(mainMenuImg1,(int)(2.5*ButtonWidth),ButtonHeight);
		mainMenuImg2 = ResizeDrawable(mainMenuImg2,(int)(2.5*ButtonWidth),ButtonHeight);
		//init buttons
		playAgain.setImageDrawable(playAgainImg1);
		mainMenu.setImageDrawable(mainMenuImg1);
		playAgain.setOnTouchListener(this);
		mainMenu.setOnTouchListener(this); 

		//evth invisible
		tapStart.setVisibility(View.VISIBLE);
		yourName.setVisibility(View.INVISIBLE);
		playAgain.setVisibility(View.INVISIBLE);
		mainMenu.setVisibility(View.INVISIBLE);
		nameField.setVisibility(View.INVISIBLE);
	    Pause.setVisibility(View.VISIBLE);
		Leap.setVisibility(View.VISIBLE);
		Shield.setVisibility(View.VISIBLE);
		Invert.setVisibility(View.VISIBLE);
		ChangeRotation.setVisibility(View.VISIBLE);
		
		//arena edges
		ArenaTL=new Point((int)(ScrWidth/8.0),(int)(ScrHeight/8.0));
	    ArenaTR=new Point((int)(7.0*ScrWidth/8.0),(int)(ScrHeight/8.0));
	    ArenaBL=new Point((int)(ScrWidth/8.0),(int)(7.0*ScrHeight/8.0));
	    ArenaBR=new Point((int)(7.0*ScrWidth/8.0),(int)(7.0*ScrHeight/8.0));
		//we need a black hole
		BlackHole=new Point();
		BlackHole.x=(int)(ScrWidth/2.0);
		BlackHole.y=(int)(ScrHeight/2.0);
		
	    //player:
		PlayerTextures=new Drawable[15];
		PlayerTextures[0] = context.getResources().getDrawable(R.drawable.bluedead);
		PlayerTextures[1] = context.getResources().getDrawable(R.drawable.blue10);	
		PlayerTextures[2] = context.getResources().getDrawable(R.drawable.blue20);
		PlayerTextures[3] = context.getResources().getDrawable(R.drawable.blue30);
		PlayerTextures[4] = context.getResources().getDrawable(R.drawable.blue40);
		PlayerTextures[5] = context.getResources().getDrawable(R.drawable.blue50);
		PlayerTextures[6] = context.getResources().getDrawable(R.drawable.blue60);
		PlayerTextures[7] = context.getResources().getDrawable(R.drawable.blue70);
		PlayerTextures[8] = context.getResources().getDrawable(R.drawable.blue80);
		PlayerTextures[9] = context.getResources().getDrawable(R.drawable.blue90);
		PlayerTextures[10] = context.getResources().getDrawable(R.drawable.blue100);
		ShieldedTexture=context.getResources().getDrawable(R.drawable.shieldedplayerblue);
		//creating
		player=new Player();
		player.Initialize(ShieldedTexture,context,ScrHeight,ScrWidth,bg,ArenaTL,ArenaTR,ArenaBL,ArenaBR,PlayerTextures);
		
		//setting the timer
		timerPeriod=20;
		tm = new Timer();
		tm.schedule(new TimerTask()
		{
			public void run()
			{
				TickLoop();//20 ms game loop
			}
		} , 0, timerPeriod);
		
		//asteroids, initialization
		double s=player.size;
		asterCnt=0;
		asters = new ArrayList<Asteroid>(100);
		lastAster=0;
		
		//All BALANCE vars
		
		//asters: type 1-slow aster, 2-medium aster, 3-fast aster
		asterVels=new double[] {0, (int)(player.inc/3.0), (int)(2.0*player.inc/3.0), player.inc};
		asterSizes=new int[] {0,(int)((3.0/2.0)*s), (int)s, (int)((2.0/3.0)*s)};//2 4 6
		asterDmgs=new int[] {0, 35, 25, 15};
		minNewAsterTime=4000;
		maxNewAsterTime=12000;
		newAsterTime=3000;
		//hPacks
		hpVel=(int)(player.inc/12.0*5.0); //2.5
		hpSize=(int)((3.0/4.0)*s);
		hpValue=20;
		hpLifetime=7000;
		minNewHpTime=15000;
		maxNewHpTime=25000;
		newHpTime=10000;
		//basic spells
		LeapCdTime=3000;
		ShieldCdTime=15000;
		InvertCdTime=8000;
		LeapRange=(ArenaTR.x-ArenaTL.x)/2; 
		ShieldDuration=3000;
		
		//1 hp for all
		hPack=new HealthPack();
		lastHpDied=0;
		//1 dead to begin!!
		hPack.Init(hpVel,hpSize,hpValue,hpLifetime,BlackHole,context,bg);
		
		//init
		pause=true;
		gameOver=false;
		
		//pointer pos
		clickPos=new Point();

		//draw once!
		DrawAll();
	}
	
	void ReadLanguage()
	{
		hsRead = getSharedPreferences("hs", 0);
		hsEdit= hsRead.edit();
		
		lang=hsRead.getString("1337", "e");
		if( lang.charAt(0)=='e')
			language=0;
		else if( lang.charAt(0)=='s')
			language=1;
	}
	
	String makeScoreString(int time)
	{
		//Ms -> nice look
		String str=new String();
		mins=time/60000;
		secs=(time%60000)/1000;
		mss=time-mins*60000-secs*1000;

		if(time==timeElapsed)
				str=" ";
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
		if(time==highscorems)
			str+=" ";
		
		return str;
	}
	
	void TakeCareOfButtons()
	{
		        //BUTTONS
		
				//read drawables
		
				PauseOk = context.getResources().getDrawable(R.drawable.pauseblue);	
				PauseOk = ResizeDrawable(PauseOk,ButtonWidth,ButtonHeight);

				PauseUsed = context.getResources().getDrawable(R.drawable.pauseblueused);	
				PauseUsed = ResizeDrawable(PauseUsed,ButtonWidth,ButtonHeight);

				Play = context.getResources().getDrawable(R.drawable.playblue);	
				Play = ResizeDrawable(Play,ButtonWidth,ButtonHeight);

				LeapOk = context.getResources().getDrawable(R.drawable.leapblue);	
				LeapOk = ResizeDrawable(LeapOk,ButtonWidth,ButtonHeight);
				LeapCd = context.getResources().getDrawable(R.drawable.leapbluecd);	
				LeapCd = ResizeDrawable(LeapCd,ButtonWidth,ButtonHeight);
				
				ShieldOk = context.getResources().getDrawable(R.drawable.shieldblue);	
				ShieldOk = ResizeDrawable(ShieldOk,ButtonWidth,ButtonHeight);
				ShieldCd = context.getResources().getDrawable(R.drawable.shieldbluecd);	
				ShieldCd = ResizeDrawable(ShieldCd,ButtonWidth,ButtonHeight);
				
				InvertOk = context.getResources().getDrawable(R.drawable.invertblue);	
				InvertOk = ResizeDrawable(InvertOk,ButtonWidth,ButtonHeight);
				InvertCd = context.getResources().getDrawable(R.drawable.invertbluecd);	
				InvertCd = ResizeDrawable(InvertCd,ButtonWidth,ButtonHeight);

				ChangeRotationOk = context.getResources().getDrawable(R.drawable.changerotationblue);	
				ChangeRotationOk = ResizeDrawable(ChangeRotationOk,ButtonWidth,ButtonHeight);
				ChangeRotationPressed = context.getResources().getDrawable(R.drawable.changerotationpressedblue);	
				ChangeRotationPressed = ResizeDrawable(ChangeRotationPressed,ButtonWidth,ButtonHeight);
				
				//init every button 
				//connect with listener, connect with layout
				
				Pause = (ImageButton) findViewById(R.id.Button01);
				Pause.setOnTouchListener(this);
				
				Leap = (ImageButton) findViewById(R.id.Button02);
				Leap.setOnTouchListener(this);
				
				Shield = (ImageButton) findViewById(R.id.Button03);
				Shield.setOnTouchListener(this);
				
				Invert = (ImageButton) findViewById(R.id.Button04);
				Invert.setOnTouchListener(this);
				
				ChangeRotation = (ImageButton) findViewById(R.id.Button05);
				ChangeRotation.setOnTouchListener(this);
				
	}

	Drawable ResizeDrawable(Drawable image,int width,int height) 
	{
		temp = ((BitmapDrawable)image).getBitmap();
		resbit = Bitmap.createScaledBitmap(temp, width, height, true);
	    resized = new BitmapDrawable(getResources(), resbit );
		return resized;
	}
	
	void SpawnAster(int type)
	{
		double angPercentage=Math.random();//0 to 1 ang
		Asteroid temp=new Asteroid();
		temp.Initialize(angPercentage,type, asterVels, asterSizes, asterDmgs,
				BlackHole, context, bg);
		asters.add(temp);
		asterCnt++;
	}
	
	void SpawnHealthPack()
	{
		double angPercentage=Math.random();//0 to 1
		hPack.Respawn(angPercentage,BlackHole);//just respawn
	}
	
	void DrawAll()
	{
		for(int i=0;i<=asterCnt-1;i++)
			 asters.get(i).Draw();
		hPack.Draw();
		player.Draw();
	}

	@Override
    public boolean onTouchEvent(MotionEvent event) 
	{
			    //begin the game (unpause technically, but we removed it)
		        if(event.getAction() == MotionEvent.ACTION_UP) 
		        {
		        	clickPos.x=(int) event.getX();
		        	clickPos.y=(int) event.getY();
		        	if(timeElapsed==0 && PointDist(clickPos,BlackHole) < ScrWidth/4.0)
		        	{
		        		    tapStart.setVisibility(View.INVISIBLE);
		        			pause=false;
		        	}
		        }
		         return super.onTouchEvent(event);
	}
		
	public void TickLoop() 
	{
		this.runOnUiThread(MovingTimerTick);
	}
	
	public void initiateEnd()
	{
		//hide all
		yourName.setVisibility(View.VISIBLE);
		nameField.setVisibility(View.VISIBLE);
		Pause.setVisibility(View.INVISIBLE);
		Leap.setVisibility(View.INVISIBLE);
		Shield.setVisibility(View.INVISIBLE);
		Invert.setVisibility(View.INVISIBLE);
		ChangeRotation.setVisibility(View.INVISIBLE);
		
		//dedraw
		bg.removeView(hPack.currImage);
	    for(int i=0;i<=asterCnt-1;i++)
	    	bg.removeView(asters.get(i).currImage);
	    player.Draw();
	    
		//over!
		pause=true;
		gameOver=true;
		
		if(language==0)
		 bg.setBackgroundResource(R.drawable.backover);
		else
		 bg.setBackgroundResource(R.drawable.srbbackover);
	}
	
	private Runnable MovingTimerTick = new Runnable() 
	{
		public void run() 
		{
			//game loop
			
			//endgame button delay
			if(gameOver && showButtonsDelay>0)
			{
				showButtonsDelay-=timerPeriod;
				if(showButtonsDelay<=0)
				{
				  playAgain.setVisibility(View.VISIBLE);
				  mainMenu.setVisibility(View.VISIBLE);
				}	
			}
			
			//if pause go away
			if(pause) return;
			
			//update time
			timeElapsed+=timerPeriod;//period!
			score.setText( makeScoreString(timeElapsed) );
			
			//new hs?
			if(!newHighScore && timeElapsed>highscorems)
			{
				newHighScore=true;
				hscore.setTextColor(Color.RED);
			}
			
			//New Aster?
			if(timeElapsed-lastAster>newAsterTime)
			{
				//a part of [min,max] interval add to min
				lastAster=timeElapsed;	
				newAsterTime=minNewAsterTime+(maxNewAsterTime-minNewAsterTime)*(Math.random());

				//[0,1) [0,3) [1,4) 1,2,3
				typeRand = (int)(Math.random()*3.0+1.0);
				SpawnAster(typeRand);
			}
			
			//Update Asters
			if(asterCnt>0)
			{
				
			 for(int i=0;i<=asterCnt-1;i++)
			 {
				
				if(asters.get(i).dying==4)
				{
					bg.removeView(asters.get(i).currImage);
					asters.remove(i);
					asterCnt--;
				}
				else
				{
					asters.get(i).Update(false,player,timeElapsed,ArenaTL,ArenaBR);
					if(player.hp==0)
					{
						initiateEnd();
						return;
					}
				}
			 } 
			
			}
			
			//New HPack?
			if(hPack.dead==1 && timeElapsed-lastHpDied>newHpTime)
			{
				newHpTime=minNewHpTime+(maxNewHpTime-minNewHpTime)*(Math.random());
				lastHpDied=-1;
				SpawnHealthPack();
			}
			
			//Update HPack
			hPack.CheckExpiration(timeElapsed);
			hPack.Update(false,player, timeElapsed, ArenaTL, ArenaBR);
			
			//he just died
			if(lastHpDied==-1 && hPack.dead==1)
				lastHpDied=timeElapsed;
			
			//Update Player
			player.Update(ArenaTL,ArenaTR,ArenaBL,ArenaBR);
			
			//check cds
			if(LeapOnCd && timeElapsed-LeapUsedTime > LeapCdTime)
			{
				LeapOnCd=false;
				Leap.setImageDrawable(LeapOk);
			}
			if(ShieldOnCd && timeElapsed-ShieldUsedTime > ShieldCdTime)
			{
				ShieldOnCd=false;
				Shield.setImageDrawable(ShieldOk);
			}
			if(InvertOnCd && timeElapsed-InvertUsedTime > InvertCdTime)
			{
				InvertOnCd=false;
				Invert.setImageDrawable(InvertOk);
			}
			
			//shield broke
			if(player.shielded && timeElapsed-ShieldUsedTime > ShieldDuration)
			{
				player.shielded=false;
				bg.removeView(player.shield);
			}
			
			//DRAW
			DrawAll();
		}
	};
	
	public int getHighScore()
	{
		String highScoreString;
		hsRead = getSharedPreferences("hs", 0);
		//read
		highScoreString=hsRead.getString("1", "/");
		if(highScoreString.length()>1)
		  return Integer.parseInt(highScoreString.substring(L+1,highScoreString.length()));
		else
		  return 0;
		//substring(a,b) cuts from a to b-1
	}
	
	public String getLastUserName()
	{
		//vars
		hsRead = getSharedPreferences("hs", 0);
		
		//read
		highScoreString=hsRead.getString("69", "Firepig");
		
		//delete the spaces
		int cut2=highScoreString.indexOf(" ");
		if(cut2==-1)
			cut2=highScoreString.length();
		if(highScoreString.length()>1)
			return highScoreString.substring(0,cut2);
		else
			return "";
	}
	
	public void updateHighScores(String currName, int currScore)
	{
		//mainmenu/playagain triggers this
		
		//init
		hsRead = getSharedPreferences("hs", 0);
		hsEdit= hsRead.edit();
		
		//0 for last name user used
		hsEdit.putString("0",currName);
		
		//format: "8LETNAME~SCORE"
		int addBlanks=L-currName.length();
		String currFullString=currName;
		for(int i=1;i<=addBlanks;i++)
			currFullString+=" ";
		currFullString+="~";
		currFullString+=currScore;
		
		//read
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
		
		//put the new score
		hsEdit.putString("69",currFullString);//last
		for(int i=10;i>=1;i--)
		{
			key = Integer.toString(i+1);
			if(currScore>hsScores[i])
				hsEdit.putString(key,hsFullString[i]);
			else
			{
				hsEdit.putString(key,currFullString);
				break;
			}
		}
		
		if(newHighScore)
		  hsEdit.putString("1",currFullString);
		
		hsEdit.commit();
		return;
	}
	
	@Override
	public boolean onTouch(View buttonPressed, MotionEvent action) 
	{
		//pressing buttons
		
		//endgame first
		if(buttonPressed.getId()==playAgain.getId())
		{
		        	if(action.getAction() == MotionEvent.ACTION_DOWN)
		        	{
		        		playAgain.setImageDrawable(playAgainImg2);
		        		userName = nameField.getText().toString();
		        		userName=userName.toUpperCase(Locale.US);
		        		updateHighScores(userName,timeElapsed);
		    			Intent Intent1 = new Intent(context,SinglePlayer.class);
		    		    startActivity(Intent1);
		    		    finish();
			        }
		        	if(action.getAction() == MotionEvent.ACTION_UP)
				    {
		        		playAgain.setImageDrawable(playAgainImg1);
				    }
				    return true;
		}
		if(buttonPressed.getId()==mainMenu.getId())
		{
		        	if(action.getAction() == MotionEvent.ACTION_DOWN)
		        	{
		        		mainMenu.setImageDrawable(mainMenuImg2);
		        		userName = nameField.getText().toString();
		        		userName=userName.toUpperCase(Locale.US);
		        		updateHighScores(userName,timeElapsed);
		    		    finish();
			        }
		        	if(action.getAction() == MotionEvent.ACTION_UP)
				    {
		        		mainMenu.setImageDrawable(mainMenuImg1);
				    }
				    return true;
		}
		
		if(gameOver)
			return true;
		//if not game over, other buttons

		if(buttonPressed.getId()==Pause.getId())
		{
			if(action.getAction() == MotionEvent.ACTION_UP)
			{
				if(timeElapsed>0 && !pauseUsed)
				{
					pause=true;
					pauseUsed=true;
					Pause.setImageDrawable(Play);
				}
				else if(timeElapsed>0 && pause)
				{
					pause=false;
					Pause.setImageDrawable(PauseUsed);
				}
			}
		}
		
		if(pause)
			return true;
		//if not pause, other buttons
		
		//first basic ability
		if(!LeapOnCd && buttonPressed.getId()==Leap.getId() && action.getAction() == MotionEvent.ACTION_DOWN)
		{
			//it's on cd
			Leap.setImageDrawable(LeapCd);
			LeapUsedTime=timeElapsed;
			LeapOnCd=true;
			
			//do shit, KRLJ
			//16 possibilities(cw,L,leapsover)
			if(player.cw)
			{
				if(player.side=='L')
				{
					if( LeapRange > (player.pos.y - ArenaTL.y) )
					{
						player.pos.x+=LeapRange-(player.pos.y - ArenaTL.y);
						player.pos.y=ArenaTL.y;
						player.side='U';
						player.incx=player.inc;
						player.incy=0;
					}
					else
					{
						player.pos.y-=LeapRange;
					}
				}
				if(player.side=='U')
				{
					if( LeapRange > (ArenaTR.x - player.pos.x) )
					{
						player.pos.y+=LeapRange-(ArenaTR.x - player.pos.x);
						player.pos.x=ArenaTR.x;
						player.side='R';
						player.incx=0;
						player.incy=player.inc;
					}
					else
					{
						player.pos.x+=LeapRange;
					}
				}
				if(player.side=='R')
				{
					if( LeapRange > (ArenaBR.y - player.pos.y) )
					{
						player.pos.x-=LeapRange-(ArenaBR.y - player.pos.y);
						player.pos.y=ArenaBR.y;
						player.side='D';
						player.incx=-player.inc;
						player.incy=0;
					}
					else
					{
						player.pos.y+=LeapRange;
					}
				}
				if(player.side=='D')
				{
					if( LeapRange > (player.pos.x - ArenaBL.x) )
					{
						player.pos.y-=LeapRange-(player.pos.x - ArenaBL.x);
						player.pos.x=ArenaBL.x;
						player.side='L';
						player.incx=0;
						player.incy=-player.inc;
					}
					else
					{
						player.pos.x-=LeapRange;
					}
				}
			}
			else
			{
				if(player.side=='L')
				{
					if( LeapRange > (ArenaBL.y - player.pos.y) )
					{
						player.pos.x+=LeapRange-(ArenaBR.y - player.pos.y);
						player.pos.y=ArenaBL.y;
						player.side='D';
						player.incx=player.inc;
						player.incy=0;
					}
					else
					{
						player.pos.y+=LeapRange;
					}
				}
				if(player.side=='U')
				{
					if( LeapRange > (player.pos.x - ArenaTL.x) )
					{
						player.pos.y+=LeapRange-(player.pos.x - ArenaTL.x);
						player.pos.x=ArenaTL.x;
						player.side='L';
						player.incx=0;
						player.incy=player.inc;
					}
					else
					{
						player.pos.x-=LeapRange;
					}
				}
				if(player.side=='R')
				{
					if( LeapRange > (player.pos.y - ArenaTR.y) )
					{
						player.pos.x-=LeapRange-(player.pos.y - ArenaTR.y);
						player.pos.y=ArenaTR.y;
						player.side='U';
						player.incx=-player.inc;
						player.incy=0;
					}
					else
					{
						player.pos.y-=LeapRange;
					}
				}
				if(player.side=='D')
				{
					if( LeapRange > ( ArenaBR.x - player.pos.x) )
					{
						player.pos.y-=LeapRange-( ArenaBR.x - player.pos.x);
						player.pos.x=ArenaBR.x;
						player.side='R';
						player.incx=0;
						player.incy=-player.inc;
					}
					else
					{
						player.pos.x+=LeapRange;
					}
				}
			}
			
			return true;
		}
		
		//second basic ability
		if(!ShieldOnCd && buttonPressed.getId()==Shield.getId() && action.getAction() == MotionEvent.ACTION_DOWN)
		{
			//set on cd
			Shield.setImageDrawable(ShieldCd);
			ShieldUsedTime=timeElapsed;
			ShieldOnCd=true;
			
			player.shielded=true;
			//aster class -> aster so false
			//timer tick -> expire so false
			//player -> do not draw it
			return true;
		}
				
		//third basic ability
		if(!InvertOnCd && buttonPressed.getId()==Invert.getId() && action.getAction() == MotionEvent.ACTION_DOWN)
		{
			//set on cd
			Invert.setImageDrawable(InvertCd);
			InvertUsedTime=timeElapsed;
			InvertOnCd=true;
			
			//change dir ofc
			if(player.cw)
				player.cw=false;
			else
				player.cw=true;
			
			//find where to put him
			if(player.side=='D')
			{
				player.side='U';
				player.pos.y=ArenaTL.y;
				return true;
			}
			if(player.side=='L')
			{
				player.side='R';
				player.pos.x=ArenaTR.x;
				return true;
			}
			if(player.side=='U')
			{
				player.side='D';
				player.pos.y=ArenaBL.y;
				return true;
			}
			if(player.side=='R')
			{
				player.side='L';
				player.pos.x=ArenaTL.x;
				return true;
			}
		}
		//change rot
		if(buttonPressed.getId()==ChangeRotation.getId())
		{
	        	if(action.getAction() == MotionEvent.ACTION_DOWN)
	        	{
				   ChangeRotation.setImageDrawable(ChangeRotationPressed);       	
		    	   player.incx*=-1;
				   player.incy*=-1;
				   if(player.cw)
					player.cw=false;
				   else
					player.cw=true;
		         }
	        	
	        	if(action.getAction() == MotionEvent.ACTION_UP)
			       ChangeRotation.setImageDrawable(ChangeRotationOk); 
	        	
			    return true;
	   }
	   return true;
	}
	
	public double PointDist(Point a,Point b)
	{
		double dx=Math.abs(a.x-b.x);
		double dy=Math.abs(a.y-b.y);
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	@Override
	public void onDestroy() 
	{
	    super.onDestroy();
		//fixing memory leaks
		temp=null;
		resbit=null;
		resized=null;
	    playAgainImg1=null;
		playAgainImg2=null;
		mainMenuImg1=null;
		mainMenuImg2=null;
		PauseOk=null;
		PauseUsed=null;
		Play=null;
		LeapOk=null;
		LeapCd=null;
		InvertOk=null;
		InvertCd=null; 
		ShieldOk=null; 
		ShieldCd=null;
		ChangeRotationOk=null; 
		ChangeRotationPressed=null;
	    Pause.setImageDrawable(null);
		Invert.setImageDrawable(null);
		Leap.setImageDrawable(null);
		Shield.setImageDrawable(null);
		ChangeRotation.setImageDrawable(null);
		mainMenu.setImageDrawable(null);
		playAgain.setImageDrawable(null);
	}
}
