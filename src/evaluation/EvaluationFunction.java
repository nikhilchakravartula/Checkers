package evaluation;

import state.State;

public interface EvaluationFunction {
    /**
     * @param state
     * @param turn
     * @return evaluation function of the state with respect to turn.
     */
    double getEvaluation(State state, int turn);
}
