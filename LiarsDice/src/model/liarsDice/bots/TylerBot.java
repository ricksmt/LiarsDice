package model.liarsDice.bots;

import model.liarsDice.gameInfo.*;
import model.liarsDice.gameLogic.*;

/**
 * Tyler's bot so far.
 */
public class TylerBot extends LiarsDiceBot 
{
	boolean verbose = false;
	int numGames = 0;
	int numRounds = 0;

	@Override
	public Decision getDecision(GameInfo gi) 
	{
		Bid myBid = new Bid(gi.getTotalDice() / 3, 6);
		if(gi.getCurrentBid() == null)
		{
			return myBid;
		}
		else if (checkValidDecision(myBid, gi))
		{
			return myBid;
		}
		else
		{
			return new Challenge();
		}
	}
	
	@Override
	public String getName() 
	{
		return "Tyler's Bot";
	}

	@Override
	public void reportRoundResults(GameInfo info)
	{
		say("Game " + ++numGames + ": ");
		for (Round r : info.getGameHistory().getRounds())
		{
			displayRound(r);
		}
	}
	
	private String decisionToString(Decision decision) 
	{
		if (decision instanceof Challenge)
			return "Challenge";
		else if (decision instanceof Bid)
		{
			Bid bid = (Bid)decision;
			return "Bid " + bid.getFrequency() + " " + bid.getFaceValue() + "s";
		}
		else
			return "No decision";
	}

	private void displayRound(Round r) 
	{
		say("Round " + ++numRounds + ": ");
		for (Turn t : r.getTurns())
		{
			say("\t" + t.getPlayerID() + ": " + decisionToString(t.getDecision()));
		}
		say("Result: " + r.getResult());
	}

	private void say(String string) 
	{
		if (verbose)
			System.out.println(string);
	}
}
