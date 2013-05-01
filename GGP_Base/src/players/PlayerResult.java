package players;

import java.util.ArrayList;
import java.util.HashMap;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;

public class PlayerResult{
	public Move bestMoveSoFar;
	public ArrayList<Move> legitimateMoves = new ArrayList<Move>();
	/* A double 0-100 representing how good the current best move is */
	public double bestMoveScore;
	public Move sureMove;
	public HashMap<MachineState,Integer> memoizedMachineStateGoals = new HashMap<MachineState,Integer>();
	public HashMap<MachineState,Double> memoizedMachineStates = new HashMap<MachineState,Double>();
	
	public PlayerResult(){}

	public synchronized void setBestMoveSoFar(Move move){
		bestMoveSoFar = move;
	}
	public synchronized Move getBestMoveSoFar(){
		return bestMoveSoFar;
	}	
	
	public synchronized void putMemoizedState(MachineState state, Double value){
		memoizedMachineStates.put(state, value);
	}
	
	public synchronized Double getMemoizedState(MachineState state){
		return memoizedMachineStates.get(state);
	}
	
	public synchronized boolean containsMemoizedState(MachineState state){
		return memoizedMachineStates.containsKey(state);
	}
	
	public synchronized void setBestMoveScore(double score){
		bestMoveScore = score;
	}
	public synchronized double getBestMoveScore(){
		return bestMoveScore;
	}
	
	public synchronized int getMemoizedStateGoal(MachineState state){
		return memoizedMachineStateGoals.get(state);
	}
	
	public synchronized void putMemoizedStateGoal(MachineState state, int goal){
		memoizedMachineStateGoals.put(state,goal);
	}
	
	public synchronized boolean containsMemoizedStateGoal(MachineState state){
		return memoizedMachineStateGoals.containsKey(state);
	}
}