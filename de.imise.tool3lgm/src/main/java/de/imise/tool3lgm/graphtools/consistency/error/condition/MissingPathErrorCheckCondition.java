package de.imise.tool3lgm.graphtools.consistency.error.condition;

import de.imise.tool3lgm.graphtools.consistency.error.solution.ErrorSolution;
import de.imise.tool3lgm.graphtools.consistency.error.solution.MissingPathErrorSolution;
import de.imise.tool3lgm.graphtools.path.metapaths.IMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SectionMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;

/**
 * @author AXS (15.09.2020)
 */
public class MissingPathErrorCheckCondition extends ErrorCheckCondition {

    /**
     * If the error occurs because of missing paths of elements connected to the
     * source element, this metapath indicates the connection from the source
     * element to these faulty elements. These are the real start elements of
     * the {@link SectionMetaPath}
     * {@link #toConnectableAndToConnectedSectionMetaPath} If this metapath is
     * <code>null</code>, then the passed start element(s) are considered to be
     * the output elements of the error.
     */
    private final IMetaPath toRealStartElements;

    /**
     * Starting from the real start elements causing the error, this metapath
     * describes the connection to all elements that could be connected and that
     * are actually connected as an {@link SectionMetaPath} with 2 single
     * metapaths.
     */
    private final SectionMetaPath toConnectableAndToConnectedSectionMetaPath;

    /**
     * @param toRealStartElements If the error occurs because of missing paths
     *            of elements connected to the source element, this metapath
     *            indicates the connection from the source element to these
     *            faulty elements. These are the real start elements of the
     *            {@link SectionMetaPath}
     *            {@link #toConnectableAndToConnectedSectionMetaPath} If this
     *            metapath is <code>null</code>, then the passed start
     *            element(s) are considered to be the output elements of the
     *            error.
     * @param toConnectableElements Starting from the real start elements
     *            causing the error, this metapath describes the connection to
     *            all elements, of which at least one should be connected.
     * @param toConnectedElements Starting from the real start elements that
     *            cause the error, this metapath describes the connection to all
     *            elements that are actually connected.
     * @param errorResBaseKey The base resource key of the SectionMetaPath,
     *            which is formed from the passed metapaths. This key is used to
     *            display a meaningful description and a tooltip in the error
     *            table.
     * @param toFixTheErrorMetaPath This metapath describes the connections
     *            starting from the given start element to the element type to
     *            which the start element has to be connected in order to fix
     *            the error.
     */
    public MissingPathErrorCheckCondition(final IMetaPath toRealStartElements, final IMetaPath toConnectableElements, final IMetaPath toConnectedElements, final String errorResBaseKey, final SimpleMetaPath toFixTheErrorMetaPath) {
        this(toRealStartElements, toConnectableElements, toConnectedElements, errorResBaseKey, new MissingPathErrorSolution(toFixTheErrorMetaPath));
    }

    /**
     * @param toRealStartElements If the error occurs because of missing paths
     *            of elements connected to the source element, this metapath
     *            indicates the connection from the source element to these
     *            faulty elements. These are the real start elements of the
     *            {@link SectionMetaPath}
     *            {@link #toConnectableAndToConnectedSectionMetaPath} If this
     *            metapath is <code>null</code>, then the passed start
     *            element(s) are considered to be the output elements of the
     *            error.
     * @param toConnectableElements Starting from the real start elements
     *            causing the error, this metapath describes the connection to
     *            all elements, of which at least one should be connected.
     * @param toConnectedElements Starting from the real start elements that
     *            cause the error, this metapath describes the connection to all
     *            elements that are actually connected.
     * @param errorResBaseKey The base resource key of the SectionMetaPath,
     *            which is formed from the passed metapaths. This key is used to
     *            display a meaningful description and a tooltip in the error
     *            table.
     * @param errorSolution The solution to solve the error.
     */
    private MissingPathErrorCheckCondition(final IMetaPath toRealStartElements, final IMetaPath toConnectableElements, final IMetaPath toConnectedElements, final String errorResBaseKey, final ErrorSolution errorSolution) {
        super(errorSolution);
        this.toRealStartElements = toRealStartElements;
        toConnectableAndToConnectedSectionMetaPath = new SectionMetaPath(errorResBaseKey, toConnectableElements, toConnectedElements);
    }

    /**
     * @return the {@link #toRealStartElements}
     */
    public final IMetaPath getMetaPathToRealStartElements() {
        return toRealStartElements;
    }

    /**
     * @return the {@link #toConnectableAndToConnectedSectionMetaPath}
     */
    public final SectionMetaPath getToConnectableAndToConnectedSectionMetaPath() {
        return toConnectableAndToConnectedSectionMetaPath;
    }

    /**
     * @return Starting from the real start elements causing the error, this
     *         metapath describes the connection to all elements, of which at
     *         least one should be connected.
     */
    public final IMetaPath getToConnectableMetaPath() {
        return toConnectableAndToConnectedSectionMetaPath.getFirstSubMetaPath();
    }

    /**
     * @return Starting from the real start elements that cause the error, this
     *         metapath describes the connection to all elements that are
     *         actually connected.
     */
    public final IMetaPath getToConnectedMetaPath() {
        return toConnectableAndToConnectedSectionMetaPath.getLastSubMetaPath();
    }

    /**
     * @return This metapath describes the connections starting from the given
     *         start element to the element type to which the start element has
     *         to be connected in order to fix the error.
     */
    public final SimpleMetaPath getToFixTheErrorMetaPath() {
        return errorSolution.getPanelMetaPath();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (toConnectableAndToConnectedSectionMetaPath == null ? 0 : toConnectableAndToConnectedSectionMetaPath.hashCode());
        result = prime * result + (toRealStartElements == null ? 0 : toRealStartElements.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MissingPathErrorCheckCondition other = (MissingPathErrorCheckCondition) obj;
        if (toConnectableAndToConnectedSectionMetaPath == null) {
            if (other.toConnectableAndToConnectedSectionMetaPath != null) {
                return false;
            }
        } else if (!toConnectableAndToConnectedSectionMetaPath.equals(other.toConnectableAndToConnectedSectionMetaPath)) {
            return false;
        }
        if (toRealStartElements == null) {
            if (other.toRealStartElements != null) {
                return false;
            }
        } else if (!toRealStartElements.equals(other.toRealStartElements)) {
            return false;
        }
        return true;
    }

}
