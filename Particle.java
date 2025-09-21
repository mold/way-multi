/**
This class is for all the flying particles.
*/

import java.awt.*;

public class Particle
{
	private int w, h; //The width and height of Particle
	private int x, y; //The x and y coordinates Particle
	private double xV; //The current speed of Box in x-axis
	private double doubleX; //The decimal x coordinate

	public Particle()
	{
		w = (int)(Math.random()*5+1);
		h = w;

		x = (int)(Math.random()*Way.w);
		y = (int)(Math.random()*Way.h);

		xV = (Math.random()*2+3);

		doubleX = x;
	}

	/**
	Get/set methods
	*/
	//Returns xV
	public double getXV(){return xV;}

	//Returns x
	public int getX(){return x;}

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
		g.setColor(Color.DARK_GRAY);
		g.fillRect(x, y, w, h);
	}

	/**
	Updates the position
	*/
	public void updatePosition()
	{
		doubleX -= xV;
		x = (int)doubleX;
	}
}
