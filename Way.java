/**
The main class
Handles the actual game
*/

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class Way extends JFrame implements Runnable, KeyListener, MouseListener
{
	//default resolution 1000*700
	public static int w = 1000, h = 800;

	// Get the size of the default screen
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

	Thread t = new Thread(this);
	int sleeptime = (int)(1000/50); //1000/UPS, default 1000/50

	Player[] player = new Player[2]; //Creates the player array
	Box[] boxes = new Box[(h*w)/6000]; //Creates an array for the boxes and waves (60)
	Particle[] particles = new Particle[500];
	Item item = new Item(); //Creates the item

	public static double gravity = 0.5; //The gravitational acceleration (d 0.5)
	long timeLimitMillis = 120*1000; //The time limit in milliseconds (Seconds*1000)
	int numberOfSlowDownBoxes = 0; //The number of times the player has used the slowing down function

	long timeStartMillis, currentTimeMillis; //For the timer
	long timePauseMillis, timePauseStartMillis, timeTotalPauseMillis; //To keep track of the pause time like

	long timeStopMillis, timeStopStartMillis; //How long the boxes have been stopped
	int timeStopTime; //How long the boxes should be stopped

	int explosionTimer;
	boolean explode;

	//Variables for game states
	boolean gameStateStart = true;
		boolean showInstructions = false;
	boolean gameStateRun = false;
	boolean gameStateEnd = false;
	boolean gameStatePause = false;

	//The images used in the game
	Image playerRight;
	Image playerLeft;
	Image playerRunRight;
	Image playerRunLeft;
	Image playerCrouchRight;
	Image playerCrouchLeft;
	Image itemSprite;

	public static boolean slowDownBoxes = false;

	//For double buffering
	Image dbImage;
	Graphics dbGraphics;

	public Way()
	{
		  h = dim.height;
    	 w = dim.width;

		setSize(w, h);
		setTitle("Way MP");
		setResizable(false);
		setBackground(Color.black);

		addKeyListener(this);

		for(int i = 0; i < player.length; i++) player[i] = new Player(i+1);

		loadImages();

		for(int i = 0; i < player.length; i++) player[i].setImages(playerRight, playerLeft, playerCrouchRight, playerCrouchLeft, playerRunRight, playerRunLeft);

		item.setImage(itemSprite);

		setVisible(true);

		//Start the game
		t.start();
	}

	/**
	Loads all the sprites
	*/
	public void loadImages()
	{
		Toolkit tk = Toolkit.getDefaultToolkit();
		MediaTracker mt = new MediaTracker(this);

		//load all images
		playerRight = tk.getImage("img/playerRight.gif");
		mt.addImage(playerRight, 1);

		playerLeft = tk.getImage("img/playerLeft.gif");
		mt.addImage(playerLeft, 2);

		playerCrouchRight = tk.getImage("img/playerCrouchRight.gif");
		mt.addImage(playerCrouchRight, 3);

		playerCrouchLeft = tk.getImage("img/playerCrouchLeft.gif");
		mt.addImage(playerCrouchLeft, 4);

		playerRunRight = tk.getImage("img/playerRunRight.gif");
		mt.addImage(playerRunRight, 5);

		playerRunLeft = tk.getImage("img/playerRunLeft.gif");
		mt.addImage(playerRunLeft, 5);

		itemSprite = tk.getImage("img/item.gif");
		mt.addImage(itemSprite, 8);

		try
		{
			mt.waitForAll();
		}
		catch(InterruptedException ie)
		{}
	}

	/**
	Sets/resets things before the start of the game
	*/
	public void init()
	{
		gameStateEnd = false;
		gameStatePause = false;
		gameStateStart = false;

		//Create all the boxes
		for(int i = 0; i < boxes.length; i++)
		{
			boxes[i] = new Box();
		}

		//Create all particles
		for(int i = 0; i < particles.length; i++)
		{
			particles[i] = new Particle();
		}

		if(!showInstructions)
		{
			item.setX(w/2);
			item.setY(h/2);
		}

		showInstructions = false;

		//Starts the timer
		timeStartMillis = System.currentTimeMillis();

		explosionTimer = 0;
		explode = true;

		slowDownBoxes = false;
		numberOfSlowDownBoxes = 0;

		//Resets the player
		for(int i = 0; i < player.length; i++) player[i].reset();
		gameStateRun = true;
	}

	public static void main(String [] dkd)
	{
		new Way();
	}

	public void run()
	{
		while(true)
		{
			while(gameStateRun)
			{
				/**Move particles*/
				for(int i = 0; i < particles.length; i++)
				{
					particles[i].updatePosition();

					if(particles[i].getX()+particles[i].getWidth() < 0) particles[i].setX(w);
					if(particles[i].getX() > w) particles[i].setX(0);
				}


				/**Player*/
				for(int i = 0; i < player.length; i++)
				{
					player[i].setStance(); //Make the player crouch or stand up
					player[i].checkCollisionsCrouch(boxes); //Check for collisions when the player has stopped crouching
					player[i].moveX(); //Move player in x-axis
				}

				/**Boxes*/
				//Slow all boxes down until they stop
				if(slowDownBoxes)
				{
					for(Box tmp: boxes)
					{
						if(tmp.getXV() != 0)
						{
							int XVint = (int)(tmp.getXV()*10);

							if(XVint > 0) XVint -= 1;
							else XVint += 1;

							tmp.setXV(XVint/10.0);
						}
					}

					if(timeStopTime == 0)
					{
						timeStopTime = (int)(Math.random()*15+5);
						timeStopStartMillis = currentTimeMillis;
					}

					timeStopMillis = currentTimeMillis-timeStopStartMillis;

					if(timeStopMillis > timeStopTime*1000)
					slowDownBoxes = false;
				}

				//Make sure the boxes have the correct speed
				//i.e. accelerate them if they've been stopped
				if(!slowDownBoxes)
				{
					for(Box tmp: boxes)
					{
						if(tmp.getXV() != tmp.getXVStart())
						{
							int XVint = (int)(tmp.getXV()*10);

							if(tmp.getXVStart() > 0) XVint += 1;
							else XVint -= 1;

							tmp.setXV(XVint/10.0);
						}
					}

					timeStopTime = 0;
				}

				//Moves all boxes
				for(int i = 0; i < boxes.length; i++)
				{
					boxes[i].updatePosition();

					if(boxes[i].getX()+boxes[i].getWidth() < 0) boxes[i] = new Box();
					if(boxes[i].getX() > w) boxes[i] = new Box();
				}

				/**Player*/
				for(int i = 0; i < player.length; i++)
				{
					player[i].keepInside(boxes); //Keep the player inside the game area
					player[i].checkCollisionsX(boxes); //Check for collisions in x-axis

					if(player[i].isAlive())
					{
						player[i].moveY(); //move player in y-axis
						player[i].checkCollisionsY(boxes); //Check for collisions in y-axis
					}

					player[i].checkItemCatch(item, boxes); //Check if player has caught an item
					player[i].resetCollisions(); //Reset all collision booleans
				}

				currentTimeMillis = System.currentTimeMillis() - timeStartMillis - timeTotalPauseMillis; //Refresh the timer

				for(int i = 0; i < player.length; i++)
				{
					//Ends the game if player is dead
					if(!player[i].isAlive())
					{
						if(player[i].getItemsLeft() < 10)
						{
							player[i].setItemsLeft(player[i].getItemsLeft()+1);
							if(player[i].getPlayerNo() == 1) player[i].setX(w-50-player[i].getWidth());
							if(player[i].getPlayerNo() == 2) player[i].setX(50);
							player[i].setY(h-20);
							player[i].keyLeft(false);
							player[i].keyRight(false);
							player[i].keyUp(false);
							player[i].keyDown(false);

							if(player[i].getPlayerNo() == 1) player[i].dirLeft();
							if(player[i].getPlayerNo() == 2) player[i].dirRight();

							player[i].setAlive(true);
						}
						else
						{
							gameStateRun = false;
							gameStateEnd = true;
							player[i].keyLeft(false);
							player[i].keyRight(false);
							player[i].keyDown(false);
						}
					}

					//Ends game if player has caught all items
					if(player[i].hasCaughtAllItems())
					{
						gameStateRun = false;
						gameStateEnd = true;
					}
				}

				//Pause for some time
				try
				{
					t.sleep(sleeptime);
				}
				catch(InterruptedException ie){}

				repaint();

			} //gameStateRun

			while(gameStateStart)
			{
				repaint();

				//Pause for some time
				try
				{
					t.sleep(sleeptime*2);
				}
				catch(InterruptedException ie){}
			}//gameStateStart

			while(gameStatePause)
			{
				repaint();

				timePauseMillis = System.currentTimeMillis()-timePauseStartMillis;

				//Pause for some time
				try
				{
					t.sleep(sleeptime*2);
				}
				catch(InterruptedException ie){}
			}//gameStatePause

			while(gameStateEnd)
			{
				repaint();
				//Pause for some time
				try
				{
					t.sleep(sleeptime*2);
				}
				catch(InterruptedException ie){}
			}//gameStateEnd

		}//true

	}//run

	public void keyPressed(KeyEvent ke)
	{
		/**
		For gameStateRun

		Left key - moves player left
		Right key - moves player Right
		Up key - makes player jump
		Down key - makes player crouch
		S - slows down all boxes
		Esc - opens the pause menu
		*/

		if(gameStateRun)
		{

			//PLAYER 1 - ARROW KEYS + ENTER
				if(ke.getKeyCode() == ke.VK_LEFT)
				{
					player[0].keyLeft(true);
					player[0].dirLeft();
				}

				if(ke.getKeyCode() == ke.VK_RIGHT)
				{
					player[0].keyRight(true);
					player[0].dirRight();
				}

				if(ke.getKeyCode() == ke.VK_UP)
				{
					player[0].keyUp(true);
				}

				if(ke.getKeyCode() == ke.VK_DOWN)
				{
					player[0].keyDown(true);
				}

				if(ke.getKeyCode() == ke.VK_ENTER)
				{
					player[0].epicWave(boxes);
				}
			//PLAYER 1

			//PLAYER 2 - WASD + 1
			if(ke.getKeyCode() == ke.VK_A)
				{
					player[1].keyLeft(true);
					player[1].dirLeft();
				}

				if(ke.getKeyCode() == ke.VK_D)
				{
					player[1].keyRight(true);
					player[1].dirRight();
				}

				if(ke.getKeyCode() == ke.VK_W)
				{
					player[1].keyUp(true);
				}

				if(ke.getKeyCode() == ke.VK_S)
				{
					player[1].keyDown(true);
				}

				if(ke.getKeyCode() == ke.VK_1)
				{
					player[1].epicWave(boxes);
				}
			//PLAYER 2

			if(ke.getKeyCode() == ke.VK_ESCAPE)
			{
				gameStatePause = true;
				gameStateRun = false;
				for(int i = 0; i < player.length; i++)
				{
					player[i].keyLeft(false);
					player[i].keyRight(false);
				}

				timePauseStartMillis = System.currentTimeMillis();
			}

/**
DEVELOPER KEY CHEATS LOL
**/
if(ke.getKeyCode() == ke.VK_H)
{
	slowDownBoxes = !slowDownBoxes;
	if(!slowDownBoxes) numberOfSlowDownBoxes++; //Counts how many time the boxes have been slowed down
}

if(ke.getKeyCode() == ke.VK_G)
{
	player[0].itemCaught();
	player[1].itemCaught();
}
/**
//DEVELOPER KEY CHEATS LOL
**/
		}

		if(gameStatePause)
		{
			if(ke.getKeyCode() == ke.VK_C)
			{
				gameStateRun = true;
				gameStatePause = false;
				timeTotalPauseMillis += timePauseMillis;
			}

			if(ke.getKeyCode() == ke.VK_Q)
			{
				System.exit(0);
			}

			if(ke.getKeyCode() == ke.VK_R)
			{
				init();
			}
		}

		if(gameStateStart)
		{
			if(ke.getKeyCode() == ke.VK_S)
			{
				init();
			}

			if(ke.getKeyCode() == ke.VK_I) showInstructions = !showInstructions;

			if(ke.getKeyCode() ==  ke.VK_Q) System.exit(0);
		}

		if(gameStateEnd)
		{
			if(ke.getKeyCode() == ke.VK_R)
			{
				init();
			}

			if(ke.getKeyCode() == ke.VK_Q) System.exit(0);
		}

	}

	public void keyReleased(KeyEvent ke)
	{
		if(gameStateRun)
		{
			//PLAYER 1 - ARROW KEYS
				if(ke.getKeyCode() == ke.VK_LEFT)
				{
					player[0].keyLeft(false);
					player[0].setXV(0);
				}

				if(ke.getKeyCode() == ke.VK_RIGHT)
				{
					player[0].keyRight(false);
					player[0].setXV(0);
				}

				if(ke.getKeyCode() == ke.VK_DOWN)
				{
					player[0].keyDown(false);
				}

				if(ke.getKeyCode() == ke.VK_UP)
				{
					player[0].keyUp(false);
				}

			//PLAYER 2 - WASD
				if(ke.getKeyCode() == ke.VK_A)
				{
					player[1].keyLeft(false);
					player[1].setXV(0);
				}

				if(ke.getKeyCode() == ke.VK_D)
				{
					player[1].keyRight(false);
					player[1].setXV(0);
				}

				if(ke.getKeyCode() == ke.VK_S)
				{
					player[1].keyDown(false);
				}

				if(ke.getKeyCode() == ke.VK_W)
				{
					player[1].keyUp(false);
				}
		}
	}

	public void mouseExited(MouseEvent me)
	{

	}

	public void mouseEntered(MouseEvent me)
	{

	}

	public void mouseReleased(MouseEvent me)
	{

	}

	public void mousePressed(MouseEvent me)
	{

	}

	public void mouseClicked(MouseEvent me)
	{

	}




	public void keyTyped(KeyEvent ke)
	{

	}

	public void paint(Graphics g)
	{
		if( dbGraphics == null ) //haven't created a db image
		{
			dbImage = createImage(w, h);
			dbGraphics = dbImage.getGraphics();
		}

		//clear the window
		dbGraphics.setColor( getBackground() );
		dbGraphics.fillRect(0,0,w,h);

		//Draw if the game isn't in start mode
		if(!gameStateStart)
		{
			//draw the Particles
			for(Particle part: particles)
			{
				part.draw(dbGraphics);
			}


			//Draw the pointboxes

			//PLAYER 1
				//outlines
			for(int i = 0; i < 10; i++)
			{
				dbGraphics.setColor(Color.red);
				dbGraphics.drawRect(w-40, 30+40*i, 30, 30);
			}
				//pointboxes
			for(int i = 0; i < 10-player[0].getItemsLeft(); i++)
			{
				dbGraphics.setColor(Color.red);
				dbGraphics.fillRect(w-40, 30+40*i, 30, 30);
			}

			//PLAYER 2
				//outlines
			for(int i = 0; i < 10; i++)
			{
				dbGraphics.setColor(Color.red);
				dbGraphics.drawRect(10, 30+40*i, 30, 30);
			}
				//pointboxes
			for(int i = 0; i < 10-player[1].getItemsLeft(); i++)
			{
				dbGraphics.setColor(Color.red);
				dbGraphics.fillRect(10, 30+40*i, 30, 30);
			}


			/*
			//Draw the timeboxes
			int noTimeBoxes = (int)(30-(currentTimeMillis/1000)/(timeLimitMillis/1000/30));
			for(int i = 0; i < noTimeBoxes; i++)
			{
				dbGraphics.setColor(Color.red);
				dbGraphics.fillRect(w-65, 30+13*i, 15, 9);
			}
			*/

			//draw the Item
			item.draw(dbGraphics);

			//draw the Boxes
			for(Box box: boxes)
			{
				box.draw(dbGraphics);
			}

			//draw players
			for(int i = 0; i < player.length; i++)
			{
				player[i].draw(dbGraphics);
			}
		}

		if(gameStateStart)
		{
			dbGraphics.setColor(Color.white);
			dbGraphics.drawRect((int)(w/4*1.5), h/5, w/4, (h/5)*3);

			dbGraphics.drawString("Way 2009", (int)(w/4*1.5)+30, h/5+50);
			dbGraphics.drawString("by dkd", (int)(w/4*1.5)+30, h/5+60);

			dbGraphics.drawString("S: Start game", (int)(w/4*1.5)+30, h/5+150);
			dbGraphics.drawString("I: Show/hide instructions", (int)(w/4*1.5)+30, h/5+162);
			dbGraphics.drawString("Q: Exit", (int)(w/4*1.5)+30, h/5+174);

			if(showInstructions)
			{
				dbGraphics.setColor(Color.red);

				//Draw pointboxes
				for(int i = 0; i < 8; i++)
				{
					dbGraphics.fillRect(w-40, 30+40*i, 30, 30);
				}

				//draw timeboxes
				for(int i = 0; i < 20; i++)
				{
					dbGraphics.fillRect(w-65, 30+13*i, 15, 9);
				}

				item.setX(w-33);
				item.setY(30+40*10);
				item.draw(dbGraphics);

				dbGraphics.setColor(Color.white);

				dbGraphics.drawString("Time left", w-120, 25+13*20);
				dbGraphics.drawString("Items left", w-96, 19+40*8);
				dbGraphics.drawString("Item - ", w-70, 42+40*10);
				dbGraphics.drawString("* Control your character", (int)(w/4*2.5)+20, h/5+14);
				dbGraphics.drawString("   with the arrow keys.", (int)(w/4*2.5)+20, h/5+2*14);
				dbGraphics.drawString("* Catch 10 items before", (int)(w/4*2.5)+20, h/5+3*14);
				dbGraphics.drawString("   the time runs out.", (int)(w/4*2.5)+20, h/5+4*14);
				dbGraphics.drawString("* Stop the boxes with the S key.", (int)(w/4*2.5)+20, h/5+5*14);
				dbGraphics.drawString("   This may or may not help you,", (int)(w/4*2.5)+20, h/5+6*14);
				dbGraphics.drawString("   but it will cost points.", (int)(w/4*2.5)+20, h/5+7*14);
				dbGraphics.drawString("* If you get crushed between", (int)(w/4*2.5)+20, h/5+8*14);
				dbGraphics.drawString("   two boxes, you die.", (int)(w/4*2.5)+20, h/5+9*14);
				dbGraphics.drawString("* If you hit a gray box, you die.", (int)(w/4*2.5)+20, h/5+10*14);
				dbGraphics.drawString("* If you run out of time, you DIE.", (int)(w/4*2.5)+20, h/5+11*14);
				dbGraphics.drawString("* May the force be with you.", (int)(w/4*2.5)+20, h/5+13*14);
			}
		}//gameStateStart


		if(gameStateEnd)
		{
			for(int i = 0; i < player.length; i++)
			{
				//If a player has died
				if(!player[i].isAlive())
				{
					//Explosion
					dbGraphics.setColor(Color.red);
					dbGraphics.fillOval(player[i].getX()+(player[i].getWidth()/2)-(explosionTimer),
										player[i].getY()+(player[i].getHeight()/2)-(explosionTimer),
										2*(explosionTimer),
										2*(explosionTimer));

					if(explosionTimer > 20) explode = false;
					//if(explosionTimer < 0) explode = true;

					if(explode) explosionTimer++;
					//if(!explode) explosionTimer--;

					player[i].draw(dbGraphics);

					dbGraphics.setColor(Color.black);
					dbGraphics.fillRect((int)(w/4*1.5), (int)(1.5*h/5), w/4, 2*h/5);
					dbGraphics.setColor(Color.white);
					dbGraphics.drawRect((int)(w/4*1.5), (int)(1.5*h/5), w/4, 2*h/5);

					if(player[i].deathBySquish())
					{
						dbGraphics.drawString("Player " + player[i].getPlayerNo() + " got squished and", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40);
						dbGraphics.drawString("DIED DIED DIED DIED", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+1*14);

						dbGraphics.drawString("R: Restart game", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+6*14);
						dbGraphics.drawString("Q: Quit", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+7*14);
					}

					if(player[i].deathByDeadlyBox())
					{
						dbGraphics.drawString("Player " + player[i].getPlayerNo() + " got hit by a", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40);
						dbGraphics.drawString("deadly box and", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+14);
						dbGraphics.drawString("DIED DIED DIED DIED", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+2*14);

						dbGraphics.drawString("R: Restart game", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+6*14);
						dbGraphics.drawString("Q: Quit", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+7*14);
					}
				}//if !player.isAlive()

				if(player[i].hasCaughtAllItems())
				{
					//Explosion
					dbGraphics.setColor(Color.green);
					dbGraphics.fillOval(player[i].getX()+(player[i].getWidth()/2)-(explosionTimer),
										player[i].getY()+(player[i].getHeight()/2)-(explosionTimer),
										2*(explosionTimer),
										2*(explosionTimer));

					if(explosionTimer > 50) explode = false;

					if(explode) explosionTimer++;

					player[i].draw(dbGraphics);

					dbGraphics.setColor(Color.black);
					dbGraphics.fillRect((int)(w/4*1.5), (int)(1.5*h/5), w/4, 2*h/5);
					dbGraphics.setColor(Color.white);
					dbGraphics.drawRect((int)(w/4*1.5), (int)(1.5*h/5), w/4, 2*h/5);

					dbGraphics.drawString("Player " + player[i].getPlayerNo() + " won! Congratulations!", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40);

					dbGraphics.drawString("Score", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+2*14);
					dbGraphics.drawString("Player 1: " + ((12-player[0].getItemsLeft())*54.31-((currentTimeMillis+500)/10000)*3.14), (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+3*14);
					dbGraphics.drawString("Player 2: " + ((12-player[1].getItemsLeft())*54.31-((currentTimeMillis+500)/10000)*3.14), (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+4*14);

					dbGraphics.drawString("Time: " + (int)((currentTimeMillis+500)/1000) + "s", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+6*14);

					dbGraphics.drawString("R: Restart game", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+8*14);
					dbGraphics.drawString("Q: Quit", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+9*14);
				}// if player.hasCaughtAllItems()
			}//for



			}//gameStateEnd

		if(gameStatePause)
		{
			dbGraphics.setColor(Color.black);
			dbGraphics.fillRect((int)(w/4*1.5), (int)(1.5*h/5), w/4, 2*h/5);
			dbGraphics.setColor(Color.white);
			dbGraphics.drawRect((int)(w/4*1.5), (int)(1.5*h/5), w/4, 2*h/5);

			dbGraphics.drawString("GAME PAUSED", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40);

			dbGraphics.drawString("C: Continue game", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+6*14);
			dbGraphics.drawString("R: Restart game", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+7*14);
			dbGraphics.drawString("Q: Quit", (int)(w/4*1.5)+30, (int)(1.5*h/5)+40+8*14);
		}//gameStatePause


		//draw everything
		g.drawImage(dbImage, 0, 0, null);
	}

}

