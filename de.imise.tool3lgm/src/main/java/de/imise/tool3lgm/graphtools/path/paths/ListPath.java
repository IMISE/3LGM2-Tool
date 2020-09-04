package de.imise.tool3lgm.graphtools.path.paths;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.util.collections.CollectionUtils;

/**
 * @author AXS (10.03.2020)
 */
public abstract class ListPath extends AbstractPath implements Iterable<AbstractPath> {

    /** Ein SequencePath enthält eine Liste anderer Pfade */
    private final List<AbstractPath> paths;

    /**
     * @param metaPath
     * @param startElement
     * @param endElement
     * @param paths
     */
    public ListPath(final MetaPath metaPath, final ModelElement startElement, final ModelElement endElement, final AbstractPath[] paths) {
        super(metaPath, startElement, endElement);
        this.paths = CollectionUtils.ensureImmutable(Arrays.asList(paths));
    }

    /**
     * @param metaPath
     * @param startElement
     * @param endElement
     * @param paths
     */
    public ListPath(final MetaPath metaPath, final ModelElement startElement, final ModelElement endElement, final List<AbstractPath> paths) {
        super(metaPath, startElement, endElement);
        this.paths = CollectionUtils.ensureImmutable(paths);
    }

    @Override
    public Iterator<AbstractPath> iterator() {
        return paths.iterator();
    }

    /**
     * @return
     */
    public int size() {
        return paths.size();
    }

    /**
     * @param i
     * @return
     */
    public AbstractPath get(final int i) {
        return paths.get(i);
    }

    @Override
    protected final void replace(final ModelElement original, final ModelElement replacement) {
        for (AbstractPath path : paths) {
            path.replace(original, replacement);
        }
    }

    @Override
    public boolean isValid() {
        if (paths == null) {
            return false;
        }
        int pathCount = paths.size();
        if (pathCount == 0) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paths.size(); i++) {
            sb.append(paths.get(i));
            if (i < paths.size() - 1) {
                sb.append(" <-> ");
            }
        }
        return sb.toString();
    }

}
