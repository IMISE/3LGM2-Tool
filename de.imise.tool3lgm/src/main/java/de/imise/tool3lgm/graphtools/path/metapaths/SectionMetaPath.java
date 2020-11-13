package de.imise.tool3lgm.graphtools.path.metapaths;

/**
 * @author AXS
 * @create 13.10.2010
 */
public class SectionMetaPath extends ParallelMetaPath {

    /**
     * @param metaPaths
     */
    public SectionMetaPath(final IMetaPath... metaPaths) {
        super(metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public SectionMetaPath(final String baseResKeyOrName, final IMetaPath... metaPaths) {
        super(baseResKeyOrName, metaPaths);
    }

    /**
     * @param other
     */
    public SectionMetaPath(final ParallelMetaPath other) {
        super(other);
    }

    @Override
    public boolean canBeRecursive() {
        //bei Schnittmengenpfaden müssen alle enthaltenen Pfade rekursiv sein, damit es Ergebniselemente geben kann
        for (IMetaPath metaPath : subMetaPaths) {
            if (!metaPath.canBeRecursive()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isCreatable(final boolean checkCreateEndElement) {
        for (IMetaPath metaPath : getSubMetaPaths()) {
            if (!metaPath.isCreatable(checkCreateEndElement)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public SectionMetaPath createInstance(final IMetaPath... subMetaPaths) {
        return new SectionMetaPath(subMetaPaths);
    }

}
