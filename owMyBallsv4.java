import acm.program.*;

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.*;
import acm.util.*;
/*taken from class example: run in eclipse to I can view how maybe collision physics might work */
public class owMyBallsv4 extends GraphicsProgram {
	
	/** Width and height of application window in pixels */
	public static final int applicationWidth = 400; 
	public static final int applicationHeight = 600;

	/** Size (diameter) of the ball */
	private static final int ballRadius = 8;	
	private double ballDiameter=2*ballRadius;
	//sets up Y vector
	private double vy =5;
	private double vx ;
	private GOval ball; 
	private GRect paddle;
	private GRect hitbox;
	private GRect background;
	private int delay = 50;
	
	/** Dimensions of the paddle */
	private static final int paddleWidth = 60;
	private static final int paddleHeight = 10;
	
	/** Offset of the paddle up from the bottom */
	private static final int paddleYOffset = 30;
	private static final double paddleYCord = applicationHeight-paddleYOffset;

	/*end conditions*/
	private GLabel winMsg;
	private GLabel loseMsg;
	private GLabel startMsg;
	
	private int brickYOffset =2;
	private int nBrickRows = 10;
	private int nBricksPerRow = 10;
	private int brickSep = 4;
	private int brickHeight = 8;
	private int brickWidth = (applicationWidth-(brickSep*(nBricksPerRow-1)))/nBricksPerRow;
	private int nBricks=nBrickRows*nBricksPerRow;
	private int nFlashes=5;
	
	private int nTurns=3;
	
	private int slowRebounds=7;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//make messages white, figure out how to "lock" background again... somewhere in the checkforCollision()
	
	public void run() {
		background = new GRect(applicationWidth, applicationHeight);
		background.setFilled(true);
		background.setColor(Color.BLACK);
		setup();
		makePaddle();
		makeBricks();
		addMouseListeners();
		playBreakout();
	}
	
	public void mouseMoved(MouseEvent e){
		paddle.setLocation(e.getX()-paddleWidth/2, paddleYCord );
		hitbox.setLocation(e.getX()-paddleWidth/2, paddleYCord-ballDiameter );
		}
	private void playBreakout() {
		background.sendToBack();
		winMsg = new GLabel ("You Win!");
		loseMsg = new GLabel("You Lose. Better Luck Next Time.");
		startMsg = new GLabel("You have "+nTurns+" tries left. Click to start");
		startMsg.setColor(Color.WHITE);
		add(startMsg,(applicationWidth-startMsg.getWidth())/2, ((applicationHeight-startMsg.getAscent())/2)-ballDiameter);
		waitForClick();
		remove(startMsg);
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		moveBall(); 
		winMsg = new GLabel ("You Win!");
		winMsg.setColor(Color.WHITE);
		loseMsg = new GLabel("Sorry, Better Luck Next Time");
		loseMsg.setColor(Color.WHITE);
		if(nBricks==0) {
			pause(delay*nFlashes);
			add(winMsg,(applicationWidth-winMsg.getWidth())/2, ((applicationHeight-winMsg.getAscent())/2)-ballDiameter);
			remove(ball);
		}else if(nTurns==0) {
			pause(delay*nFlashes);
			add(loseMsg,(applicationWidth-loseMsg.getWidth())/2, ((applicationHeight-loseMsg.getAscent())/2)-ballDiameter);
			remove(ball);
		}
	}
	
	private void moveBall() {
		while(nBricks>0 && nTurns>0) {
			ball.move(vx, vy);
			checkForCollision();
			checkForMiss();
			checkNumBounces();
			pause(delay);
		}
		
	}
	
	private void checkNumBounces() {
		if(slowRebounds==7) {
			delay=delay/2;
			slowRebounds++;
		}
		
	}
	//creates ball and sets it in middle of field
	private void setup() {
		this.resize(applicationWidth , applicationHeight);
		add(background);
		ball = new GOval(applicationWidth/2-ballRadius, applicationHeight/2-ballRadius, 2*ballRadius, 2*ballRadius); 
   		ball.setFilled(true);
   		ball.setColor(Color.WHITE);
   		add(ball);
	}
	
	private void makePaddle() {
		paddle = new GRect(applicationWidth/2, paddleYCord,paddleWidth, paddleHeight); 
		paddle.setFilled(true);
		paddle.setColor(Color.WHITE);
		add(paddle);
		hitbox = new GRect(applicationWidth/2, 50,paddleWidth, paddleHeight); 
		hitbox.setFilled(true);
		hitbox.setVisible(false);
		add(hitbox);
	}
	
	private void checkForCollision() {
		if(ball.getY()<=0 ) {
			vy=-vy;
		}
			
		if(ball.getX()<=0 || ball.getX()+ballDiameter >= getWidth()) {
			vx=-vx;
		}
		//check for other objects -- modified from https://gist.github.com/NatashaTheRobot/1375730
				GObject vertCollider = getVertCollidingObject();
				if (vertCollider !=background) {
					if (vertCollider==hitbox) {
							vy = -vy;
							slowRebounds++;
					}
					else if(vertCollider != ball&&vertCollider !=paddle) 
						{ if (vertCollider != null) {
								remove(vertCollider);
								remove(vertCollider);
								nBricks--;
								vy = -vy;
							}
							pause (delay);
						}
				}
					
	}
	   
	private void checkForMiss() {
			if(ball.getY()+ballDiameter>= getHeight()) {
				nTurns--;
				remove(ball);
				if(nTurns!=0) {
					setup();
					playBreakout();
					}
				}
	}
	
	private void makeBricks() {
		int rowTopLine =brickYOffset;
		int j = 1;
		for(int i=0; i< nBrickRows;i++){ 
			addRow(rowTopLine,j);
			if(i%2!=0) {
				j++;
			}
			if(j>5) {
				j=1;
			}
			rowTopLine+= brickHeight+brickSep;
		}
	}
		
		private void addRow(int yCord, int brickCol) {	
			int xCord=brickSep/2;
			for(int i=0;i< nBricksPerRow;i++){ 
				GRect brick = new GRect(brickWidth, brickHeight);
				add(brick, xCord, yCord);
				brick.setFilled(true);
				//takes exported brickCol value and matches it to row
				switch(brickCol) {
				case 1:brick.setColor(Color.RED);
					break;
				case 2:brick.setColor(Color.ORANGE);
					break;
				case 3:brick.setColor(Color.YELLOW);
					break;
				case 4:brick.setColor(Color.GREEN);
					break;
				case 5:brick.setColor(Color.CYAN);
					break;
				}
				
				xCord+=brickWidth+brickSep;
			}
		 }
		//checks up/down edges of ball
	private GObject getVertCollidingObject() {
		//top left
		if((getElementAt(ball.getX(), ball.getY())) != null) {
	         return (getElementAt(ball.getX(), ball.getY()));
	      }
		//top right
		else if(getElementAt(ball.getX()+ballDiameter, (ball.getY())) != null ){
	         return getElementAt(ball.getX()+ballDiameter, (ball.getY()));
	      }
		//bottom left
		else if(getElementAt(ball.getX(), (ball.getY() + ballDiameter)) != null ){
	         return getElementAt(ball.getX(), (ball.getY() + ballDiameter));
	      }
		//bottom right
		else if(getElementAt(ball.getX()+ballDiameter, (ball.getY()+ballDiameter)) != null ){
	         return getElementAt(ball.getX()+ballDiameter, (ball.getY()+ballDiameter));
	      }
		else{
	         return null;
	      }
		
	}
}
		

