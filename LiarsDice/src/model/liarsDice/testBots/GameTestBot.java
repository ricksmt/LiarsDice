package model.liarsDice.testBots;

import java.util.List;

import model.liarsDice.gameInfo.GameInfo;
import model.liarsDice.gameInfo.Round;
import model.liarsDice.gameLogic.Bid;
import model.liarsDice.gameLogic.Challenge;
import model.liarsDice.gameLogic.Decision;
import model.liarsDice.gameLogic.Die;
import model.liarsDice.gameLogic.LiarsDiceBot;

public abstract class GameTestBot extends LiarsDiceBot{

	protected Decision loseStrategy(GameInfo gi) {
		List<Round> gameRounds = gi.getGameHistory().getRounds();
		int numTurns = gameRounds.get(gameRounds.size() - 1).getTurns().size();
		List<Die> myDice = gi.getMyDice();
		
		if (numTurns < 2) {
			//indicate with bid the dice you have
			int freq = howManyOfValue(1, myDice) + 1;
			if (howManyOfValue(6, myDice) == 0)
				return new Bid(freq, 6);
			if (howManyOfValue(5, myDice) == 0)
				return new Bid(freq, 5);
			if (howManyOfValue(4, myDice) == 0)
				return new Bid(freq, 4);
			if (howManyOfValue(3, myDice) == 0)
				return new Bid(freq, 3);
			return new Bid(1, 2);
		} else if (numTurns < 4) {
			Bid lastBid = gi.getCurrentBid();
			if (lastBid.getFaceValue() == 2)
				return new Bid(gi.getTotalDice(), 6);
			return new Challenge();
		} else {
			return new Challenge();
		}
	}
	
	protected Decision winStrategy(GameInfo gi) {
		List<Round> gameRounds = gi.getGameHistory().getRounds();
		int numTurns = gameRounds.get(gameRounds.size() - 1).getTurns().size();
		List<Die> myDice = gi.getMyDice();

		if (numTurns < 1) {
			return new Bid(1, 2);
		} else if (numTurns < 3) {
			//respond to number of dice he has
			Bid lastBid = gi.getCurrentBid();
			int lastValue = lastBid.getFaceValue();
			if (lastValue > 2) {
				int freq = lastBid.getFrequency() 
						+ howManyOfValue(lastValue, myDice)
						+ howManyOfValue(1, myDice);
				return new Bid(freq, lastValue);
			} else {
				return new Bid(gi.getTotalDice(), 2);
			}
			
		} else {
			return new Challenge();
		}
	}

	protected int howManyOfValue(int value, List<Die> dice) {
		int count = 0;
		for (int i=0; i<dice.size(); i++) {
			if (dice.get(i).getValue() == value)
				count++;
		}
		return count;
	}

	@Override
	public abstract String getName();

	@Override
	public abstract Decision getDecision(GameInfo currentGameInfo);
}
