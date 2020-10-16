package de.imise.tool3lgm.graphtools.consistency.error.condition;

import de.imise.tool3lgm.graphtools.consistency.error.solution.ErrorSolution;
import de.imise.tool3lgm.graphtools.consistency.error.solution.MissingPathErrorSolution;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SectionMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;

/**
 * @author AXS (15.09.2020)
 */
public class MissingPathErrorCheckCondition extends ErrorCheckCondition {

    /**
     *
     */
    private final MetaPath toRealStartElements;

    /**
     *
     */
    private final SectionMetaPath toConnectableAndToConnectedSectionMetaPath;

    /**
     * @param toRealStartElements
     * @param toConnectableElements
     * @param toConnectedElements
     * @param errorResBaseKey
     * @param errorSolutionPanelMetaPath
     */
    public MissingPathErrorCheckCondition(final MetaPath toRealStartElements, final MetaPath toConnectableElements, final SimpleMetaPath toConnectedElements, final String errorResBaseKey, final SimpleMetaPath errorSolutionPanelMetaPath) {
        this(toRealStartElements, toConnectableElements, toConnectedElements, errorResBaseKey, new MissingPathErrorSolution(errorSolutionPanelMetaPath));
    }

    /**
     * @param toRealStartElements
     * @param toConnectableElements
     * @param toConnectedElements
     * @param errorResBaseKey
     * @param errorSolution
     */
    public MissingPathErrorCheckCondition(final MetaPath toRealStartElements, final MetaPath toConnectableElements, final SimpleMetaPath toConnectedElements, final String errorResBaseKey, final ErrorSolution errorSolution) {
        super(errorSolution);
        this.toRealStartElements = toRealStartElements;
        toConnectableAndToConnectedSectionMetaPath = new SectionMetaPath(errorResBaseKey, toConnectableElements, toConnectedElements);
    }

    /**
     * @return the toRealStartElements
     */
    public final MetaPath getMetaPathToRealStartElements() {
        return toRealStartElements;
    }

    /**
     * @return the toConnectableAndToConnectedSectionMetaPath
     */
    public final SectionMetaPath getToConnectableAndToConnectedSectionMetaPath() {
        return toConnectableAndToConnectedSectionMetaPath;
    }

    /**
     * @return
     */
    public final MetaPath getToConnectableMetaPath() {
        return toConnectableAndToConnectedSectionMetaPath.getFirstSubMetaPath();
    }

    /**
     * @return
     */
    public final SimpleMetaPath getToConnectedMetaPath() {
        return (SimpleMetaPath) toConnectableAndToConnectedSectionMetaPath.getLastSubMetaPath();
    }

}
