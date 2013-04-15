package players;

import org.ggp.base.util.statemachine.Move;

public class PlayerResult{
	public Move bestMoveSoFar;
	/* An int 0-100 representing how good the current best move is */
	public int bestMoveScore;
	public PlayerResult(){}

	public synchronized void setBestMoveSoFar(Move move){
		bestMoveSoFar = move;
	}
	public synchronized Move getBestMoveSoFar(){
		return bestMoveSoFar;
	}	
	public void setBestMoveScore(int score){
		bestMoveScore = score;
	}
	public int getBestMoveScore(){
		return bestMoveScore;
	}
}