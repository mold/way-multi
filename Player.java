/**
The player class

Here are all player specific values stored: position, size, speed, various constants and variables, current
status and so on and so on. Things that haven't got to do with the actual running of the game.
See below for commented blocks of variables.

Use setX() instead of "x =" when changing x, so that doubleX is changed as well
*/

import java.awt.*;

public class Player
{

	//player constants
	private int w, h; //The width and height of Player
	private int x, y; //The x and y coordinates Player
	private double doubleX; //The exact (with decimals) x-coordinate
	private double xV; //The current speed of Player in x-axis
	private int yV; //The current speed of Player in y-axis
	private int xSpeed; //The speed by which Player moves, WHEN he moves, in x-axis (the speed "constant").
	private int jumpSpeed; //The speed upwards at a jump'
	private int playerNo; //What player

	//Variables for moving or to determine movement
	private boolean crouching;
	private boolean jumping;
	private boolean falling;
	private boolean keyLeft;
	private boolean keyRight;
	private boolean dirRight; //What direction the player is facing
	private boolean dirLeft;
	private boolean keyDown;
	private boolean keyUp;
	private int jumpFallTimer;

	//Variables for player status
	private boolean alive; //If the player is alive or not
	private boolean deathBySquish = false; //The player was crushed between two boxes
	private boolean deathByTime = false; //The time ran out before the player finished
	private boolean deathByDeadlyBox = false; //The player was killed by a killer box
	private boolean caughtAllItems = false; //The player has caught all items
	private int itemsLeft; //How many items left to catch
	private int wave; //How many waves the player has launched (goes to ten and resets)

	//Variables for collisions
	private boolean collRight = false; //If the player's right side has collided with something (from the RL player's point of view)
	private boolean collLeft = false; //If the player's left side has collided with something (from the RL player's point of view)
	private boolean collHead = false; //If the player's head (top) has collided with something
	private boolean collFeet = false; //If the player's feet (bottom) has collided with something
	private boolean collGround = false; //If the player has collided with the ground

	private int points; //Number of points (calculated at the end of every game)

	private Image playerRight;
	private Image playerLeft;
	private Image playerCrouchRight;
	private Image playerCrouchLeft;
	private Image playerRunRight;
	private Image playerRunLeft;

	public Player(int n)
	{
		w = 15; //default 15
		h = 20;	//default 20
		x = 50;
		y = Way.h-20;
		xV = 0;
		yV = 0;
		xSpeed = 5; //default 5
		doubleX = x;
		jumpSpeed = 10; //default 10
		alive = true;
		itemsLeft = 10;
		points = 0;
		crouching = false;
		if(n == 2) dirRight = true;
		if(n == 1) dirLeft = true;
		playerNo = n;
		wave = 0;
	}

	/**
	Get/set methods
	*/
	//Returns xV
	public double getXV(){return xV;}

	//Returns yV
	public int getYV(){return yV;}

	//Returns xSpeed
	public int getXSpeed(){return xSpeed;}

	//Returns x
	public int getX(){return x;}

	//Returns y
	public int getY(){return y;}

	//Returns width
	public int getWidth(){return w;}

	//Returns height
	public int getHeight(){return h;}

	//Returns jumpSpeed
	public int getJumpSpeed(){return jumpSpeed;}

	//Returns items left
	public int getItemsLeft(){return itemsLeft;}

	//Returns points
	public int getPoints(){return points;}

	//Returns a Rectangle the size of the player
	public Rectangle getBounds()
	{
		return new Rectangle(x, y, w, h);
	}

	//Returns playerNo
	public int getPlayerNo()
	{
		return playerNo;
	}

	//Sets x
	public void setX(int xx)
	{
		x = xx;
		doubleX = xx;
	}

	//Sets items left
	public void setItemsLeft(int i)
	{
		itemsLeft = i;
	}

	//Sets alive
	public void setAlive(boolean b)
	{
		alive = b;
	}

	//Returns true if the player has caught all items
	public boolean hasCaughtAllItems(){return caughtAllItems;}

	//Returns deathBySquish
	public boolean deathBySquish(){return deathBySquish;}

	//Returns deathByTime
	public boolean deathByTime(){return deathByTime;}

	//Returns deathByDeadlyBox
	public boolean deathByDeadlyBox(){return deathByDeadlyBox;}

	//Sets y
	public void setY(int yy){y = yy;}

	//Sets xV
	public void setXV(double xv){xV = xv;}

	//Sets yV
	public void setYV(int yv){yV = yv;}

	//Sets keyDown
	public void keyDown(boolean b){keyDown = b;}

	//Sets keyUp
	public void keyUp(boolean b){keyUp = b;}

	//Sets keyRight
	public void keyRight(boolean b){keyRight = b;}

	//Sets keyLeft
	public void keyLeft(boolean b){keyLeft = b;}

	//Sets dirRight
	public void dirRight()
	{
		dirRight = true;
		dirLeft = false;
	}

	//Sets dirLeft
	public void dirLeft()
	{
		dirLeft = true;
		dirRight = false;
	}

	//Sets the images
	public void setImages(Image pR, Image pL, Image pCR, Image pCL, Image pRR, Image pRL)
	{
		playerRight = pR;
		playerLeft = pL;
		playerCrouchRight = pCR;
		playerCrouchLeft = pCL;
		playerRunRight = pRR;
		playerRunLeft = pRL;

	}

	//Sets points
	public void setPoints(int p)
	{
		points = p;
	}

	//Sets deathBySquish
	public void deathBySquish(boolean b){deathBySquish = b;}

	//Sets deathByTime
	public void deathByTime(boolean b){deathByTime = b;}

	//Sets deathByDeadlyBox
	public void deathByDeadlyBox(boolean b){deathByDeadlyBox = b;}

	/**
	Kills the player
	*/
	public void die()
	{
			alive = false;
			keyLeft = false;
			keyRight = false;
	}

	/**
	Returns true if the player hasn't died
	*/
	public boolean isAlive()
	{
		return alive;
	}

	/**
	Removes one from itemsLeft
	*/
	public void itemCaught()
	{
		itemsLeft--;
		if(itemsLeft == 0)
		{
			caughtAllItems = true;
		}
	}

	/**
	Draws Player on the specified Graphics
	*/
	public void draw(Graphics g)
	{
		if(jumping && !crouching)
		{
			if(dirRight) g.drawImage(playerRight, x, y, null);
			if(dirLeft) g.drawImage(playerLeft, x, y, null);
		}
		else
		{
			if(dirRight && !crouching && !keyRight) g.drawImage(playerRight, x, y, null);
			if(dirRight && !crouching && keyRight) g.drawImage(playerRunRight, x, y, null);
			if(dirRight && crouching) g.drawImage(playerCrouchRight, x, y, null);
			if(dirLeft && !crouching && !keyLeft) g.drawImage(playerLeft, x, y, null);
			if(dirLeft && !crouching && keyLeft) g.drawImage(playerRunLeft, x, y, null);
			if(dirLeft && crouching) g.drawImage(playerCrouchLeft, x, y, null);
		}

		g.setColor(Color.white);

		for(int i = 0; i < playerNo; i++)
		{
			g.fillRect(x+17, y+4*i, 2, 2);
		}

		//g.drawString(playerNo + "", x+17, y+5);

	}

	/**
	Fire an EPIC WAVE
	*/
	public void epicWave(Box[] boxes)
	{
		if(wave > 10) wave = 0;

		if(itemsLeft < 10)
		{
			int dir;
			if(dirLeft) dir = 0;
			else dir = 1;

			boxes[(wave+(playerNo-1)*10)] = new Box(x+(w/2), y+(h/2), dir);

			//setItemsLeft(itemsLeft+1);
		}

		wave++;
	}

	/**
	Move player in x-axis
	*/
	public void moveX()
	{
		//Moves the player according to the player's current xV
		//Not in any other way
		updatePosition();

		//Moves the player XSpeed pixels to the left
		if(keyLeft)	walkLeft();

		//Moves the player XSpeed pixels to the right
		if(keyRight) walkRight();
	}

	/**
	Moves the player in y-axis
	*/
	public void moveY()
	{
		//The player jumps if the up key is being pressed
		//and he's not falling or jumping
		if(keyUp)
		{
			if(!falling && !jumping)
			{
				jumping = true;
				jumpFallTimer = 0;
			}
		}

		//Update player y-speed if he's falling
		if(falling)
		{
			//setYV( (int)(-(-gravity*jumpFallTimer)) );
			yV = (int)(-(-Way.gravity*jumpFallTimer));

			//setY( player.getY()+player.getYV() );
			y += yV;

			jumpFallTimer++;
		}

		//If the player is in a jump
		if(jumping)
		{
			//setYV( (int)(-(player.getJumpSpeed()-gravity*jumpFallTimer)) ); //Calculates the y-speed of the player using the formula vy0-g1 (and inverts this)
			yV = (int)(-(jumpSpeed-Way.gravity*jumpFallTimer));

			//player.setY( player.getY()+player.getYV() );
			y += yV;

			jumpFallTimer++;

			if(yV == 0) //TODO: Fix 21 to something that varies with jump speed (jumpSpeed*10 probably), but fix the rest of the code
			{
				jumping = false;
				falling = true;
				jumpFallTimer = 1;
			}

		}
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
	Moves the player left
	*/
	public void walkLeft()
	{
		doubleX -= xSpeed;
		x = (int)doubleX;
	}

	/**
	Moves the player right
	*/
	public void walkRight()
	{
		doubleX += xSpeed;
		x = (int)doubleX;
	}

	/**
	Makes the player crouch if the sent boolean is true
	Makes the player stand up if it's false
	*/
	public void crouch(boolean c)
	{
		crouching = c;

		if(c)
		{
			h = 10;
			y += 10;
			xSpeed = 3;
		}

		if(!c)
		{
			h = 20;
			y -= 10;
			xSpeed = 5;
		}
	}

	/**
	Makes the player crouch if the down key is being pressed
	Makes the player uncrouch if the down key is not being pressed
	*/
	public void setStance()
	{
		//Makes the player crouch if he isn't already crouching
		if(keyDown && !crouching)
		{
			crouch(true);
		}

		//Makes the player stand. If there's something
		//in the way, he keeps crouching (even if the down key
		//is still being pressed)
		if(!keyDown && crouching)
		{
			crouch(false);
		}
	}

	/**
	Checks for collisions with all boxes (after player has changed stance)
	*/
	public void checkCollisionsCrouch(Box[] boxes)
	{
		for(Box tmp: boxes)
		{
			if(tmp.checkHit(getBounds()))
			{
				//Kills the player if he collided with a deadly box
				if(tmp.isDeadly())
				{
					die();
					deathByDeadlyBox = true;
				}

				else
				{
					//Player keeps crouching if there's a box above him
					crouch(true);
				}
			}
		}
	}

	/**
	Checks for collisions with all boxes (after player and boxes have moved in x-axis)
	*/
	public void checkCollisionsX(Box[] boxes)
	{
		//Check collisions with boxes
		//(The player has only moved on the x-axis so he can only have collided sideways)
		for(Box tmp: boxes)
		{
			//If player has collided with a box he is placed next to the box (in x-axis)
			if(tmp.checkHit(getBounds()))
			{
				//Kills the player if the box he collided with was deadly
				if(tmp.isDeadly())
				{
					die();
					deathByDeadlyBox = true;
				}//if(tmp.isDeadly())

				if(tmp.isStopper())
				{
					tmp.setX(-100);
					Way.slowDownBoxes = true;
				}

				else
				{
					//If player collided with the box on his left side
					if(getX() > tmp.getX())
					{
						//Places the player next to the box
						setX(tmp.getX()+tmp.getWidth());

						collLeft = true;

						//Checks if the player was placed on another box to the right
						//If so, both collRight and collLeft are true.
						for(Box tmp2: boxes)
						{
							if(tmp2.checkHit(getBounds()))
							{
								if(x < tmp.getX())
								{
									if(tmp2.isDeadly())
									{
										die();
										deathByDeadlyBox = true;
									}

									else collRight = true;
								}
							}
						}

					}//If player collided with the box on his left side

					//If player collided with the box on his right side
					if(x < tmp.getX())
					{
						//Places the player next to the box
						setX(tmp.getX()-w);

						collRight = true;

						//Checks if the player was placed on another box to the left
						//If so, both collRight and collLeft are true.
						for(Box tmp2: boxes)
						{
							if(tmp2.checkHit(getBounds()))
							{
								if(getX() > tmp.getX())
								{
									if(tmp2.isDeadly())
									{
										die();
										deathByDeadlyBox = true;
									}

									else collLeft = true;
								}
							}
						}
					}//If player collided with the box on his right side

					xV = 0;

				}//if the box wasn't deadly

			}//If player has collided with a box

		}//Check collisions with boxes (for-loop)

		//Kills player if he's collided with something on both sides
		if(collRight && collLeft)
		{
			die();
			jumping = false;
			falling = false;
			deathBySquish = true;
		}
	}

	/**
	Checks for collisions with all boxes in y-axis
	*/
	public void checkCollisionsY(Box[] boxes)
	{
		collFeet = true;
		collHead = true;
		//Runs until the player isn't colliding with any box anymore
		//This is done to prevet the player from dying from a deadly box that's just a few pixels below the
		//top side of the box he's landning on.
		while(collFeet || collHead)
		{
			collFeet = false;
			collHead = false;
			for(Box tmp: boxes)
			{
				if(tmp.checkHit(getBounds()))
				{
					if(tmp.isDeadly())
					{
						die();
						deathByDeadlyBox = true;
					}

					if(tmp.isStopper())
					{
						tmp.setX(-100);
						Way.slowDownBoxes = true;
					}

					else
					{
						//If player is falling he is put ON TOP OF the box he collided with
						if(falling)
						{
							collFeet = true;
							xV = tmp.getXV();
							y = tmp.getY()-h;
						}

						//If player is falling he is put UNDERNEATH the box he collided with
						if(jumping)
						{
							collHead = true;
							xV = tmp.getXV();
							y = tmp.getY()+tmp.getHeight();
						}
					}
				}
			}

			//If player has landed on a box
			if(collFeet)
			{
				yV = 0;
				jumpFallTimer = 0;
				falling = false;
				jumping = false;
			}

			//If player jumped into a box from underneath
			if(collHead)
			{
				yV = 0;
				jumping = false;
				falling = true;
				jumpFallTimer = 1;
			}
		}

		//check collision with ground
		if(y+h > Way.h)
		{
			y = Way.h-h;
			falling = false;
			jumping = false;
			yV = 0;
			xV = 0;

			jumpFallTimer = 0;

			collGround = true;
		}

		//Makes the player fall if there's no box underneath him
		//(i.e. if he would collide 1 px down)
		if(!falling && !jumping)
		{
			falling = true;
			y += 1;

			for(Box tmp: boxes)
			{
				if(tmp.checkHit(getBounds()))
				{
					falling = false;
					xV = tmp.getXV();
				}
			}

			if(y+h > Way.h)
			{
				falling = false;
				xV = 0;;
			}

			y -= 1;
			jumpFallTimer = 0;
		}
	}

	/**
	Keeps player inside the game area
	Player appears on the other side if he leaves the area
	*/
	public void keepInside(Box[] boxes)
	{
		//Keep inside left
		if(x+w < 0)
		{
			setX(Way.w);

			for(Box tmp: boxes)
			{
				if(tmp.checkHit(getBounds()))
				{
					setX(0-w);
					if(tmp.isDeadly())
					{
						deathByDeadlyBox = true;
						die();
					}
				}
			}
		}

		//keep inside right
		if(x > Way.w)
		{
			setX(0-w);

			for(Box tmp: boxes)
			{
				if(tmp.checkHit(getBounds()))
				{
					setX(Way.w);
					if(tmp.isDeadly())
					{
						deathByDeadlyBox = true;
						die();
					}
				}
			}
		}
	}

	/**
	Checks if player has collided with an item and thus caught it¨
	*/
	public void checkItemCatch(Item item, Box[] boxes)
	{
		//Check if player has caught the Item
		if(item.checkHit(getBounds()))
		{
			itemCaught();

			//Ends the game if player has caught all items
			if(caughtAllItems)
			{
				item.setX(Way.w);
			}
			else item.updatePosition(boxes);
		}
	}

	/**
	Resets the collisions (at the end of every loop)
	*/
	public void resetCollisions()
	{
		collLeft = false;
		collRight = false;
		collHead = false;
		collFeet = false;
		collGround = false;
	}

	/**
	Resets everything at the start of a game
	*/
	public void reset()
	{
		if(playerNo == 2) x = 50;
		if(playerNo == 1) x = Way.w-50-w;
		y = Way.h-20;
		xV = 0;
		yV = 0;
		doubleX = x;

		keyDown = false;
		keyUp = false;
		keyLeft = false;
		if(playerNo == 1) dirLeft = true;
		keyRight = false;
		jumping = false;
		falling = false;

		jumpFallTimer = 0;

		itemsLeft = 10;
		points = 0;

		deathBySquish = false;
		deathByTime = false;
		deathByDeadlyBox = false;
		alive = true;
		caughtAllItems = false;
	}

}
