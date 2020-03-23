package de.imise.tool3lgm.graphtools.consistency.checker;

import java.util.Collection;

import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.model.GDCollection;

/**
 * @author AXS (22.03.2020)
 */
public interface ConsistencyErrorChecker {

    /**
     * @return all errors this checker found
     */
    public Collection<AbstractConsistencyError> getErrors(GDCollection gdcoll);

}
