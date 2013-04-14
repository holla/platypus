package org.ggp.base.player.gamer.statemachine.FirstPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GameAnalysisException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;


public class FirstPlayer extends StateMachineGamer{
	
	private static final String PLAYER_NAME = "First Player";

	
	public void stateMachineSelectMove(){

	}

	@Override
	public StateMachine getInitialStateMachine() {
		// TODO Auto-generated method stub
		return new ProverStateMachine();
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		// TODO Auto-generated method stub

	}

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		// TODO Auto-generated method stub

		long start = System.currentTimeMillis();
		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		
		/* Thread-safe object used to store the best move so far */
		PlayerResult fullSearchResult = new PlayerResult();
		Thread infinitePlayer = new Thread(new FullSearchPlayer(fullSearchResult));
		
		infinitePlayer.start();
		try {
			/* Sleep for 2 seconds less than the maximum time allowed */
			Thread.sleep(timeout-start-2000);
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
		/* Tell the thread searching for the best move it is done so it can exit */
		infinitePlayer.interrupt();
		Move bestMove = fullSearchResult.getBestMoveSoFar();
		long stop = System.currentTimeMillis();

		notifyObservers(new GamerSelectedMoveEvent(moves, bestMove, stop - start));
		return bestMove;
	}

	@Override
	public void stateMachineStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stateMachineAbort() {
		// TODO Auto-generated method stub

	}

	@Override
	public void analyze(Game g, long timeout) throws GameAnalysisException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return PLAYER_NAME;
	}
	
	@Override
	public DetailPanel getDetailPanel(){
		return new SimpleDetailPanel();
	}
	
	private class FullSearchPlayer implements Runnable{
		/* The maximum number of levels to which this player will descend looking for moves */
		private int max_depth;
		private PlayerResult playerResult;
		
		public FullSearchPlayer(int max_depth){
			this.max_depth = max_depth;
		}
		public FullSearchPlayer(PlayerResult playerResult){
			/* Defaults to an "infinite" player that has no maximum depth */
			this(Integer.MAX_VALUE);
			this.playerResult = playerResult;
		}
		
		@Override
		public void run() {
			List<Move> moves;
			StateMachine theMachine;
			Role role;
			MachineState currentState = getCurrentState();
			List<List<Move>> turnsToProcess = new ArrayList<List<Move>>();
			try {
				theMachine = getStateMachine();
				role = getRole();
				moves = theMachine.getLegalMoves(getCurrentState(), role);
				/* Default Random Move in case of time-out, etc */
				playerResult.setBestMoveSoFar(moves.get(new Random().nextInt(moves.size())));
				
				/* Adds the first turn's worth of moves to consider */
				turnsToProcess.add(moves);
				
				/* Searches to see if it can win and if so wins */
				while(true){
					if(turnsToProcess.isEmpty()) break;
					if(Thread.currentThread().isInterrupted()) break;
					List<Move> currentMoves = turnsToProcess.remove(0);
					for(Move moveUnderConsideration: currentMoves){
						MachineState nextState = theMachine.getRandomNextState(currentState,role,moveUnderConsideration);
						/* If the move allows this player to win, take it */
						if(theMachine.isTerminal(nextState)){
							Integer myScore = theMachine.getGoal(nextState,role);
							if(myScore==100) playerResult.setBestMoveSoFar(moveUnderConsideration);
						}
					}
				}
				
				
				
				
			} catch (MoveDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransitionDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoalDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}		
	}
	
	private class InfiniteRandomPlayer implements Runnable{
		
		private PlayerResult playerResult;
		
		public InfiniteRandomPlayer(PlayerResult playerResult){
			this.playerResult = playerResult;
		}

		@Override
		public void run() {
			List<Move> moves;
			try {
				moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
				/* Keeps generating random moves to be the "best" move until it is told that it is done */
				while(!Thread.currentThread().isInterrupted()){
					playerResult.setBestMoveSoFar(moves.get(new Random().nextInt(moves.size())));
				}
			} catch (MoveDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}		
	}
	
	/* A thread-safe class to communicate results between the main thread and other threads performing computations */
	private class PlayerResult{
		private Move bestMoveSoFar;
		/* An int 0-100 representing how good the current best move is */
		private int bestMovePower;
		public PlayerResult(){}
		
		private synchronized void setBestMoveSoFar(Move move){
			bestMoveSoFar = move;
		}
		private synchronized Move getBestMoveSoFar(){
			return bestMoveSoFar;
		}	
		private void setBestMovePower(int power){
			bestMovePower = power;
		}
		private int getBestMovePower(){
			return bestMovePower;
		}
	}
	
}