package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SectionMetaPath;

/**
 * @author AXS (21.03.2020)
 */
public class MissingPathError extends AbstractPathError {

    /**
     * @param me
     * @param metaPath
     * @param gdcoll
     */
    public MissingPathError(final ModelElement me, final SectionMetaPath metaPath, final GDCollection gdcoll) {
        super(me, metaPath, gdcoll);
    }

    @Override
    public String getErrorFieldString() {
        SectionMetaPath metaPath = (SectionMetaPath) getMetaPath();
        AbstractMetaPath firstMetaPath = metaPath.getFirstMetaPath();
        ElementaryMetaPath lastElementaryMetaPath = firstMetaPath.getLastElementaryMetaPath();
        return lastElementaryMetaPath.getFullName();
    }

}
