package org.ggp.base.player.gamer.statemachine.PlatypusPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class TerminalStateProximity {
	private static final double COUNTS_TIMEREMAINING = 1000;
	private static final double MONTECARLO_TIMEREMAINING = 5000+COUNTS_TIMEREMAINING;
	private StateMachine stateMachine;
	private MachineState initialState;
	private Map<GdlSentence, Integer> terminalSentenceCounts = new HashMap<GdlSentence, Integer>();
	private int numberTerminalStates;
	int numberGdlSentences;
	
	/* @param timeout the system time by which it must be done */
	public TerminalStateProximity(long timeout, StateMachine stateMachine, MachineState initialState) throws MoveDefinitionException, TransitionDefinitionException{
		this.stateMachine = stateMachine;
		this.initialState = initialState;
		
		/* temporary */
		long terminationTimeLimit = timeout-(long)MONTECARLO_TIMEREMAINING;
		Set<MachineState> randomTerminalStates = generateRandomTerminalStates(terminationTimeLimit);
		
		numberTerminalStates = randomTerminalStates.size();
		
		/* temporary */
		terminalSentenceCounts = generateTerminalSentenceCounts(randomTerminalStates);
	}
	
	
	/**
	 * Uses Monte-Carlo simulation to generate random states until it finds terminal ones; starts from initialstate at each point
	 * @param finishTime the time at which this method stops generating random terminal states
	 * @return a set containing randomly generate terminal state
	 * @throws TransitionDefinitionException 
	 * @throws MoveDefinitionException 
	 */
	private Set<MachineState> generateRandomTerminalStates(long finishTime) throws MoveDefinitionException, TransitionDefinitionException{
		Set<MachineState> terminalStates = new HashSet<MachineState>();
		while(System.currentTimeMillis()<finishTime){
			MachineState currentState = initialState;
			while(!stateMachine.isTerminal(currentState)){
				currentState = stateMachine.getRandomNextState(currentState);
			}
			terminalStates.add(currentState);
		}
		return terminalStates;
	}
	
	/**
	 * For each sentence that appears in a terminal state, counts the number of times it appears in the given terminal states
	 * @param terminalStates sample of terminal states to count GDL sentences in
	 * @return a map from GDL sentences to the number of times they occur in the terminal states
	 */
	private Map<GdlSentence,Integer> generateTerminalSentenceCounts(Set<MachineState> terminalStates){
		Map<GdlSentence, Integer> sentenceCounts = new HashMap<GdlSentence,Integer>();
		for(MachineState state : terminalStates){
			for(GdlSentence sentence : state.getContents()){
				if(sentenceCounts.containsKey(sentence)){
					sentenceCounts.put(sentence, sentenceCounts.get(sentence)+1);
				} else{
					sentenceCounts.put(sentence, 1);
				}
			}
		}
		numberGdlSentences = terminalSentenceCounts.keySet().size();
		return sentenceCounts;
	}
	
	/**
	 * @param state the state to evaluate
	 * @return a real number from 0 to 100 indicating the value of a given state, with 100 being most valuable
	 */
	public double evaluateState(MachineState state){
		
		double heuristic = 0;
		for(GdlSentence sentence : state.getContents()){
			heuristic+=terminalSentenceCounts.get(sentence)*100/numberGdlSentences/numberTerminalStates;
		}
		
		return heuristic;
	}
	
}
