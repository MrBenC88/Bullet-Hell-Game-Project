/*
 * Minions Class are here to show you the movement, shooting, loading pictures and random direction
 * This class handles all the basic minion movements
 * Created by: Johnny, Yang, Ben
*/

import javax.imageio.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;

class Minions {
	// movement, hp, and damage it causes
	private int x, y;
	private int xVel, yVel; // velocity movement
	private static final int RIGHT_WALL = 610;
	private static final int LEFT_WALL = 28;
	private static final int UP_WALL = 85;
	private static final int DOWN_WALL = 450; 
	private boolean down;
	// Images & other
	private BufferedImage img;
	private Random r = new Random();
	//difficulty of minions + vel + bullets
	private ArrayList<Bullet> gun = new ArrayList<Bullet>();
	private double probShoot; // the probability of the enemy shooting	
	
	public Minions(int x, int y) {
		// all the settings in here are assumed to be normal, so if you're going ultimate berserk, expect these guys to move faster
		this.x=x;
		this.y=y;
		xVel = 2;
		yVel = 2;
		down = false;
		//------------------------
		loadPics(); // loads random pics
		
	}
	//LoadPic will randomly load pictures from each different minion
	private void loadPics() {
		try {
			int n = r.nextInt(7);
			img = ImageIO.read(new File("game/minion"+n+".png")); // this will randomize each minion character
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}
	//------THE BIG STUFF----------
	private void setD() {
	    double speed = 2.0; // might change
	    double direction = Math.random() * 2 * Math.PI; // goes in a complete opposite direction (in a unit circle)
	    xVel = (int) (speed * Math.cos(direction)); // adjusts the speed
	    yVel = (int) (speed * Math.sin(direction)); // adjusts how fast it's going up and down
	}
	public void move() {
		x += xVel;
		if (!down) {
			y -= yVel;
		}
		else {
			y += yVel;
		}
		if (y > DOWN_WALL) {
			down = true;
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
	public void addShot() {
		if (Math.random() < probShoot) { // 1% chance of shooting
			gun.add(new Bullet(x+5,y+5));
		}
	}
	public void shoot() {
		if (!gun.isEmpty()) {
			for (Bullet b : gun) {
				b.shoot(false); // false because it's a cpu
			}
		}
	}
	// removes bullet if y > w.e
	public void checkBullet() {
		for (int i=0; i < gun.size(); i++) {
			if (gun.get(i).getY() > DOWN_WALL+200) {//we've added 200 for checking at the bottom
				gun.remove(i);
			}
		}
	}
	//--------------------------------------
	//GETTING VALUES
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Rectangle getRect() {
		return new Rectangle(x,y,img.getWidth(),img.getHeight());
	}
	public BufferedImage getImg() {
		return img;
	}
	public ArrayList<Bullet> getGun() {
		return gun;
	}
	//----------------------------------
	public void setProb(double num) {
		probShoot = num; // sets the probability
	}
}