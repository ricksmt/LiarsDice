package model.liarsDice.bots;

import java.util.List;

import model.liarsDice.gameInfo.GameInfo;
import model.liarsDice.gameInfo.PlayerInfo;
import model.liarsDice.gameLogic.Bid;
import model.liarsDice.gameLogic.Challenge;
import model.liarsDice.gameLogic.Decision;
import model.liarsDice.gameLogic.LiarsDiceBot;

/**
 * Test bot which simply adds one to the frequency of the current bid, 
 * or Challenges if the current bid's frequency is more than the total number of dice.
 */
public class DumbBot extends LiarsDiceBot {

	@Override
	public String getName() {
		return "Dumb Bot";
	}

	@Override
	public Decision getDecision(GameInfo currentGameInfo) {
		Bid b = currentGameInfo.getCurrentBid();
		if(b == null){
			return new Bid(2, 2);
		}
		else if(b.getFrequency() > getTotalDice(currentGameInfo)){
			return new Challenge();
		}
		else{
			return new Bid(b.getFrequency() + 1, b.getFaceValue());
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

}
