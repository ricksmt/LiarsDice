package model.liarsDice.bots;

import java.util.List;

import model.liarsDice.gameInfo.GameInfo;
import model.liarsDice.gameInfo.PlayerInfo;
import model.liarsDice.gameLogic.Bid;
import model.liarsDice.gameLogic.Challenge;
import model.liarsDice.gameLogic.Decision;
import model.liarsDice.gameLogic.Die;
import model.liarsDice.gameLogic.LiarsDiceBot;

/**
 * Slightly more intelligent test bot which bids or challenges based on raw probabilities of dice rolls.
 */
public class SmartBot extends LiarsDiceBot {

	public String getName() {
		return "Smart Bot";
	}

	public Decision getDecision(GameInfo currentGameInfo) {
		Bid currentBid = currentGameInfo.getCurrentBid();
		if(currentBid == null){
			return calculateFirstBid(currentGameInfo);
		}
		else{
			return makeDecision(currentGameInfo);
		}
	}

	private Decision makeDecision(GameInfo currentGameInfo) {
		int[] myDiceFrequencies = getMyDiceFrequencies(currentGameInfo);
		int totalDice = getTotalDice(currentGameInfo);
		int othersDice = totalDice - currentGameInfo.getMyDice().size();
		Bid currentBid = currentGameInfo.getCurrentBid();
		int onesGuess = othersDice/6 + myDiceFrequencies[0];
		int bidFrequencyGuess = myDiceFrequencies[currentBid.getFaceValue() - 1] + othersDice/6;
		if(currentBid.getFrequency() > (onesGuess + bidFrequencyGuess)){ //I don't think there are that many in the game
			return new Challenge();
		}
		else if(currentBid.getFrequency() < (onesGuess + bidFrequencyGuess)){
			return new Bid(currentBid.getFrequency() + 1, currentBid.getFaceValue());
		}
		else{ //currentBid is spot on with my guess
			int myDiceOfCurrentBid = myDiceFrequencies[currentBid.getFaceValue() - 1];
			for(int i = currentBid.getFaceValue(); i < myDiceFrequencies.length; i++){
				if(myDiceFrequencies[i] >= myDiceOfCurrentBid){
					return new Bid(currentBid.getFrequency(), i + 1);
				}
			}
			for(int i = 1; i < currentBid.getFaceValue(); i++){
				if(myDiceFrequencies[i] > myDiceOfCurrentBid){
					return new Bid(currentBid.getFrequency() + 1, i + 1);
				}
			}
			return new Challenge();
		}
	}

	private int getTotalDice(GameInfo currentGameInfo) {
		int totalDice = currentGameInfo.getMyDice().size();
		List<PlayerInfo> players = currentGameInfo.getAllPlayersInfo();
		for(PlayerInfo p : players){
			totalDice += p.getNumDice();
		}
		return totalDice;
	}

	private Decision calculateFirstBid(GameInfo currentGameInfo) {
		int[] myDiceFrequencies = getMyDiceFrequencies(currentGameInfo);
		int maxValue = -1, maxFrequency = -1;
		for(int i = 1; i < myDiceFrequencies.length; i++){
			if(myDiceFrequencies[i] >= maxFrequency){
				maxValue = i+1;
				maxFrequency = myDiceFrequencies[i];
			}
		}
		int bidFrequency = maxFrequency + myDiceFrequencies[0];
		return new Bid(bidFrequency, maxValue);
	}
	
	private int[] getMyDiceFrequencies(GameInfo currentGameInfo) {
		int[] myDiceFrequencies = new int[6];
		for(Die d : currentGameInfo.getMyDice()){
			myDiceFrequencies[d.getValue() - 1]++;
		}
		return myDiceFrequencies;
	}

}
