package players;

import java.util.List;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class MinimaxSubplayer extends Subplayer {

	public MinimaxSubplayer(StateMachine stateMachine, Role role,
			PlayerResult playerResult, MachineState currentState) {
		super(stateMachine, role, playerResult, currentState);
		// TODO Auto-generated constructor stub
	}
	
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
		int score = Integer.MAX_VALUE;
		List<List<Move>> jointMoves = stateMachine.getLegalJointMoves(state, role, move);
		for (int i = 0; i < jointMoves.size(); i++) {
			List<Move> jointMove = jointMoves.get(i);
			MachineState newState = stateMachine.getNextState(state, jointMove);
			int result = maxscore(newState);
			if (result < score) {
				score = result;
			}
		}
		return score;
	}

	//consider adding a depth parameter (int depth) to only search tree to a certain depth
	// and add condition to base case if (depth == maxDepth)
	private int maxscore(MachineState state) throws MoveDefinitionException, GoalDefinitionException, TransitionDefinitionException {
		if (stateMachine.isTerminal(state)) {
			return stateMachine.getGoal(state, role);
		}
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		int score = Integer.MIN_VALUE;
		for (Move move : moves) {
			int result = minscore(move, state);
			if (result > score) {
				score = result;
			}
		}
		return score;
	}

}
