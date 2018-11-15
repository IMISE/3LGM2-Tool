package de.imise.tool3lgm.graphtools.path.meta;

import java.util.List;

/**
 * @author AXS
 * @create 13.10.2010
 */
public class RecursiveMetaPath extends AbstractMetaPath {

    /**
     * Der durch diesen Metapfad gekapselte Metapfad, der mehrfach hintereinander
     * ausführbar sein sollte.
     */
    private AbstractMetaPath realMetaPath;

    /**
     * @param metaPath
     */
    public RecursiveMetaPath(final AbstractMetaPath metaPath) {
        this(metaPath, metaPath.name);
    }

    /**
     * @param metaPath
     * @param name
     */
    public RecursiveMetaPath(final AbstractMetaPath metaPath, final String name) {
        super(metaPath.startElementClasses, metaPath.endElementClasses, name);
    }

    /**
     * @param metaPath
     * @param name
     */
    public RecursiveMetaPath(final AbstractMetaPath metaPath, final String forwardName, final String backwardName) {
        super(metaPath.startElementClasses, metaPath.endElementClasses, forwardName);
    }

    /**
     * @return the realMetaPath
     */
    public AbstractMetaPath getRealMetaPath() {
        return realMetaPath;
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) && realMetaPath.equals(obj);
    }

    @Override
    public boolean isValid() {
        if (!realMetaPath.isValid()) {
            return false;
        }
        //Nur zur Sicherheit nochmal testen, denn eigentlich sollte das nirgends anders gesetzt werden können
        if (!(startElementClasses == realMetaPath.startElementClasses && endElementClasses == realMetaPath.endElementClasses)) {
            return false;
        }
        //Endelemente müssen auch wieder Startelemente sein können
        return isStartClass(endElementClasses, true, true);
    }

    @Override
    protected String createName() {
        return realMetaPath.createName();
    }

    @Override
    public boolean isCreateable() {
        return false;
    }

    @Override
    public List<ElementaryMetaPath> getSimpleMetaPath() {
        return null; // realMetaPath.getSimpleMetaPath(); ist hier nicht richtig, weil es in einem SequenceMetaPath
                     // nicht mehr eindeutig ist, dass es sich hier um einen sich wiederholenden Pfad gehandelt hat
    }

    @Override
    public boolean isDirected() {
        return realMetaPath.isDirected();
    }

    @Override
    public boolean containsHasPartEdge() {
        return realMetaPath.containsHasPartEdge();
    }

}
