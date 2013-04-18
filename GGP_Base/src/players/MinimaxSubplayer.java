package players;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class MinimaxSubplayer extends Subplayer {

	public MinimaxSubplayer(StateMachine stateMachine, Role role,
			PlayerResult playerResult, MachineState currentState, Thread parentThread) {
		super(stateMachine, role, playerResult, currentState, parentThread);
		// TODO Auto-generated constructor stub
	}
	Map<MachineState,Integer> memoizedStatesMinValues = new HashMap<MachineState,Integer>();
	Map<MachineState,Integer> memoizedStatesMaxValues = new HashMap<MachineState,Integer>();
	
	@Override
	public void run() {
		try {
			List<Move> moves = stateMachine.getLegalMoves(currentState, role);
			int score = Integer.MIN_VALUE;
			Move bestMoveSoFar = null;
			for (Move move : moves) {
				if (Thread.currentThread().isInterrupted()) return;
				int result = minscore(move, currentState);
				System.out.println("MOVE: " + move + ", result: " + result);
				if (result > score) {
					score = result;
					bestMoveSoFar = move;
					playerResult.setBestMoveSoFar(bestMoveSoFar);
					playerResult.setBestMoveScore(score);
				}
				//parentThread.interrupt();
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
	
	

	private int minscore(Move move, MachineState state) throws MoveDefinitionException, GoalDefinitionException, TransitionDefinitionException {
		if(memoizedStatesMinValues.containsKey(state)){
			//System.out.println(state);
			return memoizedStatesMinValues.get(state);
		}
		int score = Integer.MAX_VALUE;
		List<List<Move>> jointMoves = stateMachine.getLegalJointMoves(state, role, move);
		Collections.shuffle(jointMoves);
		for (int i = 0; i < jointMoves.size(); i++) {
			List<Move> jointMove = jointMoves.get(i);
			MachineState newState = stateMachine.getNextState(state, jointMove);
			int result = maxscore(newState);
			if (result < score) {
				score = result;
			}
		}
		//memoizedStatesMinValues.put(state,score);
		return score;
	}

	//consider adding a depth parameter (int depth) to only search tree to a certain depth
	// and add condition to base case if (depth == maxDepth)
	private int maxscore(MachineState state) throws MoveDefinitionException, GoalDefinitionException, TransitionDefinitionException {
		if (stateMachine.isTerminal(state)) {
			int goal = stateMachine.getGoal(state,role);
			memoizedStatesMaxValues.put(state, goal);
			return goal;
		}
		if(memoizedStatesMaxValues.containsKey(state)){
			//System.out.println("Memoized value!");
			return memoizedStatesMaxValues.get(state);
		}
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		int score = Integer.MIN_VALUE;
		Collections.shuffle(moves);
		for (Move move : moves) {
			int result = minscore(move, state);
			if (result > score) {
				score = result;
			}
		}
		memoizedStatesMaxValues.put(state,score);
		return score;
	}

}
