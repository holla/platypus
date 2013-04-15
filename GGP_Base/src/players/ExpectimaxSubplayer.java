package players;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class ExpectimaxSubplayer extends Subplayer {

	public ExpectimaxSubplayer(StateMachine stateMachine, Role role,
			PlayerResult playerResult, MachineState currentState) {
		super(stateMachine, role, playerResult, currentState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		try {
			Map<Move, List<MachineState>> moveStateMap = super.stateMachine.getNextStates(super.currentState, super.role);
			int valMax = Integer.MIN_VALUE;
			int currentStateScore = eval(super.currentState);
			for (Move move : moveStateMap.keySet()) {
				List<MachineState> possNextStates = moveStateMap.get(move);
				
				for (MachineState nextState : possNextStates) {
					int val = Math.max(valMax, currentStateScore + minimax(nextState, super.role, 0));
					if (val > valMax) {
						valMax = val;
						super.playerResult.setBestMoveSoFar(move);
						super.playerResult.setBestMoveScore(valMax);
					}
				}
			}
		} catch (MoveDefinitionException e) {
			// TODO Auto-generated catch block
		} catch (TransitionDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//consider adding a depth parameter (int depth) to only search tree to a certain depth
	// and add condition to base case if (depth == maxDepth)
	private int minimax(MachineState state, Role currRole, int playersPlayed) throws MoveDefinitionException, TransitionDefinitionException {
		if (playersPlayed == super.stateMachine.getRoles().size()|| super.stateMachine.isTerminal(state)) {
			return eval(state);
		}
		
		if (currRole == super.role) {
			int val = Integer.MIN_VALUE;
			
			for (Move move : super.stateMachine.getLegalMoves(state, currRole)) {
				//look at all possible states instead of just a random one
				MachineState randomNextState = super.stateMachine.getRandomNextState(state, currRole, move);
				val = Math.max(val, minimax(randomNextState, getNextRole(currRole), ++playersPlayed));
			}
			return val;
		} else {
			int sum = 0;
			for (Move move : super.stateMachine.getLegalMoves(state, currRole)) {
				//look at all possible states instead of just a random one
				MachineState randomNextState = super.stateMachine.getRandomNextState(state, currRole, move);
				sum += minimax(randomNextState, getNextRole(currRole), ++playersPlayed);
			}
			return sum / super.stateMachine.getLegalMoves(state, currRole).size();
		}
	}
	
	private Role getNextRole(Role currRole) {
		Map<Role, Integer> roleMap = super.stateMachine.getRoleIndices();
		int nextRoleIndex = (roleMap.get(currRole) + 1) % super.stateMachine.getRoles().size();
		Role nextRole = super.stateMachine.getRoles().get(nextRoleIndex);
		return nextRole;
	}

	private int eval(MachineState machineState) {
		// TODO Auto-generated method stub
		return 0;
	}

}
