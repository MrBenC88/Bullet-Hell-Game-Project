import java.util.*;
import java.io.*;

public class test2 {
	public static void main(String [] args) {
		ArrayList<Minions> n = new ArrayList<Minions>();
		n.add(new Minions(32,53));
		n.add(new Minions(332,50));
		n.add(new Minions(312,52));
		n.get(0).getGun().add(new Bullet(53,23));
		ArrayList<Bullet> shot = n.get(0).getGun();
		if (n.get(0).getGun().equals(shot)) {
			System.out.println("TRUE");
		}
	}
}