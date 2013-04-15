package players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class AlphaBetaSubplayer extends Subplayer {

	public AlphaBetaSubplayer(StateMachine stateMachine, Role role,
			PlayerResult playerResult, MachineState currentState) {
		super(stateMachine, role, playerResult, currentState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		List<Move> legalMoves;
		try {
			legalMoves = super.stateMachine.getLegalMoves(super.currentState, super.role);
			int alpha;
			int beta;
			int vMax = Integer.MAX_VALUE;
			for (Move move : legalMoves) {
				//pruuuuuune!
			}
		} catch (MoveDefinitionException e) {
			// TODO Auto-generated catch block
		}
	
		
	}

}
