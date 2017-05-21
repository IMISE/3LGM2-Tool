/**
 * 
 */
package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.dialog.panel.PathConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.AwbAufPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.PDVBKonfPanel;
import de.imise.tool3lgm.graphtools.elements.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.DbsDatVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.DoksDokVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtntKommstVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.OrgAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.graphtools.elements.node.ABKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsprogramm;
import de.imise.tool3lgm.graphtools.elements.node.AufOrgKombination;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.DBKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.Datensatztyp;
import de.imise.tool3lgm.graphtools.elements.node.Dokumententyp;
import de.imise.tool3lgm.graphtools.elements.node.EtntEtdtKombination;
import de.imise.tool3lgm.graphtools.path.MetaPath;

/**
 * @author AXS
 */
public class ErrorSolutionLibraryVersion {

    @SuppressWarnings("rawtypes")
    private static Class[][] abKonfigToAufgabeEdgeClasses = {
        {
                AwbkAufOrgVerbindung.class, AufAufOrgVerbindung.class
        }
    };
    @SuppressWarnings("rawtypes")
    private static Class[][] abKonfigToAnwendungsbausteinEdgeClasses = {
        {
            AwbAwbkVerbindung.class
        }
    };
    @SuppressWarnings("rawtypes")
    private static Class[][] dbKonfigToAnwendungsbausteinEdgeClasses = {
        {
            PdvbkAwbVerbindung.class
        }
    };

    /**
     * Falls man mal verschiedene Metamodelle gleichzeitug nutzen will, muss man für jede
     * Metamodellversion eine eigene Klasse mit Lösungen anlegen. Daher kann man die Lösungen nicht
     * einfach statisch abfragen.
     */
    public ErrorSolutionLibraryVersion() {
        super();
    }

    // wenn man keine Error-Solution für einen Min oder Max-Fehler findet, wird ein neues
    // OneToNUndirectedConnectionPanel angefügt, das den Fehler anzeigt
    // wenn die minimale Kardinalität eines initalSubTypes unterschritten ist, dann einfach einen
    // neuen anlegen (Anwendungsparogramm bei AWB, Orgplan bei KAWB)
    // Kanten ohne start und end einfach löschen

    @SuppressWarnings("unchecked")
    private final ErrorSolution[] CARDINALITY_ERROR_SOLUTIONS = {
            new ErrorSolution(AufOrgKombination.class, AufAufOrgVerbindung.class, NConnectionPanel.class, "Aufgabe"),
            new ErrorSolution(ABKonfiguration.class, AwbAwbkVerbindung.class, new MetaPath(ABKonfiguration.class, Aufgabe.class, abKonfigToAufgabeEdgeClasses), PathConnectionPanel.class, "ABKonfiguration"),
            new ErrorSolution(ABKonfiguration.class, AwbkAufOrgVerbindung.class, new MetaPath(ABKonfiguration.class, Anwendungsbaustein.class, abKonfigToAnwendungsbausteinEdgeClasses), AwbAufPanel.class, "Aufgabe_p"),
            new ErrorSolution(Anwendungsprogramm.class, RawbAwpVerbindung.class, NConnectionPanel.class, "Anwendungsbaustein"),
            // new ErrorSolution(Bausteinschnittstelle.class, AwbKommssVerbindung.class, ???),
            // new ErrorSolution(Anwendungsprogramm.class, AwpSwpVerbindung.class) nur max
            // BssKommstVerbindung.class, Bausteinschnittstelle.class, nur max
            new ErrorSolution(Datensatztyp.class, DbsDatVerbindung.class, NConnectionPanel.class, "Datenbanksystem"), new ErrorSolution(Dokumententyp.class, DoksDokVerbindung.class, NConnectionPanel.class, "Dokumentensammlung"),
            new ErrorSolution(Dokumententyp.class, DoksDokVerbindung.class, NConnectionPanel.class, "Dokumentensammlung"), new ErrorSolution(EtntEtdtKombination.class, EtntKommstVerbindung.class, NConnectionPanel.class, "Kommunikationsstandard"),
            new ErrorSolution(AufOrgKombination.class, OrgAufOrgVerbindung.class, NConnectionPanel.class, "Organisationseinheit"),
            new ErrorSolution(DBKonfiguration.class, PdvbPdvbkVerbindung.class, new MetaPath(DBKonfiguration.class, Anwendungsbaustein.class, dbKonfigToAnwendungsbausteinEdgeClasses), PDVBKonfPanel.class, "PhysischerDVBaustein_p"),
    };

    /**
     * Liste aller Elementarten, die bei Unterschreitung der Anzahl der zugehörigen Kantenart sofort
     * gelöscht werden.
     * 
     * @return / private final Pair[] MINCARDINALITY_NO_SOLUTION_ERRORS = { new Pair<Class<? extends
     *         ModelElement>, Class<? extends Kante>>(DBKonfiguration.class,
     *         PdvbkAwbVerbindung.class), new Pair<Class<? extends ModelElement>, Class<? extends
     *         Kante>>(ABKonfiguration.class, AwbkAufOrgVerbindung.class), }; /**
     * @param error
     * @return
     */
    public final ErrorSolution getSolution(final AbstractError error) {
        for (ErrorSolution es : CARDINALITY_ERROR_SOLUTIONS) {
            if (es.getTargetClass().isAssignableFrom(error.getModelElement().getClass()) && es.getEdgeClass() == error.errorField) {
                return es;
            }
        }
        return null;
    }

    // /**
    // * @return
    // */
    // public final Pair<Class<? extends ModelElement>, Class<? extends Kante>>[]
    // getMinCardinalityNoSolutuinErrors(){
    // return MINCARDINALITY_NO_SOLUTION_ERRORS;
    // }

}
