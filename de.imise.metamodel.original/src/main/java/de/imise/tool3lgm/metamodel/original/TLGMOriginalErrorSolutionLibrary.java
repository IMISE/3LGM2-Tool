package de.imise.tool3lgm.metamodel.original;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.consistency.ErrorSolution;
import de.imise.tool3lgm.graphtools.consistency.ErrorSolutionLibrary;
import de.imise.tool3lgm.graphtools.dialog.element.panel.PathConnectionPanel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPathCreator;
import de.imise.tool3lgm.metamodel.original.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.metamodel.original.node.ABKonfiguration;
import de.imise.tool3lgm.metamodel.original.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Aufgabe;
import de.imise.tool3lgm.metamodel.original.node.DBKonfiguration;

/**
 * @author AXS (24.03.2020)
 */
public class TLGMOriginalErrorSolutionLibrary extends ErrorSolutionLibrary {

    // wenn man keine Error-Solution für einen Min oder Max-Fehler findet, wird ein neues
    // PathConnectionPanel angefügt, das den Fehler anzeigt wenn die minimale Kardinalität
    //eines initalSubTypes unterschritten ist, dann einfach einen neuen anlegen
    //(Anwendungsparogramm bei AWB, Orgplan bei KAWB) Kanten ohne start und end einfach
    //löschen (das macht der ModelCleaner beim Einlesen und Speichern)

    @Override
    protected final Collection<ErrorSolution> getErrorSolutions() {
        MetaModel metaModel = getMetaModel();
        SimpleMetaPath smp;

        smp = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, ABKonfiguration.class, Aufgabe.class, AwbkAufOrgVerbindung.class, AufAufOrgVerbindung.class);
        ErrorSolution solution1 = new ErrorSolution(metaModel, ABKonfiguration.class, AwbAwbkVerbindung.class, smp, PathConnectionPanel.class, "Anwendungsbaustein_p");

        smp = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, ABKonfiguration.class, Anwendungsbaustein.class, AwbAwbkVerbindung.class);
        ErrorSolution solution2 = new ErrorSolution(metaModel, ABKonfiguration.class, AwbkAufOrgVerbindung.class, smp, PathConnectionPanel.class, "Aufgabe_p");

        smp = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, DBKonfiguration.class, Anwendungsbaustein.class, PdvbkAwbVerbindung.class);
        ErrorSolution solution3 = new ErrorSolution(metaModel, DBKonfiguration.class, PdvbPdvbkVerbindung.class, smp, PathConnectionPanel.class, "PhysischerDVBaustein_p");
        return ImmutableList.of(solution1, solution2, solution3);
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
        return TLGMOriginalMetaModel.class;
    }
}
