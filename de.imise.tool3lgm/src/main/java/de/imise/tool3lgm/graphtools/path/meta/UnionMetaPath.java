package de.imise.tool3lgm.graphtools.path.meta;

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
     * @param name
     * @param metaPaths
     */
    public UnionMetaPath(final String name, final AbstractMetaPath... metaPaths) {
        super(name, metaPaths);
    }

    @Override
    public String createName() {
        //TODO:das hier macht nur super
        return "UnionMetaPath#createName() is not implemented";
    }

    @Override
    protected boolean isForwardRecursive() {
        //bei Vereinigungsmengen reicht es, wenn einer der enthaltenen Pfade für die Elemente rekursiv ist
        for (AbstractMetaPath metaPath : metaPaths) {
            if (metaPath.isForwardRecursive()) {
                return true;
            }
        }
        return false;
    }

}
