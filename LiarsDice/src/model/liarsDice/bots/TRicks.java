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
	
	private static double TRUST = 0.4;
	private Random random = new Random();
	private static int GAME_COUNT = 0;
	private static int WIN_COUNT = 0;
	private static int TOP_TWO = 0;

	public String getName() { return "TRicks"; }

	public Decision getDecision(GameInfo currentGameInfo) {
		if(currentGameInfo.getCurrentBid() == null)
			return calculateFirstBid(currentGameInfo);
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
		if(currentBid.getFrequency() >
			(onesGuess + bidFrequencyGuess + Math.log((double)totalDice / 6))) return new Challenge();
		else if(currentBid.getFrequency() < (onesGuess + bidFrequencyGuess)){
			if(currentBid.getFaceValue() == 6)
				return new Bid(currentBid.getFrequency() + 1, currentBid.getFaceValue());
			else return new Bid(currentBid.getFrequency(), currentBid.getFaceValue() + 1);
		}
		else{// currentBid is spot on with my guess. Now I'm freaking out.
			List<Round> rounds = currentGameInfo.getGameHistory().getRounds();
			List<Turn> turns = rounds.get(rounds.size() - 1).getTurns();
			int opponent = turns.get(turns.size() - 1).getPlayerID();
			double veracity = 0;
			int count = 0;
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
			veracity = ((double)(count / 2 + veracity)/(double)count);
			if(veracity < TRUST) return new Challenge();// Liar?!?
			
			// Now I'm gonna make them freak out.
			int high = 5;
			for(int i = 0; i < 5; i++) if(myDiceFrequencies[i] > myDiceFrequencies[high]) high = i;
			high++;
			if(high > currentBid.getFaceValue())
				return finalDecision(new Bid(currentBid.getFrequency(), high), currentGameInfo);
			else if(high == currentBid.getFaceValue())
				return finalDecision(new Bid(currentBid.getFrequency() + 1, high), currentGameInfo);
			else if(veracity > random.nextDouble())
				return finalDecision(new Bid(currentBid.getFrequency() + 1, currentBid.getFaceValue()), currentGameInfo);
			else return finalDecision(new Bid(currentBid.getFrequency() + 1,
				random.nextBoolean() ? high : random.nextInt(5) + 2), currentGameInfo);
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

	public void reportRoundResults(GameInfo gameInfo){
		GAME_COUNT++;
		if(gameInfo.getMyPlayerID() == gameInfo.getWinnerID()) WIN_COUNT++;
		else{
			List<Round> rounds = gameInfo.getGameHistory().getRounds();
			for(int i = rounds.size() - 1; i >= 0; i--){
				for(Turn turn: rounds.get(i).getTurns()){
					if(turn.getPlayerID() == gameInfo.getMyPlayerID()){
						if(i + 1 == rounds.size()){
							TOP_TWO++;
						}
						break;
					}
				}
			}
		}
	}
}
