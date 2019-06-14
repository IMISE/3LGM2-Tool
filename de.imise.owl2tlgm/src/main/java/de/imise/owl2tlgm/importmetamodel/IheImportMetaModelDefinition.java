package de.imise.owl2tlgm.importmetamodel;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.imise.owl2tlgm.importmetamodel.edge.IheDomain_Edge;
import de.imise.owl2tlgm.importmetamodel.edge.IheIntegrationProfile_Edge;
import de.imise.owl2tlgm.importmetamodel.edge.IheTransaction_Edge;
import de.imise.owl2tlgm.importmetamodel.node.Actor;
import de.imise.owl2tlgm.importmetamodel.node.Domain;
import de.imise.owl2tlgm.importmetamodel.node.IntegrationProfile;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.imexport.ImportMetaModelDefinition;

/**
 * Definition der Klassen und Kanten, die aus Protege in 3LGM importiert werden können sollen.
 *
 * @author AXS (10 Jun 2019)
 */
public class IheImportMetaModelDefinition extends ImportMetaModelDefinition {

    @Override
    public Set<Class<? extends Node>> getNodes() {
        return ImmutableSet.of(Domain.class, IntegrationProfile.class, Actor.class);
    }

    @Override
    public Set<Class<? extends Edge>> getEdges() {
        return ImmutableSet.of(IheDomain_Edge.class, IheIntegrationProfile_Edge.class, IheTransaction_Edge.class);
    }

}
