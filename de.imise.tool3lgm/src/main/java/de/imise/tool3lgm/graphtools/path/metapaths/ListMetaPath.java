package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.util.collections.CollectionUtils;

/**
 * @author AXS (10 Dec 2018)
 */
public abstract class ListMetaPath extends MetaPath {

    /**
     * The default reskey for the path is the same like Edge (= is connected with)
     */
    private static final String DEFAULT_RESKEY = Edge.class.getSimpleName() + "_f";

    /**
     * Liste der Pfade, die dieser Metapfad parallel enthält.
     */
    protected final List<MetaPath> subMetaPaths;

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
    public ListMetaPath(final MetaPath... subMetaPaths) {
        this(null, subMetaPaths);
    }

    /**
     * @param metaModel
     * @param baseResKeyOrName
     * @param subMetaPaths
     */
    public ListMetaPath(final String baseResKeyOrName, final MetaPath... subMetaPaths) {
        this(subMetaPaths[0], baseResKeyOrName, ImmutableList.copyOf(subMetaPaths));
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
        this(other, baseResKeyOrName, CollectionUtils.ensureImmutable(other.subMetaPaths));
    }

    /**
     * @param metaModelSource
     * @param baseResKeyOrName
     * @param subMetaPaths
     */
    private ListMetaPath(final MetaModelSpecific metaModelSource, final String baseResKeyOrName, final List<MetaPath> subMetaPaths) {
        super(metaModelSource.getMetaModel());
        this.baseResKeyOrName = !Strings.isNullOrEmpty(baseResKeyOrName) ? baseResKeyOrName : DEFAULT_RESKEY;
        this.subMetaPaths = subMetaPaths;
        initStartEndClasses();
    }

    protected abstract void initStartEndClasses();

    /**
     * @return the metaPaths
     */
    @Override
    public final List<MetaPath> getSubMetaPaths() {
        return subMetaPaths;
    }

    /**
     * @param index
     * @return
     */
    public MetaPath getSubMetaPath(final int index) {
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
        //wenn der Pfad aus Sicht des MetaPath valide ist
        if (super.getInvalidityCheckResult().invalidReason == null) {
            if (subMetaPaths.size() == 0) {
                invalidityCheckResult = new InvalidityCheckResult(InvalidReason.INVALID_LIST_PATH_EMPTY);
            } else {
                //jeden Einzelpfad durchgehen
                for (int i = 0; i < subMetaPaths.size(); i++) {
                    MetaPath metaPath = subMetaPaths.get(i);
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
    public Iterable<MetaPath> iterableSubMetaPaths() {
        return CollectionUtils.iterable(subMetaPaths);
    }

    /**
     * Setzt den MetaPath am angegebenen Index in der Liste der MetaPaths auf rekursiv.
     *
     * @param metaPathIndex
     */
    public final void setInterpretAsRecursive(final int metaPathIndex, final boolean recursive) {
        MetaPath metaPath = subMetaPaths.get(metaPathIndex);
        metaPath.setInterpretAsRecursive(recursive);
    }

    /**
     * @return
     */
    public final MetaPath getFirstSubMetaPath() {
        return subMetaPaths.get(0);
    }

    /**
     * @return
     */
    public final MetaPath getLastSubMetaPath() {
        return subMetaPaths.get(getSubMetaPathCount() - 1);
    }

}
