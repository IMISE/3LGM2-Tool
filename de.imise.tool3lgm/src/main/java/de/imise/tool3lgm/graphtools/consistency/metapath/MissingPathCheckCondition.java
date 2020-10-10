package de.imise.tool3lgm.graphtools.consistency.metapath;

import de.imise.tool3lgm.graphtools.consistency.ErrorSolution;
import de.imise.tool3lgm.graphtools.consistency.checker.CheckCondition;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SectionMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;

/**
 * @author AXS (15.09.2020)
 */
public class MissingPathCheckCondition extends CheckCondition {

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
     * @param errorSolutionPthToPropertyDialogElement
     * @param errorSolutionPanelMetaPath
     */
    public MissingPathCheckCondition(final MetaPath toRealStartElements, final MetaPath toConnectableElements, final MetaPath toConnectedElements, final MetaPath errorSolutionPthToPropertyDialogElement, final SimpleMetaPath errorSolutionPanelMetaPath) {
        this(toRealStartElements, toConnectableElements, toConnectedElements, new ErrorSolution(errorSolutionPthToPropertyDialogElement, errorSolutionPanelMetaPath));
    }

    /**
     * @param toRealStartElements
     * @param toConnectableElements
     * @param toConnectedElements
     */
    public MissingPathCheckCondition(final MetaPath toRealStartElements, final MetaPath toConnectableElements, final MetaPath toConnectedElements, final ErrorSolution errorSolution) {
        super(errorSolution);
        this.toRealStartElements = toRealStartElements;
        toConnectableAndToConnectedSectionMetaPath = new SectionMetaPath(toConnectableElements, toConnectedElements);
    }

    /**
     * @return the toRealStartElements
     */
    public final MetaPath getToRealStartElements() {
        return toRealStartElements;
    }

    /**
     * @return the toConnectableAndToConnectedSectionMetaPath
     */
    public final SectionMetaPath getToConnectableAndToConnectedSectionMetaPath() {
        return toConnectableAndToConnectedSectionMetaPath;
    }

}
