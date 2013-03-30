package model.liarsDice;

import model.liarsDice.gameInfo.GameHistory;
import model.liarsDice.gameInfo.GameInfo;

public interface LiarsDiceView {

	void requestDecision(GameInfo gameInfo);

	void reportRoundResults(GameInfo gameInfo);

}
