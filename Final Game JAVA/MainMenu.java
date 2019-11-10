/* MainMenu.java
 * Created by Yang, Johnny, Ben
 * MainMenu class handles all the main menu screens. It will also handle going into the game,
 * but the game mechanics will be handled on GamePanel instead.
 * The MainPanel will include the store menu as well, but most of the store mechanics will happen in Store.java. 
 * This is also the main program you should run off of.
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

public class MainMenu extends JFrame implements ActionListener {
	javax.swing.Timer myTimer;
	MenuPanel menu;
	GamePanel game;
	AudioClip menuMusic;
	
	public MainMenu() {
		super("Project Twilight"); 
		// class of its own
		setSize(900,700);
		// Creation of menus
		menu = new MenuPanel();
		menu.setSize(900,700);
		menu.setVisible(true);
		add(menu);
		// Creation of Game Handling
		game = new GamePanel();
		game.setSize(900,700);
		add(game);
		// Other stuff
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		// Timer creation
		myTimer = new javax.swing.Timer(10, this);
		myTimer.start();
		menuMusic = Applet.newAudioClip(getClass().getResource("sound/menumusic.wav")); // menu music
   		menuMusic.loop();
	}
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if (menu.onGame()) {
			menu.setVisible(false);
			game.setVisible(true);
			game.requestFocus();
			game.run();
			if (game.getScreen().equals("quit")) {
				menuMusic.loop();
				menu.setScreen("");
				game.resetScreen();
			}
			else if (game.getScreen().equals("mode")) {
				game.setBullet(menu.bulletSelect); // sets the bullet obtained from shop
			}
			else if (game.getScreen().equals("miniongame")) {
				menuMusic.stop(); // music stop
			}
			game.repaint();
		}
		else {
			menu.setVisible(true);
			game.setVisible(false);
			menu.requestFocus();
			menu.repaint();
		}
		// Sets the title bar
		if (menu.getScreen().equals("store")) {
			setTitle("Kawaii Point SuperStore");
		}
		else if (menu.getScreen().equals("game")) {
			setTitle("Project Twilight Game");
		}
		else if (menu.getScreen().equals("help")) {
			setTitle("Project Twilight Instructions");
		}
		else if (menu.getScreen().equals("")) {
			setTitle("Project Twilight");
		}
	}
	public static void main(String[] args) {
		MainMenu start = new MainMenu();
	}
}	

class MenuPanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener {
   	// This will be our selection, it switches our pages(instructions, credits, etc)
   	// gameReady will be used for starting up GamePanel
   	private String screen;
   	// these are highlights for the main menu screen.
   	private BufferedImage menu,start,help,quit,store,instruct;
   	private int mx,my;
   	// STORE INFORMATION
   	private Store shop;
   	public String bulletSelect;
   	private int keepnum;
   	// MUSIC, SOUND FX!! WOO!!
   	AudioClip button, button2;
   	
   	public MenuPanel() {
   		// mouse
   		addMouseMotionListener(this);
   		addMouseListener(this);
   		setFocusable(true);
   		// keyboard
   		addKeyListener(this);
  	 	// pages and other games
   		screen = "";
   		// Store info
   		shop = new Store();
   		// IMAGE IO
   		try {
   			// These are highlights
   			menu = ImageIO.read(new File("menus/main.jpg"));
   			start = ImageIO.read(new File("menus/start.jpg"));
   			help = ImageIO.read(new File("menus/instruct.jpg"));
   			quit = ImageIO.read(new File("menus/quit.jpg"));
   			store = ImageIO.read(new File("menus/store.jpg"));
   			// actual pic
   			instruct = ImageIO.read(new File("menus/help.jpg"));
   		}
   		catch (IOException ex) {
   			 System.out.println(ex);
   		}
   		bulletSelect = "userbullet0.png";
		button = Applet.newAudioClip(getClass().getResource("sound/beep.wav")); // this button is for main menu
		button2 = Applet.newAudioClip(getClass().getResource("sound/beep2.wav")); // this is for shop
   	}
   	public boolean onGame() {
   		if (screen.equals("game")) {
   			return true;
   		}
   		return false;
   	}
   	public String getScreen() {
   		return screen;
   	}
   	public void setScreen(String s) {
   		screen = s;
   	}
    // MOUSE LISTENER IS NEEDED TO FOR SHOPS AND STUFF
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}    
    public void mouseClicked(MouseEvent e) {
    	// we do this so that button.play() won't spam the sound if you hold down the mouse
    	// in this section here, we are changing the menu screen selections
    	if (screen.equals("")) {
			if (mx > 363 && mx < 540 && my > 444 && my < 467) { 
				button.play();
				screen = "game";
			}
			else if (mx > 274 && mx < 630 && my > 482 && my < 508) {
				button.play();
				screen = "help";
			}
		   	else if (mx > 366 && mx < 541 && my > 524 && my < 551) {
		   		button.play();
		   		shop.readFile("stats.txt"); // reads it again, so it can update from playing the game
		   		screen = "store";
		   	}
	    	else if (mx > 387 && mx < 520 && my > 587 && my < 612) {
	    		button.play();
	    		System.exit(0);
	    	}
    	}
    	// store selection
    	else if (screen.equals("store")) {
    		if (mx > 0 && mx < 230 && my > 110 && my < 160) {
    			button2.play();
    			shop.setScreen("stats");
    		}
    		else if (mx > 0 && mx < 230 && my > 190 && my < 240) {
    			button2.play();
    			shop.setScreen("user");
    		}
    		else if (mx > 0 && mx < 230 && my > 280 && my < 330) {
    			button2.play();
    			shop.setScreen("misc");
    		}
    		else if (mx > 700 & mx < 870 && my > 620 && my < 670) {
    			button2.play();
    			shop.setScreen("");
    			screen = "";
    		}
    		// this here will check for shop buying, in stats or in the character selection(a.k.a user)
    		if (shop.getScreen().equals("stats")) {
    			if (shop.getHit(shop.getStatRect(),mx,my) != -1) {
    				int n = shop.getHit(shop.getStatRect(),mx,my);
    				button2.play();
    				shop.buy("stats", n);
    			}
    		}
    		else if (shop.getScreen().equals("user")) {
    			if (shop.getHit(shop.getCharRect(),mx,my) != -1) {
    				int n = shop.getHit(shop.getCharRect(),mx,my);
    				if (!shop.getBought(shop.getCharBought(),n)) {
   	    				button2.play();
    					shop.buy("char", n);
    				}
    			}
    		}
    		else if (shop.getScreen().equals("misc")) {
    			if (shop.getHit(shop.getMiscRect(),mx,my) != -1) {
    				int n = shop.getHit(shop.getMiscRect(),mx,my);
    				if (!shop.getBought(shop.getMiscBought(),n)) {
    					button2.play();
    					shop.buy("misc", n);
    				}
    				else {
						button2.play();
						keepnum = n;
    				}
    			}
    			// once you've selected your button, you hit the select button to confirm
				if (shop.getSelHit(mx,my)) {
					button2.play();
					bulletSelect = "userbullet"+keepnum+".png"; // selects the bullet
					System.out.println("true");
				}
    		}
    	}
    }  
    public void mousePressed(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {
    	mx = e.getX();
    	my = e.getY();
    }
    public void mouseDragged(MouseEvent e){}
    // keyboard stuff
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !screen.equals("")) { // escape button
        	screen = ""; // just goes back to main menu
        }
    }
    public void keyReleased(KeyEvent e) {}
    
    public void paintComponent(Graphics g){
    //----------------------------MAIN MENU-----------------------------------------
    // in here, we are only getting the highlighting of the screen
    	if (screen.equals("")) { // "" is main menu
    		g.drawImage(menu,0,0,this);
	    	// highlights game
	    	if (mx > 363 && mx < 540 && my > 444 && my < 467) { 
	    		g.drawImage(start,0,0,this);
	    	}
	    	// highlights instructions
	    	else if (mx > 274 && mx < 630 && my > 482 && my < 508) {
	    		g.drawImage(help,0,0,this);
	    	}
	    	// this highlights the store
	    	else if (mx > 366 && mx < 541 && my > 524 && my < 551) {
	    		g.drawImage(store,0,0,this);  	
	    	}
	    	else if (mx > 387 && mx < 520 && my > 587 && my < 612) {
	    		g.drawImage(quit,0,0,this);
	    	}
    	}
     //-----------------------------STORE--------------------------------------------
    	else if (screen.equals("store")) {
    		// new background image for store
    		g.drawImage(shop.getPic(),0,-25,this);
    		// font initalizations and colour
			g.setFont(new Font("Modern", Font.BOLD, 40));
    		g.setColor(Color.white);
    		// Highlights the stats bar
    		if (mx > 0 && mx < 230 && my > 110 && my < 160) {
    			g.drawImage(shop.getStatPic(),0,-25,this);
    		}
    		// Highlights the character bar
    		else if (mx > 0 && mx < 230 && my > 190 && my < 240) {
    			g.drawImage(shop.getCharPic(),0,-25,this);
    		}
    		// highlights the misc bar
    		else if (mx > 0 && mx < 230 && my > 280 && my < 330) {
    			g.drawImage(shop.getMiscPic(),0,-25,this);
    		}
    		// Highlights the quit bar
    		else if (mx > 700 & mx < 870 && my > 620 && my < 670) {
    			g.drawImage(shop.getQuitPic(),0,-25,this);
    		}
    		// stats side
    		if (shop.getScreen().equals("stats")) {
    			// we draw out the stat info
    			g.drawImage(shop.getSlideStatPic(),242,102,this);
				g.drawString(String.format("%d",shop.getInfo(Store.SPEED)),530,220);
				g.drawString(String.format("%d",shop.getInfo(Store.LIVES)),530,295);
				g.drawString(String.format("%d",shop.getInfo(Store.ATK)),530,370);
    		}
    		else if (shop.getScreen().equals("user")) {
    			g.drawImage(shop.getSlideUserPic(),242,102,this);
    			// this will draw the highlight values, making it much more easier on us.
    			if (shop.getHit(shop.getCharRect(),mx,my) != -1) {
    				int n = shop.getHit(shop.getCharRect(),mx,my);
    				g.drawImage(shop.getInCharPic(n),242,102,this);
    				// this checks if you've already bought the item
    				if (!shop.getBought(shop.getCharBought(),n)) {
    					g.drawImage(shop.getLockPic(),-3,345,this);
    				}
    			}
    		}
    		else if (shop.getScreen().equals("misc")) {
    			g.drawImage(shop.getSlideMiscPic(),242,102,this);
    			g.drawImage(shop.getSelPic(),375,615,this);
    			int num = shop.getHit(shop.getMiscRect(),mx,my);
    			if (num != -1) {
    				g.drawImage(shop.getInMiscPic(num),242,102,this);
					if (!shop.getBought(shop.getMiscBought(),num)) {
						g.drawImage(shop.getLockPic(),-3,345,this);
					}
    			}
    			if (shop.getSelHit(mx,my)) {
    				g.drawImage(shop.getSelHighPic(),375,615,this);
    			}
    		}
	    	// SHOP PTS!!!! (or money)
		    g.drawString(String.format("%d",shop.getInfo(Store.MONEY)),75,660);
    	}
    	//-------------------------------------------------------------------
    	else if (screen.equals("help")) {
    		g.drawImage(instruct,0,0,this);
    	}
    }
}