package model.liarsDice.gameLogic;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import model.Game;
import model.Player;
import model.liarsDice.gameInfo.GameHistory;
import model.liarsDice.gameInfo.GameInfo;
import model.liarsDice.gameInfo.PlayerInfo;
import model.liarsDice.gameInfo.Result;
import model.liarsDice.gameInfo.Turn;


/**
 * Contains the logic for running a game of Liar's Dice.  Keeps track of the game history and players.
 */
public class LiarsDiceGame implements Game {
	private GameHistory history;
	private int turnIndex;
	private List<LiarsDicePlayer> players;
	private Bid currentBid;
	private boolean debug = false;
	private long microsecBeforeTimeout;
	private List<String> exceptionLog;
	
	/**
	 * Constructor.
	 * @param players A list of all the players who will participate in the game.
	 */
	public LiarsDiceGame(List<LiarsDicePlayer> players){
		history = new GameHistory();
		this.players = players;
		turnIndex = 0;
		currentBid = null;
		microsecBeforeTimeout = Long.MAX_VALUE;
		exceptionLog = new ArrayList<String>();
	}
	
	/**
	 * Plays out the game with the players given to the constructor. (Until only one player has dice.)
	 * @return The victorious player.
	 */
	public Player runGame() {
		while(!isGameOver()){
			try {
				playRound();
				for(int i=0; i < players.size(); i++) {
					LiarsDicePlayer p = players.get(i);
					p.reportRoundResults(createGameInfo(true, i)); //TODO assumes bot will be fast and exception-free
				}
			} catch (InterruptedException e) {
				return null; //end game if interrupted
			}
		}
		
		//determine the winner
		LiarsDicePlayer winner = null;
		for(LiarsDicePlayer p : players){
			if(p.getNumDice() > 0){
				winner = p;
			}
		}
		return winner;		
	}
	
	/**
	 * Plays a single round of the game. Players with dice are given turns until 
	 * a player challenges, throws an exception, times out, or returns an invalid decision.
	 * 
	 * @throws InterruptedException 
	 */
	private void playRound() throws InterruptedException {
		for(LiarsDicePlayer p : players){
			p.rerollDice();
		}
		currentBid = null;
		history.addNewRound();
		Result roundResult = Result.UNFINISHED;
		while(roundResult == Result.UNFINISHED){
			roundResult = collectAndProcessDecision();
		}
		history.endRound(roundResult);
	}
	
	/**
	 * Creates a GameInfo object for the player of the given index.
	 * @param revealAllDice Tells whether the dice should be revealed for all players.
	 * @param playerIndex The index of the player for which the GameInfo is being created.
	 * @return A newly created GameInfo object, complete with GameHistory and PlayerInfo objects.
	 */
	private GameInfo createGameInfo(boolean revealAllDice, int playerIndex)
	{
		ArrayList<PlayerInfo> allPlayersInfo = new ArrayList<PlayerInfo>();
		for(Player p : players){
			boolean hidePlayerDice = true;
			if(revealAllDice){
				hidePlayerDice = false;
			}
			else if(p.getID() == players.get(playerIndex).getID()){
				hidePlayerDice = false;
			}
			allPlayersInfo.add(new PlayerInfo((LiarsDicePlayer)p, hidePlayerDice));
		}
		GameInfo gi = new GameInfo(currentBid, new GameHistory(history),
				playerIndex, allPlayersInfo);
		
		return gi;
	}

	/**
	 * Given a player's decision, processes that decision and updates whose turn it is 
	 * (and, if applicable, removes a die from a player).
	 * <p>
	 * There are five cases for after getting a player's decision: 
	 * <li>The player ran out of time
	 * <li>The player threw an exception
	 * <li>The decision was found to be invalid
	 * <li>The decision was a valid challenge
	 * <li>The decision was a valid bid
	 * <p>
	 * <br>In the first three cases, the player's statistics are modified. In the first 
	 * four cases a die is taken away and the turn given to the player who lost 
	 * the die (or his successor if he is out), and the roundResult is changed. In 
	 * the last case the turn passes on to the successor of the current player, and 
	 * the current bid is updated. In all five cases a new turn is added to the 
	 * history object, (but this must be done before changing the turnIndex).
	 * @return Result of the current round. (Result.UNFINISHED if round isn't over yet)
	 * @throws InterruptedException 
	 */
	private Result collectAndProcessDecision() throws InterruptedException {
		Result roundStatus = Result.UNFINISHED;
		Decision decision = null;
		GameInfo gi = createGameInfo(false, turnIndex);
		try{
			decision = getDecisionTimed(players.get(turnIndex), gi);
		}
		catch(DecisionTimeout dt) {
			roundStatus = Result.TIMEOUT;
			players.get(turnIndex).getStatistics().increaseTimeouts();
			history.addTurn(new Turn(players.get(turnIndex).getID(), null));
			takeAwayDieAndSetNextTurn(turnIndex);
			return roundStatus;
		}
		catch(ExecutionException e){ //checking against exceptions thrown by bot
			logException((Exception)e.getCause());
			roundStatus = Result.EXCEPTION;
			players.get(turnIndex).getStatistics().increaseExceptions();
			history.addTurn(new Turn(players.get(turnIndex).getID(), null));
			takeAwayDieAndSetNextTurn(turnIndex);
			return roundStatus;
		}

		history.addTurn(new Turn(players.get(turnIndex).getID(), decision));
		if(!isValidDecision(decision, gi)){
			roundStatus = Result.INVALIDDECISION;
			players.get(turnIndex).getStatistics().increaseInvalidDecisions();
			//maybe log later
			takeAwayDieAndSetNextTurn(turnIndex);
		}
		else if(decision instanceof Challenge){
			if(numberOfDiceWithValue(currentBid.getFaceValue()) >= currentBid.getFrequency()){
				takeAwayDieAndSetNextTurn(turnIndex);
				roundStatus = Result.LOSING_CHALLENGE;
			}
			else{
				takeAwayDieAndSetNextTurn(previousTurnIndex(turnIndex));
				roundStatus = Result.WINNING_CHALLENGE;
			}
		}
		else //normal bid
		{
			Bid bid = (Bid)decision;
			currentBid = bid;
			turnIndex = nextTurnIndex(turnIndex);
		}
		return roundStatus;
	}

	/**
	 * Retrieves a player's decision for the current game, with a hard time cutoff
	 * defined by microsecBeforeTimeout.
	 * @param player The player object being queried for its decision
	 * @param gi The history of the current game, to be handed to the player
	 * @return The player's decision
	 * @throws DecisionTimeout If the player exceeds the time limit.
	 * @throws ExecutionException If the player throws an exception. The exception
	 *      that was thrown will be in ExecutionException::getCause().
	 * @throws InterruptedException 
	 */
	private Decision getDecisionTimed(LiarsDicePlayer player,
			GameInfo gi) throws DecisionTimeout, ExecutionException, InterruptedException {
		
		Decision decision = null;
		
		ExecutorService svc = Executors.newFixedThreadPool(1) ;
		Future<Decision> decisionFuture = 
				svc.submit( new DecisionGettingCallable(player, gi) ) ;
		svc.shutdown() ;
		
		try {
			if (!svc.awaitTermination(microsecBeforeTimeout, TimeUnit.MICROSECONDS))
				throw new DecisionTimeout();
			decision = decisionFuture.get();
		} 
		catch (InterruptedException e) {
			throw e;
		}
		
		return decision;
	}
	
	/**
	 * A helper class for getDecisionTimed, this represents a function to call getDecision 
	 * on a player with the given gameInfo object. The player and gameInfo objects are 
	 * passed into the constructor, and the call() method returns the decision.
	 */
	private class DecisionGettingCallable implements Callable<Decision>
	{
		public LiarsDicePlayer player;
		public GameInfo gi;
		
		public DecisionGettingCallable(LiarsDicePlayer player, GameInfo gi) {
			this.player = player;
			this.gi = gi;
		}
		
		public Decision call() throws InterruptedException {
			return player.getDecision(gi);
		}
	}
	
	/**
	 * The exception thrown when a player exceeds the time limit in getDecision().
	 */
	@SuppressWarnings("serial")
	private class DecisionTimeout extends Exception {}

	/**
	 * Determines whose turn it is next. (Skips over players with no dice.)
	 * @param turnIndex The current turn index.
	 * @return The next turn index.
	 */
	private int nextTurnIndex(int turnIndex) {
		int tempIndex = turnIndex;
		do{
			tempIndex++;
			if(tempIndex >= players.size()){
				tempIndex = 0;
			}
			if(tempIndex == turnIndex){
				//not error - only one player left with dice
				break;
			}
		}while(players.get(tempIndex).getNumDice() <= 0);
		return tempIndex;
	}

	/**
	 * Determines whose turn it was last. (Skips over players with no dice.)
	 * @param turnIndex The current turn index.
	 * @return The last turn index.
	 */
	private int previousTurnIndex(int turnIndex) {
		int tempIndex = turnIndex;
		do{
			tempIndex--;
			if(tempIndex < 0){
				tempIndex = players.size() - 1;
			}
			if(tempIndex == turnIndex){
				//not error - only one player left with dice
				break;
			}
		}while(players.get(tempIndex).getNumDice() <= 0);
		return tempIndex;
	}

	/**
	 * Counts up the total number of dice that count as the given dieNumber. 
	 * (dieNumber + wilds)
	 * @param dieNumber The face value to total.
	 * @return Total number of dice with face value dieNumber + wild dice.
	 */
	private int numberOfDiceWithValue(int dieNumber) {
		int count = 0;
		for(LiarsDicePlayer p : players){
			for(Die d : p.getDice()){
				if(d.getValue() == dieNumber || d.getValue() == Die.WILD){
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Takes away a die from the player at loseIndex and sets whose turn it is next.
	 * If this player has no more dice, this updates the number of players still in the game.
	 * @param loseIndex The player who must lose a die.
	 */
	private void takeAwayDieAndSetNextTurn(int loseIndex) {
		players.get(loseIndex).removeDie();
		if(players.get(loseIndex).getNumDice() <= 0){
			this.turnIndex = nextTurnIndex(loseIndex);
		}
		else{
			this.turnIndex = loseIndex;
		}
	}

	/**
	 * Checks to see if there is only one player with dice left.
	 * @return true if game is over, false otherwise.
	 */
	public boolean isGameOver() {
		int playersLeft = 0;
		for(LiarsDicePlayer p : players){
			if(p.getNumDice() > 0){
				playersLeft++;
			}
		}
		return playersLeft == 1;
	}

	/**
	 * Checks the validity of the given decision against the current state of the game.
	 * 
	 * Validity rules:
	 * 1. A Challenge when the current bid is null is invalid. (The current bid will be null on the first turn of each round.) 
	 * 2. A Bid with frequency greater than the total number of dice left in the game is invalid.
	 * 3. To be valid, a new bid must increase either the frequency, or the face value, or both (in relation to the current bid).
	 * 4. As long as the current bid is not null, a Challenge is always valid. (The current bid will be null on the first turn of each round.)
	 * @param decision The decision to be checked for validity.
	 * @param currentGameInfo The current state of the game.
	 * @return true if the given decision is valid, false otherwise
	 */
	public static boolean isValidDecision(Decision decision, GameInfo currentGameInfo){
		if(decision == null || currentGameInfo == null){
			return false;
		}
		Bid currentBid = currentGameInfo.getCurrentBid();
		if(decision instanceof Challenge){
			if(currentBid == null){
				return false; //can't challenge first turn of the round
			}
			return true; //otherwise challenge is always valid
		}
		else{
			Bid bid = (Bid)decision;
			if(bid.getFaceValue() < 2 || bid.getFaceValue() > 6){
				return false; //invalid dieNumber
			}
			if(bidFrequencyTooHigh(bid, currentGameInfo)){
				return false;
			}
			if(currentBid == null){ //first bid of round
				if(bid.getFrequency() < 1){
					return false; //can't bid 0 or less
				}
				return true;
			}
			else{ //not first bid of round
				if(bid.getFrequency() < currentBid.getFrequency()){
					return false; //frequency must be >= current frequency
				}
				else if(bid.getFrequency() > currentBid.getFrequency()){
					return true; //an increased frequency is always valid (assuming a valid dieNumber - above)
				}
				else{ //frequency == current frequency
					if(bid.getFaceValue() <= currentBid.getFaceValue()){
						return false; //must increase the dieNumber if not increasing frequency
					}
					return true;
				}
			}
		}
	}
	
	/**
	 * Private method to check whether the bid frequency is higher than the total number of dice left in the game.
	 * @param bid The current bid.
	 * @param gi The current state of the game.
	 * @return true if the given bid's frequency is too high, false otherwise
	 */
	private static boolean bidFrequencyTooHigh(Bid bid, GameInfo gi) {
		int totalDice = 0;
		for(PlayerInfo p : gi.getAllPlayersInfo()){
			totalDice += p.getNumDice();
		}
		return (bid.getFrequency() > totalDice);
	}

	/**
	 * Sets the timeout in microseconds for a single decision.
	 * @param microsecBeforeTimeout Number of microseconds before timeout
	 */
	public void setTimeout(long microsecBeforeTimeout) {
		this.microsecBeforeTimeout = microsecBeforeTimeout;
	}

	/**
	 * Adds the stack trace of an exception to the exceptionLog.
	 * @param e The exception to log
	 */
	private void logException(Exception e) {
		String exceptionString;
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		exceptionString = stringWriter.toString();
		exceptionLog.add(exceptionString);
		if (debug)
			System.out.println(exceptionString);
	}
	
	/**
	 * Prints the exception log to standard output.
	 */
	@SuppressWarnings("unused")
	private void printLog() {
		System.out.println("Exception Log:");
		for (int i=0; i<exceptionLog.size(); i++)
			System.out.println(exceptionLog.get(i));
	}
}
