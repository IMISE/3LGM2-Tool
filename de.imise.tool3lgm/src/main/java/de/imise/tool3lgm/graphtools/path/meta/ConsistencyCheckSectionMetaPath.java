package de.imise.tool3lgm.graphtools.path.meta;

import java.util.List;

/**
 * Special {@link SectionMetaPath} for missing path consistency checks. The path always consists
 * of only two sub MetaPaths. The first is an {@link AbstractMetaPath} which leads to all elements
 * which can be connected. The second sub MetaPath must be a creatable {@link SimpleMetaPath} which
 * leads to all elements, which are really connected. If there is an consistency issue because not
 * enough elements are connected over the second metapath which are marked as needed through the
 * first metapath, so the solution is to create paths to the needed element over the creatable
 * second MetaPath.
 *
 * @author AXS (27.03.2020)
 */
public class ConsistencyCheckSectionMetaPath extends SectionMetaPath {

    /**
     * @param baseResKeyOrName
     * @param firstMetaPathToConnectableElements
     * @param secondMetaPathToConnectedElements
     */
    public ConsistencyCheckSectionMetaPath(final String baseResKeyOrName, final AbstractMetaPath firstMetaPathToConnectableElements, final SimpleMetaPath secondSubMetaPathToConnectedElements) {
        super(baseResKeyOrName, firstMetaPathToConnectableElements, secondSubMetaPathToConnectedElements);
        if (!isCreatable(secondSubMetaPathToConnectedElements)) {
            throw new IllegalArgumentException(secondSubMetaPathToConnectedElements.getFullName());
        }
    }

    /**
     * @return
     */
    public final AbstractMetaPath getFirstSubMetaPathToConnectableElements() {
        return subMetaPaths.get(0);
    }

    /**
     * @return
     */
    public SimpleMetaPath getSecondSubMetaPathToConnectedElements() {
        return (SimpleMetaPath) subMetaPaths.get(1);
    }

    /**
     * Checks if the second metapath is createable. The in the difference to the
     * {@link SequenceMetaPath#isCreatable(boolean)} this function checks if the
     * metaPath starts with a combination of two ElemtaryMetaPaths where the second
     * ElementaryMetaPath is the other direction of the first metapath. If this
     * condition is fulfilled the metaPaths counts as createable if the sub metapath
     * from the third ElementaryMetaPath must be createable over
     * {@link SequenceMetaPath#isCreatable(boolean)}.
     *
     * @param secondSubMetaPathToConnectedElements
     * @return
     */
    private boolean isCreatable(final SimpleMetaPath secondSubMetaPathToConnectedElements) {
        SimpleMetaPath pathToCheckCreatable = secondSubMetaPathToConnectedElements;
        List<ElementaryMetaPath> elementaryMetaPaths = pathToCheckCreatable.getElementaryMetaPaths();
        if (elementaryMetaPaths.size() > 2) {
            ElementaryMetaPath firstElementaryMetaPath = elementaryMetaPaths.get(0);
            ElementaryMetaPath secondElementaryMetaPath = elementaryMetaPaths.get(1);
            secondElementaryMetaPath = secondElementaryMetaPath.getOtherDirection();
            if (firstElementaryMetaPath.equals(secondElementaryMetaPath)) {
                pathToCheckCreatable = pathToCheckCreatable.getSubPath(1);
            }
        }
        return pathToCheckCreatable.isCreatable(false);
    }

}
