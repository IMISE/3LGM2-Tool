package de.imise.tool3lgm.graphtools.path.meta;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.util.collections.CollectionUtils;

/**
 * @author AXS (10 Dec 2018)
 */
public abstract class ListMetaPath extends AbstractMetaPath {

    /**
     * Liste der Pfade, die dieser Metapfad parallel enthält.
     */
    protected final List<AbstractMetaPath> metaPaths;

    /**
     * BasisResourcenschlüssel oder Name des Pfades. Wenn dieser Schlüssel nicht mit der jeweiligen Richtung "_f" (FORWARD) pder "_b" (BACKWARD) und
     * auch nicht so wie hier übergeben in den Resourcen gefunden wird, dann wird er selbst als Name gesetzt.
     */
    protected final String baseResKeyOrName;

    /**
     * @param metaModel
     * @param metaPaths
     */
    public ListMetaPath(final AbstractMetaPath... metaPaths) {
        this(null, metaPaths);
    }

    /**
     * @param metaModel
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public ListMetaPath(final String baseResKeyOrName, final AbstractMetaPath... metaPaths) {
        super(metaPaths[0].getMetaModel());
        this.baseResKeyOrName = baseResKeyOrName;
        this.metaPaths = ImmutableList.copyOf(metaPaths);
        initStartEndClasses();
    }

    protected abstract void initStartEndClasses();

    /**
     * @return the metaPaths
     */
    @Override
    public final List<AbstractMetaPath> getMetaPaths() {
        return metaPaths;
    }

    /**
     * @return
     */
    public final int getMetaPathCount() {
        return metaPaths.size();
    }

    @Override
    protected String createName() {
        return metaModel.getResStringWithoutError(baseResKeyOrName);
    }

    public enum InvalidReason {
        INVALID_LIST_PATH_EMPTY
    }

    @Override
    public InvalidityCheckResult getInvalidityCheckResult() {
        //wenn der Pfad aus Sicht des AbstractMetaPath valide ist
        if (super.getInvalidityCheckResult().invalidReason == null) {
            if (metaPaths.size() == 0) {
                invalidityCheckResult = new InvalidityCheckResult(InvalidReason.INVALID_LIST_PATH_EMPTY);
            } else {
                //jeden Einzelpfad durchgehen
                for (int i = 0; i < metaPaths.size(); i++) {
                    AbstractMetaPath metaPath = metaPaths.get(i);
                    InvalidityCheckResult innerInvalidityCheckResult = metaPath.getInvalidityCheckResult();
                    //wenn der Einzelpfad nicht valide ist
                    if (innerInvalidityCheckResult.invalidReason != null) {
                        //GesamtResult mit Pfadindex versehen
                        invalidityCheckResult = innerInvalidityCheckResult.index1 < 0 ? new InvalidityCheckResult(innerInvalidityCheckResult.invalidReason, i)
                                : new InvalidityCheckResult(innerInvalidityCheckResult.invalidReason, i, innerInvalidityCheckResult.index1);
                        break;
                    }
                }
            }
        }
        return invalidityCheckResult;
    }

    /**
     * @return iterable over all metapaths
     */
    public Iterable<AbstractMetaPath> iterableMetaPaths() {
        return CollectionUtils.iterable(metaPaths);
    }

    /**
     * Setzt den MetaPath am angegebenen Index in der Liste der MetaPaths auf rekursiv.
     *
     * @param metaPathIndex
     */
    public final void setRecursive(final int metaPathIndex, final boolean recursive) {
        AbstractMetaPath metaPath = metaPaths.get(metaPathIndex);
        metaPath.setRecursive(recursive);
    }

    /**
     * @return
     */
    public final AbstractMetaPath getFirstMetaPath() {
        return metaPaths.get(0);
    }

    /**
     * @return
     */
    public final AbstractMetaPath getLastMetaPath() {
        return metaPaths.get(getMetaPathCount() - 1);
    }

}
