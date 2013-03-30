package model.liarsDice.bots;

import java.util.List;
import java.util.Random;

import model.liarsDice.gameInfo.GameInfo;
import model.liarsDice.gameInfo.PlayerInfo;
import model.liarsDice.gameInfo.Result;
import model.liarsDice.gameInfo.Round;
import model.liarsDice.gameInfo.Turn;
import model.liarsDice.gameLogic.Bid;
import model.liarsDice.gameLogic.Challenge;
import model.liarsDice.gameLogic.Decision;
import model.liarsDice.gameLogic.Die;
import model.liarsDice.gameLogic.LiarsDiceBot;

/**
 * Slightly more intelligent test bot which bids or challenges based on raw probabilities of dice rolls.
 */
public class TRicks extends LiarsDiceBot {
	
	private static int STRETCH = 1;// I'm willing to believe STRETCH more than the average.
	private Random random = new Random();

	public String getName() {
		return "TRicks";
	}

	public Decision getDecision(GameInfo currentGameInfo) {
		Bid currentBid = currentGameInfo.getCurrentBid();
		if(currentBid == null) return calculateFirstBid(currentGameInfo);
		else return makeDecision(currentGameInfo);
	}

	private Decision makeDecision(GameInfo currentGameInfo) {
		Bid currentBid = currentGameInfo.getCurrentBid();
		int totalDice = currentGameInfo.getTotalDice();
		if(currentBid.getFrequency() < totalDice / 3 ||
				(currentBid.getFrequency() == totalDice / 3 &&
				currentBid.getFaceValue() < 6)) return calculateFirstBid(currentGameInfo);
		
		// At this point we should start getting uncomfortable.
		int[] myDiceFrequencies = getMyDiceFrequencies(currentGameInfo);
		int othersDice = totalDice - currentGameInfo.getMyDice().size();
		int onesGuess = othersDice/6 + myDiceFrequencies[0];
		int bidFrequencyGuess = myDiceFrequencies[currentBid.getFaceValue() - 1] + othersDice/6;
		if(currentBid.getFrequency() > (onesGuess + bidFrequencyGuess + STRETCH)) return new Challenge();
		else if(currentBid.getFrequency() < (onesGuess + bidFrequencyGuess)){
			
			if(currentBid.getFaceValue() == 6)
				return new Bid(currentBid.getFrequency() + 1, currentBid.getFaceValue());
			else return new Bid(currentBid.getFrequency(), currentBid.getFaceValue() + 1);
		}
		else{// currentBid is spot on with my guess. Now I'm freaking out.
			List<Round> rounds = currentGameInfo.getGameHistory().getRounds();
			List<Turn> turns = rounds.get(rounds.size() - 1).getTurns();
			int opponent = turns.get(turns.size() - 1).getPlayerID();
			int veracity = 0, count = 0;
			for(Round round: rounds){
				switch(round.getResult()){
					case WINNING_CHALLENGE:
						if(round.getTurns().get(round.getTurns().size() - 2).getPlayerID() == opponent){
							veracity--;
							count++;
						}
						break;
					case LOSING_CHALLENGE:
						if(round.getTurns().get(round.getTurns().size() - 2).getPlayerID() == opponent){
							veracity++;
							count++;
						}
						break;
					default:
						break;
				}
			}
			if(veracity < 0) return new Challenge();// Liar?!?
			
			// Now I'm gonna make them freak out.
			int high = 5;
			for(int i = 0; i < 5; i++) if(myDiceFrequencies[i] > myDiceFrequencies[5]) high = i;
			if(high > currentBid.getFaceValue()) return new Bid(currentBid.getFrequency(), high);
			else if(currentBid.getFaceValue() != 6) return new Bid(currentBid.getFrequency(), 6);
			if(((double)(count / 2 + veracity)/(double)count) > random.nextDouble())
				return new Bid(currentBid.getFrequency() + 1, currentBid.getFaceValue());
			else return new Bid(currentBid.getFrequency() + 1,
				random.nextBoolean() ? high : random.nextInt(5) + 2);
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

	public void reportRoundResults(GameInfo gameInfo){
		if(gameInfo.getMyPlayerID() == gameInfo.getWinnerID()) System.out.println("I won!!!");
	}
}
