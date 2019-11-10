import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class test extends JFrame implements ActionListener {
	GamePanel game;
	javax.swing.Timer myTimer;
	
	public test() {
		super("test");
		setSize(900,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game = new GamePanel();
		add(game);
		setVisible(true);
		setResizable(false);
		myTimer = new javax.swing.Timer(10,this);
		myTimer.start();
	}
	public void actionPerformed(ActionEvent evt) {
		game.repaint();
	}
	public static void main(String[] args) {
		new test();
	}
}

class GamePanel extends JPanel implements MouseListener, MouseMotionListener {
	private int mx,my;
	BufferedImage pic, pic2;
	
	public GamePanel() {
		setFocusable(true);
		setSize(900,700);
		setVisible(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		try {
			pic = ImageIO.read(new File("game/gameover0.jpg"));
			//pic2 = ImageIO.read(new File("game/highscore.png"));
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}
	public void mouseMoved(MouseEvent e){ mx = e.getX(); my = e.getY();}
	public void mouseDragged(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseClicked(MouseEvent e) {
		System.out.println("("+mx+","+my+")");
	}
	public void mousePressed(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	
	public void paintComponent(Graphics g) {
		g.drawImage(pic,0,0,this);
	}
}