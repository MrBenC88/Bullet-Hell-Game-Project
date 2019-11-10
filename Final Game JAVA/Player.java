/* Player.java
 * Johnny, Ben, Yang
 * The Player class handles the movements, the shooting and hitbox collisions. There is another class for Bullet that is in Player. We felt this was easier, as we 
 * implemented the bullet class into Minions as well.
 * We also included a picture of the player, with possible character unlocks soon too.
*/

import java.awt.Rectangle;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

class Player {
	public static final int MONEY = 0;
	public static final int SPEED = 1;
	public static final int LIVES = 2;
	public static final int ATK = 3;
	// Movement of player
	private int x,y,score,delay,move,bomb,defLives;
	private ArrayList<Bullet> userBullet;
	private ArrayList<Integer> stats = new ArrayList<Integer>();
	private ArrayList<String> charSel = new ArrayList<String>();
	private ArrayList<String> bulletSel = new ArrayList<String>();
	// Character Variables
	private BufferedImage charPic, bombPic;
	private String moveImg, mode; // mode = difficulty
	// BOMB
	private boolean bombActive, dest;
	private int bombx,bomby,bombDmg;
	
	public Player(int x, int y, int bomb) {
		this.x = x;
		this.y = y;
		this.bomb = bomb;
		// other
		delay = 0; 
		loadFile("stats.txt"); // only loads what's on
		// we will use these variable as a way to reset the game/ start over the game
		defLives = stats.get(LIVES);
		userBullet = new ArrayList<Bullet>();
		mode = "normal"; // default setting
		// IMAGE IO
		moveImg = "user/reimu"; // default character
		move = 0;
		//bomb
		bombActive = false;
		dest = false;
		bombx = 275;
		bomby = 700; // give it behind
		bombDmg = 100;
		try {
			bombPic = ImageIO.read(new File("game/bomb.png"));	
		}
		catch(IOException ex) {System.out.println(ex);}
		loadChar(move);
	}
	private void loadChar(int dir) {
		try {
			charPic = ImageIO.read(new File(moveImg+dir+".png"));
		}
		catch (IOException ex) {
			System.out.println(ex);
		}
	}
	private void loadFile(String file) {
		Scanner infile = null;
		try {
			infile = new Scanner(new File(file));
			int n = Integer.parseInt(infile.nextLine());
			for (int i=0; i < n; i++) {
				if (i >= 0 && i < 4) {
					stats.add(Integer.parseInt(infile.nextLine())); //stats = money,speed,stuff like that
				}
				else if (i >= 4 && i < 7) {
					charSel.add(infile.nextLine());
				}
				else if (i >= 7) {
					bulletSel.add(infile.nextLine());
				}
			}
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}
	//we empty so we can load/update the file again.
	public void updateFile(String file) {
		stats.clear();
		charSel.clear();
		bulletSel.clear();
		loadFile(file);
	}
	// writes file but main method is to update the file for money to use at shop
	public void writeFile(String file) {
		Scanner infile;
		PrintWriter outfile;
		// before going in, we need to set the lives back to default
		stats.set(LIVES, defLives);
		try {
			infile = new Scanner(new File(file));
			int n = Integer.parseInt(infile.nextLine());
			outfile = new PrintWriter(new BufferedWriter(new FileWriter("stats.txt")));
			outfile.println(n); // this is the # of items to write in
			for (Integer i : stats) {
				outfile.println(i);
			}
			for (String i : charSel) {
				outfile.println(i);
			}
			for (String i : bulletSel) {
				outfile.println(i);
			}
			outfile.close();
		}
		catch (IOException ex) {
			System.out.println(ex);
		}
	}
	//---------------------------------------------------------------
	public void move(boolean[] keys) {
		if(keys[KeyEvent.VK_LEFT] ){
			x -= stats.get(SPEED);
			move = 1;
			if(x<=28){
				x=28;
			}
		}
		if(keys[KeyEvent.VK_RIGHT] ){
			x += stats.get(SPEED);
			move = 2;
			if(x>=570){
				x=569;
			}
		}
		if(keys[KeyEvent.VK_UP] ){
			y -= stats.get(SPEED);
			move = 0;
			if (y<=70) {
				y=70;
			}
		}
		if(keys[KeyEvent.VK_DOWN] ){
			y += stats.get(SPEED);
			move=0;
			if(y>=610){
				y=609;
			}
		}
		if(keys[KeyEvent.VK_Z]){
			delay++;
			if (delay > 15) { // gives us a slight delay before shooting
				userBullet.add(new Bullet(x,y)); // left side
				userBullet.add(new Bullet(x+40,y)); // right side of the person
				delay = 0; // we wanna reset the delay since it has already shot the bullet
			}
		}
		if (keys[KeyEvent.VK_X] && !bombActive) {
			if (bomb >= 0) {
				bombActive = true;
				bomb--;
			}
		}
		loadChar(move); // moves the sprite for us :)
	}
	// moves bomb to the center point of the map so it can explode
	public void bombMove() {
		if(bombx < 333){
			bombx+=1;
		}
		if(bombx > 333){
			bombx-=1;
		}
		if(bomby < 354){
			bomby+=1;
		}
		if(bomby > 354){
			bomby-=1;
		}
		if (bombx == 333 && bomby == 354) {
			dest = true; // its reached!
		}
	}
	// should clear screen for bomb
	public void bombAttack(ArrayList<Minions> bad) {
		if (bombActive) {
			bombMove();
			if (dest) {
				for (int i=0; i < bad.size(); i++) {
					if (new Rectangle(0,0,800,600).contains(bad.get(i).getX(),bad.get(i).getY())) {
						bad.remove(i);	
					}
				}
				bombActive = false;
				dest = false;
				bombx = 275;
				bomby = 700;			
			}
		}
	}
	// should clear screen for bomb
	// we've overloaded this method to use the parameters for boss instead
	public void bombAttack(MiniBoss mBoss) {
		if (bombActive) {
			bombMove();
			if (dest) {
				mBoss.loseHP(100);
				bombActive = false;
				dest = false;
				bombx = 275;
				bomby = 700;
			}
		}
	}
	// this one overloads for boss
	public void bombAttack(Boss boss) {
		if (bombActive) {
			bombMove();
			if (dest) {
				boss.loseHP(100);
				bombActive = false;
				dest = false;
				bombx = 275;
				bomby = 700;
			}
		}
	}
	// add bomb
	public void addBomb() {
		if (bomb < 3) {
			bomb++;
		}
	}
	public void gainBombDmg() {
		bombDmg += 100;
	}
	// loses a life when a collision occurs
	public void loseLife() {
		if (stats.get(LIVES) > 0) {
			stats.set(LIVES, stats.get(LIVES)-1);
		}
	}
	// this will add score, through the parameters
	public void addScore(int range) {
		score += range;
	}
	// this checks if the user has no lives.
	public boolean isDead() {
		if (stats.get(LIVES) <= 0) {
			return true;
		}
		return false;
	}
	// this method moves the bullet to shoot
	public void shoot() {
		for (int i=0; i<userBullet.size(); i++) {
			userBullet.get(i).shoot(true);
		}
	}
	// this checks for collisions on the window screen only
	public void checkBullet() {
		for (int i=0; i < userBullet.size(); i++) {
			if (userBullet.get(i).getY() < 70) {
				userBullet.remove(i);
			}
		}
	}
	// resets the whole place, back to its initial position
	public void reset() {
		bomb = 3; // default no matter what
		score = 0; // resets score
		stats.set(LIVES, 5); // resets the life, you can only buy once to risk
	}
	// converts your score into kawaii points :(
	public void convertMoney() {
		stats.set(MONEY, stats.get(MONEY)+score/2);
	}
	//-----------------------------------------------------------------------
	// Player Get values
	public Rectangle getRect() {
		return new Rectangle(x, y, charPic.getWidth(), charPic.getHeight());
	}
	public BufferedImage getPic() {
		return charPic;
	}
	public BufferedImage getBombPic() {
		return bombPic;
	}
	public int getBX() {
		return bombx;
	}
	public int getBY() {
		return bomby;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getW() {
		return charPic.getWidth();
	}
	public int getH() {
		return charPic.getHeight();
	}
	public int getLives() {
		return stats.get(LIVES);
	}
	public int getDmg() {
		return stats.get(ATK);
	}
	public int getScore() {
		return score;
	}
	public int getBombs() {
		return bomb;
	}
	public boolean getBombAct() {
		return bombActive;
	}
	// get the bullet ArrayList in player
	public ArrayList<Bullet> getBullet() {
		return userBullet;
	}
	public boolean getChar(int num) {
		if (charSel.get(num).equals("true")) {
			if (num == 0) {
				moveImg = "user/reimu";
			}
			else if (num == 2) {
				moveImg = "user/marisa";
			}
			else if (num == 1) {
				moveImg = "user/remilia";
			}
			return true;
		}
		return false;
	}
	//---------------------------------------------------------------------------------
	public void setX(int dx) {
		x = dx;
	}
	public void setY(int dy) {
		y = dy;
	}
	public void setBombDmg(int dmg) {
		bombDmg = dmg;
	}
}