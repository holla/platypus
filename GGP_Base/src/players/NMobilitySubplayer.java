package players;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

/*** NOT TESTED ***/

public class NMobilitySubplayer extends Subplayer{
	
//	private class Tuple<X,Y>(){
//		public final X x;
//		public final Y y;
//		public Tuple(X x, Y y){
//			this.x = x;
//			this.y = y;
//		}
//	}
	
	
	public static HashMap<MachineState, Integer> neighborsCache = new HashMap<MachineState, Integer>();
	
	public NMobilitySubplayer(StateMachine stateMachine, Role role,
			PlayerResult playerResult, MachineState currentState, Logger log) {
		super(stateMachine, role, playerResult, currentState, log);
		
	}
	@Override
	public void run() {
		try {
			List<Move> moves = stateMachine.getLegalMoves(currentState, role);
			double score = Integer.MIN_VALUE;
			Move bestMoveSoFar = null;
			for (Move move : moves) {
				if (Thread.currentThread().isInterrupted()) return;
				double result = minscore(move, currentState);
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
	
	

	private double minscore(Move move, MachineState state) throws MoveDefinitionException, GoalDefinitionException, TransitionDefinitionException {
		if(Thread.currentThread().isInterrupted()) return Integer.MAX_VALUE;
		double score = Double.MAX_VALUE;
		List<List<Move>> jointMoves = stateMachine.getLegalJointMoves(state, role, move);
		Collections.shuffle(jointMoves);
		for (int i = 0; i < jointMoves.size(); i++) {
			List<Move> jointMove = jointMoves.get(i);
			MachineState newState = stateMachine.getNextState(state, jointMove);
			double result = maxscore(newState);
			if (result < score) {
				score = result;
			}
		}
		//memoizedStatesMinValues.put(state,score);
		return score;
	}

	//consider adding a depth parameter (int depth) to only search tree to a certain depth
	// and add condition to base case if (depth == maxDepth)
	private double maxscore(MachineState state) throws MoveDefinitionException, GoalDefinitionException, TransitionDefinitionException {
		if(Thread.currentThread().isInterrupted()) return Integer.MAX_VALUE;
		if (stateMachine.isTerminal(state)) {
			double goal = getMobilityNormalized(state, role, 2);
			return goal;
		}
		if(playerResult.containsMemoizedState(state)){
			//System.out.println("Memoized value!");
			return playerResult.getMemoizedState(state);
		}
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		double score = Integer.MIN_VALUE;
		Collections.shuffle(moves);
		for (Move move : moves) {
			double result = minscore(move, state);
			if (result > score) {
				score = result;
			}
		}
		if(!Thread.currentThread().isInterrupted()){
			playerResult.putMemoizedState(state,new Double(score));
		}
		return score;
	}
	
	
	public double normalizeNumber(double num){
		return (1.0 - (1.0 / num));
	}
	
	public double getMobilityNormalized(MachineState state, Role role, int n, HashSet<MachineState> set){
		return normalizeNumber(getMobility(state, role, n, set));
	}
	
	public double getMobilityNormalized(MachineState state, Role role, int n){
		return normalizeNumber(getMobility(state, role, n, new HashSet<MachineState>()));
	}
	
	/* Return number of states that the current role  can end up in after n turns */
	
	public double getMobility(MachineState state, Role role, int n, HashSet<MachineState> reachedStates){
		try {
		if (reachedStates.contains(state)){
			return 0;
		}	
		else if (n <= 0){
			return 0;
		}
		else if (n==1){
			return stateMachine.getLegalMoves(state,role).size();	
		}
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		Map<Move, List<MachineState> > nextStates = stateMachine.getNextStates(state, role);
		int totalNumberOfMoves = 0;
		for (Move move : moves){
			List<MachineState> possibleNextStates = nextStates.get(move);
			for (MachineState nextState: possibleNextStates){
				totalNumberOfMoves += getMobility(nextState, role, n-1, reachedStates);
			}
		}
		return totalNumberOfMoves;
		
		} catch (MoveDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransitionDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
		
		
		
		
	}
	
}
