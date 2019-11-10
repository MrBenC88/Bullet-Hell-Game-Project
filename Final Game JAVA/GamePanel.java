/*
 * GamePanel.java
 * Created by: Johnny, Ben, Yang
 * This is where all the game mechanics happens. We felt that we needed to separate menu and game apart so that it is much more cleaner
 * and it will help us be much more organized.
 * The gamepanel handles the collisions, movements, enemy AI and drawing. It takes all the classes/functions and puts them into one. We've included a bg image
 * as well as a bullet pic for the user and enemy.
*/

import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.*;
import javax.sound.sampled.AudioSystem;

class GamePanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener {
	// IMAGES/BACKGROUNDS/GRAPHICS
	private BufferedImage gameBG, modemainBG, gameoverBG,gameoverBG1,gameoverBG2,hpBar,highscore,hs,hs1,hs2;
	private BufferedImage[] modeBG = new BufferedImage[3];
	private BufferedImage[] selBG = new BufferedImage[4];
	private BufferedImage userBulletPic, minionBulletPic, heart, bomb;
	private String bulletSel = "userbullet0.png"; // bullet for user
	private Font statsFont, livesFont, scoreFont;
	// PLAYER INFORMATION
	private Player ply;
	private boolean[] keys;
	// MOUSE + TIMER(Timer is used for 3 sec delay)
	private int mx,my,time;
	// game screen
	private String screen;
	// hitbox screen for Difficulty selection
	private Rectangle[] modeRect, selRect;
	private String[] modeText; // shows between the difficulty
	private String mode; // default
	//levels, minions, sub-bosses and bosses
	private ArrayList<Integer> nMinions = new ArrayList<Integer>(); // number of minions
	private ArrayList<Minions> baddies = new ArrayList<Minions>(); // Minions inside
	private ArrayList<Boolean> minCollide = new ArrayList<Boolean>(); // check who's collide
	private ArrayList<MiniBoss> mBoss = new ArrayList<MiniBoss>();
	private ArrayList<Boss> actualBoss = new ArrayList<Boss>(); // our total boss
	private int stage, stageMax, willSpawn; // the stage/level. (up to 10)
	// MUSIC & OTHER
	private Random rand = new Random(); // random variable for rand range
	private	AudioClip gunSound, bossMusic, badMusic, mBossMusic; // this variable makes a sound when you hit an enemy (minions, sub bosses and bosses)
	// SCORE LIST
	private ArrayList<Score> scoreList = new ArrayList<Score>();
	private String name;
	
	public GamePanel() {
		// Images & Font
		try {
			gameBG = ImageIO.read(new File("game/inGameScreen.jpg")); // background
			modemainBG = ImageIO.read(new File("game/diffmain.jpg"));
			gameoverBG = ImageIO.read(new File("game/gameover0.jpg"));
			gameoverBG1 = ImageIO.read(new File("game/gameover1.jpg"));
			gameoverBG2 = ImageIO.read(new File("game/gameover2.jpg"));
			highscore = ImageIO.read(new File("game/highscore.png"));
			hs = ImageIO.read(new File("game/hs.jpg"));
			hs1 = ImageIO.read(new File("game/hsnew.jpg"));
			hs2 = ImageIO.read(new File("game/hsmain.jpg"));
			// Health bar for sub boss and mini boss
			hpBar = ImageIO.read(new File("game/hpbar.png"));
			for (int i=0; i < modeBG.length; i++) {
				modeBG[i] = ImageIO.read(new File("game/diff"+(i+1)+".jpg"));
			}
			for (int i=0; i < selBG.length; i++) {
				selBG[i] = ImageIO.read(new File("game/characterselect"+i+".jpg"));
			}
			minionBulletPic = ImageIO.read(new File("game/enemybullet.png"));
			heart = ImageIO.read(new File("game/heart.png"));
			bomb = ImageIO.read(new File("game/bomb.png"));
			statsFont = Font.createFont(Font.TRUETYPE_FONT, new File("batmanforeveralternate.ttf")).deriveFont(25F); // set it to 25px
			livesFont = statsFont.deriveFont(25F);
			scoreFont = statsFont.deriveFont(20F);
		}
		catch(IOException | FontFormatException ex){
			System.out.println(ex);
		}
		// bullet image loading
		loadBullet(bulletSel);// loads bullet default but will check later on
		// Keyboard Interactions
		addKeyListener(this);
		keys = new boolean[KeyEvent.KEY_LAST+1];
		// Mouse Interactions
		addMouseMotionListener(this);
   		addMouseListener(this);
		// Characters Movement and Picture selection
		ply = new Player(285,610,3); // x,y,bombs
		selRect = new Rectangle[] {new Rectangle(305,286,270,377),new Rectangle(519,144,356,421),new Rectangle(23,137,262,362)};
		// diffculty selection
		modeRect = new Rectangle[] {new Rectangle(352,457,217,27),new Rectangle(343,499,237,24),new Rectangle(387,569,136,22)};
		modeText = new String[] {"normal","berserk"};
		// Level Initalizations, Minion Initalizations, Bosses and Sub bosses.
		loadFile("levels.txt"); // we load up the level screen(it will also load subbosses and bosses)
		// MINIONS
		stage = 0;
		time = 300; // this is the time
		loadMinions(nMinions.get(stage)); // loads at what current stage it is in
		screen = "mode";
		// SOUNDS & MUSIC
		gunSound = Applet.newAudioClip(getClass().getResource("sound/gunhit.wav"));
		badMusic = Applet.newAudioClip(getClass().getResource("sound/minionmusic.wav"));
		bossMusic = Applet.newAudioClip(getClass().getResource("sound/bossmusic.wav"));
		// SCORES
		loadScores("highscore.txt");
	}
	private void loadScores(String file) {
		Scanner infile = null;
		try {
			infile = new Scanner(new File(file));
			while (infile.hasNextLine()) {
				String[] hs = infile.nextLine().split(",");
				scoreList.add(new Score(hs[0],Integer.parseInt(hs[1])));
			}
		}
		catch (IOException ex) {
			System.out.println(ex);
		}
	}
	// this writes an existing, and then updates
	private void updateScore(String file) {
		PrintWriter outfile;
		if (checkScore()) { // checks if it's helped
			scoreList.add(new Score(name, ply.getScore()));
			Collections.sort(scoreList); // sorts it from acsending order
			Collections.reverse(scoreList); // sorts it back from descending order instead
			scoreList.remove(scoreList.size()-1); // we want to remove the last one so it says top 10 scores
			try {
				outfile = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				for (Score s : scoreList) {
					outfile.println(s.getName() + "," + s.getScore());
				}
				outfile.close();
			}
			catch(IOException ex) {
				System.out.println(ex);
			}
		}
	}
	private void loadBullet(String file) {
		try {
			userBulletPic = ImageIO.read(new File("game/"+file));
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}
	private void loadFile(String file) { // loads any specific file
		Scanner infile = null;
		try {
			infile = new Scanner(new File(file));
			stageMax = Integer.parseInt(infile.nextLine());
			for (int i=0; i < stageMax; i++) {
				String[] stats = infile.nextLine().split(","); // split the commas so we can decide on what to add for each class
				nMinions.add(Integer.parseInt(stats[0])); // minion arraylist easier for us
				mBoss.add(new MiniBoss(randRange(250,300),300,Integer.parseInt(stats[1]),Integer.parseInt(stats[2]))); // added through the constructor
				mBoss.get(i).setPic("subboss"+Integer.toString(i+1)+".png"); // loads picture from 1 to 10
				// boss
				actualBoss.add(new Boss(275,300,Integer.parseInt(stats[3]),Integer.parseInt(stats[4]))); 
				actualBoss.get(i).setPic("boss"+Integer.toString(i+1)+".png");
			}
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}
	// this is to load minions, this will help on each level too
	public void loadMinions(int n) {
		baddies.clear();
		minCollide.clear();	
		for (int i=0; i < n; i++) {
			// random x,y locations near the top
			int x = randRange(31,600);
			int y = randRange(-10,5);
			baddies.add(new Minions(x,y));
			minCollide.add(false); // at the default, all collisions are false
		}
	}
	// gives us the ability to get a range of random integers.
	public int randRange(int min, int max) {
		return rand.nextInt((max-min) + 1) + min;
	}
	public String getScreen() {
		return screen;
	}
	public void resetScreen() {
		screen ="mode";
	}
	// this function checks score is in the high score list
	public boolean checkScore() {
		return ply.getScore() >= scoreList.get(scoreList.size()-1).getScore();
	}
	// gives us a bullet pic
	public void setBullet(String s) {
		bulletSel = s;
	}
	public void resetPos() {
		ply.getBullet().clear();
		ply.setX(285);
		ply.setY(610);
	}
	public void resetGame() {
		resetPos();
		ply.reset();
		// we don't have to set anything other because they are in the files instead
		stage = 0; 
		time = 300;
		// reloads minions, sub boss and boss
		loadMinions(nMinions.get(stage));
		for (int i=0; i < stageMax; i++) {
			mBoss.get(i).reset();
			actualBoss.get(i).reset();
		}
	}
	// this function will determine the difficulty
	public void setMode(String s) {
		// Set the difficulty for minions
		// setProb determines the probability of a minion shooting
		if (s.equals("normal")) {
			for (Minions m : baddies) {
				m.setProb(0.005); // very low haha
			}
			willSpawn = 10;
		}
		else if (s.equals("berserk")) {
			for (Minions m : baddies) {
				m.setProb(0.01); // a little bit higher, but still harder
			}
			willSpawn = 12;
		}
	}
	// does everything
	public void run() {
		if (screen.equals("miniongame")) {
			// PLAYERS MOVEMENT & SHOOTING
			ply.move(keys);
			ply.shoot();
			ply.bombAttack(baddies);
			// MINIONS MOVEMENT & SHOOTING!
			doAllMinion();
			// PLAYER & MINION COLLISION!
			playerCollision(); 
			// CHECKS BULLET COLLISION AT THE WALL
			ply.checkBullet();
			if (baddies.isEmpty()) {
				badMusic.stop();
				bossMusic.loop();
				resetPos();
				screen = "miniboss";
			} 
		}
		else if (screen.equals("miniboss")) {
			// This is a 3 second delay, so that the boss and the player can get set up and be positioned
			if (time > 0) {
				time--;
			}
			else if (time <= 0) {
				// PLAYERS MOVEMENT & SHOOTING
				ply.move(keys);
				ply.shoot();
				ply.bombAttack(mBoss.get(stage));
				// Collision for shooting
				mBossHitPlayer();
				doAllSubBoss();
				// CHECKS BULLET COLLISION AT THE WALL
				ply.checkBullet();
				// Regenerates HP if you don't hit it
				mBoss.get(stage).regenHP();
			}
			// if the mini boss is dead, we can go to the next stage
			if (mBoss.get(stage).isDead()) {
				ply.addBomb();
				ply.addScore(randRange(300,700)); // score range
				time = 300;
				resetPos();
				screen = "boss";
			}
		}
		else if (screen.equals("boss")) {
			// This is a 3 second delay, so that the boss and the player can get set up and be positioned
			if (time > 0) {
				time--;
			}
			else if (time <= 0) {
				// PLAYERS MOVEMENT & SHOOTING
				ply.move(keys);
				ply.shoot();
				ply.bombAttack(actualBoss.get(stage));
				// Collision
				bossHitPlayer();
				doAllBoss();
				ply.checkBullet();
				// Regenerates HP if you don't hit within' a certain amount of time
				actualBoss.get(stage).regenHP();
			}
			// now we check if the boss is dead
			if (actualBoss.get(stage).isDead()) {
				bossMusic.stop();
				badMusic.loop();
				ply.addBomb();
				ply.gainBombDmg();// increases bomb dmg by 100
				ply.addScore(randRange(750,1250));
				time = 300;// resets value for next time
				resetPos();
				stage++; // you move onto the next stage!
				// Checks if you've won the stage, before continuing
				if (isWinner()) {
					if (checkScore()) {
						name = JOptionPane.showInputDialog("Name:");
						updateScore("highscore.txt");
					}
					screen = "gamewin";
					bossMusic.stop();
	    			badMusic.stop();
				}
				else {
					// MINION INCREASES
					loadMinions(nMinions.get(stage)); // loads the minion for the next stage
					setMode(mode); // resets the probability of shooting
					willSpawn += 2; // increases # of spawn 
					screen = "miniongame";
				}
			}
		}
		// checks if player is dead before continuing the game
		if (!screen.equals("gameover")) {
	    	if (ply.isDead()) {
				if (checkScore()) {
					name = JOptionPane.showInputDialog("Name:");
					updateScore("highscore.txt");
				}
				bossMusic.stop();
    			badMusic.stop();	
				screen = "gameover";
			}
		}
	}
	// overloading methods so we can use the 2nd parameter freely
	public boolean hit(Bullet b, Minions bad) {
		return b.getRect().intersects(bad.getRect());
	}
	// can only use for minions
	public boolean hit(Bullet b, Player p) {
		return b.getRect().intersects(p.getRect());
	}
	// checks for normal player bullets to minions
	public boolean hit(Bullet b, MiniBoss mBoss) {
		return b.getRect().intersects(mBoss.getRect());
	}
	public boolean hit(Bullet b, Boss boss) {
		return b.getRect().intersects(boss.getRect());
	}
	//------------------- MINION FUNCTIONS ---------------------------------------------
	// this function helps the minion move & shoot
	public void doAllMinion() {
		for (int i=0; i < baddies.size(); i++) {// 5 will spawn if difficulty is normal, else 7 will spawn!!!! (or if mod is making it spawn only the remainder)
			if (i < willSpawn) {
				baddies.get(i).move();
				if (!minCollide.get(i)) {
					baddies.get(i).addShot();
				}
				baddies.get(i).shoot();
				baddies.get(i).checkBullet(); // checks bullet here
			}
		}
	}
	public void hitGoodGuys(Player p, Map<Point,ArrayList<Bullet>> shotGrid, Map<Point,Integer> indexGrid) {
		int pgX = p.getX()/10, pgY = p.getY()/10; //these are grid pts for the player
		for (int gx=pgX-1; gx < pgX+4; gx++) { // we check in 4 instead
			for (int gy=pgY-1; gy < pgY+2; gy++) { // 4x4
				Point keys = new Point(gx,gy);
				if (shotGrid.containsKey(keys)) {
					ArrayList<Bullet> shots = shotGrid.get(keys);
					int index = indexGrid.get(keys);
					for (int i=shots.size()-1; i>=0; i--) {
						if (hit(shots.get(i),p) && index != -1) {
							baddies.get(index).getGun().remove(shots.get(i));
							shots.remove(i);
							ply.loseLife(); // player loses life due to being hit by the bullet
							return;
						}
					}	
				}
			}
		}
	}
	public void hitBadGuys(Bullet b, Map<Point,ArrayList<Minions>> badGrid) {
		int bgX = b.getX()/10, bgY = b.getY()/10; // we will use these as gridX's, and gridY's to determine the bullets whereabouts.
		for (int gx=bgX-1; gx < bgX+3; gx++) { // we check through 9.
			for (int gy=bgY-1; gy < bgY+3; gy++) {
				Point keys = new Point(gx,gy);
				if (badGrid.containsKey(keys)) {
					ArrayList<Minions> guys = badGrid.get(keys);
					for (int j=guys.size()-1; j>=0; j--) {
						if (hit(b,guys.get(j))) {
							ply.getBullet().remove(b);
							minCollide.set(baddies.indexOf(guys.get(j)), true); // set the collision to true
							gunSound.play(); // plays the sound
							guys.remove(j);
							ply.addScore(randRange(10,50)); // adds a randomized score from 10-50
							return;
						}
					}
				}
			}
		}
	}
	// Collision between player's bullet & the minion itself.
	public void playerCollision() {
		Map<Point, ArrayList<Minions>> badGrid = new HashMap<Point, ArrayList<Minions>>();
		for (Minions bad : baddies) {
			int x = bad.getX()/10, y= bad.getY()/10;
			Point keys = new Point(x,y);
			if (!badGrid.containsKey(keys)) {
				badGrid.put(keys, new ArrayList<Minions>());
			}
			badGrid.get(keys).add(bad);
		}
		for (int i=ply.getBullet().size()-1; i >= 0; i--) {
			hitBadGuys(ply.getBullet().get(i),badGrid);
		}
		// Collision between the minion's bullet & the player itself
		Map<Point, ArrayList<Bullet>> shotGrid = new HashMap<Point, ArrayList<Bullet>>();
		Map<Point, Integer> indexGrid = new HashMap<Point, Integer>(); // we want to keep track of the index for the minions so we can remove the bullet.
		for (int i=0; i < baddies.size(); i++) {
			for (Bullet shots : baddies.get(i).getGun()) {
				int x = shots.getX()/10, y = shots.getY()/10;
				Point keys = new Point(x,y);
				if (!shotGrid.containsKey(keys)) {
					shotGrid.put(keys, new ArrayList<Bullet>());
					indexGrid.put(keys, 0);
				}
				shotGrid.get(keys).add(shots);
				indexGrid.put(keys, i);
			}
		}
		hitGoodGuys(ply,shotGrid,indexGrid);
	}
	// ----------------SUB BOSS FUNCTIONS----------------------------
	// this function helps do all the sub bosses' movement and shooting
	public void doAllSubBoss() {
		mBoss.get(stage).move();
		mBoss.get(stage).shoot();
		// Collision checking
		hitSubBoss();
		mBoss.get(stage).checkBullet();
	}
	// this function detects whether or not the mini boss has been hit
	public void hitSubBoss() {
		for (int i=0; i < ply.getBullet().size(); i++) {
			if (hit(ply.getBullet().get(i),mBoss.get(stage))) {
				ply.getBullet().remove(i);
				gunSound.play();
				mBoss.get(stage).loseHP(ply.getDmg());
			}
		}
	}
	// this takes care of player getting hit from either sub boss(mini boss) or the actual boss)
	public void	mBossHitPlayer() {
		for (int i=0; i < mBoss.get(stage).getGun().size(); i++) {
			if (hit(mBoss.get(stage).getGun().get(i),ply)) {
				mBoss.get(stage).getGun().remove(i); // removes bullet after contact
				ply.loseLife();
			}
		}
	}
	//-------------BOSS--------------------------
	// this function compiles all the boss functions into one
	public void doAllBoss() {
		actualBoss.get(stage).move();
		actualBoss.get(stage).shoot();
		// Collision check
		hitBoss();
		actualBoss.get(stage).checkBullet();
	}
	// checks if the player's bullet hits the boss
	public void hitBoss() {
		for (int i=0; i < ply.getBullet().size(); i++) {
			if (hit(ply.getBullet().get(i),actualBoss.get(stage))) {
				ply.getBullet().remove(i);
				gunSound.play();
				actualBoss.get(stage).loseHP(ply.getDmg());
			}
		}
	}
	// checks if the boss' bullet hits the player 
	public void bossHitPlayer() {
		for (int i=0; i < actualBoss.get(stage).getGun().size(); i++) {
			if (hit(actualBoss.get(stage).getGun().get(i),ply)) {
				actualBoss.get(stage).getGun().remove(i);
				ply.loseLife();
			}
		}
	}
	// ----------------DRAWING IN GENERAL-----------------
	public void playerDraw(Graphics g) {
		g.drawImage(ply.getPic(), ply.getX(), ply.getY(), this);
		// checks for bullet
		if (!ply.getBullet().isEmpty()) {
			for (Bullet b : ply.getBullet()) {
				g.drawImage(userBulletPic, b.getX(), b.getY(), this);
			}	
		}
		// bomb
		if (ply.getBombAct()) {
			g.drawImage(ply.getBombPic(), ply.getBX(), ply.getBY(), this);
		}
	}
	public void minionDraw(Graphics g) {
		for (int i=0; i < baddies.size(); i++) {
			if (i < willSpawn) {
				if (!minCollide.get(i)) { // this will make the minions invisible, but their bullet will still show
					g.drawImage(baddies.get(i).getImg(), baddies.get(i).getX(), baddies.get(i).getY(), this);
				}
				// we can check for bullets as well in a for loop
				if (!baddies.get(i).getGun().isEmpty()) {
					for (Bullet b : baddies.get(i).getGun()) {
						g.drawImage(minionBulletPic, b.getX(), b.getY(), this);
					}	
				}
				if (baddies.get(i).getGun().isEmpty() == true && minCollide.get(i) == true) { 
					baddies.remove(i);
					minCollide.remove(i); // we finally remove 
				}	
			}
		}
	}
	public void miniBossDraw(Graphics g) {
		// we will call this variable to organize
		MiniBoss subBoss = mBoss.get(stage);  
		//--------------------------------------
		g.drawImage(hpBar,250,10,this);
		g.setFont(livesFont);
		g.setColor(Color.white);
		g.drawString(String.format("%d", subBoss.getLives()), 279,46);
		g.setColor(Color.red);
		g.fillRect(subBoss.getHealthBar().x,subBoss.getHealthBar().y,subBoss.getHealthBar().width,subBoss.getHealthBar().height);
		g.drawImage(subBoss.getPic(),subBoss.getX(),subBoss.getY(),this);
		if (!subBoss.getGun().isEmpty()) {
			for (Bullet b : subBoss.getGun()) {
				g.drawImage(mBoss.get(stage).getBulletPic(), b.getX(), b.getY(), this);
			}
		}
		g.setFont(statsFont);
		g.setColor(Color.white);		
		if (time > 0) { // a countdown timer until you can fight the mini boss
			g.drawString(String.format("%d", time/100),750,550);
		}
		g.drawString("Sub Boss",670,530);
	}
	public void bossDraw(Graphics g) {
		Boss boss = actualBoss.get(stage);
		//-------------------------------
		g.drawImage(hpBar,250,10,this);
		g.setFont(livesFont);
		g.setColor(Color.white);
		g.drawString(String.format("%d", boss.getLives()), 279,46);
		g.setColor(Color.red);
		g.fillRect(boss.getHealthBar().x, boss.getHealthBar().y, boss.getHealthBar().width, boss.getHealthBar().height);
		g.drawImage(boss.getPic(), boss.getX(), boss.getY(), this);
		for (Bullet b : boss.getGun()) {
			g.drawImage(boss.getBulletPic(), b.getX(), b.getY(), this);
		}
		g.setFont(statsFont);
		g.setColor(Color.white);
		if (time > 0) { // a countdown timer until you can fight the mini boss
			g.drawString(String.format("%d", time/100),750,550);
		}
		// draws to indicate we're at boss level
		g.drawString("Boss",700,530);
	}
	// this function displays all the stats on the right hand side
	public void displayStats(Graphics g) {
		g.setColor(Color.white);
    	g.drawString(mode,720,170); // difficulty settings
    	g.drawImage(userBulletPic, 798,315,this);
    	g.drawString(String.format("%d",ply.getScore()),775,623); // score
    	g.drawString(String.format("Stage %d",stage+1),700,575);
		if (ply.getLives() <= 3) {
			int hx = 750;
			for (int i=0; i < ply.getLives(); i++) {
				g.drawImage(heart,hx,210,this);
				hx += heart.getWidth();
			}
		}
		else {
			g.drawImage(heart,750,210,this); 
			g.drawString(String.format("%dx",ply.getLives()),700,230);
		}
		if(ply.getBombs() <= 3) {
			int bx = 750;
			for (int i=0; i < ply.getBombs(); i++) {
				g.drawImage(bomb,bx,270,this);
				bx += bomb.getWidth();
			}
		}
		else {
			g.drawImage(bomb,750,270,this);
			g.drawString(String.format("%dx",ply.getBombs()),700,290);
		}
	}
	// this function display the high scores
	// this one is for game over screen
	public void displayScore(Graphics g) {
		g.setFont(scoreFont);
		int height = 157;
		for (int i=0; i < scoreList.size(); i++) {
			if (i%2==0) { // some colour matches lol
				g.setColor(Color.white);
			}
			else { 
				g.setColor(Color.red);
			}
			g.drawString((i+1)+". "+scoreList.get(i).getName(), 255, height);
			g.drawString(""+scoreList.get(i).getScore(), 470, height);
			height += 28; // increases for separation
		}
	}
	// tghis one takes care for the game winning screen
	public void displayScore2(Graphics g) {
		g.setFont(scoreFont);
		int height = 127;
		for (int i=0; i < scoreList.size(); i++) {
			if (i%2==0) { // colour matches lol
				g.setColor(Color.blue);
			}
			else { 
				g.setColor(Color.red);
			}
			g.drawString((i+1)+". "+scoreList.get(i).getName(), 375, height);
			g.drawString(""+scoreList.get(i).getScore(), 558, height);
			height += 28; // increases for separation
		}
	}
	//------------------------OTHER METHODS---------------------------------
	// Collision for mouse selections
	public int collideMode(Rectangle[] bounds,int mx, int my) {
		for (int i=0; i < bounds.length; i++) {
			if (bounds[i].contains(mx,my)) {
				return i;
			}
		}
		return -1;
	}
	// this method just checks if you've won the game
	public boolean isWinner() {
		return stage >= stageMax; // >= because we did stage++;
	}
	//----------------------------------------------------------------------
	// KEYLISTENER
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    // MOUSE LISTENER IS NEEDED TO FOR SHOPS AND STUFF
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}    
    public void mouseClicked(MouseEvent e) {
    	if (screen.equals("mode") && collideMode(modeRect,mx,my) != -1) {
    		int modeHit = collideMode(modeRect,mx,my);
    		if (modeHit != 2) {
    			setMode(modeText[modeHit]); // sets the mode for the game (to set others)
    			mode = modeText[modeHit]; // set the new mode so we can use to set bullet's speed
    			loadBullet(bulletSel); // this loads the new bullet now
    			screen = "selection"; // it will go directly to game
    		}
    		else if (modeHit == 2) {
    			screen = "quit";
    		}
    	}
    	else if (screen.equals("selection") && collideMode(selRect,mx,my) != -1) {
    		int charHit = collideMode(selRect,mx,my);
    		if (ply.getChar(charHit)) {
    			badMusic.loop();
	    		ply.updateFile("stats.txt"); // updates the file again
	    		screen = "miniongame";
    		}
    	}
    	else if(screen.equals("gameover")){
    		if(mx>=2 && mx<=226 && my>=92 && my<=153){
    			ply.convertMoney(); 
    			ply.writeFile("stats.txt"); 
    			resetGame();
    			screen="mode";
    		}
    		// writeFile essentially updates the stats.txt, (life will change to 5 if u bought)
    		else if(mx>=2 && mx<=229 && my>=176 && my<=235){
    			ply.convertMoney(); // converts your score to money
    			ply.writeFile("stats.txt"); // updates the file
    			resetGame();
    			screen="quit";
    		}
    	}
    	else if (screen.equals("gamewin")) {
    		if (mx > 0 && mx < 257 && my > 50 && my < 118) {
    			ply.convertMoney(); 
    			ply.writeFile("stats.txt"); 
    			resetGame();
    			screen="mode";
    		}
    		else if (mx > 0 && mx < 261 && my > 140 && my < 208) {
    			ply.convertMoney();
    			ply.writeFile("stats.txt");
    			resetGame();
    			screen="quit";
    		}
    	}
    }  
    public void mousePressed(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {
    	mx = e.getX();
    	my = e.getY();
    }
    public void mouseDragged(MouseEvent e){}
    //--DRAW--
    public void paintComponent(Graphics g) {
		g.setFont(statsFont); // default font
    	if (screen.equals("miniongame")) { // the game
        	g.drawImage(gameBG,0,0,this);
        	displayStats(g);
    		playerDraw(g);
    		minionDraw(g);
    	}
    	else if (screen.equals("miniboss")) {
    		g.drawImage(gameBG,0,0,this);
    		displayStats(g);
    		playerDraw(g);
    		miniBossDraw(g);
    	}
    	else if (screen.equals("boss")) {
    		g.drawImage(gameBG,0,0,this);
    		displayStats(g);
    		playerDraw(g);
    		bossDraw(g);
    	}
    	else if (screen.equals("mode")) { // selecting difficulty
    		g.drawImage(modemainBG,0,0,this);
    		int modeHit = collideMode(modeRect,mx,my); // helps us register numbers
    		if (modeHit != -1) {
    			g.drawImage(modeBG[modeHit],0,0,this); // highlights the one
    		}
    	}
    	// our character selection screen
    	else if (screen.equals("selection")) {
    		g.drawImage(selBG[0],0,0,this); 
	    	int charHit = collideMode(selRect,mx,my); // this is for checking the mouse boundaries with the character selection
    		if (charHit != -1) {
    			g.drawImage(selBG[charHit+1],0,0,this);	
    		}
    	}
    	// Game over screen
    	else if (screen.equals("gameover")) {
    		g.drawImage(gameoverBG,0,0,this);
    		if(mx>=2 && mx<=226 && my>=92 && my<=153) {
    			g.drawImage(gameoverBG2,0,0,this);
    		}
    		else if(mx>=2 && mx<=229 && my>=176 && my<=235) {
    			g.drawImage(gameoverBG1,0,0,this);
    		}
    		displayScore(g);
    	}
    	// if you manage to beat the game, bravo (normal is like hard, and berserk is just insane)
    	else if (screen.equals("gamewin")) {
    		g.drawImage(hs,0,0,this);
    		if (mx > 0 && mx < 257 && my > 50 && my < 118) {
    			g.drawImage(hs1,0,0,this);
    		}
    		else if (mx > 0 && mx < 261 && my > 140 && my < 208) {
    			g.drawImage(hs2,0,0,this);
    		}
    		// high scores table
    		g.drawImage(highscore,350,50,this);
    		displayScore2(g);
    	}
    }
}