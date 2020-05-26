/**
 *
 */
package de.imise.tool3lgm.graphtools.model;

import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;

/**
 * @author AXS
 */
public interface GDCollectionOwner extends MetaModelSpecific {

    /**
     * Liefert die {@link GDCollection}, die mit diesem Objekt assoziiert ist.
     *
     * @return
     */
    public GDCollection getCollection();

    @Override
    default Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
        GDCollection gdcoll = getCollection();
        return gdcoll.getMetaModelDefinitionClass();
    }

}
