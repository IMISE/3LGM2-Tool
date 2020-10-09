package de.imise.tool3lgm.imexport;

import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition.DefaultMetaModelDefinitionAdapter;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;

/**
 * Grundeklasse zur Definition eines ganz einfachen 3LGM-Metamodells, das für den Import aus externen Datenquellen genutzt werden kann. Es definiert
 * nur eine Menge von Knoten und eine Menge von Kanten.
 *
 * @author AXS (9 Jun 2019)
 */
public abstract class ImportMetaModelDefinition extends DefaultMetaModelDefinitionAdapter {

    @SuppressWarnings("unchecked")
    private final Class<? extends Node>[] nodesArray = getNodes().toArray(new Class[0]);

    @SuppressWarnings("unchecked")
    private final Class<? extends Edge>[] edgesArray = getEdges().toArray(new Class[0]);

    /** @return alle Knotenklassen des Metamodells */
    public abstract Set<Class<? extends Node>> getNodes();

    /** @return alle Kantenklassen des Metamodells */
    public abstract Set<Class<? extends Edge>> getEdges();

    @Override
    public final Class<? extends ModelElement>[] getAllDomainLayerNodes() {
        return nodesArray;
    }

    @Override
    public final Class<? extends Edge>[] getAllEdges() {
        return edgesArray;
    }

    @Override
    public final Class<? extends ModelElement>[] getAllInterDomainLogicalLayerNodes() {
        return super.getAllInterDomainLogicalLayerNodes();
    }

    @Override
    public final Class<? extends ModelElement>[] getAllLogicalLayerNodes() {
        return super.getAllLogicalLayerNodes();
    }

    @Override
    public final Class<? extends ModelElement>[] getAllInterLogicalPhysicalLayerNodes() {
        return super.getAllInterLogicalPhysicalLayerNodes();
    }

    @Override
    public final Class<? extends ModelElement>[] getAllPhysicalLayerNodes() {
        return super.getAllPhysicalLayerNodes();
    }

    @Override
    public final Set<Class<? extends ModelElement>> getImportableNodes() {
        return super.getImportableNodes();
    }

    @Override
    public final Set<Class<? extends ModelElement>> getGenerateNameClasses() {
        return super.getGenerateNameClasses();
    }

}
