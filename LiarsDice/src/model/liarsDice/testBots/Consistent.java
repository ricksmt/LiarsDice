package model.liarsDice.testBots;

import model.liarsDice.gameInfo.GameInfo;
import model.liarsDice.gameLogic.Bid;
import model.liarsDice.gameLogic.Decision;
import model.liarsDice.gameLogic.LiarsDiceBot;


/**
 * Very simple test bot which always returns the same bid. (Will probably be invalid decision a lot.)
 */
public class Consistent extends LiarsDiceBot {

	@Override
	public String getName() {
		return "Consistent";
	}

	@Override
	public Decision getDecision(GameInfo currentGameInfo) {
		return new Bid(20,5);
	}

}
