/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency.error.type;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;

/**
 * @author AXS
 * @created 13.09.2008
 */
public class MinCardinalityError extends AbstractCardinalityError {

    /**
     * @param me
     * @param elementaryMetaPath
     * @param cardValue
     */
    public MinCardinalityError(final ModelElement me, final ElementaryMetaPath elementaryMetaPath, final int cardValue) {
        super(me, elementaryMetaPath, cardValue);
    }

}
