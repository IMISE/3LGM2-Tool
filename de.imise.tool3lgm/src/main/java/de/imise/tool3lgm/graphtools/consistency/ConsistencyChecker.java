package de.imise.tool3lgm.graphtools.consistency;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JTable;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractCardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractError;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractIDError;
import de.imise.tool3lgm.graphtools.consistency.error.ErrorSolution;
import de.imise.tool3lgm.graphtools.consistency.error.ErrorSolutionLibraryVersion;
import de.imise.tool3lgm.graphtools.consistency.error.MaxCardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.MinCardinalityError;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.PathConnectionPanel;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GDCollectionChangeAdapter;
import de.imise.tool3lgm.graphtools.path.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPathHandler;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.PropertyDialogUserFieldPanel;

/**
 * Die Klasse prüft die Konsistenz eines Modells. Es werden alle Kardinalitäten überprüft und
 * fehlerhafte Elemente zurück gegeben.
 *
 * @author AXS created on 06.08.2008
 */
public final class ConsistencyChecker extends GDCollectionChangeAdapter {

    /** Checks the consistency of a model. This instance is used for the current selected Model */
    private static ConsistencyChecker consistencyChecker;

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
    private ConsistencyDefinition consistencyDefinition;

    private final UniqueIDChecker idChecker;

    /**
     * Erzeugt einen neuen <code>ConsistencyChecker</code> mit initialisierter <code>ErrorSolutionLibraryVersion</code>.
     *
     * @param gdcoll
     * @param changeContext
     */
    private ConsistencyChecker(final GDCollection gdcoll, final boolean changeContext) {
        solutionsLibrary = new ErrorSolutionLibraryVersion();
        idChecker = new UniqueIDChecker();
        consistencyDefinition = gdcoll == null ? null : new ConsistencyDefinition(gdcoll.getMetaModel());
        if (changeContext) {
            changeContext(gdcoll);
        } else {
            this.gdcoll = gdcoll;
        }

    }

    /**
     * Legt einen neuen Consistency-Checker an, der sich als Listener beim HauptModell der
     * übergebenen Collection registeriert.
     *
     * @param gdcoll
     */
    public ConsistencyChecker(final GDCollection gdcoll) {
        this(gdcoll, true);
    }

    /**
     * @return <code>true</code>, wenn diese Instanz eine {@link ConsistencyDefinition} besitzt
     */
    private boolean isValid() {
        return consistencyDefinition != null;
    }

    /**
     * @return the ConsistencyChecker instance for the current selected model (GDCollection)
     */
    public static ConsistencyChecker getConsistencyChecker() {
        GDCollection gdcoll = Static.getSelectedGDCollection();
        if (consistencyChecker == null) {
            consistencyChecker = new ConsistencyChecker(gdcoll);
        } else if (consistencyChecker.gdcoll != gdcoll) {
            consistencyChecker.changeContext(gdcoll);
        }
        return consistencyChecker;
    }

    /**
     * Setzt die übergebene Collection als aktuelle Collection
     *
     * @param gscoll
     */
    public void changeContext(final GDCollection gdcoll) {
        if (this.gdcoll != gdcoll) {
            if (this.gdcoll != null) {
                this.gdcoll.removeGDCollectionChangeListener(this);
            }
            this.gdcoll = gdcoll;
            if (this.gdcoll != null) {
                gdcoll.addGDCollectionChangeListener(this);
            }
        }
        resetConsistencyDefinition();
        updateErrorTable();
    }

    /**
     * @param consistencyDefinition
     */
    public void resetConsistencyDefinition() {
        consistencyDefinition = gdcoll != null ? new ConsistencyDefinition(gdcoll.getMetaModel()) : null;
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
        ConsistencyChecker checker = new ConsistencyChecker(gdcoll, false);
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
        // Transaktion mehr offen ist
        if (gdcoll != null && !gdcoll.getTman().isInTransaction()) {
            updateErrorTable();
        }
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
        if (!isValid()) {
            return;
        }
        Class<? extends ModelElement> meClass = me.getClass();
        MetaModel metaModel = gdcoll.getMetaModel();
        ElementaryMetaPathHandler elementaryMetaPathHandler = metaModel.getElementaryMetaPathHandler();
        Class<? extends Edge>[] edgeTypes = metaModel.getEdgeTypes(meClass);
        // nur Elementarten beachten, die wenigstens eine Edge besitzen können
        for (Class<? extends Edge> edgeClass : edgeTypes) {
            EdgeCardinality forwardCardinality = consistencyDefinition.getForwardCardinality(edgeClass);
            EdgeCardinality backwardCardinality = consistencyDefinition.getBackwardCardinality(edgeClass);
            //wenn es keine Min-Max-Fehler geben kann -> weiter
            if (forwardCardinality == ZERO_UNLIMITED && backwardCardinality == ZERO_UNLIMITED) {
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
            int minStartCard = forwardCardinality.min();
            int maxStartCard = forwardCardinality.max();
            int minEndCard = backwardCardinality.min();
            int maxEndCard = backwardCardinality.max();
            boolean meHasStartClass = metaModel.isStartClass(edgeClass, meClass);
            boolean meHasEndClass = metaModel.isEndClass(edgeClass, meClass);

            ElementaryMetaPath forwardElementaryMetaPath = elementaryMetaPathHandler.getForwardMetaPath(edgeClass);
            // Bei Teil-Von-Beziehungen oder Beziehungen bei denen meClass
            // sowohl Start- als auch Endklasse sein können
            if (HasPartEdge.class.isAssignableFrom(edgeClass)) {
                if (meHasStartClass) {
                    if (meIsStartConnections.size() < minStartCard) {
                        returnList.add(new MinCardinalityError(me, forwardElementaryMetaPath, gdcoll, minEndCard));
                    }
                    if (meIsStartConnections.size() > maxStartCard) {
                        returnList.add(new MaxCardinalityError(me, forwardElementaryMetaPath, meIsStartConnections, gdcoll, maxEndCard));
                    }
                }
                if (meHasEndClass) {
                    if (meIsEndConnections.size() < minEndCard) {
                        returnList.add(new MinCardinalityError(me, forwardElementaryMetaPath.getOtherDirection(), gdcoll, minStartCard));
                    }
                    if (meIsEndConnections.size() > maxEndCard) {
                        returnList.add(new MaxCardinalityError(me, forwardElementaryMetaPath.getOtherDirection(), meIsEndConnections, gdcoll, maxStartCard));
                    }
                }
            } else if (meHasStartClass && meHasEndClass) {
                int card = minStartCard < minEndCard ? minEndCard : minStartCard;
                if (connections.size() < card) {
                    returnList.add(new MinCardinalityError(me, forwardElementaryMetaPath, gdcoll, card));
                }
                card = maxStartCard < maxEndCard ? maxStartCard : maxEndCard;
                if (connections.size() > card) {
                    returnList.add(new MaxCardinalityError(me, forwardElementaryMetaPath, connections, gdcoll, card));
                }
            } else if (meHasStartClass) {
                if (connections.size() < minStartCard) {
                    returnList.add(new MinCardinalityError(me, forwardElementaryMetaPath, gdcoll, minStartCard));
                }
                if (connections.size() > maxStartCard) {
                    returnList.add(new MaxCardinalityError(me, forwardElementaryMetaPath, connections, gdcoll, maxStartCard));
                }
            } else if (meHasEndClass) {
                if (connections.size() < minEndCard) {
                    returnList.add(new MinCardinalityError(me, forwardElementaryMetaPath.getOtherDirection(), gdcoll, minEndCard));
                }
                if (connections.size() > maxEndCard) {
                    returnList.add(new MaxCardinalityError(me, forwardElementaryMetaPath.getOtherDirection(), connections, gdcoll, maxEndCard));
                }
            } else {
                System.err.println("Die Edge darf gar nicht für dieses Element existieren!");
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
    private Collection<ModelElement> getSolutionPropertyDialogElement(final AbstractError error) {
        if (error == null) {
            return null;
        }
        ErrorSolution es = solutionsLibrary.getSolution(error);
        if (es == null) {
            return new HashSet<>();
        }
        SimpleMetaPath pathToDialogElement = es.getPathToPropertyDialogElement();
        ModelElement me = error.getModelElement();
        if (pathToDialogElement != null) {
            Collection<ModelElement> connected = MetaPathFunctions.getConnectedElements(me, pathToDialogElement);
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
                ElementaryMetaPath elementaryMetaPath = cardError.getElementaryMetaPath();
                ModelElement me = cardError.getModelElement();
                ElementPropertyDialog dialog = me.getPropertyDialog();
                Class<? extends ModelElement> errorConnectedClass = elementaryMetaPath.getEndClass();
                ElementsNameBuilder elementsNameBuilder = gdcoll.getElementsNameBuilder();
                String tabName = elementaryMetaPath.isSingleConnection() ? elementsNameBuilder.getDisplayableName(errorConnectedClass) : elementsNameBuilder.getDisplayablePluralName(errorConnectedClass);
                int existingTabIndex = dialog.selectTab(tabName, PathConnectionPanel.class);
                ImageIcon icon = Tool3lgmConstants.getIcon("error.gif");
                if (existingTabIndex < 0) {
                    dialog.addPathConnectionPanel(elementaryMetaPath.getEdgeClass());
                    dialog.setLastTabIcon(Tool3lgmConstants.getIcon("error.gif"));
                    dialog.setLastTabTitle(tabName);
                    dialog.selectTab(tabName, PathConnectionPanel.class);
                } else {
                    dialog.setTabIcon(existingTabIndex, icon);
                    dialog.setTabTitle(existingTabIndex, tabName);
                }
                dialog.showDialog();
            } else {
                Collection<ModelElement> solutionPropertyDialogElement = getSolutionPropertyDialogElement(error);
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
