package de.imise.tool3lgm.graphtools.consistency.metapath;

import java.util.List;

import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SectionMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SequenceMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;

/**
 * Special {@link SectionMetaPath} for missing path consistency checks. The path always consists
 * of only two sub MetaPaths. The first is an {@link MetaPath} which leads to all elements
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
     * If the {@link #errorCorrectingCreatableMetaPath} is not the whole
     * secondSubMetaPathToConnectedElements then
     */
    private SimpleMetaPath subMetaPathToRealStartElement;

    /**
     * This SimpleMetaPath is equals to the secondSubMetaPathToConnectedElements
     * or a subpath of this and is the metaPath that must be created to remove
     * the error.
     */
    private SimpleMetaPath errorCorrectingCreatableMetaPath;

    /**
     * Special {@link SectionMetaPath} for missing path consistency checks. The path always consists
     * of only two sub MetaPaths. The first is an {@link MetaPath} which leads to all elements
     * which can be connected. The second sub MetaPath must be a creatable {@link SimpleMetaPath} which
     * leads to all elements, which are really connected. If there is an consistency issue because not
     * enough elements are connected over the second metapath which are marked as needed through the
     * first metapath, so the solution is to create paths to the needed element over the creatable
     * second MetaPath.
     *
     * @param baseResKeyOrName
     * @param firstMetaPathToConnectableElements
     * @param secondMetaPathToConnectedElements
     */
    public ConsistencyCheckSectionMetaPath(final String baseResKeyOrName, final MetaPath firstMetaPathToConnectableElements, final SimpleMetaPath secondSubMetaPathToConnectedElements) {
        super(baseResKeyOrName, firstMetaPathToConnectableElements, secondSubMetaPathToConnectedElements);
        if (!isCreatable(secondSubMetaPathToConnectedElements)) {
            throw new IllegalArgumentException(secondSubMetaPathToConnectedElements.getFullName());
        }
    }

    /**
     * @return
     */
    public final MetaPath getFirstSubMetaPathToConnectableElements() {
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
        errorCorrectingCreatableMetaPath = secondSubMetaPathToConnectedElements;
        List<ElementaryMetaPath> elementaryMetaPaths = errorCorrectingCreatableMetaPath.getElementaryMetaPaths();
        if (elementaryMetaPaths.size() > 2) {
            ElementaryMetaPath firstElementaryMetaPath = elementaryMetaPaths.get(0);
            ElementaryMetaPath secondElementaryMetaPath = elementaryMetaPaths.get(1);
            secondElementaryMetaPath = secondElementaryMetaPath.getOtherDirection();
            if (firstElementaryMetaPath.equals(secondElementaryMetaPath)) {
                subMetaPathToRealStartElement = errorCorrectingCreatableMetaPath.getSubPath(0, 1);
                if (!subMetaPathToRealStartElement.isSingleConnection()) { //mit mehreren Startelementen verbunden zu sein, mach aus meiner Sicht keinen Sinn
                    subMetaPathToRealStartElement = secondSubMetaPathToConnectedElements;
                } else {
                    errorCorrectingCreatableMetaPath = errorCorrectingCreatableMetaPath.getSubPath(1);
                }
            }
        }
        return errorCorrectingCreatableMetaPath.isCreatable(false);
    }

    /**
     * @return the errorCorrectingCreatableMetaPath that is the SimpleMetaPath equals
     *         to the secondSubMetaPathToConnectedElements or a subpath of this and
     *         is the metaPath that must be created to remove the error
     */
    public SimpleMetaPath getErrorCorrectingCreatableMetaPath() {
        return errorCorrectingCreatableMetaPath;
    }

    /**
     * @return the subMetaPathToRealStartElements
     */
    public SimpleMetaPath getSubMetaPathToRealStartElement() {
        return subMetaPathToRealStartElement;
    }

}
