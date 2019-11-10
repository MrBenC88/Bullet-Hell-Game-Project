/**

 * @(#)MainGame.java
 *
 *
 * @author 
 * @version 1.00 2015/4/25
 */

import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;

public class MainGame extends JFrame implements ActionListener{
	javax.swing.Timer myTimer;   
	GamePanel game;
	//class for the top menu bar, the borderlayout, buttons, j-slider, music. defines them all and sets timer.
	JMenuBar menuTopBar;
	JMenuItem startGame,theMenu,instructions,completedGame, about, credits, off;
	JMenu menu,menu2,menu3;
	JMenuItem menuItem;
    JButton cButton = new JButton("Center");
    JSlider js;
    
    public MainGame() {
		super("Project Twilight");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900,700);//x: 0,600
						 //y: 50,610
		setVisible(true);	
		game = new GamePanel();
		add(game);
		setLayout(new BorderLayout());	
		//create the Menu; this stores two Menu items as Info and About
		menu = new JMenu("Info");
		menu2 = new JMenu("About");
		menu3 = new JMenu("Music");
		js = new JSlider();
		menu3.add(js);	
		//Create Menu Items
		startGame = new JMenuItem("Start Game");
		theMenu = new JMenuItem("Menu");
		instructions = new JMenuItem("How to Play");
		completedGame = new JMenuItem("Quit");
		about = new JMenuItem("Story");
		credits = new JMenuItem("Credits");
		off = new JMenuItem("Disable");
		
		//Add the Addlistener to initalize the response on click
		instructions.addActionListener(this);
		completedGame.addActionListener(this);
		about.addActionListener(this);
		credits.addActionListener(this);
		startGame.addActionListener(this);
		theMenu.addActionListener(this);
		off.addActionListener(this);
		
		//Add the following items to the specified menu
		menu.add(startGame);
		menu.add(theMenu);
		menu.add(instructions);
		menu.add(completedGame);
		menu2.add(about);
		menu2.add(credits);
		menu3.add(off);
		
		//This adds the menus onto the top bar
		menuTopBar = new JMenuBar();
		menuTopBar.add(menu);
		menuTopBar.add(menu2);
		menuTopBar.add(menu3);
		
		//Places the Game onto the center and the menu bar on the top
		//add(game, BorderLayout.CENTER);
		add(menuTopBar,BorderLayout.NORTH);
		
		setResizable(false);
		myTimer = new javax.swing.Timer(10, this);	 // trigger every 10 ms
		myTimer.start();
    }
	

	public void actionPerformed(ActionEvent evt){
		Object source = evt.getSource();
		game.move();
		game.repaint();
	}
    public static void main(String[] arguments) {
		MainGame frame = new MainGame();		
    }
}

class GamePanel extends JPanel implements KeyListener{
	private int boxx,boxy;
	private boolean []keys;
	private Image back, character;
	public String moveImg="userstand.png";
	private MainGame mainFrame;
	private int menuOptionSelect, gunTimeInterval, gunCountDown, barx,bary,mx,my,lives, gunX, gunY,speedCoolDown, score; //gunTimeInterval is the time interval between projectiles; gunCountDown is the duration of the upgrade
	public boolean inGame = true;
	String currentScreen = "main";//Want the default screen to be the main screen
	int wait  = 0;																							
	private boolean increasedSpeed, gunActive=false,start, win, lose, hasPow, shoot=false;//increasedSpeed is a flag for determining if the ball's speed increases due to the speed upgrade
	//gunActive is another flag which determines if the user has the gun upgrade or not
	BufferedImage bulletPic = null;
	BufferedImage main = null;
	BufferedImage instructionPic = null;
	BufferedImage aboutPic = null;
	BufferedImage creditPic = null;
	BufferedImage storyPic = null;
	BufferedImage storeSelect = null;
	BufferedImage gameScreenSelect = null;
	BufferedImage instructionScreenSelect = null;
	BufferedImage exitScreenSelect = null;
	ArrayList<Gun> userGunList = new ArrayList <Gun>(); //issue with gun line arraylist
	
	public GamePanel(){
		keys = new boolean[KeyEvent.KEY_LAST+1];
		back = new ImageIcon("aatest1.jpg").getImage();
	    boxx = 170;
        boxy = 170;
        gunY = boxy;
		setSize(800,600);
        addKeyListener(this);
        try{
        	bulletPic = ImageIO.read(new File ("bullet.png"));
			main = ImageIO.read(new File ("mainmenu1.jpg"));
			//instructionPic = ImageIO.read(new File ("mainmenu1instructions.jpg"));
			creditPic = ImageIO.read(new File ("credits.png"));  
			aboutPic = ImageIO.read(new File ("story.png"));  
			//back = ImageIO.read(new File ("back.png")); //background of main game
			gameScreenSelect = ImageIO.read(new File ("mainmenu1start.png"));
			storeSelect = ImageIO.read(new File ("mainmenu1store.png"));
			instructionScreenSelect = ImageIO.read(new File ("mainmenu1instructions.png"));
			exitScreenSelect = ImageIO.read(new File ("mainmenu1quit.png"));
			
			
        }
		catch (IOException e){
		}
	}
	public void theMenu(){
		//control selections in the main menu
		if(wait > 0){//A time between key pressed events to prevent key input spam
			wait --;
		}
		if(wait == 0){
			//users use up down arrow keys to choose options
			if(keys[KeyEvent.VK_ENTER]){ //enter to select
				if(menuOptionSelect == 0){
					currentScreen = "gameON";
				}
				else if(menuOptionSelect == 1){
					currentScreen = "how2play";
				}
				else if(menuOptionSelect == 2 ){
					currentScreen = "store";
				}
				else if(menuOptionSelect == 3 ){
					System.exit(0);
				}
				
			}
			if(keys[KeyEvent.VK_DOWN]){
				menuOptionSelect ++;
				if(menuOptionSelect >= 2){
					menuOptionSelect = 2;
				}
				wait = 11;
			}
			if (keys[KeyEvent.VK_UP]){
				menuOptionSelect --;
				if(menuOptionSelect <= 0){
					menuOptionSelect = 0;
				}
				wait = 11;
			}
		}
	}
    
	public void restart(){ 
		//Method for reseting all game components and having a new game
		start  = false;
	}
	public void completedGame(){
		//method for the completed game condition
		inGame = false;
	}
	//These are methods that changes the screen to the main page if the user presses the escape key
	public void instructions(){
		//This is a method that changes the screen to the main page if the user presses the escape key
		 
		if (keys[KeyEvent.VK_ESCAPE]){
			currentScreen = "main";
		}
	}
	public void credits(){
		if (keys[KeyEvent.VK_ESCAPE]){
			currentScreen = "main";
		}
	}

	public void move(){
		//shoot= true;
		if(keys[KeyEvent.VK_RIGHT] ){
			boxx += 5;
			moveImg="userright.png";
			if(boxx>=500){
				boxx=499;
			}
		}
		if(keys[KeyEvent.VK_LEFT] ){
			boxx -= 5;
			moveImg="userleft.png";
			if(boxx<=0){
				boxx=1;
			}
		}
		if(keys[KeyEvent.VK_UP] ){
			boxy -= 5;
			moveImg="userstand.png";
			if (boxy<=50){
				boxy=51;
			}
		}
		if(keys[KeyEvent.VK_DOWN] ){
			boxy += 5;
			moveImg="userstand.png";
			if(boxy>=610){
				boxy=609;
			}
		}
		if(keys[KeyEvent.VK_Z] ){
			gunActive=true;
		}
	/*	if(keys[KeyEvent.VK_X] ){
			BOMB!
		}*/
		if(gunActive== true){
			if(shoot==true){
				for(int i = 0; i < userGunList.size(); i++){
					userGunList.get(i).shoot();
				}
				
			}				
		}
    	if(gunActive == true){
    		shoot = true;
    		System.out.println("checkpoint");
    		setGunXValue(boxx);
    		setGunYValue(boxy);
    		Gun gunR = new Gun(boxx + 40, boxy);
    		Gun gunL = new Gun(gunX,boxy); //gun on each side of paddle
    		userGunList.add(gunR);
    		userGunList.add(gunL);
    			
    		
    	}
		gunActive=false;
	}
	
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    
    public void paintComponent(Graphics g){ 	
    	character = new ImageIcon(moveImg).getImage();
    	g.drawImage(back,0,0,this);
		g.setColor(Color.blue);  
		//g.fillRect(boxx,boxy,40,40);
		g.drawImage(character,boxx,boxy,this);  
		if(shoot==true){//draw the projectile
			for(int i = 0; i < userGunList.size(); i ++){
				userGunList.get(i).shoot();
		    	g.drawImage(bulletPic,userGunList.get(i).gunX,userGunList.get(i).gunY,this);
		        System.out.println("fire~");		        	
		    }
		}
		if(menuOptionSelect == 0){//default option is on new game option
			g.drawImage(gameScreenSelect,0,0,this);
		}
		else if(menuOptionSelect == 1){//instruction selection
			g.drawImage(instructionScreenSelect,0,0,this);
		}
		else if(menuOptionSelect == 2){//instruction selection
			g.drawImage(storeSelect,0,0,this);
		}
		else if(menuOptionSelect ==3){//exit selection
			g.drawImage(exitScreenSelect,0,0,this);
		}
		
    }
    
	public void setGunXValue(int x){
		gunX = boxx;
	}
	public void setGunYValue(int y){
		gunY = boxy;
	}
	/*public void gunCollide(Gun userGun){
		// a method that checks to see if gun bullets hits brick(s)
		for(int j = 0; j < 56; j ++){
			if(!bricks[j].isDestroyed()){
				if(userGun.getRect().intersects(bricks[j].getRect())){
					userGunList.remove(userGun);
					bricks[j].setDestroyed(true);
				}
			}				
		}
		
	}*/
//==================================================================
	//MouseListener
	public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {} 
	public void mouseEntered(MouseEvent e) {}

    public void mouseClicked(MouseEvent e){
	}  

}