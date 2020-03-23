package de.imise.tool3lgm.graphtools.consistency.checker;

import java.util.ArrayList;
import java.util.Collection;

import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.model.GDCollection;

/**
 * @author AXS (22.03.2020)
 */
public class MissingPathChecker implements ConsistencyErrorChecker {

    @Override
    public Collection<AbstractConsistencyError> getErrors(final GDCollection gdcoll) {
        ArrayList<AbstractConsistencyError> errors = new ArrayList<>();
        return errors;
    }

}
