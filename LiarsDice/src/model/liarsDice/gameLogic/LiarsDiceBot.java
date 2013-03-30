package model.liarsDice.gameLogic;
import model.Bot;
import model.liarsDice.gameInfo.GameInfo;

/**
 * Parent class for all contest entries.  Each team will create a class which extends LiarsDiceBot.
 */
public abstract class LiarsDiceBot implements Bot {
	
	/**
	 * This is the method that a game will call when it is this bot's turn. (This method must be implemented.)
	 * 
	 * If this method throws an exception or returns an invalid decision, this bot automatically loses the round (and subsequently loses a die).
	 * A checkValidDecision method is provided for your convenience to ensure a decision is valid before returning it.
	 * It is strongly recommended that you check the validity of each decision before returning it.
	 * @param currentGameInfo All relevant information for the current state of the game.
	 * @return A decision: either a Bid or a Challenge.
	 * @throws InterruptedException If the bot is interrupted.
	 */
	public abstract Decision getDecision(GameInfo currentGameInfo) throws InterruptedException;
	
	/**
	 * At the end of each round, the game history is given to each bot.  
	 * Feel free to ignore this method if you're not into machine learning.  :)
	 * @param gameHistory The history of the game up to the end of the current round.
	 * @throws InterruptedException If the bot is interrupted.
	 */
	public void reportRoundResults(GameInfo gameInfo) throws InterruptedException {
		//You don't have to implement this method. At the end of a game, we will report the results to each bot in case they are interested.
	}
	
	/**
	 * Checks the validity of the given decision against the current state of the game.
	 * 
	 * Validity rules:
	 * 1. A Challenge when the current bid is null is INVALID. (The current bid will be null on the first turn of each round.) 
	 * 2. A Bid with frequency greater than the total number of dice left in the game is INVALID.
	 * 3. To be VALID, a new bid must increase either the frequency, or the face value, or both (in relation to the current bid).
	 * 4. As long as the current bid is not null, a Challenge is always VALID. (The current bid will be null on the first turn of each round.)
	 * @param decision The decision to be checked for validity.
	 * @param currentGameInfo The current state of the game.
	 * @return true if the given decision is valid, false otherwise
	 */
	public static boolean checkValidDecision(Decision decision, GameInfo currentGameInfo){
		return LiarsDiceGame.isValidDecision(decision, currentGameInfo);
	}
}
