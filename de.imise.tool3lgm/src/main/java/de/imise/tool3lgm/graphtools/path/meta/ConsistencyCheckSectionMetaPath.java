package de.imise.tool3lgm.graphtools.path.meta;

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

}
