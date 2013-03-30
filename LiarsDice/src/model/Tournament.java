package model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * This class runs the overall tournament.
 */
public class Tournament {
	/**
	 * gameFactory: Tournament delegates to this factory the job of creating the right kind 
	 * 				of game to run and the right kinds of players.
	 * allPlayers: Tournament keeps a master list of all players that could play.
	 * participatingPlayers: The subset of players that are selected to play in this tournament.
	 * microsecBeforeTimeout: How long this tournament allows player functions to run.
	 * combosVsPermutations: Set true if you want all possible combinations of players, 
	 * 						 with play order randomized. False will make the tournament 
	 * 						 run every possible permutation instead.
	 */
	private GameFactory gameFactory; 
	private List<Player> allPlayers, participatingPlayers;
	private long microsecBeforeTimeout;
	private boolean combosVsPermutations = true;
	
	/**
	 * Constructor. (Pass in the GameFactory associated with the game you want to play.)
	 * @param gf The specific GameFactory to be used for the tournament.
	 */
	public Tournament(GameFactory gf)
	{
		gameFactory = gf;
		allPlayers = gameFactory.getPlayers();
		participatingPlayers = new LinkedList<Player>();
		for (Player p : allPlayers) {
			participatingPlayers.add(p);
		}
	}

	/**
	 * @return List of all player implementations.
	 */
	public List<Player> getPlayers() {
		return allPlayers;
	}

	/**
	 * @return List of all player implementations competing in the tournament.
	 */
	public List<Player> getParticipatingPlayers() {
		return participatingPlayers;
	}
	
	/**
	 * Adds a player to the collection of players to be included in the tournament.
	 * @param index The index (with respect to all players) of the player to be added to the collection of participating players.
	 */
	public void addPlayer(int index){
		Player p = allPlayers.get(index);
		if(!participatingPlayers.contains(p)){
			participatingPlayers.add(p);
		}
	}
	
	/**
	 * Removes a player from the collection of players to be included in the tournament.
	 * If that player is not currently participating, this method does nothing.
	 * @param index The index (with respect to all players) of the player to be removed from the collection of participating players.
	 */
	public void removePlayer(int index){
		Player p = allPlayers.get(index);
		if(participatingPlayers.contains(p)){
			participatingPlayers.remove(p);
		}
	}

	/**
	 * Sets the limit on turn times for each bot.  (If a player goes over that time, they lose the round/game.)
	 * @param microsecBeforeTimeout Number of seconds allowed for each player to take a turn. (Can be less than 1.)
	 */
	public void setTimeout(long microsecBeforeTimeout) {
		this.microsecBeforeTimeout = microsecBeforeTimeout;
	}
	
	/**
	 * Resets all of the statistics of all players to zero.
	 */
	public void resetPlayerStats() {
		for(Player p : allPlayers){
			p.resetStatistics();
		}
	}

	/**
	 * @param botsPerGame The number of players to play in each individual game. 
	 * 					  (2 <= botsPerGame <= total # players)
	 * @param gameRepeats The number of times to repeat each 
	 * 					  combination/permutation of the tournament.
	 * @return The number of games that would be run if runTournament were 
	 * 		   run now with these parameters.
	 */
	public int getNumGamesForSettings(int botsPerGame, int gameRepeats) {
		if (botsPerGame > participatingPlayers.size())
			botsPerGame = participatingPlayers.size();
		int numGames = gameRepeats;
		for (int i=0; i<botsPerGame; i++) {
			numGames *= (participatingPlayers.size() - i);
		}
		if (combosVsPermutations) {
			for (int i=0; i<botsPerGame; i++) {
				numGames /= (botsPerGame - i);
			}
		}
		return numGames;
	}
	
	/**
	 * Runs the tournament with all the players.  Runs every possible 
	 * combination/permutation of the players (repeated as many times 
	 * as requested). This guarantees that each player will play at 
	 * least one game (per repeat) against every possible subset of the 
	 * rest of the players. A tournament will only be run if there are 
	 * at least two players.
	 * @param botsPerGame The number of players to play in each individual game. 
	 * 					  (2 <= botsPerGame <= total # players)
	 * @param gameRepeats The number of times to repeat each 
	 * 					  combination/permutation of the tournament.
	 */
	public void runTournament(int botsPerGame, int gameRepeats)
	{
		for(Player p : allPlayers){
			p.resetStatistics();
		}
		
		if(participatingPlayers.size() < 2 || gameRepeats < 1){
			return;
		}
		if(botsPerGame > participatingPlayers.size()){
			botsPerGame = participatingPlayers.size();
		}
		else if(botsPerGame < 2){
			botsPerGame = 2;
		}
		
		long start = System.currentTimeMillis();
		if (combosVsPermutations)
			runAllCombos(botsPerGame, gameRepeats);
		else {
			for(int j = 0; j < gameRepeats; j++){
				runAllPermutations(botsPerGame, new LinkedList<Player>());
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("tournament time: " + (end - start) + "ms");
	}

	/**
	 * Gives access to the decision to run all permutations or to run all 
	 * combinations with randomized turn order.
	 * @param choice True means all combinations, false means all permutations. 
	 */
	public void setCombosVsPermutations(boolean choice) {
		combosVsPermutations = choice;
	}
	
	/**
	 * Recursively runs a game for every permutation, as constrained by botsPerGame.
	 * Also updates statistics at end of each game.
	 * @param botsPerGame The number of players to play in each individual game.
	 * @param playersSoFar The players who are already included in the current game being constructed.
	 */
	private void runAllPermutations(int botsPerGame, LinkedList<Player> playersSoFar){
		for(Player current : participatingPlayers){
			if(!playersSoFar.contains(current)) {
				playersSoFar.add(current);
				if(playersSoFar.size() == botsPerGame){
					runSingleGame(playersSoFar);
				}
				else{
					runAllPermutations(botsPerGame, playersSoFar);
				}
				playersSoFar.removeLast();
			}
		}
	}
	
	/**
	 * Runs a game for every combination, as constrained by botsPerGame and gameRepeats.
	 * Also updates statistics at end of each game.
	 * @param botsPerGame The number of players to play in each individual game.
	 * @param gameRepeats The number of times to repeat each combination of the tournament.
	 */
	private void runAllCombos(int botsPerGame, int gameRepeats){
		List<List<Player>> combosList = new ArrayList<List<Player>>();
		combosList.add(new ArrayList<Player>());
		combosList = createAllCombos(botsPerGame, participatingPlayers, combosList);
		for(int j = 0; j < gameRepeats; j++){
			Collections.shuffle(combosList);
			for(List<Player> playerCombo : combosList){
				runSingleGame(playerCombo);
			}
		}
	}

	/**
	 * Expands (recursively) the list of all combos of length N to the list of all 
	 * combos of length N+playersToAdd. This function assumes that no repeats are 
	 * wanted and that the last Player in each combo prefix is of the greatest 
	 * index in participatingPlayers.
	 * @param playersToAdd The number of players to add to each combo.
	 * @param participatingPlayers The list of all players that may be added. 
	 * @param combosList A list of combo prefixes. 
	 * @return The list of all combos of length N+playersToAdd that start with the 
	 * 		   given list of prefixes.
	 */
	private List<List<Player>> createAllCombos(int playersToAdd,
			List<Player> participatingPlayers, 
			List<List<Player>> combosList) {
		ArrayList<List<Player>> combos = new ArrayList<List<Player>>();
		for (List<Player> comboPrefix : combosList) {
			Player lastPlayerChosen = null;
			boolean passedLastPlayer;
			if (comboPrefix.isEmpty()) {
				passedLastPlayer = true;
			} else {
				lastPlayerChosen = comboPrefix.get(comboPrefix.size() - 1);
				passedLastPlayer = false;
			}
			for (int i=0; i<participatingPlayers.size(); i++) {
				if (passedLastPlayer) {
					ArrayList<Player> combo = new ArrayList<Player>();
					combo.addAll(comboPrefix);
					combo.add(participatingPlayers.get(i));
					combos.add(combo);
				} else if (!comboPrefix.isEmpty() 
						&& lastPlayerChosen == participatingPlayers.get(i)) {
					passedLastPlayer = true;
				}
			}
		}
		if (playersToAdd <= 1)
			return combos;
		else
			return createAllCombos(playersToAdd - 1, participatingPlayers, combos);
	}

	/**
	 * Runs a game using the local game factory and the given players. Updates 
	 * the statistics for the players accordingly.
	 * @param players The players in the game.
	 */
	private void runSingleGame(List<Player> players) {
		if (combosVsPermutations)
			Collections.shuffle(players);
		Game game = gameFactory.getGameInstance(players);
		game.setTimeout(microsecBeforeTimeout);
		Player winner = game.runGame();
		//update stats
		for(Player p : players){
			if(p == winner){
				p.getStatistics().increaseWins();
			}
			else{
				p.getStatistics().increaseLosses();
			}
		}
	}
}
