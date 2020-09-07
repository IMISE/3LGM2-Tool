package de.imise.tool3lgm.graphtools.consistency.checker;

import java.util.Collection;

import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.model.GDCollection;

/**
 * @author AXS (22.03.2020)
 */
public interface ConsistencyErrorChecker {

    /**
     * @param gdcoll the model to check for inconsistencies
     * @return all errors this checker found
     */
    public default Collection<AbstractConsistencyError> getErrors(final GDCollection gdcoll) {
        return getErrors(gdcoll, false);
    }

    /**
     * @param gdcoll the model to check for inconsistencies
     * @return <code>true</code> if there is at least one error in the model
     */
    public default boolean hasErrors(final GDCollection gdcoll) {
        return !getErrors(gdcoll, true).isEmpty();
    }

    /**
     * @param gdcoll the model to check for inconsistencies
     * @param checkOnly if <code>true</code> not all but only the first error will be added to the return list
     * @return all errors this checker found or only the first error if <code>checkOnly</code> is <code>true</code>
     */
    public Collection<AbstractConsistencyError> getErrors(final GDCollection gdcoll, final boolean checkOnly);

    /**
     * @return the type of the errors returned by this checker
     */
    public Class<? extends AbstractConsistencyError> getErrorType();

}
