/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.consistency.error.condition.MissingPathErrorCheckCondition;
import de.imise.tool3lgm.graphtools.consistency.error.solution.CardinalityErrorSolution;
import de.imise.tool3lgm.graphtools.consistency.error.solution.ErrorSolution;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractCardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractIDError;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractPathError;
import de.imise.tool3lgm.graphtools.consistency.error.type.MaxCardinalityError;
import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.PropertyDialogUserFieldPanel;

/**
 * Diese Klasse ist dazu da, im Fehlerfall an einen besseren Ort (Tab in einem Eigenschaftsdialog) zu lenken,
 * als den Dialog des Elementes, bei dem bei einer Edge ein Kardinalitätsfehler besteht, direkt den Reiter für diese
 * fehlerhafte Edge zu öffnen.
 * <br>
 * ATTNENTION: This class will be instanciated by {@link Constructor#newInstance(Object...)}. So don't make it abstract!
 *
 * @author AXS
 */
public class ModelValidatorDefinition implements MetaModelSpecific {

    /**
     *
     */
    private Collection<ErrorSolution> errorSolutions = null;

    /**
     * @return
     */
    protected Collection<ErrorSolution> getCardinalityErrorSolutions() {
        return ImmutableList.of();
    }

    @Override
    public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
        return MetaModelDefinition.class;
    }

    /**
     * @param error
     * @return
     */
    private final ErrorSolution getSolution(final AbstractConsistencyError error) {
        ErrorSolution errorSolution = error.getErrorSolution();
        if (errorSolution != null) {
            return errorSolution;
        }
        if (errorSolutions == null) {
            errorSolutions = getCardinalityErrorSolutions();
        }
        for (ErrorSolution solution : errorSolutions) {
            Class<? extends ModelElement> targetClass = solution.getTargetClass();
            ModelElement me = error.getModelElement();
            Class<? extends ModelElement> elementClass = me.getClass();
            if (targetClass.isAssignableFrom(elementClass)) {
                if (error instanceof AbstractCardinalityError) {
                    if (solution instanceof CardinalityErrorSolution) {
                        AbstractCardinalityError cardError = (AbstractCardinalityError) error;
                        CardinalityErrorSolution cardinalityErrorSolution = (CardinalityErrorSolution) solution;
                        if (cardError.hasEdgeClass(cardinalityErrorSolution.edgeClass)) {
                            return solution;
                        }
                    }
                }
            }
        }
        return null;
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
        GDCollection gdcoll = error.getCollection();
        MetaModel metaModel = gdcoll.getMetaModel();
        ModelValidatorDefinition solutionsLibrary = metaModel.getModelValidatorDefinition();
        ErrorSolution es = solutionsLibrary.getSolution(error);
        if (es == null) {
            return new HashSet<>();
        }
        MetaPath pathToDialogElement = es.getPathToPropertyDialogElement();
        ModelElement me = error.getModelElement();
        if (pathToDialogElement != null) {
            Collection<ModelElement> connected = pathToDialogElement.getConnectedElements(me);
            if (connected.isEmpty()) {
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
        // für Fehler, für die im Eigenschaftsdialog des Elementes dann ein zusätzlicher
        // Tab angezeigt werden soll, in dem man den Fehler beheben kann
        if (error instanceof AbstractPathError) {
            GDCollection gdcoll = error.getCollection();
            MetaModel metaModel = gdcoll.getMetaModel();
            ModelValidatorDefinition solutionsLibrary = metaModel.getModelValidatorDefinition();
            ErrorSolution es = solutionsLibrary.getSolution(error);
            ImageIcon icon = Tool3lgmConstants.getIcon("error.gif");
            if (es == null) {
                AbstractPathError pathError = (AbstractPathError) error;
                MetaPath metaPath = pathError.getMetaPath();
                ModelElement me = pathError.getModelElement();
                ElementPropertyDialog dialog = me.getPropertyDialog();
                int selectedTabIndex = dialog.selectTab(metaPath);
                if (selectedTabIndex < 0) {
                    Class<? extends ModelElement> errorConnectedClass = metaPath.getEndClass();
                    ElementsNameBuilder elementsNameBuilder = gdcoll.getElementsNameBuilder();
                    String tabName = metaPath.isSingleConnection() ? elementsNameBuilder.getDisplayableName(errorConnectedClass) : elementsNameBuilder.getDisplayablePluralName(errorConnectedClass);
                    //if the maximum cardinality is exceeded -> show always a multiple connection panel
                    dialog.addPathConnectionPanel(metaPath, error instanceof MaxCardinalityError);
                    dialog.setLastTabIcon(icon);
                    dialog.setLastTabTitle(tabName);
                    dialog.selectLastTab();
                } else {
                    dialog.setTabIcon(selectedTabIndex, icon);
                }
                dialog.showDialog();
            } else {
                Collection<ModelElement> solutionPropertyDialogElement = getSolutionPropertyDialogElement(error);
                if (solutionPropertyDialogElement == null || solutionPropertyDialogElement.isEmpty()) {
                    return;
                }
                for (ModelElement connected : solutionPropertyDialogElement) {
                    ElementPropertyDialog dialog = connected.getPropertyDialog();
                    SimpleMetaPath panelMetaPath = es.getPanelMetaPath();
                    int selectedTabIndex = dialog.selectTab(panelMetaPath);
                    dialog.setTabIcon(selectedTabIndex, icon);
                    dialog.showDialog();
                }
            }
        } else if (error instanceof AbstractIDError) {
            ElementPropertyDialog dialog = error.getModelElement().getPropertyDialog();
            dialog.selectTab(PropertyDialogUserFieldPanel.class);
            dialog.showDialog();
        }
    }

    /**
     * Löscht alle Elemente komplett, die fehlerhaft sind, deren Fehler man aber nicht behandeln
     * kann. Darunter fallen alle Fehler, für die eine Error-Solution mit einem gültigen
     * <code>MetaPath</code> zu einem verbundenen Element hinterlegt ist, das aber nicht erreichtbar
     * ist, weil auch die Verbindung zu diesem Element fehlt. Somit kann der Fehler nirgends behoben
     * werden und man kann das Element löschen. In Metamodell 2.7 heißt das:
     * Anwendungsbaustein-Konfigurationen ohne einen Anwendungsbautein könnte man im Dialog der
     * Aufgaben an der mit der Anwendungsbaustein-Konfigurationen verbundenen AufOrgKombination
     * beheben. Wenn aber sowohl die Verbindung zur AufOrgKombination oder deren Verbindung zu einer
     * Aufgabe fehlt, dann wird diese Konfiguration einfach gelöscht, da man den Fehler nicht mehr
     * sinnvoll beheben kann. Das gleiche gilt für physische DV-Baustein-Konfigurationen ohne
     * Datenverarbeitungsbausteine. Dies kann man im Dialog der Anwendungsbausteine der
     * Konfiguration beheben. Fehlt aber auch diese Verbindung, dann kann man die Konfiguration
     * löschen.
     * ACHTUNG: MissingPathErrors werden hier doch nicht nach der obigen Beschreibung behandelt, d.h.
     * sie werden igoriert, da nicht klar ist, warum die über einen längeren Pfad nicht vorhandenen
     * Elemente fehlen, also an welcher Stelle der Pfad unterbrochen ist und an welchen Stellen bzw.
     * in welchen Eigenschaftsdialogen welcher Elemente im Pfad man den den Fehler beheben könnte. Die
     * Aussage der MissingPathErrors bezieht sich immer nur auf die letzte Kante im Pfad. Wenn aber
     * davor schon etwas nicht verbunden ist, kommt der Fehler auch und es gibt das Element überhaupt
     * nicht, dessen Eigenschaftsdialog zur Fehlerbehebung man öffnen sollte. Deswegen darf das als
     * fehlerhaft geltende Element aber trotzdem nicht einfach gelöscht werden.
     */
    public void clearUnfixableErrors(final GDCollection gdcoll) {
        ModelValidator modelValidator = gdcoll.getModelValidator();
        // dieses Löschen muss man nicht rückgängig machen können -> BulkMode einschalten
        boolean oldBulkMode = gdcoll.setBulkMode(true);
        //ignore MissingPathErrors resp. check only AbstractCardinalityErrors and AbstractIDErrors
        List<Class<? extends AbstractConsistencyError>> errorTypes = ImmutableList.of(AbstractCardinalityError.class, AbstractIDError.class);
        for (Class<? extends AbstractConsistencyError> errorType : errorTypes) {
            for (AbstractConsistencyError err : modelValidator.getInconsistencies(errorType)) {
                if (!isSolutionExecuteable(err)) {
                    ModelElement errorElement = err.getModelElement();
                    gdcoll.deleteElement(errorElement, TransactionManager.STANDARD_PID);
                }
            }
        }
        gdcoll.setBulkMode(oldBulkMode);
    }

    /**
     * @return
     */
    public Collection<MissingPathErrorCheckCondition> getMissingPathErrorCheckConditions() {
        return ImmutableList.of();
    }

}
