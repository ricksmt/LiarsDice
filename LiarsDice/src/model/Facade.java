package model;

import java.util.List;

import model.liarsDice.LiarsDiceGameFactory;



/**
 * This class serves as an interface for the view.  The view classes only need to call methods on this class.  
 * They don't need to rely on other classes in the model.
 */
public class Facade {
	private Tournament tournament;
	
	/**
	 * Constructor.
	 */
	public Facade()
	{
		//default to Liar's Dice as the game choice
        changeGame("LiarsDice");
	}

	/**
	 * @return A list of all players which can be used in the tournament.
	 */
	public List<Player> getPlayers() {
		return tournament.getPlayers();
	}

	/**
	 *  @return A list of the players currently used in the tournament.
	 */
	public List<Player> getParticipants() {
		return tournament.getParticipatingPlayers();
	}
	
	/**
	 * Changes which game the tournament should be running.
	 * @param gameName Name of the game which should be changed to.
	 */
	public void changeGame(String gameName){
		tournament = new Tournament(chooseGameFactory(gameName));
	}

	/**
	 * Chooses a GameFactory based on the name of the game.
	 * @param gameName Name of the associated game.
	 * @return The GameFactory specific to the game name.
	 */
	private GameFactory chooseGameFactory(String gameName) {
		//When adding in new games, expand this switch statement:
		switch(gameName){ //note: switching on a string will not work on anything before Java 1.7
			case "LiarsDice":
				return new LiarsDiceGameFactory();
			default:
				return new LiarsDiceGameFactory();
		}
	}

	/**
	 * Runs a tournament with the given constraints.
	 * @param botsPerGame How many bots will participate in each game.
	 * @param gameRepeats How many times to repeat each game.
	 */
	public void runTournament(int botsPerGame, int gameRepeats) {
		tournament.runTournament(botsPerGame, gameRepeats);
	}

	public Player runGame(String gameName, List<Player> players, long microsecBeforeTimeout) {
		GameFactory gameFactory = chooseGameFactory(gameName);
		Game game = gameFactory.getGameInstance(players);
		game.setTimeout(microsecBeforeTimeout);
		Player winner = game.runGame();
		return winner;
	}

	public Game getGame(String gameName, List<Player> players, long microsecBeforeTimeout) {
		GameFactory gameFactory = chooseGameFactory(gameName);
		Game game = gameFactory.getGameInstance(players);
		game.setTimeout(microsecBeforeTimeout);
		return game;
	}
	
	/**
	 * Sets the allowed length of time each bot will have to take a turn.
	 * @param microsecBeforeTimeout Time (in milliseconds) allowed for each bot's turn.
	 */
	public void setTimeout(long microsecBeforeTimeout) {
		tournament.setTimeout(microsecBeforeTimeout);
	}

	/**
	 * Adds or removes a player from the collection of players which will participate in the tournament.
	 * @param b true if the player should be added, false if the player should be removed.
	 * @param index The index (with respect to all players) of the player to be added to or removed from the collection of participating players.
	 */
	public void addOrRemovePlayer(Boolean b, int index) {
		if(b){
			tournament.addPlayer(index);
		}
		else{
			tournament.removePlayer(index);
		}
	}

	/**
	 * Resets statistics for every player.
	 */
	public void resetPlayerStats() {
		tournament.resetPlayerStats();		
	}
	
	/**
	 * Calculates how many games will be run with given tournament settings.
	 * @param botsPerGame The number of players allowed in each game.
	 * @param gameRepeats The number of times a tournament will repeat all games.
	 * @return The number of games that will be played when the tournament is run.
	 */
	public int numGamesForSettings(int botsPerGame, int gameRepeats) {
		return tournament.getNumGamesForSettings(botsPerGame, gameRepeats);
	}
	
	/**
	 * Gives access to the decision to run all permutations or to run all 
	 * combinations with randomized turn order.
	 * @param choice True means all combinations, false means all permutations. 
	 */
	public void setCombosVsPermutations(boolean choice) {
		tournament.setCombosVsPermutations(choice);
	}

}
