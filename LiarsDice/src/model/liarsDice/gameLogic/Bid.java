package model.liarsDice.gameLogic;

/**
 * The Bid class holds the die number and the frequency of a bid.  For example: two 3's or seven 5's.
 */
public class Bid implements Decision {
	private int faceValue, frequency;
	
	/**
	 * Constructor
	 * @param frequency The number of dice in the bid.
	 * @param faceValue The die face value of this bid.
	 */
	public Bid(int frequency, int faceValue){
		this.frequency = frequency;
		this.faceValue = faceValue;
	}
	
	/**
	 * @return The die face value of this bid.
	 */
	public int getFaceValue(){
		return faceValue;
	}

	/**
	 * @return The number of dice in the bid.
	 */
	public int getFrequency() {
		return frequency;
	}
	
	/**
	 * @return A string representation of the bid.
	 */
	public String toString(){
		return "Bid " + frequency + " " + faceValue + "s";
	}
}
