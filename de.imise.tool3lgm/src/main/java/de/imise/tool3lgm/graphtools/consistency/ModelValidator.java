package de.imise.tool3lgm.graphtools.consistency;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.graphtools.consistency.error.checker.ConsistencyErrorChecker;
import de.imise.tool3lgm.graphtools.consistency.error.checker.EdgeCardinalityChecker;
import de.imise.tool3lgm.graphtools.consistency.error.checker.MissingPathChecker;
import de.imise.tool3lgm.graphtools.consistency.error.checker.UniqueIDChecker;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractCardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.event.PropertyChangeHandler;

/**
 * Die Klasse prüft die Konsistenz eines Modells. Es werden alle Kardinalitäten
 * überprüft und fehlerhafte Elemente zurück gegeben.
 *
 * @author AXS created on 06.08.2008
 */
/**
 * @author AXS (23.03.2020)
 */
public final class ModelValidator extends PropertyChangeHandler implements LGMChangeListenerSimple {

    /** Model to be checked */
    private final GDCollection gdcoll;

    /**
     * Maps from an error type to the checker which can find this type of errors
     */
    private final Collection<ConsistencyErrorChecker> checkers;

    /**
     * Die Kardinalitäts und Fehlerdefinitionen für die bei der Prüfung
     * relevanten Kanten. Wenn diese Variable <code>null</code> ist, werden alle
     * Kanten mit ihren Originalen Kardinalitäten geprüft.
     */
    private ConsistencyDefinition consistencyDefinition;

    /** Maps from an error type to all consistency errors of this type */
    private final Multimap<Class<? extends AbstractConsistencyError>, AbstractConsistencyError> consistencyErrorTypeToConsistencyErrors;

    /**
     * Erzeugt einen neuen {@link ModelValidator} mit initialisierter
     * {@link ModelValidatorDefinition}.
     *
     * @param gdcoll
     * @param changeContext
     */
    public ModelValidator(final GDCollection gdcoll) {
        checkers = new ArrayList<>();
        this.gdcoll = gdcoll;
        checkers.add(new EdgeCardinalityChecker());
        checkers.add(new MissingPathChecker());
        checkers.add(new UniqueIDChecker());
        consistencyErrorTypeToConsistencyErrors = ArrayListMultimap.create();
        gdcoll.addAllTransactionsListener(this);
        resetConsistencyDefinition();
    }

    /**
     * @param consistencyDefinition
     */
    public void resetConsistencyDefinition() {
        MetaModel metaModel = gdcoll.getMetaModel();
        consistencyDefinition = new ConsistencyDefinition(metaModel);
    }

    /**
     * @return
     */
    public ConsistencyDefinition getConsistencyDefinition() {
        return consistencyDefinition;
    }

    /**
     * Returns the model that this checker checks
     *
     * @return checked model
     */
    public GDCollection getCollection() {
        return gdcoll;
    }

    /**
     * Updates the map with all errors
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
