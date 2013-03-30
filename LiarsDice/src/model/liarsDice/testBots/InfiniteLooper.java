package model.liarsDice.testBots;

import model.liarsDice.gameInfo.GameInfo;
import model.liarsDice.gameLogic.Challenge;
import model.liarsDice.gameLogic.Decision;
import model.liarsDice.gameLogic.LiarsDiceBot;


public class InfiniteLooper extends LiarsDiceBot {

	public String getName() {
		return "Infinite Looper";
	}

	public Decision getDecision(GameInfo currentGameInfo) throws InterruptedException {
		Thread.sleep(10000);
		return new Challenge();
	}
}