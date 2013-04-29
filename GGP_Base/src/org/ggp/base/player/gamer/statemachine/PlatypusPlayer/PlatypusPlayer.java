package org.ggp.base.player.gamer.statemachine.PlatypusPlayer;


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

import players.AlphaBetaSubplayer;
import players.MinimaxMonteCarloSubplayer;
import players.MinimaxProximitySubplayer;
import players.MinimaxSubplayer;
import players.MinimaxSubplayerBoundedDepthMobility;
import players.MinimaxSubplayerFocus;
import players.PlayerResult;
import players.SingleSearchPlayer;
import players.TerminalStateProximity;


public class PlatypusPlayer extends StateMachineGamer{

	private static final String PLAYER_NAME = "Platypus";

	private List<Move> optimalSequence = null;
	private PlayerResult playerResult = new PlayerResult();
	private TerminalStateProximity terminalStateProximity;

	@Override
	public StateMachine getInitialStateMachine() {
		// TODO Auto-generated method stub
		return new ProverStateMachine();
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {

		terminalStateProximity = new TerminalStateProximity(timeout-1000, getStateMachine(), getCurrentState(), getRole());

		//		if(getStateMachine().getRoles().size()==1){
		//			/* Single-player game, so try to brute force as much as possible */
		//			optimalSequence = solveSinglePlayerGame(getStateMachine(),getCurrentState());
		//		}

	}



	public List<Move> solveSinglePlayerGame(StateMachine theMachine, MachineState start) throws MoveDefinitionException, GoalDefinitionException, TransitionDefinitionException{
		if(theMachine.isTerminal(start)) {
			if(theMachine.getGoal(start,getRole())==100){
				System.out.println("Solved!");
				return new ArrayList<Move>();
			} else{
				/* No optimal state found */
				return null;
			}
		}
		List<Move> moves = theMachine.getLegalMoves(start, getRole());
		List<Move> bestMoves = null;
		for(Move moveUnderConsideration: moves){
			List<Move> partialBest = solveSinglePlayerGame(theMachine, theMachine.getRandomNextState(start, getRole(), moveUnderConsideration));
			if(partialBest!=null){
				partialBest.add(moveUnderConsideration);
				bestMoves = partialBest;
				break;
			}
		}
		return bestMoves;
	}


	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		long start = System.currentTimeMillis();
		playerResult.setBestMoveSoFar(null);
		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		if(moves.size()==1){
			Move bestMove = moves.get(0);
			long stop = System.currentTimeMillis();
			notifyObservers(new GamerSelectedMoveEvent(moves, bestMove, stop - start));
			return bestMove;
		}
		

		//		if(getStateMachine().getRoles().size()==1){
		//			/* Single-player game */
		//			if(optimalSequence!=null){
		//				/* Best move is the first move in the sequence */
		//				Move bestMove = optimalSequence.remove(optimalSequence.size()-1);
		//				long stop = System.currentTimeMillis();
		//				notifyObservers(new GamerSelectedMoveEvent(moves, bestMove, stop - start));
		//				return bestMove;
		//			}
		//
		//		}




		//Thread singleSearchPlayer = new Thread(new SingleSearchPlayer(getStateMachine(), getRole(), singleSearchPlayerResult,getCurrentState()));

		
		Thread playerThread = new Thread(new MinimaxMonteCarloSubplayer(getStateMachine(), getRole(), playerResult,getCurrentState(), terminalStateProximity, timeout-2000));
		playerThread.start();
		try {
			/* Sleep for 2 seconds less than the maximum time allowed */
			Thread.sleep(timeout-start-2000);
		} catch (InterruptedException e) {
			System.out.println("Done with subplayer!");
			//e.printStackTrace();
		}
		/* Tell the thread searching for the best move it is done so it can exit */
		playerThread.interrupt();
		Move bestMove = playerResult.getBestMoveSoFar();
		System.out.println("--------Best Move--------");
		if (bestMove == null) {
			bestMove = moves.get(new Random().nextInt(moves.size()));
			System.out.println("CHOSE RANDOM");
		}
		long stop = System.currentTimeMillis();
		System.out.println("best move: " + bestMove);
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

}