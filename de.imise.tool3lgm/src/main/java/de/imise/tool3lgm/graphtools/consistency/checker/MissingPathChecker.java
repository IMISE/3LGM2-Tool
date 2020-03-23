package de.imise.tool3lgm.graphtools.consistency.checker;

import java.util.Collection;

import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.model.GDCollection;

/**
 * @author AXS (22.03.2020)
 */
public class MissingPathChecker implements ConsistencyErrorChecker {

    /**
     *
     */
    private final GDCollection gdcoll;

    /**
     * @param gdcoll
     */
    public MissingPathChecker(final GDCollection gdcoll) {
        this.gdcoll = gdcoll;
    }

    @Override
    public Collection<AbstractConsistencyError> getErrors() {
        return null;
    }

}
