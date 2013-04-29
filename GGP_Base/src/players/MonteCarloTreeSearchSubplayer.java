package players;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class MonteCarloTreeSearchSubplayer extends Subplayer {

	private TerminalStateProximity terminalStateProximity;
	private int maxDepth;
	public MonteCarloTreeSearchSubplayer(StateMachine stateMachine, Role role,
			PlayerResult playerResult, MachineState currentState, TerminalStateProximity terminalStateProximity, long timeout) {
		super(stateMachine, role, playerResult, currentState, timeout);
		this.terminalStateProximity = terminalStateProximity;
	}

	@Override
	public void run() {
		try {
			long finishBy = timeout - 1000;
			
			List<Move> moves = stateMachine.getLegalMoves(currentState, role);
			if (moves.size() > 1) {		
	    		int[] moveTotalPoints = new int[moves.size()];
	    		int[] moveTotalAttempts = new int[moves.size()];
	    		
	    		// Perform depth charges for each candidate move, and keep track
	    		// of the total score and total attempts accumulated for each move.
	    		for (int i = 0; true; i = (i+1) % moves.size()) {
	    		    if (System.currentTimeMillis() > finishBy)
	    		        break;
	    		    
	    		    int[] theDepth = new int[1];
	    		    MachineState terminalState = stateMachine.performDepthCharge(currentState, theDepth);
	    		    int theScore = stateMachine.getGoal(terminalState, role);
	    		    moveTotalPoints[i] += theScore;
	    		    moveTotalAttempts[i] += 1;
	    		}
	    
	    		// Compute the expected score for each move.
	    		double[] moveExpectedPoints = new double[moves.size()];
	    		for (int i = 0; i < moves.size(); i++) {
	    		    moveExpectedPoints[i] = (double)moveTotalPoints[i] / moveTotalAttempts[i];
	    		}

	    		// Find the move with the best expected score.
	    		double bestMoveScore = moveExpectedPoints[0];
	    		for (int i = 1; i < moves.size(); i++) {
	    		    if (moveExpectedPoints[i] > bestMoveScore) {
	    	    		playerResult.setBestMoveSoFar(moves.get(i));
	    				playerResult.setBestMoveScore(moveExpectedPoints[i]);
	    		    }
	    		}
			}

						

		} catch (MoveDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GoalDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransitionDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
