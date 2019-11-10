/* MiniBoss.java
 * MiniBoss is a class for the sub-boss. The Sub Boss includes a higher HP, and deals far more greater damage.
 * It will load from the file i/o in GamePanel, which includes their HP, their name, and possibly abilities.
 * It is technically 10 minions in 1.
 */

import java.awt.Rectangle;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

class MiniBoss {
	private static final int RIGHT_WALL = 590;
	private static final int LEFT_WALL = 38;
	private static final int UP_WALL = 85;
	private static final int DOWN_WALL = 450;
	private double PROB = 0.001; // default probability
	private int velX, velY; // velocity movement
	private int x,y,hp,lives,maxLives,maxHP,hpWidth,timer;
	private double ratio;
	private BufferedImage img, bulletPic, bulletPic2;
	private Random r;
	private ArrayList<Bullet> gun = new ArrayList<Bullet>();
	
	public MiniBoss(int x, int y, int hp, int lives) {
		this.x = x;
		this.y = y;
		this.hp = hp;
		this.lives = lives;
		maxHP = hp;
		maxLives = lives;
		hpWidth = 214;
		ratio = (double)hp/(double)maxHP;
		timer = 500; // every 5 seconds (10 ticks, so it's 500)
		// sub bosses are way faster than normal minions
		velX = velY = 4;
		bulletPic = loadBullet("subbullet1.jpg");
	}
	private void loadPic(String file) {
		try {
			img = ImageIO.read(new File("game/"+file));
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}
	// loads bullet
	private BufferedImage loadBullet(String file) {
		BufferedImage bulletPic = null;
		try {
			bulletPic = ImageIO.read(new File("game/"+file));
		}
		catch(IOException ex) {
			System.out.println(ex);
		}	
		return bulletPic;
	}
	// methods
	private void setD() {
	    double speed = 4.0; // might change
	    double direction = Math.random() * 2 * Math.PI; // goes in a complete opposite direction (in a unit circle)
	    velX = (int) (speed * Math.cos(direction)); // adjusts the speed
	    velY = (int) (speed * Math.sin(direction)); // adjusts how fast it's going up and down
	}
	// this move function is from the minions and boss too
	public void move() {
		x += velX;
		y += velY;
		if (y > DOWN_WALL) {
			y = DOWN_WALL;
			setD();
		}
		else if (y < UP_WALL) {
			y = UP_WALL;
			setD();
		}
		else if (x > RIGHT_WALL) {
			x = RIGHT_WALL;
			setD();
		}
		else if (x < LEFT_WALL) {
			x = LEFT_WALL;
			setD();
		}
		else if (Math.random() < 0.01) {
			setD(); // trying something new
		}
	}
	/* there will be each attack pattern available, however,
	 * each attack will have a 1% chance of shooting, there could be a chance where
	 * the enemy can shoot all three times though
	*/
	public void shoot() {
		// This sets the probability of it actually shooting
		if (Math.random() < PROB) {
			// this expands the circle
			for (double i=0;i<Math.PI*2;i+=Math.PI/10) {
				gun.add(new FixedBullet(x+5,y+5,2*Math.cos(i),2*Math.sin(i)));
			}
		}
		if (Math.random() < PROB) {
			double maxr = Math.random()*40+80; // we want to randomly generate our max rotation
			for (double i=0; i < Math.PI * 2; i+= Math.PI/3) {
				gun.add(new RotateBullet(x+5,y+5,1,2,maxr,i,Math.PI/250));
			}
		}
		if (!gun.isEmpty()) {
			for(Bullet b : gun) {
				b.shoot(false);
			}
		}
	}
	public void checkBullet() {
		if (!gun.isEmpty()) {
			for (int i=0; i < gun.size(); i++) {
				if (gun.get(i).getY() > DOWN_WALL+200 || gun.get(i).getX() < LEFT_WALL || gun.get(i).getX() > RIGHT_WALL || gun.get(i).getY() < UP_WALL) {
					gun.remove(i); // removes 
				}
			}
		}
	}
	public void loseHP(int dmg) {
		if (hp > 0) {
			hp -= dmg;
		}
		if (hp <= 0) {
			lives--;
			hp = 0; // fix
			// if you still have lives, we can restore your HP
			if (lives > 0) {
				hp = maxHP;
			}
		}
		ratio = (double)hp/(double)maxHP;
	}
	// it will gain HP every 2 seconds
	public void regenHP() {
		timer++;
		if (timer >= 200 && hp <= maxHP) {
			hp += 5;
			timer = 0;
		}
		ratio = (double)hp/(double)maxHP;
	}
	// checks if the mini boss is finally dead
	public boolean isDead() {
		return lives <= 0;
	}
	// resets value
	public void reset() {
		hp = maxHP;
		lives = maxLives;
	}
	// GETTING VALUES
	public Rectangle getHealthBar() {
		return new Rectangle(318,34,(int)(ratio*hpWidth),12);	
	}
	public Rectangle getRect() {
		return new Rectangle(x,y,img.getWidth(),img.getHeight());
	}
	public BufferedImage getPic() {
		return img;
	}
	public BufferedImage getBulletPic() {
		return bulletPic;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getHP() {
		return hp;
	}
	public int getLives() {
		return lives;
	}
	public ArrayList<Bullet> getGun() {
		return gun;
	}
	// SETTING VALUES
	public void setPic(String pic) {
		loadPic(pic);
	}
}