package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.SimpleMetaPath;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheActor;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheActorInstance;

/**
 * @author AXS (31.01.2018)
 */
public class IheActor_IheActorInstance_Edge extends InstanciationEdge {

    public static final Class<? extends ModelElement> stcl = IheActor.class;

    public static final EdgeCardinality scard = ONE_ONE;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = IheActorInstance.class;

    static {
        addInstanciableMetaPath(IheActor_IheActorInstance_Edge.class,
                new SimpleMetaPath(IheActor.class, IheActorInstance.class, IheActor_IheInterface_Edge.class, IheInvokingInterface_InvokingInterface_Edge.class, ApplicationComponent_CommunicationInterface_Edge.class));
        addInstanciableMetaPath(IheActor_IheActorInstance_Edge.class,
                new SimpleMetaPath(IheActor.class, IheActorInstance.class, IheActor_IheInterface_Edge.class, IheProvidingInterface_ProvidingInterface_Edge.class, ApplicationComponent_CommunicationInterface_Edge.class));
    }

}
