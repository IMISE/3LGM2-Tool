package de.imise.tool3lgm.graphtools.path.meta;

/**
 * @author AXS
 * @create 13.10.2010
 */
public class SectionMetaPath extends ParallelMetaPath {

    /**
     * @param metaPaths
     */
    public SectionMetaPath(final AbstractMetaPath... metaPaths) {
        super(metaPaths);
    }

    /**
     * @param name
     * @param metaPaths
     */
    public SectionMetaPath(final String name, final AbstractMetaPath... metaPaths) {
        super(name, metaPaths);
    }

    /**
     * @param other
     */
    public SectionMetaPath(final ParallelMetaPath other) {
        super(other);
    }

    @Override
    public String createName() {
        //TODO:das hier macht nur super
        return "SectionMetaPath#createName() is not implemented";
    }

    @Override
    protected boolean isForwardRecursive() {
        //bei Schnittmengenpfaden müssen alle enthaltenen Pfade rekursiv sein, damit es Ergebniselemente geben kann
        for (AbstractMetaPath metaPath : metaPaths) {
            if (!metaPath.isForwardRecursive()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isCreatable(final boolean checkCreateEndElement) {
        for (AbstractMetaPath metaPath : getMetaPaths()) {
            if (!metaPath.isCreatable(checkCreateEndElement)) {
                return false;
            }
        }
        return true;
    }

}
