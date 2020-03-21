package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;

/**
 * @author AXS (Sa, 21.03.2020, 8:14' (Corona time...))
 */
public abstract class AbstractPathError extends AbstractConsistencyError {

    /**
     * @param me
     * @param elementaryMetaPath
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