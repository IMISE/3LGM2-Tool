package de.imise.tool3lgm.graphtools.path.meta;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Tool3lgmConstants;

/**
 * @author AXS (10 Dec 2018)
 */
public abstract class ListMetaPath extends AbstractMetaPath implements Iterable<AbstractMetaPath> {

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
     * @param metaPaths
     */
    public ListMetaPath(final AbstractMetaPath... metaPaths) {
        this(null, metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public ListMetaPath(final String baseResKeyOrName, final AbstractMetaPath... metaPaths) {
        super();
        this.baseResKeyOrName = baseResKeyOrName;
        this.metaPaths = ImmutableList.copyOf(metaPaths);
        initStartEndClasses();
    }

    protected abstract void initStartEndClasses();

    /**
     * @return the metaPaths
     */
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
        return Tool3lgmConstants.getResStringWithoutError(baseResKeyOrName);
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

    @Override
    public final Iterator<AbstractMetaPath> iterator() {
        return metaPaths.iterator();
    }

}
