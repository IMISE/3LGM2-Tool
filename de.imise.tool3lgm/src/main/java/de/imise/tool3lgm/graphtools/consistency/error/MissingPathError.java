package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;

/**
 * @author AXS (21.03.2020)
 */
public class MissingPathError extends AbstractPathError {

    /**
     * @param me
     * @param metaPath
     * @param gdcoll
     */
    public MissingPathError(final ModelElement me, final AbstractMetaPath metaPath, final GDCollection gdcoll) {
        super(me, metaPath, gdcoll);
    }

    @Override
    public String getErrorFieldString() {
        //TODO: "Hier muss noch was sinnvolles hin"
        return "Hier muss noch was sinnvolles hin";
    }

}
