/**
This class is for all the flying boxes.
*/

import java.awt.*;

public class Box
{
	private int w, h; //The width and height of Box
	private int x, y; //The x and y coordinates Box
	private double xVStart; //The initial, "actual" speed of the box
	private double xV; //The current speed of Box in x-axis
	private double doubleX; //The decimal x coordinate
	private boolean deadly; //States if the box will kill the player or not.
	private boolean stopper; //States if the box is a stopper or not
	private boolean wave; //States if the box is a wave or not

	/**
	Creates a box with:
	* Width: 20-70 px
	* Height: 10-40 px
	* Speed: (-3.0)-3.0 px/refresh. Is never 0.

	One out of 50 boxes:
	* Width: 20-70 + 100-200
	* Height: 10-40 + 50-100
	* Never deadly
	*/
	public Box()
	{
		//One out of 25 boxes is deadly
		if((int)(Math.random()*25) == 1) deadly = true;
		else deadly = false;

		w = (int)(Math.random()*50+20);
		h = (int)(Math.random()*30+10);

		//One out of 50 boxes is extra huge (and never deadly)
		if((int)(Math.random()*50) == 1)
		{
			w += (int)(Math.random()*100+100);
			h += (int)(Math.random()*50+50);
			deadly = false;
		}

		if((int)(Math.random()*25) == 1 && w < 100)
		{
			deadly = false;
			stopper = true;
		}

		y = (int)(Math.random()*(Way.h-21-h)); //Starting y-coordinate

		xV = 0;
		while(xV == 0)
		{
			xV = (Math.random()*6-3); //6-3

			//Some boxes are REALLY FaST!!!!!!!!!!!!!!!! (so called ronnil)
			if((int)(Math.random()*30) == 1)  xV = 30;

			//Gives us xV and xVStart with one decimal
			int xvint = (int)(xV*10);
			xV = xvint/10.0;
			xVStart = xvint/10.0;
		}

		if(xV > 0) x = Way.w;
		else x = 0-w;

		doubleX = x;
	}

	/**
	Creates a box that's actually an EPIC WAVE!!!!
	**/
	public Box(int xx, int yy, int dir)
	{
		h = (int)(Math.random()*200);
		w = 10;
		x = xx;
		if(dir == 0) x -= 17;
		else x += 12;
		doubleX = x;
		y = yy-h/2;

		xV = 8.0;
		if(dir == 1) xV = xV*(-1);
		xVStart = xV;

		wave = true;
	}

	/**
	Get/set methods
	*/
	//Returns xV
	public double getXV(){return xV;}

	//Returns startXV
	public double getXVStart(){return xVStart;}

	//Returns x
	public int getX(){return (int)doubleX;}

	//Returns y
	public int getY(){return y;}

	//Returns width
	public int getWidth(){return w;}

	//Returns height
	public int getHeight(){return h;}

	//Sets x
	public void setX(int xx)
	{
		x = xx;
		doubleX = xx;
	}

	//Sets y
	public void setY(int yy){y = yy;}

	//Sets xV
	public void setXV(double xv){xV = xv;}

	/**
	Draws the box to a specified Graphics
	*/
	public void draw(Graphics g)
	{
		if(deadly) g.setColor(Color.gray);
		else if(stopper) g.setColor(Color.green);
		else if(wave) g.setColor(Color.white);
		else g.setColor(Color.black);
		g.fillRect(x, y, w, h);
		g.setColor(Color.white);
		g.drawRect(x, y, w, h);

		g.setColor(Color.white);
		if(xV == 0 && xVStart > 0) g.drawLine(getX()-1, y, getX()-1, y+h);
		if(xV == 0 && xVStart < 0) g.drawLine(getX()+w+1, y, getX()+w+1, y+h);
	}

	/**
	Updates the position
	*/
	public void updatePosition()
	{
		doubleX -= xV;
		x = (int)doubleX;
	}

	/**
	Returns a Rectangle the size of the box
	*/
	public Rectangle getBounds()
	{
		return new Rectangle(x, y, w, h);
	}

	/**
	Checks collision
	*/
	public boolean checkHit( Rectangle bo )
	{
		boolean hit = getBounds().intersects( bo );

		return hit;
	}

	/**
	Returns true if the box is deadly
	*/
	public boolean isDeadly() {return deadly;}

	/**
	Returns true if the box is a stopper
	*/
	public boolean isStopper() {return stopper;}

}
