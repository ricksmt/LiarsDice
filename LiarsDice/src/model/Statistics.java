package model;

/**
 * Keeps track of the tournament statistics of a player: number of wins and losses, 
 * number of exceptions thrown, number of invalid decisions, and number of timeouts
 */
public class Statistics {
	private int wins, losses, exceptions, invalidDecisions, timeouts;

	/**
	 * Constructor. (Sets all stats to 0.)
	 */
	public Statistics() {
		wins = 0;
		losses = 0;
		exceptions = 0;
		invalidDecisions = 0;
		timeouts = 0;
	}

	/**
	 * @return Number of wins.
	 */
	public int getWins() {
		return wins;
	}
	
	/**
	 * Increases the number of wins by one.
	 */
	public void increaseWins(){
		wins++;
	}

	/**
	 * @return Number of losses.
	 */
	public int getLosses() {
		return losses;
	}
	
	/**
	 * Increases the number of losses by one.
	 */
	public void increaseLosses(){
		losses++;
	}

	/**
	 * @return Number of exceptions.
	 */
	public int getExceptions() {
		return exceptions;
	}
	
	/**
	 * Increases the number of exceptions by one.
	 */
	public void increaseExceptions(){
		exceptions++;
	}

	/**
	 * @return Number of invalid decisions.
	 */
	public int getInvalidDecisions() {
		return invalidDecisions;
	}
	
	/**
	 * Increases the number of invalid decisions by one.
	 */
	public void increaseInvalidDecisions(){
		invalidDecisions++;
	}

	/**
	 * @return Number of timeouts.
	 */
	public int getTimeouts() {
		return timeouts;
	}
	
	/**
	 * Increases the number of timeouts by one.
	 */
	public void increaseTimeouts(){
		timeouts++;
	}
	
	/**
	 * @return String representation of all the stats.
	 */
	public String toString(){
		return "Wins: " + wins + "\tLosses: " + losses + "\tExceptions: " + exceptions 
				+ "\tInvalid Decisions: " + invalidDecisions + "\tTimeouts: " + timeouts;
	}
}
