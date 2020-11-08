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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (errorSolution == null ? 0 : errorSolution.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ErrorCheckCondition other = (ErrorCheckCondition) obj;
        if (errorSolution == null) {
            if (other.errorSolution != null) {
                return false;
            }
        } else if (!errorSolution.equals(other.errorSolution)) {
            return false;
        }
        return true;
    }

}
