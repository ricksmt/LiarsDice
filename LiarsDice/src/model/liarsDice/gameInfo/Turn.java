package model.liarsDice.gameInfo;

import model.liarsDice.gameLogic.Decision;


/**
 * Represents a single turn of a Round. Holds the ID of the player whose turn it was and the decision they made.
 */
public class Turn {
	private int playerID;
	private Decision decision;
	
	/**
	 * Constructor.
	 * @param playerID The unique ID of the player whose turn this was.
	 * @param decision The Decision that player made.
	 */
	public Turn(int playerID, Decision decision)
	{
		this.playerID = playerID;
		this.decision = decision;
	}

	/**
	 * @return The unique ID of the player whose turn this was.
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * @return The Decision made this turn.
	 */
	public Decision getDecision() {
		return decision;
	}
}
