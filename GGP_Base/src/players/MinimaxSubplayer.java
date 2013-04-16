package players;

import java.util.ArrayList;
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
			PlayerResult playerResult, MachineState currentState) {
		super(stateMachine, role, playerResult, currentState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		try {
			List<Move> moves = stateMachine.getLegalMoves(currentState, role);
			int score = Integer.MIN_VALUE;
			for (Move move : moves) {
				int result = minscore(move, currentState);
				if (result > score) {
					score = result;
					playerResult.setBestMoveSoFar(move);
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
		List<Role> opponents = stateMachine.getRoles();
		int opponentIndex = opponents.get(0) == role ? 1 : 0;
		Role opponent = opponents.get(opponentIndex);
		int score = Integer.MAX_VALUE;
		//List<List<Move>> possibleMoves = getPossibleMoves(role, move);
		List<Move> opponentMoves = stateMachine.getLegalMoves(state, opponent);
		for (Move opponentMove : opponentMoves) {
			List<Move> jointMove = new ArrayList<Move>();
			jointMove.add(move);
			jointMove.add(opponentMove);
			MachineState newState = stateMachine.getNextState(state, jointMove); 
			int result = maxscore(newState);
			if (result < score) {
				score = result;
			}
		}
		return score;
	}

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


	//	@Override
	//	public void run() {
	//		try {
	//			Map<Move, List<MachineState>> moveStateMap = stateMachine.getNextStates(currentState, role);
	//			int valMax = Integer.MIN_VALUE;
	//			int currentStateScore = eval(currentState);
	//			for (Move move : moveStateMap.keySet()) {
	//				List<MachineState> possNextStates = moveStateMap.get(move);
	//				
	//				for (MachineState nextState : possNextStates) {
	//					int val = Math.max(valMax, currentStateScore + minimax(nextState, role, 0));
	//					if (val > valMax) {
	//						valMax = val;
	//						playerResult.setBestMoveSoFar(move);
	//						playerResult.setBestMoveScore(valMax);
	//					}
	//				}
	//			}
	//		} catch (MoveDefinitionException e) {
	//			// TODO Auto-generated catch block
	//		} catch (TransitionDefinitionException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} catch (GoalDefinitionException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//	}

	//consider adding a depth parameter (int depth) to only search tree to a certain depth
	// and add condition to base case if (depth == maxDepth)
	private int minimax(MachineState state, Role currRole, int playersPlayed) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		if (playersPlayed%stateMachine.getRoles().size() == 0 && stateMachine.isTerminal(state)) {
			return stateMachine.getGoal(state, currRole);
		}

		if (currRole == role) {
			int val = Integer.MIN_VALUE;

			for (Move move : stateMachine.getLegalMoves(state, currRole)) {
				//look at all possible states instead of just a random one
				// use stateMachine.getNextStates(state, currRole); to get map<Move, List<MachineState>>
				MachineState randomNextState = stateMachine.getRandomNextState(state, currRole, move);
				val = Math.max(val, minimax(randomNextState, getNextRole(currRole), ++playersPlayed));
			}
			return val;
		} else {
			int val = Integer.MAX_VALUE;
			for (Move move : stateMachine.getLegalMoves(state, currRole)) {
				//look at all possible states instead of just a random one
				MachineState randomNextState = stateMachine.getRandomNextState(state, currRole, move);
				val = Math.min(val, minimax(randomNextState, getNextRole(currRole), ++playersPlayed));
			}
			return val;
		}
	}

	private Role getNextRole(Role currRole) {
		Map<Role, Integer> roleMap = stateMachine.getRoleIndices();
		int nextRoleIndex = (roleMap.get(currRole) + 1) % stateMachine.getRoles().size();
		Role nextRole = stateMachine.getRoles().get(nextRoleIndex);
		return nextRole;
	}

	private int eval(MachineState machineState) {
		// TODO Auto-generated method stub
		return 0;
	}

}
