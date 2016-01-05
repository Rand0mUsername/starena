//Firepig
package firepig.starena;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class HealthPack extends Activity implements Cloneable
{
	//vars
	
	//basic
	RelativeLayout bg;
	
	//core
	double x,y;//real coords
	public Point pos;//coords
	double angle; //angle in radians
	double vel; //velvect
	public double velx;
	public double vely;
	
	//states
	public int dead; //hp state
	boolean moving; //before it hits a wall
	double lifeTime,stopTime;
	
	//graphics
	public ImageView currImage; //his ImageView
    Drawable texture; //texture
    public int size; 
    int value; //how much does it heal
    
    //const
	double fullcircle=2*Math.PI;
	
	
	public void Init( 
			double hpVel,int hpSize,int hpValue,double hpLifetime,
			Point BlackHole,Context context,RelativeLayout Bg)
	{
		//basic
		bg=Bg;
		
		//texture
		texture = context.getResources().getDrawable(R.drawable.healthpack);
        currImage=new ImageView(context); //image
		currImage.setImageDrawable(texture);

		//basic params
		vel=hpVel;
		size=hpSize;
		value=hpValue;
		lifeTime=hpLifetime;
		
		//position
		pos=new Point();
		
		//dead when we begin
		dead=1;
	}
	
	public void Respawn(double angPercentage,Point BlackHole)
	{
		//reset
		dead=0;
		moving=true;
		stopTime=-1;
		
		//start from the black hole
		x=BlackHole.x;
		y=BlackHole.y;
		pos.x=(int) x;
		pos.y=(int) y;
		
		//vels
		angle=angPercentage*fullcircle;//random angle
		velx=vel*Math.cos(angle);//trig!
		vely=vel*Math.sin(angle);
	}
	
	public void CheckExpiration(int timeElapsed)
	{
		//if the time is over destroy it
		if(stopTime!=-1 && timeElapsed-stopTime > lifeTime)
			{
			  dead=1;
			  velx=0;
			  vely=0;
			}
		return;
	}
	
	public void Update(boolean ShouldDie,Player player,double timeElapsed,
			Point ArenaTL, Point ArenaBR)
	{
		//angle is wrong, careful!

		//totally dead/eaten
		if(dead==1)
			return;
		
		//alive
		//check collisions with player, R, circle approx
		if( PointDist(player.pos,this.pos) 
				  < (player.size/2.0+this.size/2.0) )
	    {
			  dead=1;
			  velx=0;
			  vely=0;
			  player.hp+=value;
			  if(player.hp>100) player.hp=100;
			  return;
	    }
		
		//the other player collided, always false in SP
		if(ShouldDie)
		{
			dead=1;
			velx=0;
			vely=0;
			return;
		}
		
		if(!moving) 
			return;
		
		//check collisions with walls
		if(x+velx>ArenaBR.x)
		{ 
			x=ArenaBR.x;
			velx=0;
			vely=0;
			moving=false;
			stopTime=timeElapsed;
		}
		if(x+velx<ArenaTL.x)
		{ 
			x=ArenaTL.x;
			velx=0;
		    vely=0;
		    moving=false;
			stopTime=timeElapsed;
		}
		if(y+vely>ArenaBR.y)
		{ 
			y=ArenaBR.y;
			velx=0;
			vely=0;
			moving=false;
			stopTime=timeElapsed;
		}
		if(y+vely<ArenaTL.y)
		{
			y=ArenaTL.y;
			velx=0;
			vely=0;
			moving=false;
			stopTime=timeElapsed;
		}
		
		//move it
		x+=velx;
		y+=vely;
		
		//move it indeed
		pos.x=(int) x;
		pos.y=(int) y;
	}
	
	public void Draw()
	{
		//delete
		bg.removeView(currImage);
		
		//if dead thats it
		if(dead==1) return;
		
		//if not draw again
		bg.addView(currImage);
		
		//set params
		RelativeLayout.LayoutParams Params1 = 
				new RelativeLayout.LayoutParams(size,size);
		Params1.topMargin=(int) this.pos.y - size/2 ;
		Params1.leftMargin=(int) this.pos.x - size/2 ;
		currImage.setLayoutParams(Params1);
		//center=center
	}

	public double PointDist(Point a,Point b)
	{
		double dx=Math.abs(a.x-b.x);
		double dy=Math.abs(a.y-b.y);
		return Math.sqrt(dx*dx+dy*dy);
	}

}