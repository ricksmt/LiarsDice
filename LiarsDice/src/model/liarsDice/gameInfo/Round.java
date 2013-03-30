package model.liarsDice.gameInfo;


import java.util.ArrayList;
import java.util.List;


/**
 * Represents one round within a game. (Starts with a bid, ends with one player losing a die.)
 */
public class Round {
	private List<Turn> turns;
	private Result result;
	
	/**
	 * Empty constructor.
	 */
	public Round(){
		turns = new ArrayList<Turn>();
		result = Result.UNFINISHED;
	}
	
	/**
	 * Constructor.
	 * @param turns List of turns already completed this round so far.
	 * @param result The current result of the round.
	 */
	public Round(List<Turn> turns, Result result){
		this.turns = turns;
		this.result = result;
	}
	
	/**
	 * Copy constructor. (Not a deep copy because Turn is unmodifiable.)
	 * @param r The Round to be copied.
	 */
	public Round(Round r){
		//copies each turn in the other round into a new turns array
		//don't need deep copy because turns are not modifiable
		turns = new ArrayList<Turn>(r.getTurns());
		result = r.result;
	}

	/**
	 * @return List of the turns taken (in order) during this Round.
	 */
	public List<Turn> getTurns() {
		return turns;
	}
	
	/**
	 * @return The Result of the Round.
	 */
	public Result getResult(){
		return result;
	}
	
	/**
	 * Adds the given turn to this Round.
	 * @param turn Turn to be added to this Round.
	 */
	public void addTurn(Turn turn){
		turns.add(turn);
	}

	/**
	 * End this Round.
	 * @param result The ending Result of this Round. (Result.UNFINISHED is invalid.)
	 */
	public void end(Result result) {
		if(result == Result.UNFINISHED){
			throw new IllegalArgumentException("Cannot end a round with Result.UNFINISHED");
		}
		this.result = result;
	}

	public boolean isOver() {
		return (result != Result.UNFINISHED);
	}
}
