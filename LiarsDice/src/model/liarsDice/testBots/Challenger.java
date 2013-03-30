package model.liarsDice.testBots;

import model.liarsDice.gameInfo.GameInfo;
import model.liarsDice.gameLogic.Challenge;
import model.liarsDice.gameLogic.Decision;
import model.liarsDice.gameLogic.LiarsDiceBot;


/**
 * Very simple test bot which always Challenges.
 */
public class Challenger extends LiarsDiceBot {

	@Override
	public String getName() {
		return "Challenger";
	}

	@Override
	public Decision getDecision(GameInfo currentGameInfo) {
		return new Challenge();
	}

}
