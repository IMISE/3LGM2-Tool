package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.Collection;

/**
 * @author AXS
 * @create 13.10.2010
 */
public class UnionMetaPath extends ParallelMetaPath {

    /**
     * @param metaPaths
     */
    public UnionMetaPath(final AbstractMetaPath... metaPaths) {
        super(metaPaths);
    }

    /**
     * @param metaPaths
     */
    public UnionMetaPath(final Collection<SimpleMetaPath> metaPaths) {
        super(metaPaths);
    }

    /**
     * @param name
     * @param metaPaths
     */
    public UnionMetaPath(final String name, final AbstractMetaPath... metaPaths) {
        super(name, metaPaths);
    }

    /**
     * @param other
     */
    public UnionMetaPath(final ParallelMetaPath other) {
        super(other);
    }

    @Override
    public String createName() {
        //TODO:das hier macht nur super
        return "UnionMetaPath#createName() is not implemented";
    }

    @Override
    protected boolean canBeRecursive() {
        //bei Vereinigungsmengen reicht es, wenn einer der enthaltenen Pfade für die Elemente rekursiv ist
        for (AbstractMetaPath metaPath : subMetaPaths) {
            if (metaPath.canBeRecursive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isCreatable(final boolean checkCreateEndElement) {
        for (AbstractMetaPath metaPath : getSubMetaPaths()) {
            if (!metaPath.isCreatable(checkCreateEndElement)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public UnionMetaPath createInstance(final AbstractMetaPath... subMetaPaths) {
        return new UnionMetaPath(subMetaPaths);
    }

}
