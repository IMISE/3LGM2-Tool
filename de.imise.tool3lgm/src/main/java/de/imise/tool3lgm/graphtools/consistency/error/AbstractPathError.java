package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;

/**
 * @author AXS (Sa, 21.03.2020, 8:14' (Corona time...))
 */
public abstract class AbstractPathError extends AbstractConsistencyError {

    /**
     * If the metaPath was initialsed with a resorce key (constructor parameter baseResKeyOrName)
     * and in the resource is a key defined with this baseResKeyOrName and the "_error_descrip"
     * suffix, then the standard description of the error from the tool resources will be replaced
     * by the metamodel specific resocure value of thsi key.
     */
    public static final String PATH_ERROR_DESCRIPTION_RESOURCE_KEY_SUFFIX = "_error_descrip";

    /**
     * @param me
     * @param metaPath
     * @param cardValue
     * @param gdcoll
     */
    public AbstractPathError(final ModelElement me, final AbstractMetaPath metaPath, final GDCollection gdcoll) {
        super(me, metaPath, gdcoll);
    }

    /**
     * @return
     */
    public AbstractMetaPath getMetaPath() {
        return (AbstractMetaPath) errorField;
    }

}