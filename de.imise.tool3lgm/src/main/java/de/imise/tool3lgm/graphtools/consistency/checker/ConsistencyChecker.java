package de.imise.tool3lgm.graphtools.consistency.checker;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JTable;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmChangeListener;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.consistency.ConsistencyDefinition;
import de.imise.tool3lgm.graphtools.consistency.ErrorSolution;
import de.imise.tool3lgm.graphtools.consistency.ErrorSolutionLibraryVersion;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractCardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractIDError;
import de.imise.tool3lgm.graphtools.consistency.error.MaxCardinalityError;
import de.imise.tool3lgm.graphtools.consistency.tableview.ConsistencyErrorTableGenerator;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.PathConnectionPanel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.path.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.PropertyDialogUserFieldPanel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Die Klasse prüft die Konsistenz eines Modells. Es werden alle Kardinalitäten überprüft und
 * fehlerhafte Elemente zurück gegeben.
 *
 * @author AXS created on 06.08.2008
 */
public final class ConsistencyChecker implements LGMChangeListenerSimple, Tool3lgmChangeListener, PropertyChangeListener {

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

    /**
     *
     */
    private final UniqueIDChecker idChecker;

    /**
     * Erzeugt einen neuen <code>ConsistencyChecker</code> mit initialisierter <code>ErrorSolutionLibraryVersion</code>.
     *
     * @param gdcoll
     * @param changeContext
     */
    private ConsistencyChecker(final GDCollection gdcoll, final boolean changeContext) {
        solutionsLibrary = new ErrorSolutionLibraryVersion();
        idChecker = new UniqueIDChecker(gdcoll);
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
     * @return the ConsistencyChecker instance for the current selected model (GDCollection)
     */
    public static ConsistencyChecker getConsistencyChecker() {
        GDCollection gdcoll = Static.getSelectedGDCollection();
        if (consistencyChecker == null) {
            consistencyChecker = new ConsistencyChecker(gdcoll);
        } else if (consistencyChecker.gdcoll != gdcoll) {
            consistencyChecker.changeContext(gdcoll);
        }
        UserProperties.addPropertyChangeListener(consistencyChecker);
        consistencyChecker.addAsToolChangeListener();
        return consistencyChecker;
    }

    /**
     * Setzt die übergebene Collection als aktuelle Collection
     *
     * @param gscoll
     */
    private void changeContext(final GDCollection gdcoll) {
        if (this.gdcoll != gdcoll) {
            if (this.gdcoll != null) {
                this.gdcoll.removeClosedTransactionsListener(this);
            }
            this.gdcoll = gdcoll;
            if (this.gdcoll != null) {
                gdcoll.addClosedTransactionsListener(this);
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
        boolean oldBulkMode = checker.gdcoll.setBulkMode(true);
        for (AbstractConsistencyError err : checker.getAllInconsistencies()) {
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
    public GDCollection getCollection() {
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
    private void updateErrorTable() {
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

    @Override
    public void elementNameChanged(final ElementContainer source) {
        dataChanged(null); //doc ist egal
    }

    /** Gibt wieder, ob Kardinalitäts-Inkonsistenzen im Modell bestehen */
    public boolean hasCardinalityInconsistencies() {
        return getInconsistencies(AbstractCardinalityError.class).size() > 0;
    }

    /**
     * @return
     */
    public Collection<AbstractConsistencyError> getAllInconsistencies() {
        return getInconsistencies(AbstractConsistencyError.class);
    }

    /**
     * @return
     */
    public Collection<AbstractConsistencyError> getCardinalityInconsistencies() {
        return getInconsistencies(AbstractCardinalityError.class);
    }

    /**
     * @return
     */
    public Collection<AbstractConsistencyError> getIDInconsistencies() {
        return getInconsistencies(AbstractIDError.class);
    }

    /**
     * @param errorClass
     * @return
     */
    private Collection<AbstractConsistencyError> getInconsistencies(final Class<? extends AbstractConsistencyError> errorClass) {
        Collection<AbstractConsistencyError> errors;
        if (errorClass.isAssignableFrom(AbstractCardinalityError.class)) {
            EdgeCardinalityChecker edgeCardinalityChecker = new EdgeCardinalityChecker(gdcoll, consistencyDefinition);
            errors = edgeCardinalityChecker.getErrors();
        } else {
            errors = new ArrayList<>();
        }
        if (errorClass.isAssignableFrom(AbstractIDError.class)) {
            Collection<AbstractConsistencyError> idErrors = idChecker.getErrors();
            errors.addAll(idErrors);
        }
        return errors;
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
    private Collection<ModelElement> getSolutionPropertyDialogElement(final AbstractConsistencyError error) {
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
    public boolean isSolutionExecuteable(final AbstractConsistencyError error) {
        return getSolutionPropertyDialogElement(error) != null;
    }

    /**
     * @param error
     * @return
     */
    public void execSolution(final AbstractConsistencyError error) {
        // 'es' ist null, wenn für den Fehler keine Solution hinterlegt wurde. Das gilt nur
        // für Fehler, für die im Eigenschaftsdialog des Elementes dann ein zusätzliches
        // OneToNUndirectedConnectionPanel angezeigt werden soll, in dem man den Fehler beheben kann
        if (error instanceof AbstractCardinalityError) {
            ErrorSolution es = solutionsLibrary.getSolution(error);
            if (es == null) {
                AbstractCardinalityError cardError = (AbstractCardinalityError) error;
                AbstractMetaPath elementaryMetaPath = cardError.getMetaPath();
                ModelElement me = cardError.getModelElement();
                ElementPropertyDialog dialog = me.getPropertyDialog();
                Class<? extends ModelElement> errorConnectedClass = elementaryMetaPath.getEndClass();
                ElementsNameBuilder elementsNameBuilder = gdcoll.getElementsNameBuilder();
                String tabName = elementaryMetaPath.isSingleConnection() ? elementsNameBuilder.getDisplayableName(errorConnectedClass) : elementsNameBuilder.getDisplayablePluralName(errorConnectedClass);
                //das folgende haut nicht hin. Die Tabs werden anscheinend immer doppelt angezeigt bzw. neu hinzugefügt, auch wenn ein identischer bereits ex.
                int existingTabIndex = dialog.selectTab(tabName, PathConnectionPanel.class);
                ImageIcon icon = Tool3lgmConstants.getIcon("error.gif");
                if (existingTabIndex < 0) {
                    //if the maximum cardinality is exceeded -> show always a multiple connection panel
                    dialog.addPathConnectionPanel(elementaryMetaPath, error instanceof MaxCardinalityError);
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

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (!UserProperties.BooleanProperty.OPTION_CHECK_CONSISTENCY.is()) {
            //die statische Instanz überall als Listener deregistrieren
            changeContext(null);
            removeAsToolChangeListener();
            UserProperties.removePropertyChangeListener(this);
        }
    }
}
