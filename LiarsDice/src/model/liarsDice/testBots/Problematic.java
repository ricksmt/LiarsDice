package model.liarsDice.testBots;

import model.liarsDice.gameInfo.GameInfo;
import model.liarsDice.gameLogic.Challenge;
import model.liarsDice.gameLogic.Decision;
import model.liarsDice.gameLogic.LiarsDiceBot;


/**
 * Very simple test bot which randomly throws exceptions or Challenges.
 */
public class Problematic extends LiarsDiceBot {

	public String getName() {
		return "Problematic";
	}

	public Decision getDecision(GameInfo currentGameInfo) {
		if(Math.random() > 0){
			int[] array = new int[1];
			array[2] = 3; //throw Exception
		}
		return new Challenge();
	}

}
