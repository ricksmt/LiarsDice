package model.liarsDice.testBots;

import java.util.List;

import model.liarsDice.gameInfo.GameInfo;
import model.liarsDice.gameInfo.Round;
import model.liarsDice.gameLogic.Decision;


/**
 * A bot designed to work with GameTestBot1 to verify correctness 
 * of the game logic code. 
 */
public class GameTestBot2 extends GameTestBot {

	public String getName() {
		return "GameTestBot2";
	}

	public Decision getDecision(GameInfo currentGameInfo) {
		List<Round> gameRounds = currentGameInfo.getGameHistory().getRounds();
		int roundNum = gameRounds.size();
		
		System.out.println("Bot2 has " + currentGameInfo.getMyDice().size());
		
		if (roundNum % 2 == 1) {
			return winStrategy(currentGameInfo);
		} else {
			return loseStrategy(currentGameInfo);
		}
	}


}