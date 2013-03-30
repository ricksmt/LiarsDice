package model;
import java.util.List;


/**
 * Generic GameFactory class which all specific game factory classes should implement.
 * (This way, the Tournament doesn't need to know what type of game it is running.)
 */
public interface GameFactory {
	
	/**
	 * Returns an instance of the game this factory is meant for.
	 * @param players List of players to be included in the current game.
	 * @return An instance of the game initialized with the given players.
	 */
	Game getGameInstance(List<Player> players);
	
	/**
	 * @return The name of the game this factory produces.
	 */
	String getGameName();
	
	/**
	 * This method can either be hardcoded (if all bot implementation names are known), 
	 * or can use reflection to find the class names in a certain folder.
	 * @return List of all bot implementations which can be used in the tournament.
	 */
	List<Player> getPlayers();
	
}
