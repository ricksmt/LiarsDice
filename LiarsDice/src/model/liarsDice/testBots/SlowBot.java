package model.liarsDice.testBots;

import model.liarsDice.gameInfo.GameInfo;
import model.liarsDice.gameLogic.Challenge;
import model.liarsDice.gameLogic.Decision;
import model.liarsDice.gameLogic.LiarsDiceBot;

public class SlowBot extends LiarsDiceBot {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Slow Poke";
	}

	@Override
	public Decision getDecision(GameInfo currentGameInfo)
			throws InterruptedException {
		long rand = (long)(Math.random() * 60);
		Thread.sleep(rand);
		return new Challenge();
	}
}
