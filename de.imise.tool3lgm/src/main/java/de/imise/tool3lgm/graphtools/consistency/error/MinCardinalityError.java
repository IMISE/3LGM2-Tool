/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;

/**
 * @author AXS
 * @created 13.09.2008
 */
public class MinCardinalityError extends AbstractCardinalityError {

    /**
     * @param me
     * @param elementaryMetaPath
     * @param cardValue
     * @param gdcoll
     */
    public MinCardinalityError(final ModelElement me, final ElementaryMetaPath elementaryMetaPath, final GDCollection gdcoll, final int cardValue) {
        super(me, elementaryMetaPath, gdcoll, cardValue);
    }

}
