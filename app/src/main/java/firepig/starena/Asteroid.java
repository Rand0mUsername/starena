//Firepig
package firepig.starena;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.Point;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Asteroid extends Activity implements Cloneable
{
	//types 1-slow aster, 2-medium aster, 3-fast aster
	
	//vars
	
	//basic
	RelativeLayout bg;
	Context context;
	
	//core
	int type;
	double x,y;//real coords
	public Point pos;//coords
	double angle; //angle in radians
	double vel; //vel vector
	public double velx;
	public double vely;
	
	//graphics
	public int dying;//do you play anim
	public ImageView currImage; //his ImageView
    Drawable[] textures;//0-alive 1-3-dead
    public int size;
    public int damage; //dmg to player
    
    //anim
    double animDelta;
    double lastAnimUpdate;
    
    //const
	double fullcircle=2*Math.PI;
	
	public void Initialize(double angPercentage,int type, double[] vels,int[] sizes,
			int[] dmgs,Point BlackHole,Context cOntext,RelativeLayout Bg)
	{
		//basic
		bg=Bg;
		context=cOntext;
		
		//he starts at the black hole
		pos=new Point();
		x=BlackHole.x;
		y=BlackHole.y;
		pos.x=(int) x;
		pos.y=(int) y;
		
		//and he's alive
		dying=0;
		
		//basic params
		vel=vels[type];//his velocity
		angle=angPercentage*fullcircle;//random angle
		velx=vel*Math.cos(angle);//trigonometry!
		vely=vel*Math.sin(angle);
		size=sizes[type];
		damage=dmgs[type];
		
		//load textures
        textures=new Drawable[5];
        textures[0] = context.getResources().getDrawable(R.drawable.asteroid);   
        textures[1] = context.getResources().getDrawable(R.drawable.asteroiddead30);
        textures[2] = context.getResources().getDrawable(R.drawable.asteroiddead60);
        textures[3] = context.getResources().getDrawable(R.drawable.asteroiddead90);
        
        //his image
        currImage=new ImageView(context); 
		currImage.setImageDrawable(textures[0]);
		
		//anim
		animDelta=100;
	}
	
	public void Update(boolean ShouldDie,Player player,double timeElapsed,
			Point ArenaTL, Point ArenaBR)
	{
		//angle is wrong after update, careful!
		
		//totally dead, chill
		if(dying==4)
			{
			  return;
			}
		
		//dying, set anim
		if(dying>0 && dying<4)
		{
			if(timeElapsed - lastAnimUpdate > animDelta)
			{
			  currImage.setImageDrawable(textures[dying++]);
			  lastAnimUpdate=timeElapsed;
			}
			return;
		}
		
		//alive:
		//check collisions with player, R, circle approximations
		if(  PointDist(player.pos,this.pos) 
				< (player.size/2.0+this.size/2.0) )
			{
			  dying=1;
			  velx=0;
			  vely=0;
			  //asteroid is dead, player depends on shield
			  if(player.shielded)
			  {
				  player.shielded=false;
				  bg.removeView(player.shield);
			  }
			  else
			  {
			     player.hp-=damage;
			     
			     if(player.hp<0) 
			        player.hp=0;
			  }
			}
		
		//other player collided?
		//in sp this is always false
		if(ShouldDie)
			{
			  dying=1;
			  velx=0;
			  vely=0;
			}
		
		//check collisions with walls
		if(x+size/2.0+velx>ArenaBR.x || x-size/2.0+velx<ArenaTL.x)
			velx*=-1;
		if(y+size/2.0+vely>ArenaBR.y || y-size/2.0+vely<ArenaTL.y)
			vely*=-1;
		
		//move him
		x+=velx;
		y+=vely;
		
		//move him indeed!
		pos.x=(int) x;
		pos.y=(int) y;
	}
	
	public void Draw()
	{
		//redraw his image
		bg.removeView(currImage);
		bg.addView(currImage);
		
		//set params
		RelativeLayout.LayoutParams Params1 = 
				new RelativeLayout.LayoutParams(size,size);
		Params1.topMargin=(int) this.pos.y - size/2 ;
		Params1.leftMargin=(int) this.pos.x - size/2 ;
		currImage.setLayoutParams(Params1);
		// -size/2 <=> his actual (x,y) is in the center
	}
	
	double PointDist(Point a,Point b)
	{
		double dx=Math.abs(a.x-b.x);
		double dy=Math.abs(a.y-b.y);
		return Math.sqrt(dx*dx+dy*dy);
	}

	
}