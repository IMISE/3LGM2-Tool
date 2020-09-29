package de.imise.tool3lgm.graphtools.consistency.checker;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.Tool3lgmChangeListener;
import de.imise.tool3lgm.graphtools.consistency.ConsistencyDefinition;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractCardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.event.PropertyChangeHandler;

/**
 * Die Klasse prüft die Konsistenz eines Modells. Es werden alle Kardinalitäten überprüft und
 * fehlerhafte Elemente zurück gegeben.
 *
 * @author AXS created on 06.08.2008
 */
/**
 * @author AXS (23.03.2020)
 */
public final class ModelValidator extends PropertyChangeHandler implements LGMChangeListenerSimple, Tool3lgmChangeListener {

    /**
     * Checks the consistency of a model. This instance is used for the current selected Model
     */
    private static ModelValidator modelValidator;

    /**
     * Modell, das überprüft wird.
     */
    private GDCollection gdcoll;

    /**
     * Maps from an error type to the checker which can find this type of errors
     */
    private final Collection<ConsistencyErrorChecker> checkers;

    /**
     * Die Kardinalitäts und Fehlerdefinitionen für die bei der Prüfung relevanten Kanten. Wenn
     * diese Variable <code>null</code> ist, werden alle Kanten mit ihren Originalen Kardinalitäten
     * geprüft.
     */
    private ConsistencyDefinition consistencyDefinition;

    /**
     * Maps from an error type to all consistency errors of this type.
     */
    private final Multimap<Class<? extends AbstractConsistencyError>, AbstractConsistencyError> consistencyErrorTypeToConsistencyErrors;

    /**
     * Erzeugt einen neuen {@link ModelValidator} mit initialisierter <code>ErrorSolutionLibraryVersion</code>.
     *
     * @param gdcoll
     * @param changeContext
     */
    public ModelValidator(final GDCollection gdcoll, final boolean changeContext) {
        checkers = new ArrayList<>();
        checkers.add(new EdgeCardinalityChecker());
        checkers.add(new MissingPathChecker());
        checkers.add(new UniqueIDChecker());
        consistencyErrorTypeToConsistencyErrors = ArrayListMultimap.create();
        if (changeContext) {
            changeContext(gdcoll);
        } else {
            this.gdcoll = gdcoll;
            resetConsistencyDefinition();
        }
    }

    /**
     * Legt einen neuen Consistency-Checker an, der sich als Listener beim HauptModell der
     * übergebenen Collection registeriert.
     *
     * @param gdcoll
     */
    private ModelValidator(final GDCollection gdcoll) {
        this(gdcoll, false);
    }

    /**
     * Initializes the static instance of {@link ModelValidator} and
     * regsiters it as ToolChangeListener. Once called, the next call
     * has no change effect.
     */
    public static final void init() {
        if (modelValidator == null) {
            modelValidator = new ModelValidator(null, true);
            modelValidator.addAsToolChangeListener();
        }
    }

    /**
     * @return the {@link ModelValidator} instance for the current selected model (GDCollection)
     */
    public static ModelValidator getModelValidator() {
        return modelValidator;
    }

    /**
     * Adds a {@link ConsistencyErrorChecker} for a special error
     * type to the static instance of {@link ModelValidator}.
     *
     * @param errorTypeChecker
     */
    public static void registerChecker(final ConsistencyErrorChecker errorTypeChecker) {
        ModelValidator modelValidator = getModelValidator();
        modelValidator.checkers.add(errorTypeChecker);
    }

    /**
     * Setzt die übergebene Collection als aktuelle Collection
     *
     * @param gscoll
     */
    private void changeContext(final GDCollection gdcoll) {
        if (this.gdcoll != gdcoll) {
            if (this.gdcoll != null) {
                this.gdcoll.removeAllTransactionsListener(this);
            }
            this.gdcoll = gdcoll;
            if (this.gdcoll != null) {
                gdcoll.addAllTransactionsListener(this);
            }
            resetConsistencyDefinition();
            updateErrors();
        }
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

    @Override
    public void model_change_changed(final GraphDocument source) {
        GDCollection gdcoll = source.getCollection();
        changeContext(gdcoll);
    }

    /**
     * Liefert das Modell, das dieser Checker überprüft.
     *
     * @return überprüftes Modell
     */
    public GDCollection getCollection() {
        return gdcoll;
    }

    /**
     *
     */
    private void updateErrors() {
        consistencyErrorTypeToConsistencyErrors.clear();
        if (gdcoll == null) {
            return;
        }
        TransactionManager transactionManager = gdcoll.getTman();
        if (transactionManager.isDeepInTransaction()) {
            return;
        }
        for (ConsistencyErrorChecker consistencyErrorChecker : checkers) {
            if (consistencyErrorChecker instanceof EdgeCardinalityChecker) {
                EdgeCardinalityChecker edgeCardinalityChecker = (EdgeCardinalityChecker) consistencyErrorChecker;
                edgeCardinalityChecker.setConsistencyDefinition(consistencyDefinition);
            }
            Collection<AbstractConsistencyError> consistencyErrors = consistencyErrorChecker.getErrors(gdcoll);
            Class<? extends AbstractConsistencyError> errorType = consistencyErrorChecker.getErrorType();
            consistencyErrorTypeToConsistencyErrors.putAll(errorType, consistencyErrors);
        }
        firePropertyChange();
    }

    @Override
    public void dataChanged(final GraphDocument source) {
        updateErrors();
    }

    @Override
    public void elementNameChanged(final ElementContainer source) {
        dataChanged(null); //doc ist egal
    }

    /**
     * @param gdcoll
     * @return
     */
    public static final boolean hasInconsistencies(final GDCollection gdcoll) {
        ModelValidator modelValidator = new ModelValidator(gdcoll);
        return modelValidator.hasInconsistencies();
    }

    /** Gibt wieder, ob Kardinalitäts-Inkonsistenzen im Modell bestehen */
    public boolean hasInconsistencies() {
        return !consistencyErrorTypeToConsistencyErrors.isEmpty();
    }

    /**
     * @return
     */
    public Collection<AbstractConsistencyError> getInconsistencies() {
        return consistencyErrorTypeToConsistencyErrors.values();
    }

    /**
     * @return
     */
    public Collection<AbstractConsistencyError> getCardinalityInconsistencies() {
        return consistencyErrorTypeToConsistencyErrors.get(AbstractCardinalityError.class);
    }

    /**
     * @param errorType
     * @param consistencyErrorTypeToConsistencyErrors
     * @return
     */
    public Collection<AbstractConsistencyError> getInconsistencies(final Class<? extends AbstractConsistencyError> errorType) {
        if (errorType == AbstractConsistencyError.class) {
            return consistencyErrorTypeToConsistencyErrors.values();
        }
        Collection<AbstractConsistencyError> inconsistencies = consistencyErrorTypeToConsistencyErrors.get(errorType);
        if (inconsistencies == null) {
            inconsistencies = new ArrayList<>();
            for (Class<? extends AbstractConsistencyError> errorTypeKey : consistencyErrorTypeToConsistencyErrors.keySet()) {
                if (errorType.isAssignableFrom(errorTypeKey)) {
                    Collection<AbstractConsistencyError> errorTypeInconsistencies = consistencyErrorTypeToConsistencyErrors.get(errorType);
                    inconsistencies.addAll(errorTypeInconsistencies);
                }
            }
        }
        return inconsistencies;
    }

}
