package model.liarsDice.testBots;

import java.util.ArrayList;

import model.liarsDice.gameLogic.LiarsDiceGame;
import model.liarsDice.gameLogic.LiarsDicePlayer;

public class GameTest {
	
	public static void main(String[] args) {
		GameTestBot1 bot1 = new GameTestBot1();
		GameTestBot2 bot2 = new GameTestBot2();
		int playerNumber = 1;
		ArrayList<LiarsDicePlayer> players = new ArrayList<LiarsDicePlayer>();
		players.add(new LiarsDicePlayer(bot1, playerNumber++));
		players.add(new LiarsDicePlayer(bot2, playerNumber++));
		LiarsDiceGame game = new LiarsDiceGame(players);
		game.runGame();

		test(1, players.get(0).getNumDice(), 0);
		test(2, players.get(1).getNumDice(), 1);
		test(3, game.isGameOver(), true);
	}
	
	private static void test(int testNum, int a, int b) {
		if (a != b) {
			System.err.println("Test " + testNum + " failed: " + 
					a + " is not equal to " + b + ".");
		}
	}
	
	private static void test(int testNum, boolean a, boolean b) {
		if (a != b) {
			System.err.println("Test " + testNum + " failed: " + 
					a + " is not equal to " + b + ".");
		}
	}

}
