package model.liarsDice.bots;

//import java.util.List;
import java.util.Random;

import model.liarsDice.gameInfo.GameInfo;
//import model.liarsDice.gameInfo.PlayerInfo;
//import model.liarsDice.gameInfo.Result;
//import model.liarsDice.gameInfo.Round;
//import model.liarsDice.gameInfo.Turn;
import model.liarsDice.gameLogic.Bid;
import model.liarsDice.gameLogic.Challenge;
import model.liarsDice.gameLogic.Decision;
import model.liarsDice.gameLogic.Die;
import model.liarsDice.gameLogic.LiarsDiceBot;

/**
 * Slightly more intelligent test bot which bids or challenges based on raw probabilities of dice rolls.
 */
public class Aggressive extends LiarsDiceBot {
	
	private Random random = new Random();
	private static int STRETCH = 1;// Optimal

	public String getName() { return "Aggressive"; }

	public Decision getDecision(GameInfo currentGameInfo) {
		if(currentGameInfo.getCurrentBid() == null)
			return calculateFirstBid(currentGameInfo);
		else return makeDecision(currentGameInfo);
	}

	private Decision makeDecision(GameInfo currentGameInfo) {
		Bid currentBid = currentGameInfo.getCurrentBid();
		int totalDice = currentGameInfo.getTotalDice() + currentGameInfo.getMyDice().size();
		if(currentBid.getFrequency() < totalDice / 3 ||
				(currentBid.getFrequency() == totalDice / 3 &&
				currentBid.getFaceValue() < 6)) return finalDecision(calculateFirstBid(currentGameInfo), currentGameInfo);
		
		// At this point we should start getting uncomfortable.
		int[] myDiceFrequencies = getMyDiceFrequencies(currentGameInfo);
		int othersDice = totalDice - currentGameInfo.getMyDice().size();
		int onesGuess = othersDice/6 + myDiceFrequencies[0];
		int bidFrequencyGuess = myDiceFrequencies[currentBid.getFaceValue() - 1] + othersDice/6;
		if(currentBid.getFrequency() > (onesGuess + bidFrequencyGuess + STRETCH)) return new Challenge();
		else if(currentBid.getFrequency() < (onesGuess + bidFrequencyGuess + STRETCH)){
			if(currentBid.getFaceValue() == 6)
				return finalDecision(new Bid(currentBid.getFrequency() + 1, currentBid.getFaceValue()), currentGameInfo);
			else return finalDecision(new Bid(currentBid.getFrequency(), currentBid.getFaceValue() + 1), currentGameInfo);
		}
		else{// currentBid is spot on with my guess. Now I'm gonna make them freak out.
			int high = 5;
			for(int i = 4; i > 0; i--) if(myDiceFrequencies[i] > myDiceFrequencies[high]) high = i;
			high++;
			if(high > currentBid.getFaceValue())
				return finalDecision(new Bid(currentBid.getFrequency(), high), currentGameInfo);
			else return finalDecision(new Bid(currentBid.getFrequency() + 1, high), currentGameInfo);
			// Notes: high - 3x; random - 2.5x; theirs - 2x;
		}
	}
	
	private Decision finalDecision(Decision decision, GameInfo currentGameInfo){
		if(checkValidDecision(decision, currentGameInfo)){
//			System.out.println(currentGameInfo.getTotalDice() + " My decision is final.");
			return decision;
		}
		else{
//			System.out.println(currentGameInfo.getTotalDice() + " You're fired.");
			return new Challenge();
		}
	}

	private Decision calculateFirstBid(GameInfo currentGameInfo) {
		if(currentGameInfo.getTotalDice() < 3) return new Bid(1, random.nextInt(5) + 2);
		return new Bid(currentGameInfo.getTotalDice() / 3, 6);
	}
	
	private int[] getMyDiceFrequencies(GameInfo currentGameInfo) {
		int[] myDiceFrequencies = new int[6];
		for(Die d : currentGameInfo.getMyDice()) myDiceFrequencies[d.getValue() - 1]++;
		return myDiceFrequencies;
	}

//	public void reportRoundResults(GameInfo gameInfo){
//		GAME_COUNT++;
//		if(gameInfo.getMyPlayerID() == gameInfo.getWinnerID()) WIN_COUNT++;
//		else{
//			List<Round> rounds = gameInfo.getGameHistory().getRounds();
//			for(int i = rounds.size() - 1; i >= 0; i--){
//				for(Turn turn: rounds.get(i).getTurns()){
//					if(turn.getPlayerID() == gameInfo.getMyPlayerID()){
//						if(i + 1 == rounds.size()){
//							TOP_TWO++;
//						}
//						break;
//					}
//				}
//			}
//		}
//	}
}
