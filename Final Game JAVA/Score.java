/* This Score class helps us create a highscore table that allows us to match with their names
 * This was the best way as we can use Comparable to compare the score, while matching up the names,
 * from the constructor.
 * Created by Johnny, Yang, Ben
*/

import java.util.*;

class Score implements Comparable<Score> {
	private String name;
	private int score;
	
	public Score (String name, int score) {
		this.name = name;
		this.score = score;
	}
	public String getName() {
		return name;
	}
	public int getScore() {
		return score;
	}
	// this function allows us to use Collections.sort, and reverse order for us to use arraylist, annd other data structures
	public int compareTo(Score other) {
		return score - other.getScore();
	}
}