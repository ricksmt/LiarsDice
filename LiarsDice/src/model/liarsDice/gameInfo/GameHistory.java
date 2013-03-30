package model.liarsDice.gameInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Keeps track of the history of a game (in the form of a collection of Rounds, which are a collection of Turns).
 */
public class GameHistory {
	private List<Round> rounds;
	
	/**
	 * Constructor.
	 */
	public GameHistory(){
		rounds = new ArrayList<Round>();
	}
	
	/**
	 * Copy constructor. (Makes a deep copy.)
	 * @param gh The GameHistory object to be copied.
	 */
	public GameHistory(GameHistory gh){
		//deep copy:
		List<Round> tempList = new ArrayList<Round>();
		for(int i = 0; i < gh.getRounds().size(); i++){
			tempList.add(new Round(gh.getRounds().get(i)));
		}
		rounds = tempList;
	}

	/**
	 * @return List of this game's Rounds.
	 */
	public List<Round> getRounds() {
		return rounds;
	}

	/**
	 * Adds a Round to the history.
	 */
	public void addNewRound(){
		rounds.add(new Round());
	}
	
	/**
	 * Adds a Turn to the current Round in the history.
	 * @param turn Turn to be added.
	 */
	public void addTurn(Turn turn){
		rounds.get(rounds.size() - 1).addTurn(turn);
	}
	
	/**
	 * Ends the current Round with the given Result.
	 * @param result End result of the current Round.
	 */
	public void endRound(Result result){
		rounds.get(rounds.size() - 1).end(result);
	}
}
