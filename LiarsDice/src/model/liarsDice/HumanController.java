package model.liarsDice;

import java.util.concurrent.Semaphore;

import model.liarsDice.gameInfo.GameInfo;
import model.liarsDice.gameLogic.Decision;
import model.liarsDice.gameLogic.LiarsDiceBot;


/**
 * This class will interface with the player view to get the human player's input.
 */
public class HumanController extends LiarsDiceBot {
	
	ViewCommunication viewCommunication;

    public ViewCommunication getViewCommunication() {
		return viewCommunication;
	}
	
	public HumanController() {
		viewCommunication = new ViewCommunication();
	}

	/**
	 * @return The name of the human player.
	 */
	public String getName() {
		return "HumanPlayer";
	}

	/**
	 * @param currentGameInfo The current state of the game.
	 * @return The human's decision.
	 * @throws InterruptedException 
	 */
	public Decision getDecision(GameInfo currentGameInfo) throws InterruptedException {
		Decision userDecision = null;
		
		viewCommunication.sendDecisionRequest(currentGameInfo);
		userDecision = viewCommunication.getDecision();
		
		return userDecision;
	}

	/**
	 * @param gameInfo The current state of the game.
	 * @throws InterruptedException 
	 */
	@Override
	public void reportRoundResults(GameInfo gameInfo) throws InterruptedException {
		viewCommunication.reportRoundResults(gameInfo);
	}
	
	public class ViewCommunication
	{
		private Semaphore s;
		private LiarsDiceView view;
		private Decision currentDecision;
		
		public ViewCommunication() {
			s = new Semaphore(0);
		}
		
		/*********** HumanController-end methods ***********/
		
		public void sendDecisionRequest(GameInfo gameInfo) {
			view.requestDecision(gameInfo);
		}
		
		public void reportRoundResults(GameInfo gameInfo) throws InterruptedException {
			view.reportRoundResults(gameInfo);
			s.acquire();
		}
		
		public Decision getDecision() throws InterruptedException {
			s.acquire();
			Decision rVal = currentDecision;
			return rVal;
		}
		
		/*********** View-end methods ***********/
		
		public void registerView(LiarsDiceView view) {
			this.view = view;
		}
		
		public void setDecision(Decision d) {
			currentDecision = d;
			s.release();
		}

		public void continueNextRound() {
			s.release();
		}
	}

}
