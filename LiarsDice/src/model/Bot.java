package model;

/**
 * Generic interface for a bot - each game will have a specific version of this which contestants will implement/extend.
 */
public interface Bot {
	/**
	 * @return A name for your bot. Have fun, but keep it G-rated. (Hopefully that's not an issue here at BYU.)
	 */
	String getName();
}
