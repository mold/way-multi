/**
The class for all the "items", the things that the player is supposed to catch.
(actually there will only be one item object created)
*/

import java.awt.*;

public class Item
{
	private int w, h; //The width and height of Item
	private int x, y; //The x and y coordinates Item
	private Image sprite; //The item sprite
	public Item()
	{
		w = 15;
		h = 15;

		x = Way.w/2;
		y = Way.h/2;
	}

	/**
	Get/set methods
	*/

	//Returns x
	public int getX(){return x;}

	//Returns y
	public int getY(){return y;}

	//Returns width
	public int getWidth(){return w;}

	//Returns height
	public int getHeight(){return h;}

	//Sets x
	public void setX(int xx){x = xx;}

	//Sets y
	public void setY(int yy){y = yy;}

	//Sets the sprite
	public void setImage(Image itemSprite)
	{
		sprite = itemSprite;
	}

	/**
	Draws the item
	*/
	public void draw(Graphics g)
	{
		g.drawImage(sprite, x, y, null);
	}

	//Returns a Rectangle the size of the item
	public Rectangle getBounds()
	{
		return new Rectangle(x, y, w, h);
	}

	/**
	Moves the item to a new location
	*/
	public void updatePosition(Box[] boxes)
	{
		boolean repeat = true;

		//Places the item at a random position
		//If it's placed on/behind a box, it's replaced
		while(repeat)
		{
			x = (int)(Math.random()*(Way.w-11));
			y = (int)(Math.random()*(Way.h-31)+20);

			repeat = false;
			for(Box tmp: boxes)
			{
				if(tmp.checkHit(getBounds())) repeat = true;
			}
		}
	}

	/**
	Checks collision
	*/
	public boolean checkHit( Rectangle bo )
	{
		boolean hit = getBounds().intersects( bo );

		return hit;
	}

}
