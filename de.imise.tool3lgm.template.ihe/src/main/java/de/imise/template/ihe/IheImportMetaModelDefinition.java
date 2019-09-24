package de.imise.template.ihe;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.MultipleEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.imexport.ImportMetaModelDefinition;

/**
 * Definition der Klassen und Kanten, die aus Protege in 3LGM importiert werden können sollen.
 *
 * @author AXS (10 Jun 2019)
 */
@SuppressWarnings("serial")
public class IheImportMetaModelDefinition extends ImportMetaModelDefinition {

    /** 3LGM2: Actor -> OWL: Actor */
    public static class Actor extends Node {
    }

    /** 3LGM2: Domain -> OWL: Domain */
    public static class Domain extends Node {
    }

    /** 3LGM2: IntegrationProfile -> OWL: IntegrationProfile */
    public static class IntegrationProfile extends Node {
    }

    /** 3LGM2: IheDomain_Edge -> OWL: iheDomain */
    public static class IheDomain_Edge extends SimpleEdge {
        public static final Class<? extends ModelElement> STCL = Domain.class;
        public static final Class<? extends ModelElement> ETCL = IntegrationProfile.class;
    }

    /** 3LGM2: IheIntegrationProfile_Edge -> OWL: iheIntegrationProfile */
    public static class IheIntegrationProfile_Edge extends SimpleEdge {
        public static final Class<? extends ModelElement> STCL = Actor.class;
        public static final Class<? extends ModelElement> ETCL = IntegrationProfile.class;
    }

    /** 3LGM2: IheTransaction_Edge -> OWL: iheTransaction */
    public static class IheTransaction_Edge extends MultipleEdge { //MultipleEdge, weil dieselben Akteure mehrfach über TransactionLinks verbunden sein können
        public static final Class<? extends ModelElement> STCL = Actor.class;
        public static final Class<? extends ModelElement> ETCL = Actor.class;
    }

    @Override
    public Set<Class<? extends Node>> getNodes() {
        return ImmutableSet.of(Domain.class, IntegrationProfile.class, Actor.class);
        //zum Testen kann man die unteren Kombinationen zurück geben lassen (bei den Edges immer dieselbe Position einschalten dann hat man immer 2 Knotenklassen und 1 Kante dazwischen und nicht alle 3 Knoten- und alle 3 Kantenklassen gleichzeitig)
        //        return ImmutableSet.of(Domain.class, IntegrationProfile.class);
        //        return ImmutableSet.of(IntegrationProfile.class, Actor.class);
        //        return ImmutableSet.of(Actor.class);
    }

    @Override
    public Set<Class<? extends Edge>> getEdges() {
        return ImmutableSet.of(IheDomain_Edge.class, IheIntegrationProfile_Edge.class, IheTransaction_Edge.class);
        //        return ImmutableSet.of(IheDomain_Edge.class);
        //        return ImmutableSet.of(IheIntegrationProfile_Edge.class);
        //        return ImmutableSet.of(IheTransaction_Edge.class);
    }

}
