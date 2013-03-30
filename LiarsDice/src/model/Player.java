package model;



/**
 * Abstract class which represents a generic Player - should be usable for any game implementation.
 * Keeps track of a unique ID as well as its tournament statistics.
 */
public abstract class Player implements Comparable {
	private int id;
	protected Statistics stats;

	/**
	 * Constructor.
	 * @param id The unique ID this Player will use for the tournament.
	 */
	public Player(int id){
		this.id = id;
		stats = new Statistics();
	}

	/**
	 * @return This player's unique ID.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * @return The chosen name of the player.
	 */
	public abstract String getName();
	
	/**
	 * @return This player's current tournament statistics.
	 */
	public Statistics getStatistics() {
		return stats;
	}
	
	/**
	 * Overrides the default compareTo. Compares players based on number of wins (more wins gets higher priority).
	 */
	public int compareTo(Object o){
		if(stats.getWins() > ((Player)o).getStatistics().getWins()){
			return -1;
		}
		return 1;
	}

	/**
	 * Resets this player's statistics to zero.
	 */
	public void resetStatistics() {
		stats = new Statistics();
	}
}
