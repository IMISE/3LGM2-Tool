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
        super(metaPath.metaModel, metaPath.startElementClasses, metaPath.endElementClasses, name);
    }

    /**
     * @param metaPath
     * @param name
     */
    public RecursiveMetaPath(final AbstractMetaPath metaPath, final String forwardName, final String backwardName) {
        super(metaPath.metaModel, metaPath.startElementClasses, metaPath.endElementClasses, forwardName);
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

    public enum InvalidReason {
        INVALID_RECURSIVE_START_CLASSES,
        INVALID_RECURSIVE_END_CLASSES,
        INVALID_RECURSIVE_START_END_CLASSES
    }

    @Override
    public InvalidityCheckResult getInvalidityCheckResult() {
        //wenn der Pfad aus Sicht des AbstractMetaPath valide ist
        if (super.getInvalidityCheckResult().invalidReason == null) {
            InvalidReason invalidReason = null;
            //Nur zur Sicherheit nochmal testen, denn eigentlich sollte das nirgends anders gesetzt werden können
            if (!startElementClasses.equals(realMetaPath.startElementClasses)) {
                invalidReason = InvalidReason.INVALID_RECURSIVE_START_CLASSES;
            } else if (!endElementClasses.equals(realMetaPath.endElementClasses)) {
                invalidReason = InvalidReason.INVALID_RECURSIVE_END_CLASSES;
            } else if (!isStartClass(endElementClasses)) { //Endelemente müssen auch wieder Startelemente sein können
                invalidReason = InvalidReason.INVALID_RECURSIVE_START_END_CLASSES;
            }
            invalidityCheckResult = new InvalidityCheckResult(invalidReason);
        }
        return invalidityCheckResult;
    }

    @Override
    protected String createName() {
        return realMetaPath.createName();
    }

    @Override
    public boolean isCreatable() {
        return false;
    }

    @Override
    public boolean isDirected() {
        return realMetaPath.isDirected();
    }

    @Override
    public boolean containsPropertyTransferEdge() {
        return realMetaPath.containsPropertyTransferEdge();
    }

    @Override
    public List<AbstractMetaPath> getMetaPaths() {
        return realMetaPath.getMetaPaths();
    }

}
