/*
 * Store.java
 * Yang, Ben, Johnny
 * This class handles all the shop mechanics. This includes the images, the process of buying items, and it checks mouse collisions as well in here.
 * It will read files, write them and update them onto stats.txt so it can be used on Player.java
 * With a text file created, it's easy to save the progress the user has made with their character, (getting unlockables).
 */
 
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Store {
	// Store money,info and pics
	// constants
	public static final int MONEY = 0;
	public static final int SPEED = 1;
	public static final int LIVES = 2;
	public static final int ATK = 3;
	private	ArrayList<Integer> storeInfo = new ArrayList<Integer>();
	private	ArrayList<String> charBought = new ArrayList<String>();
	private	ArrayList<String> miscBought = new ArrayList<String>();
	// store pictures
	private BufferedImage store, storeStats, storeUser, storeMisc, back;
	private BufferedImage statsPic, userPic, miscMainPic, lock, sel, selHigh;
	// Store major highlights
	private BufferedImage[] charPic = new BufferedImage[3];
	private BufferedImage[] miscPic = new BufferedImage[8];
	// Store Items(Purchased, money cost) 
	private boolean[] charItems, miscItems;
	// records mouse points
	private Rectangle[] charRect, miscRect, statsRect;
	private Rectangle selRect;
	// Store Screen
	private String screen;
	
	public Store() {
		// store info
		readFile("stats.txt");
		// highlighting bar of the store
		store = loadPic("main.jpg");
		lock = loadPic("lock.png");
		storeStats = loadPic("mainstats.jpg");
		storeUser = loadPic("mainuser.jpg");
		storeMisc = loadPic("mainmisc.jpg");
		back = loadPic("mainquit.jpg");
		selHigh = loadPic("selecthighlighted.png");
		// Actual pics
		sel = loadPic("selectplain.png");
		statsPic = loadPic("statspopup.jpg");
		userPic = loadPic("userpopup.jpg");
		miscMainPic = loadPic("miscpopup.jpg");
		// highlighting of the pictures above
		charPic = loadPics(charPic.length,"char");
		miscPic = loadPics(miscPic.length,"misc");
		// Store MOUSE POINTS
		charRect = new Rectangle[] {
			new Rectangle(280,170,110,220),new Rectangle(450,170,100,220),
			new Rectangle(430,400,100,190)
		};
		miscRect = new Rectangle[] {
			new Rectangle(270,270,30,20),new Rectangle(320,270,30,20),
			new Rectangle(380,270,30,20),new Rectangle(270,190,30,35),
			new Rectangle(320,190,30,35),new Rectangle(380,190,30,35),
			new Rectangle(440,190,30,35),new Rectangle(495,190,25,35)
		};
		statsRect = new Rectangle[] {
			new Rectangle(455,195,28,26),new Rectangle(589,198,38,24), //speed dec/inc
			new Rectangle(459,264,30,27),new Rectangle(592,265,36,25), //lives dec/inc
			new Rectangle(456,337,33,31),new Rectangle(596,345,33,24) // attack dec/inc
		};
		selRect = new Rectangle(375,615,sel.getWidth(),sel.getHeight());
		//selRect = new Rectangle();
		// Store Items and Price Initalizations
		charItems = new boolean[3];
		miscItems = new boolean[8];
		// OTHER
		screen = ""; // this will be for flipping pages around the store
	}
	//----------------------- Loading Pictures -----------------------------------------
	// loading single
	private BufferedImage loadPic(String name) {
		BufferedImage pic=null;
		try {
			pic = ImageIO.read(new File("store/"+name));
		}
		catch (IOException ex) {
			System.out.println(ex);
		}
		return pic;
	}
	// loading multiple
	private BufferedImage[] loadPics(int size, String name) {
		BufferedImage[] pics = new BufferedImage[size];
		try {
			for (int i=0; i < size; i++) {
				pics[i] = ImageIO.read(new File("store/"+name+(i+1)+".jpg"));
			}	
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
		return pics;
	}
	// -----------------------------------------------------------------------------------
	public void readFile(String file) {
		// this takes care of us using the method over repeatedly.
		storeInfo.clear();
		charBought.clear();
		miscBought.clear(); 
		Scanner infile = null;
		try {
			infile = new Scanner(new File(file));
			int n = Integer.parseInt(infile.nextLine());
			for (int i=0; i < n; i++) {
				if (i >= 0 && i < 4) {
					storeInfo.add(Integer.parseInt(infile.nextLine()));
				}
				else if (i >= 4 && i < 7) {
					charBought.add(infile.nextLine());
				}
				else if (i >= 7) {
					miscBought.add(infile.nextLine());
				}
			}
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}
	// rewrites the file, essentially updating the information
	private void updateFile() {
		PrintWriter outfile;
		try {
			outfile = new PrintWriter(new BufferedWriter(new FileWriter("stats.txt")));
			outfile.println("15"); // this is the number
			for (Integer i : storeInfo) {
				outfile.println(i); // we overwrite the existing files
			}
			for (String s : charBought) {
				outfile.println(s); // overwrite the character
			}
			for (String s : miscBought) {
				outfile.println(s);
			}
			outfile.close();
		}
		catch (IOException ex) {
			System.out.println(ex);
		}
	}
	// -----------------------------------------------------------------
	// STORE PICS
	public BufferedImage getPic() {
		return store;
	}
	public BufferedImage getStatPic() {
		return storeStats;
	}
	public BufferedImage getCharPic() {
		return storeUser;
	}
	public BufferedImage getMiscPic() {
		return storeMisc;
	}
	public BufferedImage getQuitPic() {
		return back;
	}
	public BufferedImage getLockPic() {
		return lock;
	}
	public BufferedImage getSlideStatPic() {
		return statsPic;
	}
	public BufferedImage getSlideUserPic() {
		return userPic;
	}
	public BufferedImage getSlideMiscPic() {
		return miscMainPic;
	}
	public BufferedImage getSelPic() {
		return sel;
	}
	public BufferedImage getSelHighPic() {
		return selHigh;
	}
	public BufferedImage getInCharPic(int i) {
		return charPic[i];
	}
	public BufferedImage getInMiscPic(int i) {
		return miscPic[i];
	}
	//---------------------------------------------------------------------
	// getting Screen, and other stuff rather than pictures
	public String getScreen() {
		return screen;
	}
	// Store info (money(kp), stats, characters)
	public int getInfo(int i) {
		return storeInfo.get(i);
	}
	public Rectangle[] getCharRect() {
		return charRect;
	}
	public Rectangle[] getMiscRect() {
		return miscRect;
	}
	public Rectangle[] getStatRect() {
		return statsRect;
	}
	public ArrayList<String> getCharBought() {
		return charBought;
	}
	public ArrayList<String> getMiscBought() {
		return miscBought;
	}
	public boolean getBought(ArrayList<String> itemBought, int i) {
		if (itemBought.get(i).equals("false")) {
			return false;
		}
		return true;
	}
	// if mouse contains inside, then we can use for any rectangle[](only for misc, and char)
	public int getHit(Rectangle[] bound, int mx, int my) {
		 for (int i=0; i < bound.length; i++) {
		 	if (bound[i].contains(mx,my)) {
		 		return i;
		 	}
		 }
		 return -1;
	}
	public boolean getSelHit(int mx, int my) {
		if(selRect.contains(mx,my)) {
			return true;
		}
		return false;
	}
	// Setting Values
	public void setScreen(String s) {
		screen = s;
	}
	// purchasing things
	public void buy(String type, int n) {
		if (type.equals("char")) { // for purchasing character
			if ((storeInfo.get(MONEY)-20000) >= 0) { // takes care of negative values
				storeInfo.set(MONEY, storeInfo.get(MONEY)-20000); // we remove money
				charBought.set(n, "true"); // we set this value to true because the item has been purchased
			}
		}
		else if (type.equals("misc")) { // for purchasing bullets (helps for comestics and probably a bigger hitbox lol)
			if ((storeInfo.get(MONEY)-5000) >= 0) {
				storeInfo.set(MONEY, storeInfo.get(MONEY)-5000);
				miscBought.set(n, "true"); // same concept as above.
			}
		}
		else if (type.equals("stats")) {
			if ((storeInfo.get(MONEY)-5000) >= 0) { // makes sure no negative values
			//-----------------------SPEED------------------------------------------
				if (n == 1 && storeInfo.get(SPEED) < 10) {
					storeInfo.set(SPEED, storeInfo.get(SPEED)+1);
					storeInfo.set(MONEY, storeInfo.get(MONEY)-5000);// pays up 5000
				}
				else if (n == 3 && storeInfo.get(LIVES) < 25) { // lives increase
					storeInfo.set(LIVES, storeInfo.get(LIVES)+1);
					storeInfo.set(MONEY, storeInfo.get(MONEY)-5000);
				}
				else if (n == 5 && storeInfo.get(ATK) < 50) {
					storeInfo.set(ATK, storeInfo.get(ATK)+1);
					storeInfo.set(MONEY, storeInfo.get(MONEY)-5000);
				}
			}
			//----------------------------------------------------------------------
			if (n == 0 && storeInfo.get(SPEED) > 1) { // speed decrease
				storeInfo.set(SPEED, storeInfo.get(SPEED)-1); // by 1
				storeInfo.set(MONEY, storeInfo.get(MONEY)+2500);// you only gain half
			}
			//----------------------------------------------------------------------
			else if (n == 2 && storeInfo.get(LIVES) > 1) { // lives decrease
				storeInfo.set(LIVES, storeInfo.get(LIVES)-1);
				storeInfo.set(MONEY, storeInfo.get(MONEY)+2500);
			}
			//----------------------------------------------------------------------
			else if (n == 4 && storeInfo.get(ATK) > 5) {// attack decrease
				storeInfo.set(ATK, storeInfo.get(ATK)-1);
				storeInfo.set(MONEY, storeInfo.get(MONEY)+2500);
			}
		}
		updateFile(); // updates the file at the end, since this method makes you update the file
	}
}