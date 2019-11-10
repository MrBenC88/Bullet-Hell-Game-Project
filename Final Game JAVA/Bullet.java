/**
 *Bullet.java
 The Gun class for the breakout main class. This class has all the components and methods
 for the gun upgrade
  @author Ben Cheung
 */

import java.awt.Rectangle;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

// the basic in the bullet class
class Bullet {
	// we used protected so we can use these variables to access from the sub classes
	protected int x,y;
	private int velY, mvelY; // velY is for users, mVelY is for computers
	
    public Bullet(int x, int y) {
     	this.x = x;
     	this.y = y;
     	velY = 5;
     	mvelY = 4;
    }
    // this is only applies to userbullet0-8.png and Minion bullets
    public Rectangle getRect(){
    	//get rectangle for each individual projectile
    	//- used for collison check
    	return new Rectangle(x,y,6,10);
    }
    // boss' big bullet ones
    public Rectangle getBRect() {
    	return new Rectangle(x,y,15,45);
    }
    // Getting values
    public int getX() {
    	return x;
    }
    public int getY() {
    	return y;
    }
    // normal shooting
    public void shoot(boolean player) {
    	if (player) {
    		y -= velY; // always default
    	}
    	else {
    		y += mvelY;
    	}
    }
}
// this shoots in a circle, but goes down as a circle(also expands the circle too!), it never rotates
class FixedBullet extends Bullet {
	private double velX,velY;
	private double px,py,pr;
	
	public FixedBullet(int x, int y, double vx, double vy) {
		super(x,y); // we use super so we can access X,Y and then use vx and vy only for this class
		velX = vx;
		velY = vy;
		px = x;
		py = y;
	}
	
	@Override
	public void shoot(boolean player) {
		px += velX;
		py += velY;
		x = (int)px;
		y = (int)py;
	}
}
// this sub class shoots in a fixed circle, but it rotates along through
class RotateBullet extends Bullet {
	private double velY, velG, velR;
	private double px,py,pr,maxR,rotate;
	
	public RotateBullet(int x, int y, double vy, double vg, double maxr, double r, double vr) {
		super(x,y);
		velY= vy;
		velG = vg;
		velR = vr; //vel
		px = x;
		py = y;
		rotate = r; // our inital rotation
		maxR = maxr; // max rotation
	}
	
	@Override
	public void shoot(boolean player) {
		if (pr < maxR) {
			pr += velG;
		}
		py += velY;
		rotate+= velR;
		x = (int)(Math.cos(rotate)* pr + px);
		y = (int)(Math.sin(rotate) * pr + py);
	}
}
