//Firepig
package firepig.starena;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.Point;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Player extends Activity implements Cloneable{
	
	//vars
	
	//basic
	RelativeLayout bg;
	
	//core
	public Point pos; //double is an overkill here
	public int hp; //health
	public boolean cw; //direction ([counter]clockwise)
	public char side; //D L R U (down left right up)
	
	//graphics
	public ImageView currImage; //texture
	public Drawable[] playerTextures; //all textures (hp dependent)
	public int size; //his size (he's a square)
	
	//moving
	public int inc; //every game tick -> move by inc
	public double incx; //current x increment
	public double incy; //current y increment
	
	//shield
	//imageview behind our imageview, handy!
	Drawable shieldedTexture;
	public ImageView shield;
	public boolean shielded;
	int shieldSize;
	
	//ok vars
	public static boolean ok; //OK
	
	public void Initialize(Drawable shieldedtexture, Context context,double ScrHeight,//inicijalizacija
				double ScrWidth, RelativeLayout Bg,
				Point ArenaTL, Point ArenaTR, 
				Point ArenaBL, Point ArenaBR,Drawable[] PlayerTextures)
	{
		//basic
		bg=Bg;
		
		//BALANCE HERE
		size=(int)(ScrHeight/15.0);
		shieldSize=(int) (size*1.15);
		inc=size/9;
		
		//basic params
		hp=100; 
		cw=true; 
		incx=-inc;
		incy=0;
		shielded=false;
		ok=true;//RAMMUS
		
		//initial position
	    pos=new Point();
		pos.x=ArenaBL.x+(ArenaBR.x-ArenaBL.x)/2; //middle
		pos.y=ArenaBL.y; //down
		side='D'; //down
		
		//graphics
		playerTextures=new Drawable[15];
		for(int i=0;i<=10;i++)
			playerTextures[i]=PlayerTextures[i];
		shieldedTexture = shieldedtexture;
		
		//set initial graphics
		currImage=new ImageView(context); 
		currImage.setImageDrawable(playerTextures[10]);
		shield=new ImageView(context); 
		shield.setImageDrawable(shieldedTexture);
	}
	
	public void Update(Point ArenaTL, Point ArenaTR, Point ArenaBL, Point ArenaBR)//basic function for player movement
	{
		//colision with asters from aster class
		
		if(cw)
		{
			if(pos.x+incx > ArenaTR.x)//hits TR
			{
				incx=0;
				incy=inc;
				side='R';
			}
			else if(pos.y+incy > ArenaBR.y)//hits BR
			{
				incy=0;
				incx=-inc;
				side='D';
			}
			else if(pos.x+incx < ArenaBL.x)//hits BL
			{
				incx=0;
				incy=-inc;
				side='L';
			}
			else if(pos.y+incy < ArenaTL.y)//hits TL
			{
				incx=inc;
				incy=0;
				side='U';
			}
		}
		else//if ccw
		{
			if(pos.x+incx < ArenaTL.x)//hits TL
			{
				incx=0;
				incy=inc;
				side='L';
			}
			else if(pos.y+incy > ArenaBL.y)//hits BL
			{
				incy=0;
				incx=inc;
				side='D';
			}
			else if(pos.x+incx > ArenaBR.x)//hits BR
			{
				incx=0;
				incy=-inc;
				side='R';
			}
			else if(pos.y+incy < ArenaTR.y)//hits TR
			{
				incx=-inc;
				incy=0;
				side='U';
			}
		}
		
		//do 1 step!
		pos.y+=incy;
		pos.x+=incx;
	}
	
	public void Draw()
	{
		//hp texture connection
		if(hp==0)
			currImage.setImageDrawable(playerTextures[0]);
		else
			currImage.setImageDrawable(playerTextures[(hp-1)/10+1]);
		
		//params
		RelativeLayout.LayoutParams imageParams = 
				new RelativeLayout.LayoutParams(size,size);
		imageParams.topMargin=(int) this.pos.y - size/2 ;
		imageParams.leftMargin=(int) this.pos.x - size/2 ;
		
		//shield params
		RelativeLayout.LayoutParams shieldParams = 
				new RelativeLayout.LayoutParams(shieldSize,shieldSize);
		shieldParams.topMargin=(int) this.pos.y - shieldSize/2 ;
		shieldParams.leftMargin=(int) this.pos.x - shieldSize/2 ;
		
		//if shield is on redraw it
		if(shielded)
		{
			bg.removeView(shield);
			bg.addView(shield);
			shield.setLayoutParams(shieldParams);
		}
		
		//redraw the player 
		bg.removeView(currImage);
		bg.addView(currImage);
		currImage.setLayoutParams(imageParams);
		//center=center!
	}

}
