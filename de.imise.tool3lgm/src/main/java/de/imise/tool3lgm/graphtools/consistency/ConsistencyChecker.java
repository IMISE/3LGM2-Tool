package de.imise.tool3lgm.graphtools.consistency;

import static de.imise.tool3lgm.graphtools.metamodel.Edge.isEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.isStartClass;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JTable;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractCardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractError;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractIDError;
import de.imise.tool3lgm.graphtools.consistency.error.ErrorSolution;
import de.imise.tool3lgm.graphtools.consistency.error.ErrorSolutionLibraryVersion;
import de.imise.tool3lgm.graphtools.consistency.error.MaxCardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.MinCardinalityError;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.PathConnectionPanel;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentAdapter;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.path.PathFinder;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.PropertyDialogUserFieldPanel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * Die Klasse prüft die Konsistenz eines Modells. Es werden alle Kardinalitäten überprüft und
 * fehlerhafte Elemente zurück gegeben.
 *
 * @author AXS created on 06.08.2008
 */
public class ConsistencyChecker extends GraphDocumentAdapter {

    /**
     * Modell, das überprüft wird.
     */
    private GDCollection gdcoll;

    /**
     * Katalog der Lösungen zu den Fehlern
     */
    private final ErrorSolutionLibraryVersion solutionsLibrary;

    /**
     * Die Kardinalitäts und Fehlerdefinitionen für die bei der Prüfung relevanten Kanten. Wenn
     * diese Variable <code>null</code> ist, werden alle Kanten mit ihren Originalen Kardinalitäten
     * geprüft.
     */
    private ConsistencyDefinition consistencyDefinition = new ConsistencyDefinition();

    private final UniqueIDChecker idChecker;

    /**
     * Erzeugt einen neuen <code>ConsistencyChecker</code> mit initialisierter <code>ErrorSolutionLibraryVersion</code>.
     */
    private ConsistencyChecker() {
        super();
        solutionsLibrary = new ErrorSolutionLibraryVersion();
        idChecker = new UniqueIDChecker();
    }

    /**
     * Legt einen neuen Consistency-Checker an, der sich als Listener beim HauptModell der
     * übergebenen Collection registeriert.
     *
     * @param gdcoll
     */
    public ConsistencyChecker(final GDCollection gdcoll) {
        this();
        changeContext(gdcoll);
    }

    /**
     * Setzt die übergebene Collection als aktuelle Collection
     *
     * @param gscoll
     */
    public void changeContext(final GDCollection gdcoll) {
        if (this.gdcoll != gdcoll) {
            if (this.gdcoll != null) {
                this.gdcoll.removeGraphDocumentListener(this);
            }
            this.gdcoll = gdcoll;
            if (this.gdcoll != null) {
                gdcoll.addGraphDocumentListener(this);
            }
        }
        updateErrorTable();
    }

    /**
     * @param consistencyDefinition
     */
    public void resetConsistencyDefinition() {
        consistencyDefinition = new ConsistencyDefinition();
    }

    /**
     * @return
     */
    public ConsistencyDefinition getConsistencyDefinition() {
        return consistencyDefinition;
    }

    /**
     * Löscht alle Elemente komplett, die fehlerhaft sind, deren Fehler man aber nicht behandeln
     * kann. Darunter fallen alle Fehler, für die eine Error-Solution mit einem gültigen <code>MetaPath</code> zu einem verbundenen Element hinterlegt
     * ist hinterlegt ist, das aber
     * nicht erreichtbar ist, weil auch die Verbindung zu diesem Element fehlt. Somit kann der
     * Fehler nirgends behoben werden und man kann das Element löschen. In Metamodell 2.7 heißt das:
     * Anwendungsbaustein-Konfigurationen ohne einen Anwendungsbautein könnte man im Dialog der
     * Aufgaben an der mit der Anwendungsbaustein-Konfigurationen verbundenen AufOrgKombination
     * beheben. Wenn aber sowohl die Verbindung zur AufOrgKombination oder deren Verbindung zu einer
     * Aufgabe fehlt, dann wird diese Konfiguration einfach gelöscht, da man den Fehler nicht mehr
     * sinnvoll beheben kann. Das gleiche gilt für physische DV-Baustein-Konfigurationen ohne
     * Datenverarbeitungsbausteine. Dies kann man im Dialog der Anwendungsbausteine der
     * Konfiguration beheben. Fehlt aber auch diese Verbindung, dann kann man die Konfiguration
     * löschen.
     */
    public static void clearUnfixableErrors(final GDCollection gdcoll) {
        ConsistencyChecker checker = new ConsistencyChecker();
        checker.gdcoll = gdcoll;
        // dieses Löschen muss man nicht rückgängig machen können -> BulkMode einschalten
        boolean oldBulkMode = checker.gdcoll.isBulkMode();
        checker.gdcoll.setBulkMode(true);
        for (AbstractError err : checker.getAllInconsistencies()) {
            if (!checker.isSolutionExecuteable(err)) {
                ModelElement errorElement = err.getModelElement();
                checker.gdcoll.deleteElement(errorElement, TransactionManager.STANDARD_PID);
            }
        }
        // für alle explizit angegebenen nicht lösbaren Fehler -> lösche die betreffenden Elemente
        // for (Pair<Class<? extends ModelElement>, Class<? extends Edge>> pair :
        // checker.solutionsLibrary.getMinCardinalityNoSolutuinErrors()){
        // ArrayList<ModelElement> elements =
        // gdcoll.getGraphDocument().getModelItems(pair.getFirstItem(), true);
        // for (ModelElement me : elements){
        // //System.err.println(me.getName() + "\t" + me.getHashString() + "\t" +
        // getMinCardinality(me.getClass(), pair.getSecondItem()) + "\t" +
        // me.countConnections(pair.getSecondItem()));
        // if (me.countConnections(pair.getSecondItem()) < getMinCardinality(me.getClass(),
        // pair.getSecondItem()))
        // gdcoll.deleteElement(me, TransactionManager.STANDARD_PID);
        // }
        // }

        checker.gdcoll.setBulkMode(oldBulkMode);
    }

    /**
     * Liefert das Modell, das dieser Checker überprüft.
     *
     * @return überprüftes Modell
     */
    GDCollection getGDCollection() {
        return gdcoll;
    }

    // //////////////////////////////////////////////////
    // Erstellen und Aktualisierern der Fehlertabelle //
    // //////////////////////////////////////////////////

    /**
     * COMMENTME
     */
    private ConsistencyErrorTableGenerator tableGenerator;

    /**
     * Liefert einen JTable, in dem alle Inkonsistenzen aufgelistet werden.
     *
     * @return
     */
    public JTable getErrorTable() {
        if (tableGenerator == null) {
            tableGenerator = new ConsistencyErrorTableGenerator(this);
        }
        return tableGenerator.getTable();
    }

    /**
     * Aktualisiert die Fehlertabelle
     */
    public void updateErrorTable() {
        if (tableGenerator == null) {
            return;
        }
        tableGenerator.updateTable();
    }

    @Override
    public void dataChanged(final GraphDocument source) {
        // nicht abgeschlossene Dialogtransaktionen ignorieren -> erst updaten, wenn keine
        // Transaktion mehr
        // offen ist
        if (!gdcoll.getTman().isInTransaction()) {
            updateErrorTable();
        }
    }

    @Override
    public void elementAdded(final GraphDocument source, final ElementContainer element) {
        dataChanged(source);
    }

    @Override
    public void elementDeleted(final GraphDocument source, final ElementContainer element) {
        dataChanged(source);
    }

    /** Gibt wieder, ob Kardinalitäts-Inkonsistenzen im Modell bestehen */
    public boolean hasCardinalityInconsistencies() {
        return getInconsistencies(AbstractCardinalityError.class).size() > 0;
    }

    public List<AbstractError> getAllInconsistencies() {
        return getInconsistencies(AbstractError.class);
    }

    public List<AbstractError> getCardinalityInconsistencies() {
        return getInconsistencies(AbstractCardinalityError.class);
    }

    public List<AbstractError> getIDInconsistencies() {
        return getInconsistencies(AbstractIDError.class);
    }

    /**
     * @return
     */
    private List<AbstractError> getInconsistencies(final Class<? extends AbstractError> errorClass) {
        List<AbstractError> errors = new ArrayList<>();

        if (gdcoll == null) {
            return errors;
        }

        GraphDocument doc = gdcoll.getMainGraphDocument();

        if (errorClass.isAssignableFrom(AbstractCardinalityError.class)) {
            for (ModelElement me : doc.getModelItems(ModelElement.class, true)) {
                addCardinalityErrors(me, errors);
            }
        }
        if (errorClass.isAssignableFrom(AbstractIDError.class)) {
            Collection<AbstractIDError> idErrors = idChecker.getIDErrors(gdcoll);
            errors.addAll(idErrors);
        }
        return errors;
    }

    /**
     * Fügt der übergebenen Error-Liste alle Kardinalitätsfehler des übergebenen Elementes hinzu.
     *
     * @param me
     * @param returnList
     */
    private void addCardinalityErrors(final ModelElement me, final List<AbstractError> returnList) {
        Class<? extends ModelElement> meClass = me.getClass();
        Class<? extends Edge>[] edgeTypes = ModelConstants.getEdgeTypes(meClass);
        // nur Elementarten beachten, die wenigstens eine Edge besitzen können
        if (edgeTypes != null) {
            for (Class<? extends Edge> edgeClass : edgeTypes) {
                EdgeCardinality startToEndCardinality = consistencyDefinition.getStartToEndCardinality(edgeClass);
                EdgeCardinality endToStartCardinality = consistencyDefinition.getEndToStartCardinality(edgeClass);
                //wenn es keine Min-Max-Fehler geben kann -> weiter
                if (startToEndCardinality == ZERO_UNIMITED && endToStartCardinality == ZERO_UNIMITED) {
                    continue;
                }

                List<Edge> connections = me.getEdges(edgeClass);
                List<Edge> meIsStartConnections = new ArrayList<>();
                List<Edge> meIsEndConnections = new ArrayList<>();
                for (Edge edge : connections) {
                    if (edge.isStart(me)) {
                        meIsStartConnections.add(edge);
                    } else {
                        meIsEndConnections.add(edge);
                    }
                }

                // entweder für die aktuelle Kantenklasse die neu gesetzten Kardinalitäten holen
                // oder die Standardwaerte laden, wenn keine neuen gesetzt wurden
                int minStartCard = startToEndCardinality.min();
                int maxStartCard = startToEndCardinality.max();
                int minEndCard = endToStartCardinality.min();
                int maxEndCard = endToStartCardinality.max();
                boolean meHasStartClass = isStartClass(edgeClass, me.getClass());
                boolean meHasEndClass = isEndClass(edgeClass, me.getClass());

                // Bei Teil-Von-Beziehungen oder Beziehungen bei denen meClass
                // sowohl Start- als auch Endklasse sein können
                if (PartOfBeziehung.class.isAssignableFrom(edgeClass)) {
                    if (meHasStartClass && meIsStartConnections.size() < minStartCard) {
                        returnList.add(new MinCardinalityError(me, edgeClass, gdcoll, minEndCard));
                    }
                    if (meHasStartClass && meIsStartConnections.size() > maxStartCard) {
                        returnList.add(new MaxCardinalityError(me, edgeClass, meIsStartConnections, gdcoll, maxEndCard));
                    }
                    if (meHasEndClass && meIsEndConnections.size() < minEndCard) {
                        returnList.add(new MinCardinalityError(me, edgeClass, gdcoll, minStartCard));
                    }
                    if (meHasEndClass && meIsEndConnections.size() > maxEndCard) {
                        returnList.add(new MaxCardinalityError(me, edgeClass, meIsEndConnections, gdcoll, maxStartCard));
                    }
                } else if (meHasStartClass && meHasEndClass) {
                    int card = minStartCard < minEndCard ? minEndCard : minStartCard;
                    if (connections.size() < card) {
                        returnList.add(new MinCardinalityError(me, edgeClass, gdcoll, card));
                    }
                    card = maxStartCard < maxEndCard ? maxStartCard : maxEndCard;
                    if (connections.size() > card) {
                        returnList.add(new MaxCardinalityError(me, edgeClass, connections, gdcoll, card));
                    }
                } else if (meHasStartClass) {
                    if (connections.size() < minStartCard) {
                        returnList.add(new MinCardinalityError(me, edgeClass, gdcoll, minStartCard));
                    }
                    if (connections.size() > maxStartCard) {
                        returnList.add(new MaxCardinalityError(me, edgeClass, connections, gdcoll, maxStartCard));
                    }
                } else if (meHasEndClass) {
                    if (connections.size() < minEndCard) {
                        returnList.add(new MinCardinalityError(me, edgeClass, gdcoll, minEndCard));
                    }
                    if (connections.size() > maxEndCard) {
                        returnList.add(new MaxCardinalityError(me, edgeClass, connections, gdcoll, maxEndCard));
                    }
                } else {
                    System.err.println("Die Edge darf gar nicht für dieses Element existieren!");
                }
            }
        }
    }

    /**
     * Diese Funktion hat folgende Rückgabewerte:<br />
     * <ol>
     * <li>Wenn für den Fehler eine <code>ErrorSolution</code> gefunden wird, die einen gültigen <code>MetaPath</code> beschreibt, über den ausgehend
     * vom Element des übergebenen Fehlers verbundene Elemente gefunden werden, dann kommen genau diese verbundenen Elemente zurück.</li>
     * <li>Wenn die gleichen Vorbedingungen gelten, wie eben, aber keine verbundenen Elemente gefunden werden, dann kommt <code>null</code>
     * zurück.</li>
     * <li>Wenn für den Fehler eine <code>ErrorSolution</code> gefunden wird, diese aber keinen <code>MetaPath</code> enthält, so kommt eine Liste mit
     * dem ModelElement des Fehlers als einzigem Element zurück</li>
     * <li>Wenn für den Fehler keine <code>ErrorSolution</code> gefunden wurde, kommt eine leere Liste zurück</li>
     * </ol>
     * Zusäztlich dazu wird auch <code>null</code> zurück gegeben, wenn der übergebene Fehler selbst <code>null</code> ist. Das kann man aber vorher
     * ausschließen, so dass die eindeutige
     * Unterscheidung der einzelnen Fehlerarten möglich ist.
     *
     * @param error
     * @return
     */
    private Set<ModelElement> getSolutionPropertyDialogElement(final AbstractError error) {
        if (error == null) {
            return null;
        }
        ErrorSolution es = solutionsLibrary.getSolution(error);
        if (es == null) {
            return new HashSet<>();
        }
        MetaPath pathToDialogElement = es.getPathToPropertyDialogElement();
        ModelElement me = error.getModelElement();
        if (pathToDialogElement != null) {
            Set<ModelElement> connected = PathFinder.getDirectConnectedElements(me, pathToDialogElement);
            if (connected.size() == 0) {
                return null;
            }
            return connected;
        }
        Set<ModelElement> al = new HashSet<>(1);
        al.add(error.getModelElement());
        return al;
    }

    /**
     * Liefert <code>true</code>, wenn es für diesen Fehler eine ausführbare Lösung gibt, sonst <code>false</code>.
     *
     * @param error
     * @return
     */
    public boolean isSolutionExecuteable(final AbstractError error) {
        return getSolutionPropertyDialogElement(error) != null;
    }

    /**
     * @param error
     * @return
     */
    public void execSolution(final AbstractError error) {
        // 'es' ist null, wenn für den Fehler keine Solution hinterlegt wurde. Das gilt nur
        // für Fehler, für die im Eigenschaftsdialog des Elementes dann ein zusätzliches
        // OneToNUndirectedConnectionPanel angezeigt werden soll, in dem man den Fehler beheben kann
        if (error instanceof AbstractCardinalityError) {
            ErrorSolution es = solutionsLibrary.getSolution(error);
            if (es == null) {
                AbstractCardinalityError cardError = (AbstractCardinalityError) error;
                Class<? extends Edge> edgeClass = cardError.getEdgeClass();
                ModelElement me = cardError.getModelElement();
                ElementPropertyDialog dialog = me.getPropertyDialog();
                String tabName = AbstractPathConnectionPanel.generateName(me.getClass(), edgeClass);
                int existingTabIndex = dialog.selectTab(tabName, PathConnectionPanel.class);
                ImageIcon icon = Tool3lgmConstants.getIcon("error.gif");
                if (existingTabIndex < 0) {
                    dialog.addPathConnectionPanel(edgeClass);
                    dialog.setLastTabIcon(Tool3lgmConstants.getIcon("error.gif"));
                    dialog.setLastTabTitle(tabName);
                    dialog.selectTab(tabName, PathConnectionPanel.class);
                } else {
                    dialog.setTabIcon(existingTabIndex, icon);
                    dialog.setTabTitle(existingTabIndex, tabName);
                }
                dialog.showDialog();
            } else {
                Set<ModelElement> solutionPropertyDialogElement = getSolutionPropertyDialogElement(error);
                if (solutionPropertyDialogElement == null || solutionPropertyDialogElement.size() == 0) {
                    return;
                }
                for (ModelElement connected : solutionPropertyDialogElement) {
                    ElementPropertyDialog dialog = connected.getPropertyDialog();
                    dialog.selectTab(es.getPanelName(), es.getPanelClass());
                    dialog.showDialog();
                }
            }
        } else if (error instanceof AbstractIDError) {
            ElementPropertyDialog dialog = error.getModelElement().getPropertyDialog();
            dialog.selectTab(PropertyDialogUserFieldPanel.class);
            dialog.showDialog();
        }
    }
}
