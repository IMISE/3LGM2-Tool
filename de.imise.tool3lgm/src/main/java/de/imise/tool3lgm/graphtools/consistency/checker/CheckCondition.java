package de.imise.tool3lgm.graphtools.consistency.checker;

import de.imise.tool3lgm.graphtools.consistency.ErrorSolution;

/**
 * @author Ich (14.09.2020)
 */
public abstract class CheckCondition {

    /**
     *
     */
    public final ErrorSolution errorSolution;

    /**
     * @param errorSolution
     */
    public CheckCondition(final ErrorSolution errorSolution) {
        this.errorSolution = errorSolution;
    }

    /**
     * @return
     */
    public final ErrorSolution getSolution() {
        return errorSolution;
    }

}
