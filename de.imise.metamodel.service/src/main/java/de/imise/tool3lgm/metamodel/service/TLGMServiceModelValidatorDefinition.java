package de.imise.tool3lgm.metamodel.service;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.consistency.ModelValidatorDefinition;
import de.imise.tool3lgm.graphtools.consistency.error.condition.MissingPathErrorCheckCondition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPathCreator;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActor_MustBeGroupedWith_Edge;
import de.imise.tool3lgm.metamodel.service.node.ApplicationSystem;
import de.imise.tool3lgm.metamodel.service.node.IheActor;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstance;

/**
 * @author AXS (24.03.2020)
 */
public class TLGMServiceModelValidatorDefinition extends ModelValidatorDefinition {

    @Override
    public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
        return TLGMServiceMetaModel.class;
    }

    // wenn man keine Error-Solution für einen Min oder Max-Fehler findet, wird ein neues
    // PathConnectionPanel angefügt, das den Fehler anzeigt wenn die minimale Kardinalität
    //eines initalSubTypes unterschritten ist, dann einfach einen neuen anlegen
    //(Anwendungsparogramm bei AWB, Orgplan bei KAWB) Kanten ohne start und end einfach
    //löschen (das macht der ModelCleaner beim Einlesen und Speichern)

    /**
     * Liste aller Elementarten, die bei Unterschreitung der Anzahl der
     * zugehörigen Kantenart sofort gelöscht werden. Das hier ist nur
     * beispielhaft aus dem
     *
     * @return
     */
    //    private final Pair<Class<? extends ModelElement>, Class<? extends Edge>>[] MINCARDINALITY_NO_SOLUTION_ERRORS = {
    //            new Pair<>(DBKonfiguration.class, PdvbkAwbVerbindung.class), new Pair<>(ABKonfiguration.class, AwbkAufOrgVerbindung.class),
    //    };

    @Override
    public Collection<MissingPathErrorCheckCondition> getMissingPathErrorCheckConditions() {
        //Der folgende MetaPfad beschreibt die nicht erfüllte must-be-grouped-with-Beziehung ausgehend vom Anwendungssystem. Fehlt eine must-be-grouped-with-Beziehung,
        //dann kommt der Fehler, dass diesem Anwendungssystem eine weitere IheActorInsatnce zugeordnet werden muss. Hat irgendeine zu gruppierende IheActorInstance gar
        //kein Anwendungssystem, dann kommt kein Fehler.
        SimpleMetaPathCreator smpc = new SimpleMetaPathCreator(this);
        SimpleMetaPath toRealStartElements = smpc.createSimpleMetaPath(ApplicationSystem.class, IheActorInstance.class, ApplicationSystem_IheActorInstance_Edge.class);
        SimpleMetaPath toConnectableElements = smpc.createSimpleMetaPath(IheActorInstance.class, IheActor.class, IheActor_IheActorInstance_Edge.class, IheActor_IheActor_MustBeGroupedWith_Edge.class);
        SimpleMetaPath toConnectedElements = smpc.createSimpleMetaPath(IheActorInstance.class, IheActor.class, ApplicationSystem_IheActorInstance_Edge.class, ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class);
        SimpleMetaPath toSolveTheErrorMetaPath = smpc.createSimpleMetaPath(ApplicationSystem.class, IheActor.class, ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class);
        MissingPathErrorCheckCondition missingPathCondition = new MissingPathErrorCheckCondition(toRealStartElements, toConnectableElements, toConnectedElements, "CONSISTENCY_ERROR_ApplicationSystem_needsGroupingOf_IheActorInstances",
                toSolveTheErrorMetaPath);
        return ImmutableSet.of(missingPathCondition);
    }

}
