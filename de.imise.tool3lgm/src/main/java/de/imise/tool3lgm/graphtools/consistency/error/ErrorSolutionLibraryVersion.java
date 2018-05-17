/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency.error;

/**
 * Diese Datei ist aktuell überflüssig und nur noch drin, damit man erkennen kann, wie die ErrorSolutions funktionieren.
 * Im Grunde ist das nur dazu da, im Fehlerfall an einen besseren Ort (Tab in einem Eigenschaftsdialog) zu lenken,
 * als den Dialog des Elementes, bei dem bei einer Edge ein Kardinalitätsfehler besteht, direkt den Reiter für diese
 * fehlerhafte Edge zu öffnen.
 *
 * @author AXS
 */
public class ErrorSolutionLibraryVersion {

    // @SuppressWarnings("rawtypes")
    // private static Class[][] abKonfigToAufgabeEdgeClasses = {
    // {
    // AwbkAufOrgVerbindung.class,
    // AufAufOrgVerbindung.class
    // }
    // };
    // @SuppressWarnings("rawtypes")
    // private static Class[][] abKonfigToAnwendungsbausteinEdgeClasses = {
    // {
    // AwbAwbkVerbindung.class
    // }
    // };
    // @SuppressWarnings("rawtypes")
    // private static Class[][] dbKonfigToAnwendungsbausteinEdgeClasses = {
    // {
    // PdvbkAwbVerbindung.class
    // }
    // };

    /**
     * Falls man mal verschiedene Metamodelle gleichzeitug nutzen will, muss man für jede
     * Metamodellversion eine eigene Klasse mit Lösungen anlegen. Daher kann man die Lösungen nicht
     * einfach statisch abfragen.
     */
    public ErrorSolutionLibraryVersion() {
        super();
    }

    // wenn man keine Error-Solution für einen Min oder Max-Fehler findet, wird ein neues
    // PathConnectionPanel angefügt, das den Fehler anzeigt
    // wenn die minimale Kardinalität eines initalSubTypes unterschritten ist, dann einfach einen
    // neuen anlegen (Anwendungsparogramm bei AWB, Orgplan bei KAWB)
    // Kanten ohne start und end einfach löschen (das macht der ModelCleaner beim Einlesen und Speichern)

    //@SuppressWarnings("unchecked")
    private final ErrorSolution[] CARDINALITY_ERROR_SOLUTIONS = {
            //            new ErrorSolution(ABKonfiguration.class, AwbAwbkVerbindung.class, new MetaPath(ABKonfiguration.class, Aufgabe.class, abKonfigToAufgabeEdgeClasses), PathConnectionPanel.class, "ABKonfiguration"),
            //            new ErrorSolution(ABKonfiguration.class, AwbkAufOrgVerbindung.class, new MetaPath(ABKonfiguration.class, Anwendungsbaustein.class, abKonfigToAnwendungsbausteinEdgeClasses), PathConnectionPanel.class, "Aufgabe_p"),
            //            new ErrorSolution(DBKonfiguration.class, PdvbPdvbkVerbindung.class, new MetaPath(DBKonfiguration.class, Anwendungsbaustein.class, dbKonfigToAnwendungsbausteinEdgeClasses), PathConnectionPanel.class, "PhysischerDVBaustein_p"),
    };

    /**
     * Liste aller Elementarten, die bei Unterschreitung der Anzahl der zugehörigen Kantenart sofort
     * gelöscht werden.
     *
     * @return / private final Pair[] MINCARDINALITY_NO_SOLUTION_ERRORS = { new Pair<Class<? extends
     *         ModelElement>, Class<? extends Edge>>(DBKonfiguration.class,
     *         PdvbkAwbVerbindung.class), new Pair<Class<? extends ModelElement>, Class<? extends
     *         Edge>>(ABKonfiguration.class, AwbkAufOrgVerbindung.class), }; /**
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

}
