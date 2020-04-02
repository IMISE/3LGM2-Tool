package de.imise.tool3lgm.graphtools.path.metapaths;

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
    protected final List<AbstractMetaPath> subMetaPaths;

    /**
     * BasisResourcenschlüssel oder Name des Pfades. Wenn dieser Schlüssel nicht
     * mit der jeweiligen Richtung "_f" (FORWARD) pder "_b" (BACKWARD) und auch
     * nicht so wie hier übergeben in den Resourcen gefunden wird, dann wird er
     * selbst als Name gesetzt.
     */
    protected final String baseResKeyOrName;

    /**
     * @param metaModel
     * @param subMetaPaths
     */
    public ListMetaPath(final AbstractMetaPath... subMetaPaths) {
        this(null, subMetaPaths);
    }

    /**
     * @param metaModel
     * @param baseResKeyOrName
     * @param subMetaPaths
     */
    public ListMetaPath(final String baseResKeyOrName, final AbstractMetaPath... subMetaPaths) {
        super(subMetaPaths[0].getMetaModel());
        this.baseResKeyOrName = baseResKeyOrName;
        this.subMetaPaths = ImmutableList.copyOf(subMetaPaths);
        initStartEndClasses();
    }

    /**
     * @param other
     */
    public ListMetaPath(final ListMetaPath other) {
        this(null, other);
    }

    /**
     * @param baseResKeyOrName
     * @param other
     */
    public ListMetaPath(final String baseResKeyOrName, final ListMetaPath other) {
        super(other.getMetaModel());
        this.baseResKeyOrName = baseResKeyOrName;
        subMetaPaths = CollectionUtils.ensureImmutable(other.subMetaPaths);
        initStartEndClasses();
    }

    protected abstract void initStartEndClasses();

    /**
     * @return the metaPaths
     */
    @Override
    public final List<AbstractMetaPath> getSubMetaPaths() {
        return subMetaPaths;
    }

    /**
     * @param index
     * @return
     */
    public AbstractMetaPath getSubMetaPath(final int index) {
        return subMetaPaths.get(index);
    }

    /**
     * @return
     */
    @Override
    public final int getSubMetaPathCount() {
        return subMetaPaths.size();
    }

    @Override
    protected String createName() {
        return metaModel.getResStringWithoutError(baseResKeyOrName);
    }

    public enum InvalidReason {
        INVALID_LIST_PATH_EMPTY
    }

    /**
     * @return the the {@link #baseResKeyOrName}
     */
    public final String getBaseResKeyOrName() {
        return baseResKeyOrName;
    }

    @Override
    public InvalidityCheckResult getInvalidityCheckResult() {
        //wenn der Pfad aus Sicht des AbstractMetaPath valide ist
        if (super.getInvalidityCheckResult().invalidReason == null) {
            if (subMetaPaths.size() == 0) {
                invalidityCheckResult = new InvalidityCheckResult(InvalidReason.INVALID_LIST_PATH_EMPTY);
            } else {
                //jeden Einzelpfad durchgehen
                for (int i = 0; i < subMetaPaths.size(); i++) {
                    AbstractMetaPath metaPath = subMetaPaths.get(i);
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
    public Iterable<AbstractMetaPath> iterableSubMetaPaths() {
        return CollectionUtils.iterable(subMetaPaths);
    }

    /**
     * Setzt den MetaPath am angegebenen Index in der Liste der MetaPaths auf rekursiv.
     *
     * @param metaPathIndex
     */
    public final void setInterpretAsRecursive(final int metaPathIndex, final boolean recursive) {
        AbstractMetaPath metaPath = subMetaPaths.get(metaPathIndex);
        metaPath.setInterpretAsRecursive(recursive);
    }

    /**
     * @return
     */
    public final AbstractMetaPath getFirstSubMetaPath() {
        return subMetaPaths.get(0);
    }

    /**
     * @return
     */
    public final AbstractMetaPath getLastSubMetaPath() {
        return subMetaPaths.get(getSubMetaPathCount() - 1);
    }

}
