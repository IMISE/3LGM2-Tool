package de.imise.tool3lgm.graphtools.consistency.error.condition;

import de.imise.tool3lgm.graphtools.consistency.error.solution.ErrorSolution;

/**
 * @author AXS (14.09.2020)
 */
public abstract class ErrorCheckCondition {

    /**
     *
     */
    public final ErrorSolution errorSolution;

    /**
     * @param errorSolution
     */
    public ErrorCheckCondition(final ErrorSolution errorSolution) {
        this.errorSolution = errorSolution;
    }

    /**
     * @return
     */
    public final ErrorSolution getSolution() {
        return errorSolution;
    }

}
