//Firepig
package firepig.starena;



import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class MultiPlayer extends Activity implements OnTouchListener 
{
	//0 blue player, down
	//1 purp player, up
	
    //basic vars
	RelativeLayout bg;
	Context context;
	Display display;
	double ScrWidth, ScrHeight;

	//time
	Timer tm; 
	int timerPeriod;
	int timeElapsed,mins,secs,mss;

	//textview and his typeface
	TextView tapStart;
	Typeface fontStarena;

	//edges
	public Point ArenaTL;
    public Point ArenaTR;
    public Point ArenaBL;
    public Point ArenaBR;
    
	//Black Hole
	Point BlackHole;
	
	//abilities
	//buttons
	ImageButton[] ChangeRotation;
	ImageButton[] Pause;
	ImageButton[] Leap, Invert, Shield;
	int ButtonWidth,ButtonHeight;
	//draws
	Drawable[] PauseOk,PauseUsed,Play;
	Drawable[] LeapOk, LeapCd, InvertOk, InvertCd, ShieldOk, ShieldCd;
	Drawable[] ChangeRotationOk, ChangeRotationPressed;
	//cds
	int LeapCdTime, ShieldCdTime, InvertCdTime;
	int[] LeapUsedTime, ShieldUsedTime, InvertUsedTime;
	boolean[] LeapOnCd, ShieldOnCd, InvertOnCd;
	int LeapRange, ShieldDuration;
	boolean[] pauseUsed; //who used it?
	
	//player
	Drawable[] PlayerTexturesB, PlayerTexturesP;
	Player bluePlayer; 
	Player purpPlayer;
	Drawable ShieldedTextureB,ShieldedTextureP;
    
	//asteroid vars
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
	boolean asteroidShouldDie; //for purple
	
	
	//HealthPackVars
	HealthPack hPack;//one!
	double hpVel;
	int hpSize;
	int hpValue;
	double hpLifetime;
	//spawning
	double lastHpDied;
	double newHpTime;		
	double minNewHpTime;
	double maxNewHpTime;
	boolean hPackShouldDie; //same as aster

	//crash vars
	int crashDmg;
	boolean crashOnCd;
	int lastCrashTime;
	int crashDelta;
	
	//endgame
	double iRatio;
	ImageView signR,signL;
	Drawable signRDr,signLDr;
	LayoutParams imgParams;
	int showButtonsDelay;
	ImageButton[] playAgain; //i igramo opet
	Drawable[] playAgainImg1,playAgainImg2;
	ImageButton[] mainMenu; //ili nazad na meni
	Drawable[] mainMenuImg1,mainMenuImg2;
	
	//init
	boolean pause;
	boolean gameOver;
	
	//pointer pos
	Point clickPos;
	
	//size, duh
	int s;

	//readers
	SharedPreferences hsRead;
	SharedPreferences.Editor hsEdit;
	
	//resizing drawables
	Bitmap temp;
	Bitmap resbit;
	Drawable resized;
	int justPaused;
	
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
		setContentView(R.layout.activity_multi_player);
		context = getApplicationContext();
		bg=(RelativeLayout) findViewById(R.id.Background2);
		
		//fetching screen size
		display = getWindowManager().getDefaultDisplay();
		ScrWidth=display.getWidth();
		ScrHeight=display.getHeight();
		
		ReadLanguage();
		
		//time and pauses
		timeElapsed=0;
		pauseUsed=new boolean[2];
		pauseUsed[0]=false;
		pauseUsed[1]=false;
		justPaused=-1;
		
		//abilities:
		ButtonWidth=(int)(ScrWidth/6.0);
		ButtonHeight=(int)(ScrWidth/6.0);
		//all arays
		Pause=new ImageButton[2];
		Leap=new ImageButton[2];
		Shield=new ImageButton[2];
		Invert=new ImageButton[2];
	    ChangeRotation=new ImageButton[2];
		LeapOk=new Drawable[2];
	    LeapCd=new Drawable[2];
		InvertOk=new Drawable[2];
		InvertCd=new Drawable[2];
		ShieldOk=new Drawable[2];
		ShieldCd=new Drawable[2];
		ChangeRotationOk=new Drawable[2];
		ChangeRotationPressed=new Drawable[2];
		PauseOk=new Drawable[2];
		PauseUsed=new Drawable[2];
		Play=new Drawable[2];
		//take care
		TakeCareOfButtons();
		//set initial textures:
		Pause[0].setImageDrawable(PauseOk[0]);
		Pause[1].setImageDrawable(PauseOk[1]);
		Leap[0].setImageDrawable(LeapOk[0]);
		Shield[0].setImageDrawable(ShieldOk[0]);
		Invert[0].setImageDrawable(InvertOk[0]);
		Leap[1].setImageDrawable(LeapOk[1]);
		Shield[1].setImageDrawable(ShieldOk[1]);
		Invert[1].setImageDrawable(InvertOk[1]);
		ChangeRotation[0].setImageDrawable(ChangeRotationOk[0]);
		ChangeRotation[1].setImageDrawable(ChangeRotationOk[1]);
		//MOAR ARRAYS
		LeapUsedTime=new int[2];
		ShieldUsedTime=new int[2]; 
		InvertUsedTime=new int[2];
		LeapOnCd=new boolean[2];
		ShieldOnCd=new boolean[2]; 
		InvertOnCd=new boolean[2];
		LeapOnCd[0]=false;
		ShieldOnCd[0]=false; 
		InvertOnCd[0]=false;
		LeapOnCd[1]=false;
		ShieldOnCd[1]=false; 
		InvertOnCd[1]=false;
		
		//endgame stuff
		tapStart = (TextView) findViewById(R.id.tapstartmp);
		signR = (ImageView) findViewById(R.id.imageView2);
		signL = (ImageView) findViewById(R.id.imageView3);
		showButtonsDelay=800;
		//play again & main menu
		playAgain=new ImageButton[2];
		mainMenu=new ImageButton[2];
		playAgainImg1=new Drawable[2];
		playAgainImg2=new Drawable[2];
		mainMenuImg1=new Drawable[2];
		mainMenuImg2=new Drawable[2];
		playAgain[0] = (ImageButton) findViewById(R.id.playAgainBlue);
		mainMenu[0] = (ImageButton) findViewById(R.id.mainMenuBlue);
		playAgain[1] = (ImageButton) findViewById(R.id.playAgainPurp);
		mainMenu[1] = (ImageButton) findViewById(R.id.mainMenuPurp);
		//font+color
		fontStarena = Typeface.createFromAsset(getAssets(),"starenafont.ttf");
		tapStart.setTypeface(fontStarena);
		//lang dependent
		if(language==0)
		{
			tapStart.setText("Tap Here To Start");
			
			playAgainImg1[0] = context.getResources().getDrawable(R.drawable.lookplayagain1);
			playAgainImg2[0] = context.getResources().getDrawable(R.drawable.lookplayagain2);
			mainMenuImg1[0] = context.getResources().getDrawable(R.drawable.lookmainmenu1);
			mainMenuImg2[0] = context.getResources().getDrawable(R.drawable.lookmainmenu2);
			
			playAgainImg1[1] = context.getResources().getDrawable(R.drawable.lookplayagain1inv);
			playAgainImg2[1] = context.getResources().getDrawable(R.drawable.lookplayagain2inv);
			mainMenuImg1[1] = context.getResources().getDrawable(R.drawable.lookmainmenu1inv);
			mainMenuImg2[1] = context.getResources().getDrawable(R.drawable.lookmainmenu2inv);
			
		}
		else
		{
		tapStart.setText("Dodirnite Ovde Za Start");
		
		playAgainImg1[0] = context.getResources().getDrawable(R.drawable.srblookplayagain1);
		playAgainImg2[0] = context.getResources().getDrawable(R.drawable.srblookplayagain2);
		mainMenuImg1[0] = context.getResources().getDrawable(R.drawable.srblookmainmenu1);
		mainMenuImg2[0] = context.getResources().getDrawable(R.drawable.srblookmainmenu2);
		
		playAgainImg1[1] = context.getResources().getDrawable(R.drawable.srblookplayagain1inv);
		playAgainImg2[1] = context.getResources().getDrawable(R.drawable.srblookplayagain2inv);
		mainMenuImg1[1] = context.getResources().getDrawable(R.drawable.srblookmainmenu1inv);
		mainMenuImg2[1] = context.getResources().getDrawable(R.drawable.srblookmainmenu2inv);
		
		}
		//resize
		playAgainImg1[0] = ResizeDrawable(playAgainImg1[0],(int)(2.5*ButtonWidth),ButtonHeight);
		playAgainImg2[0] = ResizeDrawable(playAgainImg2[0],(int)(2.5*ButtonWidth),ButtonHeight);
		mainMenuImg1[0] = ResizeDrawable(mainMenuImg1[0],(int)(2.5*ButtonWidth),ButtonHeight);
		mainMenuImg2[0] = ResizeDrawable(mainMenuImg2[0],(int)(2.5*ButtonWidth),ButtonHeight);
		playAgainImg1[1] = ResizeDrawable(playAgainImg1[1],(int)(2.5*ButtonWidth),ButtonHeight);
		playAgainImg2[1] = ResizeDrawable(playAgainImg2[1],(int)(2.5*ButtonWidth),ButtonHeight);
		mainMenuImg1[1] = ResizeDrawable(mainMenuImg1[1],(int)(2.5*ButtonWidth),ButtonHeight);
		mainMenuImg2[1] = ResizeDrawable(mainMenuImg2[1],(int)(2.5*ButtonWidth),ButtonHeight);
		//init
		playAgain[0].setImageDrawable(playAgainImg1[0]);
		mainMenu[0].setImageDrawable(mainMenuImg1[0]);
		playAgain[0].setOnTouchListener(this);
		mainMenu[0].setOnTouchListener(this); 
		playAgain[1].setImageDrawable(playAgainImg1[1]);
		mainMenu[1].setImageDrawable(mainMenuImg1[1]);
		playAgain[1].setOnTouchListener(this);
		mainMenu[1].setOnTouchListener(this); 
		
		//winner look
		
		//(in)visible
		signR.setVisibility(View.INVISIBLE);
		signL.setVisibility(View.INVISIBLE);
		tapStart.setVisibility(View.VISIBLE);
		playAgain[0].setVisibility(View.INVISIBLE);
		mainMenu[0].setVisibility(View.INVISIBLE);
		playAgain[1].setVisibility(View.INVISIBLE);
		mainMenu[1].setVisibility(View.INVISIBLE);
		for(int i=0;i<=1;i++)
		{
	      Pause[0].setVisibility(View.VISIBLE);
		  Leap[0].setVisibility(View.VISIBLE);
		  Shield[0].setVisibility(View.VISIBLE);
		  Invert[0].setVisibility(View.VISIBLE);
		  ChangeRotation[0].setVisibility(View.VISIBLE);
		}
		
		//arena
		ArenaTL=new Point((int)(ScrWidth/8.0),(int)(ScrHeight/8.0));
	    ArenaTR=new Point((int)(7.0*ScrWidth/8.0),(int)(ScrHeight/8.0));
	    ArenaBL=new Point((int)(ScrWidth/8.0),(int)(7.0*ScrHeight/8.0));
	    ArenaBR=new Point((int)(7.0*ScrWidth/8.0),(int)(7.0*ScrHeight/8.0));
		//we need a black hole
		BlackHole=new Point();
		BlackHole.x=(int)(ScrWidth/2.0);
		BlackHole.y=(int)(ScrHeight/2.0);
		
	    //player textures, a lot of them o.o
	    //ALL PURPLE TEXTURES ARE ROTATED 180 DEGREES
	    PlayerTexturesB=new Drawable[15];
		PlayerTexturesB[0] = context.getResources().getDrawable(R.drawable.bluedead);
		PlayerTexturesB[1] = context.getResources().getDrawable(R.drawable.blue10);	
		PlayerTexturesB[2] = context.getResources().getDrawable(R.drawable.blue20);
		PlayerTexturesB[3] = context.getResources().getDrawable(R.drawable.blue30);
		PlayerTexturesB[4] = context.getResources().getDrawable(R.drawable.blue40);
		PlayerTexturesB[5] = context.getResources().getDrawable(R.drawable.blue50);
		PlayerTexturesB[6] = context.getResources().getDrawable(R.drawable.blue60);
		PlayerTexturesB[7] = context.getResources().getDrawable(R.drawable.blue70);
		PlayerTexturesB[8] = context.getResources().getDrawable(R.drawable.blue80);
		PlayerTexturesB[9] = context.getResources().getDrawable(R.drawable.blue90);
		PlayerTexturesB[10] = context.getResources().getDrawable(R.drawable.blue100);
		PlayerTexturesP=new Drawable[15];
		PlayerTexturesP[0] = context.getResources().getDrawable(R.drawable.purpdead);
		PlayerTexturesP[1] = context.getResources().getDrawable(R.drawable.purp10);	
		PlayerTexturesP[2] = context.getResources().getDrawable(R.drawable.purp20);
		PlayerTexturesP[3] = context.getResources().getDrawable(R.drawable.purp30);
		PlayerTexturesP[4] = context.getResources().getDrawable(R.drawable.purp40);
		PlayerTexturesP[5] = context.getResources().getDrawable(R.drawable.purp50);
		PlayerTexturesP[6] = context.getResources().getDrawable(R.drawable.purp60);
		PlayerTexturesP[7] = context.getResources().getDrawable(R.drawable.purp70);
		PlayerTexturesP[8] = context.getResources().getDrawable(R.drawable.purp80);
		PlayerTexturesP[9] = context.getResources().getDrawable(R.drawable.purp90);
		PlayerTexturesP[10] = context.getResources().getDrawable(R.drawable.purp100);
		ShieldedTextureB= context.getResources().getDrawable(R.drawable.shieldedplayerblue);
		ShieldedTextureP= context.getResources().getDrawable(R.drawable.shieldedplayerpurp);    
	    
		//creating players
		bluePlayer=new Player();
		purpPlayer=new Player();
		bluePlayer.Initialize(ShieldedTextureB,context,ScrHeight,ScrWidth,bg,ArenaTL,ArenaTR,ArenaBL,ArenaBR,PlayerTexturesB);
		purpPlayer.Initialize(ShieldedTextureP,context,ScrHeight,ScrWidth,bg,ArenaTL,ArenaTR,ArenaBL,ArenaBR,PlayerTexturesP);
		purpPlayer.pos.y=ArenaTL.y; //purp up
		purpPlayer.incx=purpPlayer.inc; //purp different rot
		purpPlayer.side='U';
		crashOnCd=false; //didn't crash
		s=bluePlayer.size;
		
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
		
		//asteroids
		asterCnt=0;
		asters = new ArrayList<Asteroid>(100);
		lastAster=0;
		lastCrashTime=0;
		
		//BALANCE the game here
		//asters: type 0-comet, 1-slow aster, 2-medium aster, 3-fast aster
		//crashes
		crashDelta=500;
		crashDmg=5;
		//asters
		asterVels=new double[] {0, (int)(bluePlayer.inc/3.0), (int)(2.0*bluePlayer.inc/3.0), bluePlayer.inc};//2 4 6
		asterSizes=new int[] {0,(int)((3.0/2.0)*s), (int)s, (int)((2.0/3.0)*s)};
		asterDmgs=new int[] {0, 35, 25, 15};
		minNewAsterTime=3000;
		maxNewAsterTime=10000;
		newAsterTime=3000;
		//hps
		hpVel=(int)(bluePlayer.inc/12.0*5.0); //2.5
		hpSize=(int)((3.0/4.0)*s);
		hpValue=20;
		hpLifetime=3500;
		minNewHpTime=8000;
		maxNewHpTime=13000;
		newHpTime=5000;
		//abilities
		LeapCdTime=3000;
		ShieldCdTime=15000;
		InvertCdTime=8000;
		LeapRange=(ArenaTR.x-ArenaTL.x)/2; 
		ShieldDuration=3000;
		
		//hpack 1
		hPack=new HealthPack();
		lastHpDied=0;
		//dead begin
		hPack.Init(hpVel,hpSize,hpValue,hpLifetime,BlackHole,context,bg);
		
		//init
		gameOver=false;
		pause=true;

		//pointer pos
		clickPos=new Point();
		
		//draw for the first time
		DrawAll();
	}
	
	public void TakeCareOfButtons()
	{
		        //BUTTONS
		
				//drawables
				PauseOk[0] = context.getResources().getDrawable(R.drawable.pauseblue);	
				PauseOk[0] = ResizeDrawable(PauseOk[0],ButtonWidth,ButtonHeight);
				PauseOk[1] = context.getResources().getDrawable(R.drawable.pausepurp);	
				PauseOk[1] = ResizeDrawable(PauseOk[1],ButtonWidth,ButtonHeight);
				
				PauseUsed[0] = context.getResources().getDrawable(R.drawable.pauseblueused);	
				PauseUsed[0] = ResizeDrawable(PauseUsed[0],ButtonWidth,ButtonHeight);
				PauseUsed[1] = context.getResources().getDrawable(R.drawable.pausepurpused);	
				PauseUsed[1] = ResizeDrawable(PauseUsed[1],ButtonWidth,ButtonHeight);

				Play[0] = context.getResources().getDrawable(R.drawable.playblue);	
				Play[0] = ResizeDrawable(Play[0],ButtonWidth,ButtonHeight);
				Play[1] = context.getResources().getDrawable(R.drawable.playpurp);	
				Play[1] = ResizeDrawable(Play[1],ButtonWidth,ButtonHeight);

				LeapOk[0] = context.getResources().getDrawable(R.drawable.leapblue);	
				LeapOk[0] = ResizeDrawable(LeapOk[0],ButtonWidth,ButtonHeight);
				LeapCd[0] = context.getResources().getDrawable(R.drawable.leapbluecd);	
				LeapCd[0] = ResizeDrawable(LeapCd[0],ButtonWidth,ButtonHeight);
	
				ShieldOk[0] = context.getResources().getDrawable(R.drawable.shieldblue);	
				ShieldOk[0] = ResizeDrawable(ShieldOk[0],ButtonWidth,ButtonHeight);
				ShieldCd[0] = context.getResources().getDrawable(R.drawable.shieldbluecd);	
				ShieldCd[0] =  ResizeDrawable(ShieldCd[0],ButtonWidth,ButtonHeight);
				
				InvertOk[0] = context.getResources().getDrawable(R.drawable.invertblue);	
				InvertOk[0] =ResizeDrawable(InvertOk[0],ButtonWidth,ButtonHeight);
				InvertCd[0] = context.getResources().getDrawable(R.drawable.invertbluecd);	
				InvertCd[0] = ResizeDrawable(InvertCd[0],ButtonWidth,ButtonHeight);
				
				LeapOk[1] = context.getResources().getDrawable(R.drawable.leappurp);	
				LeapOk[1] = ResizeDrawable(LeapOk[1],ButtonWidth,ButtonHeight);
				LeapCd[1] = context.getResources().getDrawable(R.drawable.leappurpcd);	
				LeapCd[1] = ResizeDrawable(LeapCd[1],ButtonWidth,ButtonHeight);
				
				ShieldOk[1] = context.getResources().getDrawable(R.drawable.shieldpurp);	
				ShieldOk[1] =ResizeDrawable(ShieldOk[1],ButtonWidth,ButtonHeight);
				ShieldCd[1] = context.getResources().getDrawable(R.drawable.shieldpurpcd);	
				ShieldCd[1] = ResizeDrawable(ShieldCd[1],ButtonWidth,ButtonHeight);
				
				InvertOk[1] = context.getResources().getDrawable(R.drawable.invertpurp);	
				InvertOk[1] = ResizeDrawable(InvertOk[1],ButtonWidth,ButtonHeight);
				InvertCd[1] = context.getResources().getDrawable(R.drawable.invertpurpcd);	
				InvertCd[1] = ResizeDrawable(InvertCd[1],ButtonWidth,ButtonHeight);

				ChangeRotationOk[0] = context.getResources().getDrawable(R.drawable.changerotationblue);	
				ChangeRotationOk[0] = ResizeDrawable(ChangeRotationOk[0],ButtonWidth,ButtonHeight);
				ChangeRotationPressed[0] = context.getResources().getDrawable(R.drawable.changerotationpressedblue);	
				ChangeRotationPressed[0] = ResizeDrawable(ChangeRotationPressed[0],ButtonWidth,ButtonHeight);

				ChangeRotationOk[1] = context.getResources().getDrawable(R.drawable.changerotationpurp);	
				ChangeRotationOk[1] = ResizeDrawable(ChangeRotationOk[1],ButtonWidth,ButtonHeight);
				ChangeRotationPressed[1] = context.getResources().getDrawable(R.drawable.changerotationpressedpurp);	
				ChangeRotationPressed[1] = ResizeDrawable(ChangeRotationPressed[1],ButtonWidth,ButtonHeight);
				
				//init every button 
				//(connect with listener, connect with layout)
				
				Pause[0] = (ImageButton) findViewById(R.id.ImageButton1);
				Pause[0].setOnTouchListener(this);
				
				Leap[0] = (ImageButton) findViewById(R.id.imageButton2);
				Leap[0].setOnTouchListener(this);
				
				Shield[0] = (ImageButton) findViewById(R.id.imageButton3);
				Shield[0].setOnTouchListener(this);
				
				Invert[0] = (ImageButton) findViewById(R.id.imageButton4);
				Invert[0].setOnTouchListener(this);
				
				Pause[1] = (ImageButton) findViewById(R.id.ImageButton11);
				Pause[1].setOnTouchListener(this);
				
				Leap[1] = (ImageButton) findViewById(R.id.ImageButton12);
				Leap[1].setOnTouchListener(this);
				
				Shield[1] = (ImageButton) findViewById(R.id.ImageButton13);
				Shield[1].setOnTouchListener(this);
				
				Invert[1] = (ImageButton) findViewById(R.id.ImageButton14);
				Invert[1].setOnTouchListener(this);
				
				ChangeRotation[0] = (ImageButton) findViewById(R.id.ImageButton5);
				ChangeRotation[0].setOnTouchListener(this);
				
				ChangeRotation[1] = (ImageButton) findViewById(R.id.ImageButton15);
				ChangeRotation[1].setOnTouchListener(this);
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
	
	Drawable ResizeDrawable(Drawable image,int width,int height) 
	{
		temp = ((BitmapDrawable)image).getBitmap();
		resbit = Bitmap.createScaledBitmap(temp, width, height, true);
	    resized = new BitmapDrawable(getResources(), resbit ); 
		return resized;
	}
	
	void SpawnAster(int type)
	{
		double angPercentage=Math.random();//0-1 angle
		Asteroid temp=new Asteroid();
		temp.Initialize(angPercentage,type, asterVels, asterSizes, asterDmgs,
				BlackHole, context, bg);
		asters.add(temp);
		asterCnt++;
	}
	
	void SpawnHealthPack()
	{
		double angPercentage=Math.random();//0 to 1
		hPack.Respawn(angPercentage,BlackHole);//respawn
	}
	
	void DrawAll()
	{
		for(int i=0;i<=asterCnt-1;i++)
			 asters.get(i).Draw();
		hPack.Draw();
		purpPlayer.Draw();
		bluePlayer.Draw();
	}
	
		 @Override
    public boolean onTouchEvent(MotionEvent event) 
	{
		  	    //unpause <=> begin game
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
		
    public void handleCornerCollisions(Player p1,Player p2)
    {
    	p1.cw=false;
    	p2.cw=true;
    	if(p1.side=='L') //p2 is U
    	{
    		p1.incy=p1.inc;
    		p2.incx=p2.inc;
    		return;
    	}
    	if(p1.side=='U') //p2 is R
    	{
    		p1.incx=-p1.inc;
    		p2.incy=p2.inc;
    		return;
    	}
    	if(p1.side=='R') //p2 is D
    	{
    		p1.incy=-p1.inc;
    		p2.incx=-p2.inc;
    		return;
    	}
    	if(p1.side=='D') //p2 is L
    	{
    		p1.incx=p1.inc;
    		p2.incy=-p2.inc;
    		return;
    	}
    }
    
    public int sideToNum(char side)
    {
    	if(side=='L') return 0;
    	if(side=='U') return 1;
    	if(side=='R') return 2;
    	if(side=='D') return 3;
    	return -1;
    }
		 
	public void TickLoop() 
	{
		this.runOnUiThread(MovingTimerTick);
	}
	
	public void initiateEnd()
	{
		signR.setVisibility(View.VISIBLE); 
		signL.setVisibility(View.VISIBLE);
		//update textures
		if(bluePlayer.hp==0)
			bluePlayer.currImage.setImageDrawable(bluePlayer.playerTextures[0]);
		else
			bluePlayer.currImage.setImageDrawable(bluePlayer.playerTextures[(bluePlayer.hp-1)/10+1]);
		if(purpPlayer.hp==0)
			purpPlayer.currImage.setImageDrawable(purpPlayer.playerTextures[0]);
		else
			purpPlayer.currImage.setImageDrawable(purpPlayer.playerTextures[(purpPlayer.hp-1)/10+1]);
		
		//draw
		if(bluePlayer.hp==0 && purpPlayer.hp==0)
		{
		  if(language==0)
		  {
			 signRDr=context.getResources().getDrawable(R.drawable.drawd);
			 signLDr=context.getResources().getDrawable(R.drawable.drawl);	
		  }
		  else
		  {
			  signRDr=context.getResources().getDrawable(R.drawable.srbdrawd);
			  signLDr=context.getResources().getDrawable(R.drawable.srbdrawl);
		  }
		}
		else if(bluePlayer.hp==0)
		{
		   if(language==0)
		   {
			   signRDr=context.getResources().getDrawable(R.drawable.purpwinsd);
			   signLDr=context.getResources().getDrawable(R.drawable.purpwinsl);	
		   }
		   else
		   {
			   signRDr=context.getResources().getDrawable(R.drawable.srbpurpwinsd);
			   signLDr=context.getResources().getDrawable(R.drawable.srbpurpwinsl);
	       }
		}
		else if(purpPlayer.hp==0)
		{
		   if(language==0)
		   {
			   signRDr=context.getResources().getDrawable(R.drawable.bluewinsd);
			   signLDr=context.getResources().getDrawable(R.drawable.bluewinsl);	
		   }
		   else
		   {
			   signRDr=context.getResources().getDrawable(R.drawable.srbbluewinsd);
			   signLDr=context.getResources().getDrawable(R.drawable.srbbluewinsl);
	       }
		}
		//resize and put
		iRatio=signRDr.getIntrinsicHeight()/signRDr.getIntrinsicWidth(); 
		signRDr=ResizeDrawable(signRDr,(int)(ScrWidth/16.0),(int)(ScrWidth/16.0*iRatio));
		signLDr=ResizeDrawable(signLDr,(int)(ScrWidth/16.0),(int)(ScrWidth/16.0*iRatio));
		signR.setImageDrawable(signRDr);
		signL.setImageDrawable(signLDr);
		
		//hide all	
		for(int i=0;i<=1;i++)
		{
		  Pause[i].setVisibility(View.INVISIBLE);
		  Leap[i].setVisibility(View.INVISIBLE);
		  Shield[i].setVisibility(View.INVISIBLE);
		  Invert[i].setVisibility(View.INVISIBLE);
		  ChangeRotation[i].setVisibility(View.INVISIBLE);
		}
		
		//dedraw
		bg.removeView(hPack.currImage);
	    for(int i=0;i<=asterCnt-1;i++)
	    	bg.removeView(asters.get(i).currImage);
	    bluePlayer.Draw();
	    purpPlayer.Draw();
	    
		//over!
		pause=true;
		gameOver=true;
		if(language==0)
		 bg.setBackgroundResource(R.drawable.backovermulti);
		else
		 bg.setBackgroundResource(R.drawable.srbbackovermulti);
	}
	
	private Runnable MovingTimerTick = new Runnable() 
	{
		//game loop
		public void run() 
		{
			//show delayed buttons
			if(gameOver && showButtonsDelay>0)
			{
				showButtonsDelay-=timerPeriod;
				if(showButtonsDelay<=0)
				{
				  playAgain[0].setVisibility(View.VISIBLE);
				  mainMenu[0].setVisibility(View.VISIBLE);
				  playAgain[1].setVisibility(View.VISIBLE);
				  mainMenu[1].setVisibility(View.VISIBLE);
				}	
			}
			
			//if pause do nothing
			if(pause) return;
			
			//update time
			timeElapsed+=timerPeriod;//period!
			
			//crash cooldown
			if(crashOnCd && timeElapsed-lastCrashTime > crashDelta)
				crashOnCd=false;
			
			//player collisions
			if(!crashOnCd && PointDist(bluePlayer.pos,purpPlayer.pos) 
					< (bluePlayer.size) )
			{
				  crashOnCd=true;
			      lastCrashTime=timeElapsed;
			      
				  //execute crash
				  if(bluePlayer.shielded)
				  {
					bluePlayer.shielded=false;
					bg.removeView(bluePlayer.shield);
				  }
				  else
				  {
					bluePlayer.hp-=crashDmg;
					if(bluePlayer.hp<0) bluePlayer.hp=0;
				  }
				  if(purpPlayer.shielded)
				  {
					  purpPlayer.shielded=false;
					bg.removeView(purpPlayer.shield);
				  }
				  else
				  {
					  purpPlayer.hp-=crashDmg;
					if(purpPlayer.hp<0) purpPlayer.hp=0;
				  }
				  
				  if(bluePlayer.side!=purpPlayer.side)
				  {
					  //first->second cw
					  if((sideToNum(purpPlayer.side)-sideToNum(bluePlayer.side)+4)%4==1)
					     handleCornerCollisions(bluePlayer,purpPlayer);
					  else
						 handleCornerCollisions(purpPlayer,bluePlayer);
				  }
				  else
				  {
					  //same side
					  //up up, down down
					  if(bluePlayer.side=='L')
					  {
						  if(bluePlayer.pos.y<purpPlayer.pos.y)
						  {
							  bluePlayer.incy=-bluePlayer.inc;
							  bluePlayer.cw=true;
							  
							  purpPlayer.incy=purpPlayer.inc;
							  purpPlayer.cw=false;
						  }
						  else
						  {
							  bluePlayer.incy=bluePlayer.inc;
							  bluePlayer.cw=false;
							  
							  purpPlayer.incy=-purpPlayer.inc;
							  purpPlayer.cw=true;
						  }
					  }
					  else if(bluePlayer.side=='U')
					  {
						  if(bluePlayer.pos.x<purpPlayer.pos.x)
						  {
							  bluePlayer.incx=-bluePlayer.inc;
							  bluePlayer.cw=false;
							  
							  purpPlayer.incx=purpPlayer.inc;
							  purpPlayer.cw=true;
						  }
						  else
						  {
							  bluePlayer.incx=bluePlayer.inc;
							  bluePlayer.cw=true;
							  
							  purpPlayer.incx=-purpPlayer.inc;
							  purpPlayer.cw=false;
						  }
					  }
					  else if(bluePlayer.side=='R')
					  {
						  if(bluePlayer.pos.y<purpPlayer.pos.y)
						  {
							  bluePlayer.incy=-bluePlayer.inc;
							  bluePlayer.cw=false;
							  
							  purpPlayer.incy=purpPlayer.inc;
							  purpPlayer.cw=true;
						  }
						  else
						  {
							  bluePlayer.incy=bluePlayer.inc;
							  bluePlayer.cw=true;
							  
							  purpPlayer.incy=-purpPlayer.inc;
							  purpPlayer.cw=false;
						  }
					  }
					  else if(bluePlayer.side=='D')
					  {
						  if(bluePlayer.pos.x<purpPlayer.pos.x)
						  {
							  bluePlayer.incx=-bluePlayer.inc;
							  bluePlayer.cw=true;
							  
							  purpPlayer.incx=purpPlayer.inc;
							  purpPlayer.cw=false;
						  }
						  else
						  {
							  bluePlayer.incx=bluePlayer.inc;
							  bluePlayer.cw=false;
							  
							  purpPlayer.incx=-purpPlayer.inc;
							  purpPlayer.cw=true;
						  }
					  }
				  }
				}
			
			//if anyone is dead, end the game
			if(bluePlayer.hp==0 || purpPlayer.hp==0)
			{
				initiateEnd();
				return;
			}
			
			//New Aster?
			if(timeElapsed-lastAster>newAsterTime)
			{
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
					//first purp
					asteroidShouldDie=false;
					if(asters.get(i).dying==0 &&
							PointDist(purpPlayer.pos,asters.get(i).pos) 
							< (purpPlayer.size/2.0+asters.get(i).size/2.0) )
						{
						  asteroidShouldDie=true;
						  if(purpPlayer.shielded)
						  {
							  purpPlayer.shielded=false;
							  bg.removeView(purpPlayer.shield);
						  }
						  else
						  {
							  purpPlayer.hp-=asters.get(i).damage;
						     if(purpPlayer.hp<0) purpPlayer.hp=0;
						  }
						}
					//normal for blue
					asters.get(i).Update(asteroidShouldDie,bluePlayer,timeElapsed,ArenaTL,ArenaBR);
					
					if(bluePlayer.hp==0 || purpPlayer.hp==0)
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
			
			//update for purple
			hPackShouldDie=false;
			if(hPack.dead==0 &&
					PointDist(purpPlayer.pos,hPack.pos) 
					  < (purpPlayer.size/2.0+hPack.size/2.0) )
		    {
				hPackShouldDie=true;
				  purpPlayer.hp+=hpValue;
				  if(purpPlayer.hp>100) purpPlayer.hp=100;
		    }
			//update for blue
			hPack.Update(hPackShouldDie,bluePlayer, timeElapsed, ArenaTL, ArenaBR);			
			
			//he just died
			if(lastHpDied==-1 && hPack.dead==1)
				lastHpDied=timeElapsed;
			
			//Update Players
			bluePlayer.Update(ArenaTL,ArenaTR,ArenaBL,ArenaBR);
			purpPlayer.Update(ArenaTL,ArenaTR,ArenaBL,ArenaBR);
			
			//check cd
			if(LeapOnCd[0] && timeElapsed-LeapUsedTime[0] > LeapCdTime)
			{
				LeapOnCd[0]=false;
				Leap[0].setImageDrawable(LeapOk[0]);
			}
			if(ShieldOnCd[0] && timeElapsed-ShieldUsedTime[0] > ShieldCdTime)
			{
				ShieldOnCd[0]=false;
				Shield[0].setImageDrawable(ShieldOk[0]);
			}
			if(InvertOnCd[0] && timeElapsed-InvertUsedTime[0] > InvertCdTime)
			{
				InvertOnCd[0]=false;
				Invert[0].setImageDrawable(InvertOk[0]);
			}
			if(LeapOnCd[1] && timeElapsed-LeapUsedTime[1] > LeapCdTime)
			{
				LeapOnCd[1]=false;
				Leap[1].setImageDrawable(LeapOk[1]);
			}
			if(ShieldOnCd[1] && timeElapsed-ShieldUsedTime[1] > ShieldCdTime)
			{
				ShieldOnCd[1]=false;
				Shield[1].setImageDrawable(ShieldOk[1]);
			}
			if(InvertOnCd[1] && timeElapsed-InvertUsedTime[1] > InvertCdTime)
			{
				InvertOnCd[1]=false;
				Invert[1].setImageDrawable(InvertOk[1]);
			}
			
			//shield broke
			if(bluePlayer.shielded && timeElapsed-ShieldUsedTime[0] > ShieldDuration)
			{
				bluePlayer.shielded=false;
				bg.removeView(bluePlayer.shield);
			}
			if(purpPlayer.shielded && timeElapsed-ShieldUsedTime[1] > ShieldDuration)
			{
				purpPlayer.shielded=false;
				bg.removeView(purpPlayer.shield);
			}
			
			//DRAW
			DrawAll();
		}
	};
		
	void CheckButtons(View buttonPressed,MotionEvent action, Player player, int index)
	{
		//check basics
		//player and his index
		//sp is the same, basically
		
		if(buttonPressed.getId()==ChangeRotation[index].getId())
		{
			if(action.getAction() == MotionEvent.ACTION_DOWN)
	         {
	        		   ChangeRotation[index].setImageDrawable(ChangeRotationPressed[index]);
	        		   player.incx*=-1;
	        		   player.incy*=-1;
					   if( player.cw)
						   player.cw=false;
					   else
						   player.cw=true;
			 }
	         if(action.getAction() == MotionEvent.ACTION_UP)
			          ChangeRotation[index].setImageDrawable(ChangeRotationOk[index]);        	
	         return;
		}
		if(!LeapOnCd[index] && buttonPressed.getId()==Leap[index].getId() && action.getAction() == MotionEvent.ACTION_DOWN)
		{
						//on cd
						Leap[index].setImageDrawable(LeapCd[index]);
						LeapUsedTime[index]=timeElapsed;
						LeapOnCd[index]=true;
						
						//KRLJ
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
						return;
					
			    }
				if(!ShieldOnCd[index] && buttonPressed.getId()==Shield[index].getId() && action.getAction() == MotionEvent.ACTION_DOWN)
				{
					//on cd
					Shield[index].setImageDrawable(ShieldCd[index]);
					ShieldUsedTime[index]=timeElapsed;
					ShieldOnCd[index]=true;
					player.shielded=true;
					return;
				}
				if(!InvertOnCd[index] && buttonPressed.getId()==Invert[index].getId() && action.getAction() == MotionEvent.ACTION_DOWN)
				{
					//on cd
					Invert[index].setImageDrawable(InvertCd[index]);
					InvertUsedTime[index]=timeElapsed;
					InvertOnCd[index]=true;
					if(player.cw)
						player.cw=false;
					else
						player.cw=true;
					if(player.side=='D')
					{
						player.side='U';
						player.pos.y=ArenaTL.y;
						return;
					}
					if(player.side=='L')
					{
						player.side='R';
						player.pos.x=ArenaTR.x;
						return;
					}
					if(player.side=='U')
					{
						player.side='D';
						player.pos.y=ArenaBL.y;
						return;
					}
					if(player.side=='R')
					{
						player.side='L';
						player.pos.x=ArenaTL.x;
						return;
					}
				}
	}

	public double PointDist(Point a,Point b)
	{
		double dx=Math.abs(a.x-b.x);
		double dy=Math.abs(a.y-b.y);
		return Math.sqrt(dx*dx+dy*dy);
	}

	@Override
	public boolean onTouch(View buttonPressed, MotionEvent action) 
	{
		//update buttons
		
		//endgame
		for(int i=0;i<=1;i++)
		{
		 if(buttonPressed.getId()==playAgain[i].getId())
		 {
		        	if(action.getAction() == MotionEvent.ACTION_DOWN)
		        	{

		        		playAgain[i].setImageDrawable(playAgainImg2[i]);
		        		Intent Intent1 = new Intent(context,MultiPlayer.class);
		    		    startActivity(Intent1);
		    		    finish();
			        }
		        	if(action.getAction() == MotionEvent.ACTION_UP)
				       {
		        		playAgain[i].setImageDrawable(playAgainImg1[i]);
				       }
				    return true;
		 }
		 if(buttonPressed.getId()==mainMenu[i].getId())
		 {
		        	if(action.getAction() == MotionEvent.ACTION_DOWN)
		        	{
		        		mainMenu[i].setImageDrawable(mainMenuImg2[i]);
		        		finish();
			        }
		        	if(action.getAction() == MotionEvent.ACTION_UP)
				       {
		        		mainMenu[i].setImageDrawable(mainMenuImg1[i]);
				       }
				    return true;
		 }
		}
		
		//not endgame?
		if(gameOver)
			return true;
		
		//pause button
		for(int i=0;i<=1;i++)
		{
		   if(buttonPressed.getId()==Pause[i].getId())
		   {
			  if(action.getAction() == MotionEvent.ACTION_UP)
			  {
				  if(timeElapsed>0 && !pause && !pauseUsed[i])
				  {
						pause=true;
						pauseUsed[i]=true;
						Pause[i].setImageDrawable(Play[i]);
						justPaused=i;
				  }
				  else if(timeElapsed>0 && pause && justPaused==i)
				  {
					  	pause=false;
						Pause[i].setImageDrawable(PauseUsed[i]);
			  }   }
		    }
		}
		
		//if not pause, check normal stuff
		if(pause)
			return true;
		
		CheckButtons(buttonPressed,action,bluePlayer,0);
		CheckButtons(buttonPressed,action,purpPlayer,1);
		
		return true;
	}
	
	@Override
	public void onDestroy() 
	{
	    super.onDestroy();
		//fixing memory leaks
	    temp=null;
		resbit=null;
		resized=null;
	    for(int i=0;i<=1;i++)
	    {
		 PauseOk[i]=null;
		 PauseUsed[i]=null;
		 Play[i]=null;
		 LeapOk[i]=null;
		 LeapCd[i]=null;
		 InvertOk[i]=null;
		 InvertCd[i]=null; 
		 ShieldOk[i]=null; 
		 ShieldCd[i]=null;
		 ChangeRotationOk[i]=null; 
		 ChangeRotationPressed[i]=null;
	     Pause[0].setImageDrawable(null);
		 Invert[i].setImageDrawable(null);
		 Leap[i].setImageDrawable(null);
		 Shield[i].setImageDrawable(null);
		 ChangeRotation[i].setImageDrawable(null);
	    }
		for(int i=0;i<=1;i++)
		{
		    playAgainImg1[i]=null;
			playAgainImg2[i]=null;
			mainMenuImg1[i]=null;
			mainMenuImg2[i]=null;
			mainMenu[i].setImageDrawable(null);
			playAgain[i].setImageDrawable(null);
		}
		signR.setImageDrawable(null);
		signL.setImageDrawable(null);
		signRDr=null;
		signLDr=null;
	}
}
