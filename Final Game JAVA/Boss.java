/* The boss class handles the movement of the boss, the shooting, and the random directions and 
 * probability to shoot. It also handles the HP bar
 */

import java.awt.Rectangle;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

class Boss {
	private static final int RIGHT_WALL = 610;
	private static final int LEFT_WALL = 28;
	private static final int UP_WALL = 85;
	private static final int DOWN_WALL = 450;
	private double PROB = 0.003;
	private int x,y,hp,maxHP,lives,maxLives,velX,velY,hpWidth,timer;
	private double ratio;
	private BufferedImage img, bulletPic;
	private ArrayList<Bullet> gun = new ArrayList<Bullet>();
	
	public Boss(int x, int y, int hp, int lives) {
		this.x = x;
		this.y = y;
		this.hp = hp;
		this.lives = lives;
		timer = 500; // this timer does the regeneration hp
		maxHP = hp;
		maxLives = lives;
		hpWidth = 214;
		ratio = (double)hp/(double)maxHP; // for our rectangle's width
		bulletPic = loadBullet("bossbullet1.png");
	}
	private void loadPic(String file) {
		try {
			img = ImageIO.read(new File("game/"+file));
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}
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
	//
	private void setD() {
	    double speed = 5.0; // speeds will greatly increase since it's a boss
	    double direction = Math.random() * 2 * Math.PI; // goes in a complete opposite direction (in a unit circle)
	    velX = (int) (speed * Math.cos(direction)); // adjusts the speed
	    velY = (int) (speed * Math.sin(direction)); // adjusts how fast it's going up and down
	}
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
		else if (Math.random() < 0.05) {
			setD(); // trying something new
		}
	}
	public void shoot() {
		// This sets the probability of it actually shooting
		if (Math.random() < 0.001) {
			// this expands the circle
			for (double i=0;i<Math.PI*2;i+=Math.PI/12) {
				gun.add(new FixedBullet(x+5,y+5,2*Math.cos(i),2*Math.sin(i)));
			}
		}
		// rotating circle shot
		if (Math.random() < 0.001) {
			double maxr = Math.random()*40+80; // we want to randomly generate our max rotation
			for (double i=0; i < Math.PI * 2; i+= Math.PI/2) {
				gun.add(new RotateBullet(x+5,y+5,1,2,maxr,i,Math.PI/250));
			}
		}
		// a row of 3 shot (we made it intersect with each other, so it might be easier for the person to dodge)
		if (Math.random() < 0.003) {
			int width = x;
			for (int i=0; i < 3; i++) {
				gun.add(new Bullet(width,y+5)); // shoots out three bullets
				width += 30; // increases the bullet width 
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
	// loses hp
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
	public void reset() {
		hp = maxHP;
		lives = maxLives;
	}
	// GETTING VALS
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getLives() {
		return lives;
	}
	public Rectangle getRect() {
		return new Rectangle(x,y,img.getWidth(),img.getHeight());
	}
	public Rectangle getHealthBar() {
		return new Rectangle(318,34,(int)(ratio*hpWidth),12);	
	}
	public ArrayList<Bullet> getGun() {
		return gun;
	}
	public BufferedImage getPic() {
		return img;
	}
	public BufferedImage getBulletPic() {
		return bulletPic;
	}
	// SETTING VALUES
	public void setPic(String pic) {
		loadPic(pic);
	}
}