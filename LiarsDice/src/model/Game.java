package model;


/**
 * Generic version of a Game class. Each game added to this project will implement this 
 * interface with that game's specific logic.  The constructor should initialize all the players.
 * This is intended to be a throwaway class: create an new instance with the players for that 
 * specific game each time you want to play a game.  (This reduces the chance for persistence bugs.)
 */
public interface Game {
	
	/**
	 * The method called by the tournament when it wants to run the game.
	 * @return The Player who won that game.
	 */
	Player runGame();
	
	/**
	 * Sets the limit on turn times for each bot.  (If a player goes over that time, they lose the round/game.)
	 * @param microsecBeforeTimeout Number of microseconds allowed for each player to take a turn.
	 */
	void setTimeout(long microsecBeforeTimeout);
}
