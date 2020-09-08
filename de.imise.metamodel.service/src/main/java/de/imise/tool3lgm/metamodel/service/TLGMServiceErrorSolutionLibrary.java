package de.imise.tool3lgm.metamodel.service;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.consistency.ErrorSolution;
import de.imise.tool3lgm.graphtools.consistency.ErrorSolutionLibrary;
import de.imise.tool3lgm.graphtools.dialog.panel.InstanciationPathPanel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPathCreator;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActor_MustBeGroupedWith_Edge;
import de.imise.tool3lgm.metamodel.service.node.ApplicationSystem;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstance;

/**
 * @author AXS (24.03.2020)
 */
public class TLGMServiceErrorSolutionLibrary extends ErrorSolutionLibrary {

    // wenn man keine Error-Solution für einen Min oder Max-Fehler findet, wird ein neues
    // PathConnectionPanel angefügt, das den Fehler anzeigt wenn die minimale Kardinalität
    //eines initalSubTypes unterschritten ist, dann einfach einen neuen anlegen
    //(Anwendungsparogramm bei AWB, Orgplan bei KAWB) Kanten ohne start und end einfach
    //löschen (das macht der ModelCleaner beim Einlesen und Speichern)

    @Override
    protected final Collection<ErrorSolution> getErrorSolutions() {
        MetaModel metaModel = getMetaModel();
        SimpleMetaPath pathToPropertyDialogElement;

        pathToPropertyDialogElement = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, IheActorInstance.class, ApplicationSystem.class, ApplicationSystem_IheActorInstance_Edge.class);
        ErrorSolution solution1 = new ErrorSolution(metaModel, IheActorInstance.class, IheActor_IheActor_MustBeGroupedWith_Edge.class, pathToPropertyDialogElement, InstanciationPathPanel.class, "IheActor_p");

        return ImmutableList.of(solution1);
    }

    /**
     * Liste aller Elementarten, die bei Unterschreitung der Anzahl der zugehörigen Kantenart sofort
     * gelöscht werden.
     *
     * @return
     */
    //    private final Pair<Class<? extends ModelElement>, Class<? extends Edge>>[] MINCARDINALITY_NO_SOLUTION_ERRORS = {
    //            new Pair<>(DBKonfiguration.class, PdvbkAwbVerbindung.class), new Pair<>(ABKonfiguration.class, AwbkAufOrgVerbindung.class),
    //    };

    @Override
    public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
        return TLGMServiceMetaModel.class;
    }
}
