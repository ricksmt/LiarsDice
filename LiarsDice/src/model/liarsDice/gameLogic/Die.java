package model.liarsDice.gameLogic;
import java.util.Random;

/**
 * Represents a single die.
 */
public class Die {
	public static final int WILD = 1;
	private int value;
	
	/**
	 * Constructor. Sets the face of this die to a random number between 1 and 6, inclusive.
	 */
	public Die(){
		rollDie();
	}
	
	/**
	 * @return The value on the face of the die. [1 - 6]
	 */
	public int getValue(){
		return value;
	}
	
	/**
	 * Sets the face of this die to a random number between 1 and 6, inclusive.
	 */
	private void rollDie(){
		Random rand = new Random();
		value = rand.nextInt(6) + 1;
	}
}
