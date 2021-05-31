package de.imise.tool3lgm.graphtools.path.metapaths;

/**
 * @author AXS
 * @create 13.10.2010
 */
public class SectionMetaPath extends ParallelMetaPath {

    /**
     * @param metaPaths
     */
    public SectionMetaPath(final MetaPath... metaPaths) {
        super(metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public SectionMetaPath(final String baseResKeyOrName, final MetaPath... metaPaths) {
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
        for (MetaPath metaPath : subMetaPaths) {
            if (!metaPath.canBeRecursive()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isCreatable(final boolean checkCreateEndElement) {
        for (MetaPath metaPath : getSubMetaPaths()) {
            if (!metaPath.isCreatable(checkCreateEndElement)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isUnambiguousCreatable(final boolean checkCreateEndElement) {
        for (MetaPath metaPath : getSubMetaPaths()) {
            if (!metaPath.isUnambiguousCreatable(checkCreateEndElement)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public SectionMetaPath createInstance(final MetaPath... subMetaPaths) {
        return new SectionMetaPath(subMetaPaths);
    }

}
