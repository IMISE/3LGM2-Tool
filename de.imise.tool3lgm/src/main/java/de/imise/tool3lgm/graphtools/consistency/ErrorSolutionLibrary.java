/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.ConsistencyCheckSectionMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;

/**
 * Diese Klasse ist dazu da, im Fehlerfall an einen besseren Ort (Tab in einem Eigenschaftsdialog) zu lenken,
 * als den Dialog des Elementes, bei dem bei einer Edge ein Kardinalitätsfehler besteht, direkt den Reiter für diese
 * fehlerhafte Edge zu öffnen.
 *
 * @author AXS
 */
public abstract class ErrorSolutionLibrary implements MetaModelSpecific {

    /**
     *
     */
    private Collection<ErrorSolution> errorSolutions = null;

    /**
     * @return
     */
    protected Collection<ErrorSolution> getErrorSolutions() {
        return ImmutableList.of();
    }

    /**
     * @param error
     * @return
     */
    public final ErrorSolution getSolution(final AbstractConsistencyError error) {
        if (errorSolutions == null) {
            errorSolutions = getErrorSolutions();
        }
        for (ErrorSolution solution : errorSolutions) {
            Class<? extends ModelElement> targetClass = solution.getTargetClass();
            ModelElement me = error.getModelElement();
            Class<? extends ModelElement> elementClass = me.getClass();
            if (targetClass.isAssignableFrom(elementClass)) {
                Class<? extends Edge> edgeClass = solution.getEdgeClass();
                Object errorField = error.getErrorField();
                if (errorField instanceof ElementaryMetaPath) {
                    ElementaryMetaPath elementaryMetaPath = (ElementaryMetaPath) errorField;
                    if (elementaryMetaPath.hasEdgeClass(edgeClass)) {
                        return solution;
                    }
                } else if (errorField instanceof ConsistencyCheckSectionMetaPath) {
                    MetaModel metaModel = getMetaModel();
                    Map<ConsistencyCheckSectionMetaPath, Class<? extends Edge>> consistencyConditionMissingConnectedElementsMetaPathsMap = metaModel.getConsistencyConditionMissingConnectedElementsMetaPaths();
                    Class<? extends Edge> errorSolutionIdentifierEdgeClass = consistencyConditionMissingConnectedElementsMetaPathsMap.get(errorField);
                    if (errorSolutionIdentifierEdgeClass == edgeClass) {
                        return solution;
                    }
                }
            }
        }
        return null;
    }

}
