package players;

import java.util.HashMap;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;

public class PlayerResult{
	public Move bestMoveSoFar;
	/* An int 0-100 representing how good the current best move is */
	public int bestMoveScore;
	
	public HashMap<MachineState,Integer> memoizedMachineStates = new HashMap<MachineState,Integer>();
	
	public PlayerResult(){}

	public synchronized void setBestMoveSoFar(Move move){
		bestMoveSoFar = move;
	}
	public synchronized Move getBestMoveSoFar(){
		return bestMoveSoFar;
	}	
	
	public synchronized void putMemoizedState(MachineState state, Integer value){
		memoizedMachineStates.put(state, value);
	}
	
	public synchronized Integer getMemoizedState(MachineState state){
		return memoizedMachineStates.get(state);
	}
	
	public synchronized boolean containsMemoizedState(MachineState state){
		return memoizedMachineStates.containsKey(state);
	}
	
	public void setBestMoveScore(int score){
		bestMoveScore = score;
	}
	public int getBestMoveScore(){
		return bestMoveScore;
	}
}